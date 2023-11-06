package com.SPB.Bank77.Controller;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SPB.Bank77.Model.Transaction;
import com.SPB.Bank77.Model.Transaction.TransactionType;
import com.SPB.Bank77.Model.User;
import com.SPB.Bank77.Service.MyUserDetailsService;
import com.SPB.Bank77.Service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;
    

        
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login successful");
    }
    
    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        // Log the user out by clearing the security context
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }
    
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestParam String accountNumber, @RequestParam double amount) {
        if (amount > 0) {
            User user = userService.getUserInfoByAccountNumber(accountNumber);

            if (user != null) {
                // Perform the deposit operation
                double currentBalance = user.getBalance();
                double newBalance = currentBalance + amount;
                user.setBalance(newBalance);

                // Create a transaction record
                Transaction transaction = new Transaction();
                transaction.setUser(user);
                transaction.setTransactionType(TransactionType.DEPOSIT); // You can define this enum
                transaction.setAmount(amount);
                transaction.setTransactionDate(new Date());

                // Save the transaction record
                userService.saveTransaction(transaction);

                return ResponseEntity.ok("Deposit successful. New balance: " + newBalance);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount must be greater than zero.");
        }
    }
    
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam String accountNumber, @RequestParam double amount) {
        User user = userService.getUserInfoByAccountNumber(accountNumber);

        if (user != null) {
            double currentBalance = user.getBalance();

            if (currentBalance - amount >= 0) {
                // Sufficient balance for the withdrawal
                double newBalance = currentBalance - amount;
                user.setBalance(newBalance);

                // Create a transaction record for the withdrawal
                Transaction transaction = new Transaction();
                transaction.setUser(user);
                transaction.setTransactionType(TransactionType.WITHDRAW); // You should have the TransactionType enum
                transaction.setAmount(amount);
                transaction.setTransactionDate(new Date());

                // Save the transaction record
                userService.saveTransaction(transaction);

                return ResponseEntity.ok("Withdrawal successful. New balance: " + newBalance);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance for the withdrawal.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    
    @GetMapping("/balance")
    public ResponseEntity<String> getBalance(@RequestParam String accountNumber) {
        User user = userService.getUserInfoByAccountNumber(accountNumber);

        if (user != null) {
            double currentBalance = user.getBalance();
            return ResponseEntity.ok("Current balance for account number " + accountNumber + ": " + currentBalance);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    
    @GetMapping("/transaction-logs")
    public ResponseEntity<List<Transaction>> getTransactionLogs(@RequestParam String accountNumber) {
        User user = userService.getUserInfoByAccountNumber(accountNumber);

        if (user != null) {
            // Retrieve all transactions for the user
            List<Transaction> allTransactions = userService.getAllTransactions(user);

            // Return up to the last 10 transactions
            int startIndex = Math.max(0, allTransactions.size() - 10);
            int endIndex = allTransactions.size();

            List<Transaction> last10Transactions = allTransactions.subList(startIndex, endIndex);

            return ResponseEntity.ok(last10Transactions);
        } else {
            // Return an empty list when the user is not found
            return ResponseEntity.ok(Collections.emptyList());
        }
    }


        
    


    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        // Get the currently authenticated user
        User currentUser = myUserDetailsService.getCurrentUser();

        // Check the old password against the user's current password
        if (passwordMatches(currentUser.getPassword(), oldPassword)) {
            // Update the password securely
            boolean passwordUpdated = userService.updatePassword(currentUser, newPassword);

            if (passwordUpdated) {
                return ResponseEntity.ok("Password updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Password update failed");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect old password");
        }
    }
    
    @PostMapping("/close-account-and-logout")
    public ResponseEntity<String> closeAccountAndLogout(@RequestParam String accountNumber) {
        User user = userService.getUserInfoByAccountNumber(accountNumber);

        if (user != null) {
            if (user.getBalance() == 0.0) {
                // Delete the user's transaction history
                //TransactionRepository.deleteByUser(user); should not delete transaction history

                // Close the account
                userService.deleteUser(accountNumber);

                

                return ResponseEntity.ok("Account closed, transaction history deleted, and logged out successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account balance must be zero to close the account.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }


    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private boolean passwordMatches(String storedPassword, String inputPassword) {
        // Use the password encoder to verify if the input password matches the stored (encoded) password
        return passwordEncoder().matches(inputPassword, storedPassword);
    }
}
