package com.budu.finance.service;

import com.budu.finance.dto.DashboardResponse;
import com.budu.finance.entity.Account;
import com.budu.finance.entity.AccountType;
import com.budu.finance.entity.MortgageSummary;
import com.budu.finance.repository.AccountRepository;
import com.budu.finance.repository.MortgageSummaryRepository;
import com.budu.finance.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccountRepository accountRepository;
    private final MortgageSummaryRepository mortgageSummaryRepository;
    private final TransactionRepository transactionRepository;

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

        // 取得最新按揭總覽（如果沒有，則建立一筆）
        MortgageSummary summary = mortgageSummaryRepository.findTopByOrderByReportDateDesc();
        if (summary == null) {
            summary = MortgageSummary.builder()
                    .reportDate(LocalDate.now())
                    .mortgageBalance(mortgageAccount.getCurrentBalance())
                    .offsetTotal(offsetAccount.getCurrentBalance())
                    .parentsInOffset(BigDecimal.ZERO)           // 暫時手動維護
                    .totalParentContribution(new BigDecimal("1200000"))  // 初始首付120萬
                    .build();
            summary = mortgageSummaryRepository.save(summary);
        }

        BigDecimal effectiveDebt = summary.getEffectiveDebt() != null ?
                summary.getEffectiveDebt() :
                mortgageAccount.getCurrentBalance().subtract(offsetAccount.getCurrentBalance());

        // 本月夫妻轉入（簡化版，可後續優化）
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);

        return DashboardResponse.builder()
                .mortgageBalance(mortgageAccount.getCurrentBalance())
                .offsetTotal(offsetAccount.getCurrentBalance())
                .parentsInOffset(summary.getParentsInOffset())
                .effectiveDebt(effectiveDebt)
                .totalParentContribution(summary.getTotalParentContribution())
                .thisMonthCoupleTransfer(BigDecimal.ZERO)   // 後續可從 transaction 計算
                .build();
    }

    // 每月手動更新按揭總覽（你從銀行 App 複製數據後呼叫）
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
}