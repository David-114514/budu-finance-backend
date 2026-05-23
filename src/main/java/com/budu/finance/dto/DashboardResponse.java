package com.budu.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal mortgageBalance;           // 按揭剩餘
    private BigDecimal offsetTotal;               // 抵銷戶口總額
    private BigDecimal parentsInOffset;           // 父母在抵銷戶口的錢
    private BigDecimal effectiveDebt;             // 有效淨債務（最重要！）
    private BigDecimal totalParentContribution;   // 父母總貢獻（首付+存款-已返還）
    private BigDecimal thisMonthCoupleTransfer;   // 本月夫妻轉入抵銷總額

    // ==================== B方案：個人剩餘金額 ====================
    private List<PersonalBalance> personalBalances;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalBalance {
        private Long userId;
        private String userName;
        private BigDecimal balance;
    }
}