package com.SPB.Bank77.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SPB.Bank77.Model.User;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
	Optional<User> findByAccountNumber(String accountNumber);
	
	 void deleteByAccountNumber(String accountNo);
}

