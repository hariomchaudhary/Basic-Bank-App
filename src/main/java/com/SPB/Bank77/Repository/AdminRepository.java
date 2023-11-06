package com.SPB.Bank77.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SPB.Bank77.Model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
	Admin findByUsername(String username);
}

