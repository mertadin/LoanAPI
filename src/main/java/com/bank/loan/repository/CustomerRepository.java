package com.bank.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.loan.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // İhtiyaca göre özel sorgular burada tanımlanabilir.
}