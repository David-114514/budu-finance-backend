package com.budu.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull
    private LocalDate date;

    /**
     * 交易金額。
     *
     * 建議慣例（配合本系統「當前按揭戶口餘額 + 歷史貢獻記錄」的簡單目的）：
     * - 大多數情況輸入正數金額。
     * - 類別（CategoryType）決定對 CurrentBalance 的實際影響方向：
     *   - 轉入類（TRANSFER / INCOME / PARENT_CONTRIB）→ 增加餘額 + 計入個人貢獻
     *   - 支出類（EXPENSE / PARENT_REPAY）→ 減少餘額（讓當前餘額保持準確），但**不計入個人貢獻**
     * - 個人貢獻計算永遠只看正數金額，負數永遠不會讓貢獻減少（這是已確認的核心規則）。
     *
     * 目前仍相容既有帶負號的交易資料。
     */
    @NotNull
    private BigDecimal amount;

    private Long fromAccountId;     // 可為 null（例如純收入）
    private Long toAccountId;       // 可為 null（例如純支出）

    @NotNull
    private Long categoryId;

    @NotNull
    private Long userId;            // 操作者（丈夫或妻子）

    private Long contributorId;     // 父母貢獻時使用

    private String description;

    private BigDecimal mortgageInterestSaved = BigDecimal.ZERO;
}