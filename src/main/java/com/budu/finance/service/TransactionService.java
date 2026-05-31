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

        // 建立交易記錄（已刪除 contributor 相關）
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
                .categoryId(t.getCategory().getId())
                .categoryName(t.getCategory().getName())
                .userId(t.getUser().getId())
                .userName(t.getUser().getName())
                .mortgageInterestSaved(t.getMortgageInterestSaved())
                .build();
    }
}