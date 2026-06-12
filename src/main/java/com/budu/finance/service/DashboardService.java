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

    /**
     * 取得家庭財務 Dashboard 資料。
     * 核心是「當前按揭戶口餘額」（CurrentBalance） + 「歷史貢獻記錄」（每位家庭成員的正數轉入總額）。
     * 這兩個數字是本系統最主要的使用價值。
     */
    public DashboardResponse getDashboard() {
        // 取得當前餘額帳戶（這是使用者最關心的即時數字）
        Account currentBalanceAccount = accountRepository.findAll().stream()
                .filter(a -> a.getName().equals("CurrentBalance"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CurrentBalance account not found"));

        // 計算個人貢獻（僅正數金額，支出負數永遠不影響）
        List<DashboardResponse.PersonalBalance> personalBalances = calculatePersonalBalances();

        return DashboardResponse.builder()
                .currentBalance(currentBalanceAccount.getCurrentBalance())
                .personalBalances(personalBalances)
                .build();
    }

    /**
     * 計算每位使用者的「個人貢獻」總額。
     *
     * 設計原則（與使用者確認一致）：
     * - 個人貢獻 = 該成員所有「正數金額」交易的加總。
     * - 這代表家庭成員對按揭戶口（CurrentBalance）的毛貢獻（historical contribution records）。
     * - 任何負數金額（支出 / 流出）的交易，**絕對不會**讓該使用者的貢獻數字減少。
     * - 消費記錄本身不是本系統的重點（銀行對賬單會更詳細），但仍可透過負數交易讓「當前餘額」保持準確。
     */
    private List<DashboardResponse.PersonalBalance> calculatePersonalBalances() {
        List<Transaction> allTransactions = transactionRepository.findAll();

        Map<Long, BigDecimal> contributionMap = allTransactions.stream()
                .filter(this::isPositiveContribution)
                .collect(Collectors.groupingBy(
                        t -> t.getUser().getId(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Transaction::getAmount,
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
     * 判斷一筆交易是否應計入「個人貢獻」。
     * 只有正數金額的交易才算貢獻（符合「僅計算正數金額」的規則）。
     */
    private boolean isPositiveContribution(Transaction t) {
        return t.getUser() != null
                && t.getAmount() != null
                && t.getAmount().compareTo(BigDecimal.ZERO) > 0;
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