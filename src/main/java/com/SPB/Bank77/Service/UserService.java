package com.SPB.Bank77.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SPB.Bank77.Model.Transaction;
import com.SPB.Bank77.Model.User;
import com.SPB.Bank77.Repository.TransactionRepository;
import com.SPB.Bank77.Repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository; // Inject the TransactionRepository


    public void saveTransaction(Transaction transaction) {
        // Save the transaction to the database
        transactionRepository.save(transaction);
    }

    public void registerUser(User user) {

        // Save the user to the database
        userRepository.save(user);
    }
    
    public User getUserInfoByAccountNumber(String accountNumber) {
        // Use the UserRepository to fetch user information by account number
        Optional<User> userOptional = userRepository.findByAccountNumber(accountNumber);

        // Check if the user exists in the database
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Create a new User object without the sensitive information (password and balance)
            User userInfo = new User();
            userInfo.setFullName(user.getFullName());
            userInfo.setAddress(user.getAddress());
            userInfo.setMobileNo(user.getMobileNo());
            userInfo.setEmailId(user.getEmailId());
            userInfo.setAccountType(user.getAccountType());
            userInfo.setDateOfBirth(user.getDateOfBirth());
            userInfo.setIdProof(user.getIdProof());

            return userInfo;
        } else {
            // Handle the case where the user is not found
            throw new RuntimeException("User with account number " + accountNumber + " not found");
        }
    }
    
    public List<Transaction> getAllTransactions(User user) {
        // Retrieve all transactions for the user
        return transactionRepository.findByUser(user);
    }

    
    public void updateUser(User user) {
        // Save the updated user information to the database
        userRepository.save(user);
    }
    
    public void deleteUser(String accountNumber) {
        // Delete the user by account number
        userRepository.deleteByAccountNumber(accountNumber);
    }
    @Autowired
    private PasswordEncoder passwordEncoder; // Inject the password encoder

    public boolean updatePassword(User user, String newPassword) {
        // Hash the new password using the password encoder
        String hashedPassword = passwordEncoder.encode(newPassword);

        // Set the new hashed password in the User object
        user.setPassword(hashedPassword);

        // Save the updated user information to the database
        userRepository.save(user);

        // Return true to indicate a successful password update
        return true;
    }
    
}
