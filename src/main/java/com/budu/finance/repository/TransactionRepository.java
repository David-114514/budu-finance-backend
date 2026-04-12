package com.budu.finance.repository;

import com.budu.finance.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 查某段時間的交易（按日期倒序）
    List<Transaction> findByDateBetweenOrderByDateDesc(LocalDate start, LocalDate end);

    // 查某人操作的所有交易
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);
}