package com.SPB.Bank77.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SPB.Bank77.Model.Transaction;
import com.SPB.Bank77.Model.User;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    void deleteByUser(User user);
}
