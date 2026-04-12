package com.budu.finance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

    @NotNull
    @PositiveOrZero
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