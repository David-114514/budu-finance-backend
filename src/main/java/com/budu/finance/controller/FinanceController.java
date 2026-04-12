package com.budu.finance.controller;

import com.budu.finance.dto.*;
import com.budu.finance.repository.CategoryRepository;
import com.budu.finance.repository.UserRepository;
import com.budu.finance.service.AccountService;
import com.budu.finance.service.DashboardService;
import com.budu.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class FinanceController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final DashboardService dashboardService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // ==================== Dashboard ====================
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse dashboard = dashboardService.getDashboard();
        return ResponseEntity.ok(dashboard);
    }

    // ==================== Transactions ====================
    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(start, end);
        return ResponseEntity.ok(transactions);
    }

    // ==================== Accounts ====================
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    // ==================== Mortgage Summary ====================
    // 每月從銀行 App 複製數據後，手動更新按揭總覽
    @PostMapping("/mortgage-summary")
    public ResponseEntity<String> updateMortgageSummary(
            @RequestParam BigDecimal mortgageBalance,
            @RequestParam BigDecimal offsetTotal,
            @RequestParam BigDecimal parentsInOffset,
            @RequestParam BigDecimal totalParentContribution) {

        dashboardService.updateMortgageSummary(mortgageBalance, offsetTotal, parentsInOffset, totalParentContribution);
        return ResponseEntity.ok("Mortgage summary updated successfully");
    }

    // 簡單健康檢查
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend is running successfully! 🚀");
    }

    // ==================== Users ====================
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        // 為了快速實現，我們直接從 repository 取（之後可以加 UserService）
        List<UserDto> users = userRepository.findAll().stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .role(user.getRole())
                        .build())
                .toList();
        return ResponseEntity.ok(users);
    }

    // ==================== Categories ====================
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryRepository.findAll().stream()
                .map(cat -> CategoryDto.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .type(cat.getType())
                        .icon(cat.getIcon())
                        .build())
                .toList();
        return ResponseEntity.ok(categories);
    }
}