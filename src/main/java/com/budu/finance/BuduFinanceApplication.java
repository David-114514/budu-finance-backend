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
                User husband = User.builder()
                        .name("dudu")
                        .role(UserRole.HUSBAND)
                        .password("123456")
                        .build();

                User wife = User.builder()
                        .name("bubu")
                        .role(UserRole.WIFE)
                        .password("123456")
                        .build();

                User husbandParent = User.builder()
                        .name("duduP")
                        .role(UserRole.PARENT)
                        .password("123456")
                        .build();

                User wifeParent = User.builder()
                        .name("bubuP")
                        .role(UserRole.PARENT)
                        .password("123456")
                        .build();

                userRepository.saveAll(List.of(husband, wife, husbandParent, wifeParent));
                System.out.println("✅ 用戶初始化完成（英文帳號）");
            } else {
                System.out.println("✅ 用戶已存在，跳過初始化");
            }

            // ==================== 2. 初始化 Category ====================
            if (categoryRepository.count() == 0) {
                List<Category> categories = List.of(
                        Category.builder().name("轉入按揭戶口").type(CategoryType.TRANSFER).icon("🏦").build(),
                        Category.builder().name("按揭戶口支出").type(CategoryType.EXPENSE).icon("🛒").build()
                );
                categoryRepository.saveAll(categories);
                System.out.println("✅ 類別初始化完成（極簡 2 類別）");
            }

            // ==================== 3. 初始化 Account ====================
            if (accountRepository.count() == 0) {
                Account currentBalance = Account.builder()
                        .name("CurrentBalance")
                        .type(AccountType.OFFSET)
                        .currentBalance(BigDecimal.ZERO)
                        .build();
                accountRepository.save(currentBalance);
                System.out.println("✅ 當前餘額帳戶初始化完成");
            }

            System.out.println("🎉 Budu Finance 後端初始化資料完成！可以開始使用了！");
        };
    }
}