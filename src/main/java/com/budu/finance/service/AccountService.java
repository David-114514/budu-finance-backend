package com.budu.finance.service;

import com.budu.finance.dto.AccountDto;
import com.budu.finance.entity.Account;
import com.budu.finance.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found: " + id));
    }

    // ==================== 新增這個方法 ====================
    public Account findByName(String name) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void updateBalance(Long accountId, BigDecimal delta) {
        Account account = findById(accountId);
        account.setCurrentBalance(account.getCurrentBalance().add(delta));
        accountRepository.save(account);
    }

    private AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .currentBalance(account.getCurrentBalance())
                .currency(account.getCurrency())
                .isActive(account.isActive())
                .build();
    }
}