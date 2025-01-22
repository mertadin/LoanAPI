package com.bank.loan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bank.loan.model.Customer;
import com.bank.loan.model.Loan;
import com.bank.loan.model.LoanInstallment;
import com.bank.loan.result.PayLoanResult;
import com.bank.loan.service.CustomerService;

import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Tüm müşterileri listele
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // ID'ye göre bir müşteri getir
    @GetMapping("/{id}")
    public Optional<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    // Yeni müşteri oluştur
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    // Müşteri güncelle
    @PutMapping("/{id}")
    public Optional<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        return customerService.updateCustomer(id, updatedCustomer);
    }

    // Müşteri sil
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    // Müşteriye ait kredileri al
    @GetMapping("/{customerId}/loans")
    @PreAuthorize("#customerId == authentication.principal.id or hasRole('ADMIN')")
    public List<Loan> getLoansForCustomer(@PathVariable Long customerId) {
        return customerService.getLoansForCustomer(customerId);
    }
    
    // Admin can access all customers' loan information
    @GetMapping("/admin/{customerId}/loan")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Loan> getAdminLoan(@PathVariable Long customerId) {
        return customerService.getLoansForCustomer(customerId);
    }

    // Kredinin taksitlerini al
    @GetMapping("/loans/{loanId}/installments")
    public List<LoanInstallment> getLoanInstallments(@PathVariable Long loanId) {
        return customerService.getLoanInstallments(loanId);
    }
    
    // Krediyi ödeme
    @PostMapping("/loans/{loanId}/payLoan")
    public PayLoanResult payLoan(@PathVariable Long loanId, @RequestParam Double amount) {
        return customerService.payLoanInstallments(loanId, amount);
    }
}