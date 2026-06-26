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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        User user = resolveContributor(request, category);

        Transaction transaction = Transaction.builder()
                .date(request.getDate())
                .amount(request.getAmount())
                .category(category)
                .user(user)
                .description(request.getDescription())
                .mortgageInterestSaved(request.getMortgageInterestSaved())
                .build();

        transaction = transactionRepository.save(transaction);

        // 更新當前餘額帳戶
        Account currentBalanceAccount = accountService.findByName("CurrentBalance");
        if (currentBalanceAccount != null) {
            accountService.updateBalance(currentBalanceAccount.getId(), request.getAmount());
        }

        return toResponse(transaction);
    }

    public Page<TransactionResponse> getTransactionsByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        return transactionRepository.findByDateBetweenOrderByDateDesc(start, end, pageable)
                .map(this::toResponse);
    }

    private User resolveContributor(TransactionRequest request, Category category) {
        boolean isExpense = category.getType() == CategoryType.EXPENSE
                || category.getType() == CategoryType.PARENT_REPAY;

        if (isExpense) {
            return null;
        }

        if (request.getUserId() == null) {
            throw new RuntimeException("Contributor is required for income/transfer transactions");
        }

        return userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .date(t.getDate())
                .amount(t.getAmount())
                .description(t.getDescription())
                .categoryId(t.getCategory().getId())
                .categoryName(t.getCategory().getName())
                .userId(t.getUser() != null ? t.getUser().getId() : null)
                .userName(t.getUser() != null ? t.getUser().getName() : null)
                .mortgageInterestSaved(t.getMortgageInterestSaved())
                .build();
    }
}