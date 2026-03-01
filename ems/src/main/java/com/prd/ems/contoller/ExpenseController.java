package com.prd.ems.contoller;


import com.prd.ems.dto.ExpenseRequest;
import com.prd.ems.entity.AppUser;
import com.prd.ems.entity.Expense;
import com.prd.ems.repository.ExpenseRepository;
import com.prd.ems.repository.UserRepository;
import com.prd.ems.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseService expenseService, ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<String> addExpense(@Valid @RequestBody ExpenseRequest request, Principal principal) {
        // Extract the logged-in user's username from the JWT context
        String username = principal.getName();

        AppUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Expense expense = new Expense();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(LocalDate.now());
        expense.setPaidBy(currentUser); // Automatically assign the logged-in user

        expenseRepository.save(expense);

        return ResponseEntity.ok("Expense added successfully!");
    }

    @GetMapping("/balances")
    public ResponseEntity<Map<String, BigDecimal>> getBalances() {
        Map<String, BigDecimal> balances = expenseService.calculateBalances();
        return ResponseEntity.ok(balances);
    }
}