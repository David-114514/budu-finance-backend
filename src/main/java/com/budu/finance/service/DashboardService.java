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
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardResponse getDashboard() {
        // 取得當前餘額帳戶
        Account currentBalanceAccount = accountRepository.findAll().stream()
                .filter(a -> a.getName().equals("CurrentBalance"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CurrentBalance account not found"));

        // 計算個人貢獻
        List<DashboardResponse.PersonalBalance> personalBalances = calculatePersonalBalances();

        return DashboardResponse.builder()
                .currentBalance(currentBalanceAccount.getCurrentBalance())
                .personalBalances(personalBalances)
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