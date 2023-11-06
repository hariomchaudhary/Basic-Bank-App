package com.SPB.Bank77.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.SPB.Bank77.Model.User;
import com.SPB.Bank77.Model.User.AccountType;
import com.SPB.Bank77.Service.UserService;

import java.sql.Date;
import java.util.Random;

//Admin is already saved field in database with the password first enryped and then saved i database manually

@Controller
@RequestMapping("/admin")
public class AdminController {
   
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AdminController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

   
    @PostMapping("/register")
    @ResponseBody
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String address,
            @RequestParam String mobileNo,
            @RequestParam String emailId,
            @RequestParam AccountType accountType,
            @RequestParam double balance,
            @RequestParam Date dateOfBirth,
            @RequestParam String idProof
    ) {
        // Generate a random 8-digit account number
        String accountNumber = generateRandomAccountNumber();

        // Generate a random password
        String password = generateRandomPassword();

        // Create a new User entity
        User user = new User();
        user.setFullName(fullName);
        user.setAddress(address);
        user.setMobileNo(mobileNo);
        user.setEmailId(emailId);
        user.setAccountType(User.AccountType.SAVING);
        user.setBalance(balance);
        user.setDateOfBirth(dateOfBirth);
        user.setIdProof(idProof);
        user.setRole("USER");
        user.setAccountNumber(accountNumber); // Set the generated account number
        user.setPassword(passwordEncoder.encode(password)); // Encode the password

        if (user.getBalance() >= 1000) {
            // Register the user
            userService.registerUser(user);
            return "User registered successfully!";
        } else {
            //the case where the balance is less than 1000
        
            return "Balance is less than 1000. User registration failed.";
        }
    }

    // Generate a random 8-digit account number
    private String generateRandomAccountNumber() {
        // Generate a random number between 10000000 and 99999999
        int randomNumber = new Random().nextInt(90000000) + 10000000;
        return String.valueOf(randomNumber);
    }

    // Generate a random password
    private String generateRandomPassword() {

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialCharacters = "!@#$%^&*()-_+=<>?";

        String allCharacters = upperCase + lowerCase + numbers + specialCharacters;

        StringBuilder passwordBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(allCharacters.length());
            passwordBuilder.append(allCharacters.charAt(index));
        }

        return passwordBuilder.toString();
    }
    
    @GetMapping("/user/{accountNumber}")
    public String viewUserInfo(@PathVariable String accountNumber, Model model) {
        // Use the accountNumber to fetch user information
        User user = userService.getUserInfoByAccountNumber(accountNumber);

        // Add the user information to the model
        model.addAttribute("user", user);

        return "view-user-info"; // Return a view to display user information
    }

    @PostMapping("/user/update/{accountNumber}")
    public String updateUser(@PathVariable String accountNumber, @ModelAttribute User updatedUser) {
        // Use the accountNumber to fetch the existing user
        User existingUser = userService.getUserInfoByAccountNumber(accountNumber);

        // Update the existing user's information, excluding accountNumber, username, password, and balance
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setMobileNo(updatedUser.getMobileNo());
        existingUser.setEmailId(updatedUser.getEmailId());
        existingUser.setAccountType(updatedUser.getAccountType());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setIdProof(updatedUser.getIdProof());

        // Save the updated user information
        userService.updateUser(existingUser);

        return "redirect:/admin/dashboard"; // Redirect to the admin dashboard or another appropriate page
    }

    @PostMapping("/user/delete/{accountNumber}")
    public String deleteUser(@PathVariable String accountNumber) {
        // Use the accountNumber to fetch the user to be deleted
        User user = userService.getUserInfoByAccountNumber(accountNumber);

        // Perform any necessary checks or validation before deleting
        if (user != null) {
            // Delete the user
            userService.deleteUser(accountNumber);
        }

        return "redirect:/admin/dashboard"; // Redirect to the admin dashboard or another appropriate page
    }
    
}
