package com.example.banking.repository;

import com.example.banking.model.Account;
import com.example.banking.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface BankingRepository {
    Account saveAccount(Account account);
    Transaction saveTransaction(Transaction transaction);
    Optional<Account> findAccountById(Long id);
    List<Account> findAllAccounts();
    List<Transaction> findTransactionsByAccountId(Long accountId);
    List<Transaction> findAllTransactions(); // New method
}