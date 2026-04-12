package com.budu.finance.service;

import com.budu.finance.dto.TransactionRequest;
import com.budu.finance.dto.TransactionResponse;
import com.budu.finance.entity.*;
import com.budu.finance.repository.CategoryRepository;
import com.budu.finance.repository.TransactionRepository;
import com.budu.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Account fromAccount = request.getFromAccountId() != null ?
                accountService.findById(request.getFromAccountId()) : null;

        Account toAccount = request.getToAccountId() != null ?
                accountService.findById(request.getToAccountId()) : null;

        // 建立交易記錄
        Transaction transaction = Transaction.builder()
                .date(request.getDate())
                .amount(request.getAmount())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .category(category)
                .user(user)
                .contributor(request.getContributorId() != null ?
                        userRepository.findById(request.getContributorId()).orElse(null) : null)
                .description(request.getDescription())
                .mortgageInterestSaved(request.getMortgageInterestSaved())
                .build();

        transaction = transactionRepository.save(transaction);

        // 更新帳戶餘額
        if (fromAccount != null) {
            accountService.updateBalance(fromAccount.getId(), request.getAmount().negate());
        }
        if (toAccount != null) {
            accountService.updateBalance(toAccount.getId(), request.getAmount());
        }

        return toResponse(transaction);
    }

    public List<TransactionResponse> getTransactionsByDateRange(LocalDate start, LocalDate end) {
        return transactionRepository.findByDateBetweenOrderByDateDesc(start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .date(t.getDate())
                .amount(t.getAmount())
                .description(t.getDescription())
                .fromAccountId(t.getFromAccount() != null ? t.getFromAccount().getId() : null)
                .fromAccountName(t.getFromAccount() != null ? t.getFromAccount().getName() : null)
                .toAccountId(t.getToAccount() != null ? t.getToAccount().getId() : null)
                .toAccountName(t.getToAccount() != null ? t.getToAccount().getName() : null)
                .categoryId(t.getCategory().getId())
                .categoryName(t.getCategory().getName())
                .userId(t.getUser().getId())
                .userName(t.getUser().getName())
                .contributorId(t.getContributor() != null ? t.getContributor().getId() : null)
                .contributorName(t.getContributor() != null ? t.getContributor().getName() : null)
                .mortgageInterestSaved(t.getMortgageInterestSaved())
                .build();
    }
}