package com.The_10th_Finance.jwt;

import com.The_10th_Finance.auth.AuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtVerificationFilter extends  OncePerRequestFilter {
        private final JwtTokenizer jwtTokenizer;
        private final AuthorityUtils authorityUtils;

        // (2)
        public JwtVerificationFilter(JwtTokenizer jwtTokenizer,
                                     AuthorityUtils authorityUtils) {
            this.jwtTokenizer = jwtTokenizer;
            this.authorityUtils = authorityUtils;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            // (1)
            try {
                Map<String, Object> claims = verifyJws(request);
                setAuthenticationToContext(claims);
            } catch (SignatureException se) {
                request.setAttribute("exception", se);
            } catch (ExpiredJwtException ee) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } catch (Exception e) {
                request.setAttribute("exception", e);
            }

            filterChain.doFilter(request, response);
        }


        // (6)
        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
            String authorization = request.getHeader("Authorization");  // (6-1)

            return authorization == null || !authorization.startsWith("Bearer");  // (6-2)
        }

        private Map<String, Object> verifyJws(HttpServletRequest request) {
            String jws = request.getHeader("Authorization").replace("Bearer ", ""); // (3-1)
            String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey()); // (3-2)
            Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();   // (3-3)

            return claims;
        }

        private void setAuthenticationToContext(Map<String, Object> claims) {
            String username = (String) claims.get("username");   // (4-1)
            List<GrantedAuthority> authorities = authorityUtils.createAuthority((List)claims.get("roles"));  // (4-2)
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);  // (4-3)
            SecurityContextHolder.getContext().setAuthentication(authentication); // (4-4)
        }

    }

