package com.bank.loan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.loan.model.LoanInstallment;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

	List<LoanInstallment> findByLoanId(Long loanId);
}