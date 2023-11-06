package com.SPB.Bank77.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.SPB.Bank77.Model.User;
import com.SPB.Bank77.Repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByAccountNumber(accountNumber);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with account number: " + accountNumber);
        }

        User user = userOptional.get(); // Extract the User object from the Optional

        return org.springframework.security.core.userdetails.User.withUsername(accountNumber)
                .password(user.getPassword()) // Assuming the password is already bcrypt encoded in the database
                .roles("USER")
                .build();
    }
    public User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accountNumber = userDetails.getUsername(); // Assuming accountNumber is stored in the usertable field

        Optional<User> userOptional = userRepository.findByAccountNumber(accountNumber);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            // Handle the case where the user is not found
            throw new UsernameNotFoundException("User not found with account number: " + accountNumber);
        }
    }
}

