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

    private BigDecimal currentBalance;           // 當前按揭戶口餘額（本系統最核心的即時數字）
    private List<PersonalBalance> personalBalances; // 每位家庭成員的歷史貢獻總額（僅正數流入計入，支出不影響）

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