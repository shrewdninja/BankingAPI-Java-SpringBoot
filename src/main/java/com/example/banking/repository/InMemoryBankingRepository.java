package com.example.banking.repository;

import com.example.banking.model.Account;
import com.example.banking.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryBankingRepository implements BankingRepository {
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();
    private long accountIdCounter = 0;
    private long transactionIdCounter = 0;

    @Override
    public Account saveAccount(Account account) {
        if (account.getId() == null) {
            account.setId(++accountIdCounter);
        }
        accounts.put(account.getId(), account);
        return account;
    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(++transactionIdCounter);
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Account> findAccountById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public List<Account> findAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public List<Transaction> findTransactionsByAccountId(Long accountId) {
        return transactions.values().stream()
                .filter(t -> t.getFromAccountId().equals(accountId) || t.getToAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findAllTransactions() {
        return new ArrayList<>(transactions.values());
    }
}