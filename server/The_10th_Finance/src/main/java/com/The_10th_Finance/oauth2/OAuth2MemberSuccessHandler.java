//package com.The_10th_Finance.oauth2;
//
//import com.The_10th_Finance.auth.AuthorityUtils;
//import com.The_10th_Finance.jwt.JwtTokenizer;
//import com.The_10th_Finance.user.service.UserService;
//import org.springframework.security.core.Authentication;
////import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.net.URI;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//    private final JwtTokenizer jwtTokenizer;
//    private final AuthorityUtils authorityUtils;
//    private final UserService userService;
//
//    public OAuth2MemberSuccessHandler(JwtTokenizer jwtTokenizer, AuthorityUtils authorityUtils, UserService userService) {
//        this.jwtTokenizer = jwtTokenizer;
//        this.authorityUtils = authorityUtils;
//        this.userService = userService;
//    }
//    private void redirect(HttpServletRequest request, HttpServletResponse response, String username, List<String> authorities) throws IOException {
//        String accessToken = delegateAccessToken(username, authorities);  // (6-1)
//        String refreshToken = delegateRefreshToken(username);     // (6-2)
//
//        String uri = createURI(accessToken, refreshToken).toString();   // (6-3)
//        getRedirectStrategy().sendRedirect(request, response, uri);   // (6-4)
//    }
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        var oAuth2User = (OAuth2User)authentication.getPrincipal();
//        String email = String.valueOf(oAuth2User.getAttributes().get("email")); // (3)
//        List<String> authorities = authorityUtils.createRoles(email);           // (4)
//
//       // saveMember(email);  // (5)
//        redirect(request, response, email, authorities);  // (6)
//    }
//
//    private void saveMember(String email) {
////        User user = new User(email);
////        memberService.createMember(member);
//    }
//    private URI createURI(String accessToken, String refreshToken) {
//        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
//        queryParams.add("access_token", accessToken);
//        queryParams.add("refresh_token", refreshToken);
//
//        return UriComponentsBuilder
//                .fromPath("/{accesstocken}/{refreshtocken}")  // 엔드포인트 경로 지정
//                .buildAndExpand(accessToken, refreshToken)  // 경로 변수(expand) 설정
//                .toUri();
//    }
//
//    private String delegateAccessToken(String username, List<String> authorities) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("username", username);
//        claims.put("roles", authorities);
//
//        String subject = username;
//        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
//
//        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
//
//        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);
//
//        return accessToken;
//    }
//
//    private String delegateRefreshToken(String username) {
//        String subject = username;
//        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
//        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
//
//        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);
//
//        return refreshToken;
//    }
//}
