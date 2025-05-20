package com.example.banking.controller;

import com.example.banking.dto.AccountDTO;
import com.example.banking.dto.ErrorResponse;
import com.example.banking.dto.TransactionDTO;
import com.example.banking.service.BankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Banking API", description = "API for managing accounts and transactions")
public class BankingController {
    private static final Logger logger = LoggerFactory.getLogger(BankingController.class);
    private final BankingService bankingService;
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    private int getDecimalPlaces(BigDecimal value) {
        if (value == null) return 0;
        String[] parts = value.toString().split("\\.");
        return parts.length == 1 ? 0 : parts[1].length();
    }

    @Operation(summary = "Create a new account", description = "Creates an account with an initial balance, first name, and last name. The 'id' is auto-generated and should not be provided.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Account created successfully", content = @Content(
            schema = @Schema(implementation = AccountDTO.class),
            examples = @ExampleObject(value = "{\"id\": 1, \"balance\": 1000.00, \"firstName\": \"John\", \"lastName\": \"Doe\"}")
        )),
        @ApiResponse(responseCode = "400", description = "Bad Request: Invalid or missing fields, or ID provided", content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = "{\"status\": 400, \"error\": \"Bad Request\", \"details\": {\"balance\": \"Balance must have at most two decimal places\", \"firstName\": \"First name is required\"}}")
        ))
    })
    @PostMapping("/accounts")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        Map<String, String> errors = new HashMap<>();

        if (accountDTO == null) {
            errors.put("payload", "Request body is required");
        } else {
            if (accountDTO.getBalance() == null) {
                errors.put("balance", "Balance is required");
            } else {
                if (accountDTO.getBalance().compareTo(ZERO) < 0) {
                    errors.put("balance", "Balance must be zero or positive");
                }
                if (getDecimalPlaces(accountDTO.getBalance()) > 2) {
                    errors.put("balance", "Balance must have at most two decimal places");
                }
            }
            if (accountDTO.getFirstName() == null || accountDTO.getFirstName().trim().isEmpty()) {
                errors.put("firstName", "First name is required");
            }
            if (accountDTO.getLastName() == null || accountDTO.getLastName().trim().isEmpty()) {
                errors.put("lastName", "Last name is required");
            }
            if (accountDTO.getId() != null) {
                errors.put("id", "ID is auto-generated and should not be provided");
            }
        }

        if (!errors.isEmpty()) {
            logger.error("Validation errors: {}", errors);
            throw new IllegalArgumentException(errors.toString());
        }

        logger.info("Creating account for {} {} with initial balance: {}", 
            accountDTO.getFirstName(), accountDTO.getLastName(), accountDTO.getBalance());
        AccountDTO createdAccount = bankingService.createAccount(accountDTO);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all accounts", description = "Retrieves a list of all accounts")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of accounts retrieved successfully", content = @Content(
            schema = @Schema(implementation = AccountDTO.class),
            examples = @ExampleObject(value = "[{\"id\": 1, \"balance\": 1000.00, \"firstName\": \"John\", \"lastName\": \"Doe\"}, {\"id\": 2, \"balance\": 500.00, \"firstName\": \"Jane\", \"lastName\": \"Smith\"}]")
        ))
    })
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        logger.info("Retrieving all accounts");
        List<AccountDTO> accounts = bankingService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Operation(summary = "Get all transactions", description = "Retrieves a list of all transactions across all accounts")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of transactions retrieved successfully", content = @Content(
            schema = @Schema(implementation = TransactionDTO.class),
            examples = @ExampleObject(value = "[{\"id\": 1, \"fromAccountId\": 1, \"toAccountId\": 2, \"amount\": 50.00, \"timestamp\": \"2025-05-19T20:30:00.123\"}, {\"id\": 2, \"fromAccountId\": 2, \"toAccountId\": 1, \"amount\": 25.00, \"timestamp\": \"2025-05-19T20:31:00.456\"}]")
        ))
    })
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        logger.info("Retrieving all transactions");
        List<TransactionDTO> transactions = bankingService.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @Operation(summary = "Transfer funds between accounts", description = "Transfers a specified amount from one account to another. The 'id' and 'timestamp' are auto-generated and should not be provided.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfer successful", content = @Content(
            schema = @Schema(implementation = TransactionDTO.class),
            examples = @ExampleObject(value = "{\"id\": 1, \"fromAccountId\": 1, \"toAccountId\": 2, \"amount\": 50.00, \"timestamp\": \"2025-05-19T20:30:00.123\"}")
        )),
        @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input, insufficient funds, or same account transfer", content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = "{\"status\": 400, \"error\": \"Bad Request\", \"details\": {\"fromAccountId\": \"From account ID is required\", \"amount\": \"Amount must have at most two decimal places\"}}")
        )),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = "{\"status\": 404, \"error\": \"Not Found\", \"details\": {\"error\": \"Account not found\"}}")
        ))
    })
    @PostMapping("/transactions")
    public ResponseEntity<TransactionDTO> transferFunds(@RequestBody TransactionDTO transactionDTO) {
        Map<String, String> errors = new HashMap<>();

        if (transactionDTO == null) {
            errors.put("payload", "Request body is required");
        } else {
            if (transactionDTO.getFromAccountId() == null) {
                errors.put("fromAccountId", "From account ID is required");
            }
            if (transactionDTO.getToAccountId() == null) {
                errors.put("toAccountId", "To account ID is required");
            }
            if (transactionDTO.getAmount() == null) {
                errors.put("amount", "Amount is required");
            } else {
                if (transactionDTO.getAmount().compareTo(ZERO) <= 0) {
                    errors.put("amount", "Amount must be positive");
                }
                if (getDecimalPlaces(transactionDTO.getAmount()) > 2) {
                    errors.put("amount", "Amount must have at most two decimal places");
                }
            }
            if (transactionDTO.getId() != null) {
                errors.put("id", "ID is auto-generated and should not be provided");
            }
        }

        if (!errors.isEmpty()) {
            logger.error("Validation errors: {}", errors);
            throw new IllegalArgumentException(errors.toString());
        }

        logger.info("Processing transfer from account {} to account {} for amount {}",
                transactionDTO.getFromAccountId(), transactionDTO.getToAccountId(), transactionDTO.getAmount());
        TransactionDTO transaction = bankingService.transferFunds(transactionDTO);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @Operation(summary = "Get transaction history", description = "Retrieves all transactions for a specific account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction history retrieved", content = @Content(
            schema = @Schema(implementation = TransactionDTO.class)
        )),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = "{\"status\": 404, \"error\": \"Not Found\", \"details\": {\"error\": \"Account not found\"}}")
        ))
    })
    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(
            @Parameter(description = "ID of the account") @PathVariable Long accountId) {
        logger.info("Retrieving transaction history for account {}", accountId);
        List<TransactionDTO> transactions = bankingService.getTransactionHistory(accountId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @Operation(summary = "Get account details", description = "Retrieves details of a specific account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account details retrieved", content = @Content(
            schema = @Schema(implementation = AccountDTO.class)
        )),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = "{\"status\": 404, \"error\": \"Not Found\", \"details\": {\"error\": \"Account not found\"}}")
        ))
    })
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountDTO> getAccount(
            @Parameter(description = "ID of the account") @PathVariable Long accountId) {
        logger.info("Retrieving account details for account {}", accountId);
        AccountDTO account = bankingService.getAccount(accountId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        try {
            String errorMsg = ex.getMessage();
            errorMsg = errorMsg.substring(1, errorMsg.length() - 1);
            for (String entry : errorMsg.split(", ")) {
                String[] keyValue = entry.split("=");
                if (keyValue.length == 2) {
                    errors.put(keyValue[0], keyValue[1]);
                }
            }
        } catch (Exception e) {
            errors.put("error", "Invalid request: " + ex.getMessage());
        }
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.example.banking.exception.ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(com.example.banking.exception.ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.example.banking.exception.InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(com.example.banking.exception.InsufficientFundsException ex) {
        logger.error("Insufficient funds: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormatException(InvalidFormatException ex) {
        logger.error("Invalid format error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Invalid numeric format: " + ex.getValue());
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}