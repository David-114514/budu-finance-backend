package com.budu.finance.service;

import com.budu.finance.dto.DashboardResponse;
import com.budu.finance.entity.*;
import com.budu.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccountRepository accountRepository;
    private final MortgageSummaryRepository mortgageSummaryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardResponse getDashboard() {
        // 取得按揭與抵銷帳戶
        Account mortgageAccount = accountRepository.findAll().stream()
                .filter(a -> a.getType() == AccountType.LIABILITY && a.getName().contains("Mortgage"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Mortgage account not found"));

        Account offsetAccount = accountRepository.findAll().stream()
                .filter(a -> a.getType() == AccountType.OFFSET)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Offset account not found"));

        // 取得最新按揭總覽
        MortgageSummary summary = mortgageSummaryRepository.findTopByOrderByReportDateDesc();
        if (summary == null) {
            summary = MortgageSummary.builder()
                    .reportDate(LocalDate.now())
                    .mortgageBalance(mortgageAccount.getCurrentBalance())
                    .offsetTotal(offsetAccount.getCurrentBalance())
                    .parentsInOffset(BigDecimal.ZERO)
                    .totalParentContribution(new BigDecimal("1200000"))
                    .build();
            summary = mortgageSummaryRepository.save(summary);
        }

        BigDecimal effectiveDebt = summary.getEffectiveDebt() != null
                ? summary.getEffectiveDebt()
                : mortgageAccount.getCurrentBalance().subtract(offsetAccount.getCurrentBalance());

        // ==================== B方案：計算個人剩餘金額 ====================
        List<DashboardResponse.PersonalBalance> personalBalances = calculatePersonalBalances();

        return DashboardResponse.builder()
                .mortgageBalance(mortgageAccount.getCurrentBalance())
                .offsetTotal(offsetAccount.getCurrentBalance())
                .parentsInOffset(summary.getParentsInOffset())
                .effectiveDebt(effectiveDebt)
                .totalParentContribution(summary.getTotalParentContribution())
                .thisMonthCoupleTransfer(BigDecimal.ZERO)
                .personalBalances(personalBalances)   // ← 新增
                .build();
    }

    private List<DashboardResponse.PersonalBalance> calculatePersonalBalances() {
        List<Transaction> allTransactions = transactionRepository.findAll();

        // 只計算正數交易（存入總額），負數交易（支出）不計入貢獻
        Map<Long, BigDecimal> contributionMap = allTransactions.stream()
                .filter(t -> t.getUser() != null && t.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.groupingBy(
                        t -> t.getUser().getId(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                t -> t.getAmount(),
                                BigDecimal::add
                        )
                ));

        return userRepository.findAll().stream()
                .map(user -> DashboardResponse.PersonalBalance.builder()
                        .userId(user.getId())
                        .userName(user.getName())
                        .balance(contributionMap.getOrDefault(user.getId(), BigDecimal.ZERO))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMortgageSummary(BigDecimal mortgageBalance, BigDecimal offsetTotal,
                                      BigDecimal parentsInOffset, BigDecimal parentContribution) {
        MortgageSummary summary = MortgageSummary.builder()
                .reportDate(LocalDate.now())
                .mortgageBalance(mortgageBalance)
                .offsetTotal(offsetTotal)
                .parentsInOffset(parentsInOffset)
                .totalParentContribution(parentContribution)
                .build();
        mortgageSummaryRepository.save(summary);
    }

    /**
     * 查詢指定日期範圍內「轉入按揭戶口」的總金額（按月分組）
     */
    public List<Map<String, Object>> getMonthlyTransfer(LocalDate start, LocalDate end) {
        List<Transaction> transactions = transactionRepository.findByDateBetweenAndCategoryName(
                start, end, "轉入按揭戶口"
        );

        // 按月份分組統計
        Map<String, BigDecimal> monthlyMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDate().getYear() + "-" + String.format("%02d", t.getDate().getMonthValue()),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        return monthlyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", entry.getKey());
                    map.put("amount", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
}