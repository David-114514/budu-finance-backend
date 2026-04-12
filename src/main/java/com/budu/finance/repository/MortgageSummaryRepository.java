package com.budu.finance.repository;

import com.budu.finance.entity.MortgageSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortgageSummaryRepository extends JpaRepository<MortgageSummary, Long> {

    // 取得最新一筆按揭總覽
    MortgageSummary findTopByOrderByReportDateDesc();
}