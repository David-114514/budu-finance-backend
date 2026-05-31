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

    private BigDecimal currentBalance;           // 當前餘額（最重要）
    private List<PersonalBalance> personalBalances; // 個人貢獻

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