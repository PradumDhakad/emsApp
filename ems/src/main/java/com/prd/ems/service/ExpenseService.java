package com.prd.ems.service;


import com.prd.ems.entity.AppUser;
import com.prd.ems.entity.Expense;
import com.prd.ems.repository.ExpenseRepository;
import com.prd.ems.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public Map<String, BigDecimal> calculateBalances() {
        List<Expense> allExpenses = expenseRepository.findAll();
        List<AppUser> allUsers = userRepository.findAll();

        BigDecimal totalExpense = allExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int userCount = allUsers.size();
        if (userCount == 0) return new HashMap<>();

        // What everyone SHOULD have paid
        BigDecimal perPersonShare = totalExpense.divide(new BigDecimal(userCount), 2, RoundingMode.HALF_UP);

        // Calculate what each user ACTUALLY paid
        Map<String, BigDecimal> amountPaidPerUser = new HashMap<>();
        for (AppUser user : allUsers) {
            amountPaidPerUser.put(user.getUsername(), BigDecimal.ZERO);
        }

        for (Expense expense : allExpenses) {
            String payer = expense.getPaidBy().getUsername();
            amountPaidPerUser.put(payer, amountPaidPerUser.get(payer).add(expense.getAmount()));
        }

        // Calculate final balances (Positive = owes money, Negative = gets money back)
        Map<String, BigDecimal> finalBalances = new HashMap<>();
        for (String username : amountPaidPerUser.keySet()) {
            BigDecimal paid = amountPaidPerUser.get(username);
            finalBalances.put(username, perPersonShare.subtract(paid));
        }

        return finalBalances;
    }
}
