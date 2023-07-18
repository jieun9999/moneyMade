package com.The_10th_Finance.domain.getmonthlysum;

import com.The_10th_Finance.accounts.db.Accounts;
import com.The_10th_Finance.accounts.model.AccountsResponseDto;
import com.The_10th_Finance.accounts.service.AccountsService;
import com.The_10th_Finance.error.BusinessLogicException;
import com.The_10th_Finance.error.ExceptionCode;
import com.The_10th_Finance.monthlysum.db.MonthlySum;
import com.The_10th_Finance.monthlysum.service.MonthlySumService;
import com.The_10th_Finance.property.db.Property;
import com.The_10th_Finance.property.service.PropertyService;
import com.The_10th_Finance.user.db.User;
import com.The_10th_Finance.user.model.UserResponseDto;
import com.The_10th_Finance.user.service.UserService;
import com.The_10th_Finance.user.usermapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountMonthlySumService {
    private final UserService userService;
    private final AccountsService accountsService;
    private final MonthlySumService monthlySumService;
    private final PropertyService propertyService;

    private final UserMapper userMapper;


    private static List<Long> getAccountIdln(List<AccountsResponseDto> accountsList) {
        return accountsList.stream()
                .map(AccountsResponseDto::getAccountId)
                .collect(Collectors.toList());
    }
    private static Map<Long, String> getAccountIdToAccountTypeMap(List<AccountsResponseDto> accountsList) {
        return accountsList.stream()
                .collect(Collectors.toMap(AccountsResponseDto::getAccountId, AccountsResponseDto::getAcoountType));
    }
    private static BigDecimal getMonthlySum(Map<Long, String> accountsList,List<MonthlySum> monthlySums, BigDecimal[] currentMonthsum, BigDecimal[] previousMonthSum,int currentMonth) {
        BigDecimal currentTotal=BigDecimal.ZERO,previousTotal=BigDecimal.ZERO;
        for (MonthlySum monthlySum : monthlySums) {
            String accountType = accountsList.get(monthlySum.getAccountId());
            int month = monthlySum.getDate().getMonthValue();

            // Current month
            if(month == currentMonth) {
                currentTotal=currentTotal.add(monthlySum.getMonthlyTotal());
                if (accountType.equals("입출금")) currentMonthsum[0] = currentMonthsum[0].add(monthlySum.getMonthlyTotal());
                else if (accountType.equals("증권")) currentMonthsum[1] = currentMonthsum[1].add(monthlySum.getMonthlyTotal());
                else if (accountType.equals("현금")) currentMonthsum[2] = currentMonthsum[2].add(monthlySum.getMonthlyTotal());
                else throw new BusinessLogicException(ExceptionCode.NOTYPE);
            }
            // Previous month
            else {
                previousTotal = previousTotal.add(monthlySum.getMonthlyTotal());
                if (accountType.equals("입출금")) previousMonthSum[0] = previousMonthSum[0].add(monthlySum.getMonthlyTotal());
                else if (accountType.equals("증권")) previousMonthSum[1] = previousMonthSum[1].add(monthlySum.getMonthlyTotal());
                else if (accountType.equals("현금")) previousMonthSum[2] = previousMonthSum[2].add(monthlySum.getMonthlyTotal());
                else throw new BusinessLogicException(ExceptionCode.NOTYPE);
            }
        }
        return currentTotal.subtract(previousTotal);
    }
//    private static void getMonthlySum(List<MonthlySum> monthlySums, BigDecimal[] currentMonthTypes, BigDecimal[] previousMonthTypes,int currentMonth,MonthSummary monthSummary) {
//        for (MonthlySum monthlySum : monthlySums) {
//            TempSummary tempSummary = new TempSummary();
//            String accountType = monthlySum.getAccountType();
//            int month = monthlySum.getDate().getMonthValue();
//            BigDecimal current_total=BigDecimal.ZERO,previous_total=BigDecimal.ZERO,incom=BigDecimal.ZERO,exponse=BigDecimal.ZERO;
//            // Current month
//            if(month == currentMonth) {
//                current_total=current_total.add(monthlySum.getMonthlyTotal());
//                incom=incom.add(monthlySum.getMonthlyIncome());
//                exponse = exponse.add(monthlySum.getMonthlyExpense());
//                if (accountType.equals("입출금")) currentMonthTypes[0] = currentMonthTypes[0].add(monthlySum.getMonthlyTotal());
//                else if (accountType.equals("증권")) currentMonthTypes[1] = currentMonthTypes[1].add(monthlySum.getMonthlyTotal());
//                else if (accountType.equals("현금")) currentMonthTypes[2] = currentMonthTypes[2].add(monthlySum.getMonthlyTotal());
//                else throw new BusinessLogicException(ExceptionCode.NOTYPE);
//            }
//            // Previous month
//            else {
//                previous_total=current_total.add(monthlySum.getMonthlyTotal());
//                if (accountType.equals("입출금")) previousMonthTypes[0] = previousMonthTypes[0].add(monthlySum.getMonthlyTotal());
//                else if (accountType.equals("증권")) previousMonthTypes[1] = previousMonthTypes[1].add(monthlySum.getMonthlyTotal());
//                else if (accountType.equals("현금")) previousMonthTypes[2] = previousMonthTypes[2].add(monthlySum.getMonthlyTotal());
//                else throw new BusinessLogicException(ExceptionCode.NOTYPE);
//            }
//            TempSummary.builder().expense(exponse).income(incom).total(current_total.subtract(previous_total));
//        }
//    }

    @Transactional
    public MonthlyResoponse getMonthlySum(Long userId,int month) {
        //*MonthlyType이란 클래스를 만들어서 추후 리펙토링 예정*

        //user에 대한 정보 가져오기
        User user = userService.findByUserId(userId);
        //보안상 문제가되는 것 빼고 변환해서
        UserResponseDto userResponseDto = userMapper.userToUserResponseDto(user);

        //부동산, 차들 가져오기
        Property property = propertyService.getByUserid(userId);

        MonthlyResponseDto monthlyResponseDto = makeCacheData(userId,month);

        return MonthlyResoponse.builder()
                .userResponseDto(userResponseDto)
                .propertyResponse(property)
                .monthlyResponseDto(monthlyResponseDto)
                .build();
    }


    public MonthlyResponseDto makeCacheData(Long userId,int month){
        List<AccountsResponseDto> accountsList = accountsService.getMyAccount(userId);
        Map<Long,String> mapping = getAccountIdToAccountTypeMap(accountsList);
        //계좌ID를 리스트화하기
        List<Long> accountId = getAccountIdln(accountsList);

        //계좌ID랑 메핑되는 MonthlySum가져오기
//        List<MonthlySum> monthlySums = monthlySumService.getMonthlySumList(accountId);
        List<MonthlySum> monthlySums = monthlySumService.getMonthlySumListbyMonth(accountId, month);

        //Type에 맞게 분류해서 한달 총합 계산하기
        BigDecimal[] currentMonthTypes = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        BigDecimal[] previousMonthTypes = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        BigDecimal minus = getMonthlySum(mapping,monthlySums, currentMonthTypes,previousMonthTypes,month);

        return MonthlyResponseDto.builder()
                .accountsList(accountsList)
                .inputAccount(currentMonthTypes[0].subtract(previousMonthTypes[0]))
                .jungunAccount(currentMonthTypes[1].subtract(previousMonthTypes[1]))
                .aashAccount(currentMonthTypes[2].subtract(previousMonthTypes[2]))
                .prviousMinCurrent(minus)
                .build();
    }
}