package com.prd.ems.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;

    @ManyToOne
    @JoinColumn(name = "paid_by_id")
    private AppUser paidBy;
}
