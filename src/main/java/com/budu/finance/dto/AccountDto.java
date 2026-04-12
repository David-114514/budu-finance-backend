package com.budu.finance.dto;

import com.budu.finance.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String name;
    private AccountType type;
    private BigDecimal currentBalance;
    private String currency;
    private boolean isActive;
}