package com.budu.finance;

import com.budu.finance.entity.*;
import com.budu.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class BuduFinanceApplication {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public static void main(String[] args) {
        SpringApplication.run(BuduFinanceApplication.class, args);
    }

    @Bean
    CommandLineRunner initData() {
        return args -> {
            // ==================== 1. 初始化 User ====================
            if (userRepository.count() == 0) {
                User husband = User.builder().name("丈夫").role(UserRole.HUSBAND).build();
                User wife = User.builder().name("妻子").role(UserRole.WIFE).build();
                User husbandParent = User.builder().name("丈夫父母").role(UserRole.PARENT).build();
                User wifeParent = User.builder().name("妻子父母").role(UserRole.PARENT).build();

                userRepository.saveAll(List.of(husband, wife, husbandParent, wifeParent));
                System.out.println("✅ 用戶初始化完成");
            }

            // ==================== 2. 初始化 Category ====================
            if (categoryRepository.count() == 0) {
                List<Category> categories = List.of(
                        Category.builder().name("薪資收入").type(CategoryType.INCOME).icon("💰").build(),
                        Category.builder().name("轉入抵銷戶口").type(CategoryType.TRANSFER).icon("🏦").build(),
                        Category.builder().name("父母抵銷存款").type(CategoryType.PARENT_CONTRIB).icon("👨‍👩‍👧").build(),
                        Category.builder().name("利息節省返還父母").type(CategoryType.PARENT_REPAY).icon("🔄").build(),
                        Category.builder().name("家用開支").type(CategoryType.EXPENSE).icon("🛒").build(),
                        Category.builder().name("家電購置").type(CategoryType.EXPENSE).icon("📱").build(),
                        Category.builder().name("中介及律師費").type(CategoryType.EXPENSE).icon("📄").build()
                );
                categoryRepository.saveAll(categories);
                System.out.println("✅ 類別初始化完成");
            }

            // ==================== 3. 初始化 Account ====================
            if (accountRepository.count() == 0 || accountRepository.count() < 3) {
                // Mortgage Loan
                Account mortgage = Account.builder()
                        .name("Mortgage Loan")
                        .type(AccountType.LIABILITY)
                        .currentBalance(new BigDecimal("2800000.00"))
                        .build();

                // Offset Account
                Account offset = Account.builder()
                        .name("Offset Account")
                        .type(AccountType.OFFSET)
                        .currentBalance(BigDecimal.ZERO)
                        .build();

                // Salary / 現金帳戶
                Account salary = Account.builder()
                        .name("Salary Account")
                        .type(AccountType.ASSET)
                        .currentBalance(BigDecimal.ZERO)
                        .build();

                accountRepository.saveAll(List.of(mortgage, offset, salary));
                System.out.println("✅ 帳戶初始化完成");
            }

            System.out.println("🎉 Budu Finance 後端初始化資料完成！可以開始使用了！");
        };
    }
}