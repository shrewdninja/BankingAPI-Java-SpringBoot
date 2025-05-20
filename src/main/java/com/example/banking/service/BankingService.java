package com.example.banking.service;

import com.example.banking.dto.AccountDTO;
import com.example.banking.dto.TransactionDTO;
import com.example.banking.exception.InsufficientFundsException;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.model.Account;
import com.example.banking.model.Transaction;
import com.example.banking.repository.BankingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankingService {
    private static final Logger logger = LoggerFactory.getLogger(BankingService.class);
    private final BankingRepository repository;

    public BankingService(BankingRepository repository) {
        this.repository = repository;
    }

    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = new Account(null, accountDTO.getBalance(), accountDTO.getFirstName(), accountDTO.getLastName());
        account = repository.saveAccount(account);
        return new AccountDTO(account.getId(), account.getBalance(), account.getFirstName(), account.getLastName());
    }

    public TransactionDTO transferFunds(TransactionDTO transactionDTO) {
        Optional<Account> fromAccountOpt = repository.findAccountById(transactionDTO.getFromAccountId());
        Account fromAccount = fromAccountOpt.orElseThrow(() -> 
            new ResourceNotFoundException("From account not found: " + transactionDTO.getFromAccountId()));
        
        Optional<Account> toAccountOpt = repository.findAccountById(transactionDTO.getToAccountId());
        Account toAccount = toAccountOpt.orElseThrow(() -> 
            new ResourceNotFoundException("To account not found: " + transactionDTO.getToAccountId()));

        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (fromAccount.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + fromAccount.getId());
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(transactionDTO.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(transactionDTO.getAmount()));

        Transaction transaction = new Transaction(
                null,
                fromAccount.getId(),
                toAccount.getId(),
                transactionDTO.getAmount(),
                LocalDateTime.now()
        );
        transaction = repository.saveTransaction(transaction);

        repository.saveAccount(fromAccount);
        repository.saveAccount(toAccount);

        return new TransactionDTO(
                transaction.getId(),
                transaction.getFromAccountId(),
                transaction.getToAccountId(),
                transaction.getAmount(),
                transaction.getTimestamp().toString()
        );
    }

    public List<TransactionDTO> getTransactionHistory(Long accountId) {
        return repository.findTransactionsByAccountId(accountId).stream()
                .map(t -> new TransactionDTO(
                        t.getId(),
                        t.getFromAccountId(),
                        t.getToAccountId(),
                        t.getAmount(),
                        t.getTimestamp().toString()
                ))
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getAllTransactions() {
        logger.info("Retrieving all transactions");
        return repository.findAllTransactions().stream()
                .map(t -> new TransactionDTO(
                        t.getId(),
                        t.getFromAccountId(),
                        t.getToAccountId(),
                        t.getAmount(),
                        t.getTimestamp().toString()
                ))
                .collect(Collectors.toList());
    }

    public AccountDTO getAccount(Long accountId) {
        Optional<Account> accountOpt = repository.findAccountById(accountId);
        Account account = accountOpt.orElseThrow(() -> 
            new ResourceNotFoundException("Account not found: " + accountId));
        return new AccountDTO(account.getId(), account.getBalance(), account.getFirstName(), account.getLastName());
    }

    public List<AccountDTO> getAllAccounts() {
        return repository.findAllAccounts().stream()
                .map(account -> new AccountDTO(account.getId(), account.getBalance(), account.getFirstName(), account.getLastName()))
                .collect(Collectors.toList());
    }
}