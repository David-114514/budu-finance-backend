package com.budu.finance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mortgage_summary")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MortgageSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal mortgageBalance;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal offsetTotal;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal parentsInOffset;

    @Column(precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal effectiveDebt;   // GENERATED ALWAYS AS ...

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal totalParentContribution;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}