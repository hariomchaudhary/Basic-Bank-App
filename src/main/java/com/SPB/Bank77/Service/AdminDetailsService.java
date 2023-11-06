package com.SPB.Bank77.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SPB.Bank77.Model.Admin;
import com.SPB.Bank77.Repository.AdminRepository;

@Service
public class AdminDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository; // Assuming you have an AdminRepository

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            throw new UsernameNotFoundException("Admin not found with username: " + username);
        }

        return User.withUsername(username)
                .password(admin.getPassword()) // Assuming the password is already bcrypt encoded in the database
                .roles("ADMIN")
                .build();
    }
}
