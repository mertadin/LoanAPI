package com.bank.loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.loan.model.Customer;
import com.bank.loan.model.Loan;
import com.bank.loan.model.LoanInstallment;
import com.bank.loan.repository.CustomerRepository;
import com.bank.loan.repository.LoanInstallmentRepository;
import com.bank.loan.repository.LoanRepository;
import com.bank.loan.result.PayLoanResult;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    // Bonus: Admin and customer role logic (simplified authorization placeholder)
    public boolean isAdmin(String role) {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean canOperateForCustomer(String role, Long customerId, Long requesterId) {
        return isAdmin(role) || customerId.equals(requesterId);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> updateCustomer(Long id, Customer updatedCustomer) {
        return customerRepository.findById(id).map(customer -> {
            customer.setName(updatedCustomer.getName());
            customer.setSurname(updatedCustomer.getSurname());
            customer.setCreditLimit(updatedCustomer.getCreditLimit());
            customer.setUsedCreditLimit(updatedCustomer.getUsedCreditLimit());
            return customerRepository.save(customer);
        });
    }

    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Loan createLoan(Long customerId, Double loanAmount, Double interestRate, Integer numberOfInstallments) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if (customer.getUsedCreditLimit() + loanAmount > customer.getCreditLimit()) {
            throw new IllegalArgumentException("Insufficient credit limit");
        }

        if (!List.of(6, 9, 12, 24).contains(numberOfInstallments)) {
            throw new IllegalArgumentException("Invalid number of installments");
        }

        if (interestRate < 0.1 || interestRate > 0.5) {
            throw new IllegalArgumentException("Invalid interest rate");
        }

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(loanAmount * (1 + interestRate)); // Total amount including interest
        loan.setNumberOfInstallments(numberOfInstallments);
        loan.setCreateDate(LocalDate.now().toString());

        customer.setUsedCreditLimit(customer.getUsedCreditLimit() + loanAmount);
        customerRepository.save(customer);

        List<LoanInstallment> installments = createInstallments(loan, numberOfInstallments, loan.getLoanAmount());
        loan.setInstallments(installments);

        return loanRepository.save(loan);
    }

    public List<LoanInstallment> createInstallments(Loan loan, Integer numberOfInstallments, Double totalAmount) {
        Double installmentAmount = totalAmount / numberOfInstallments;
        System.out.println("Installment amount: " + installmentAmount);  // Debug için

        List<LoanInstallment> installments = new ArrayList();
        for (int i = 0; i < numberOfInstallments; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setRemainingAmount(installmentAmount);  // Kalan ödeme tutarı
            installment.setPaidAmount(0.0);  // Ödeme yapılmadı
            installment.setDueDate(LocalDate.now().plusMonths(i + 1));  // Taksit vade tarihi
            installments.add(installment);

            // Kaydetme işlemi eklenmeli
            loanInstallmentRepository.save(installment);
        }

        return installments;  // Oluşturulan taksitler geri döndürülür
    }
    
    public List<Loan> getLoansForCustomer(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

 // Kredinin taksitlerini al
    public List<LoanInstallment> getLoanInstallments(Long loanId) {
        return loanInstallmentRepository.findByLoanId(loanId);
    }

    // Kredinin taksitlerini ödeme
    public PayLoanResult payLoanInstallments(Long loanId, Double amount) {
        Optional<Loan> loanOptional = loanRepository.findById(loanId);
        if (!loanOptional.isPresent()) {
            return new PayLoanResult(0, 0, false);  // Kredi bulunamadı
        }

        Loan loan = loanOptional.get();
        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanId(loanId);

        double totalPaidAmount = 0;
        int installmentsPaid = 0;
        boolean loanPaidCompletely = false;

        // Taksitleri sırasıyla ödeme (erken ödeme veya geç ödeme mantığı)
        for (LoanInstallment installment : installments) {
            if (totalPaidAmount >= amount) {
                break; // Ödenmesi gereken tutar bitti
            }

            if (installment.getRemainingAmount() > 0) {
                double installmentAmount = installment.getRemainingAmount();
                double paidAmount = calculatePaidAmount(installment, amount - totalPaidAmount);
                totalPaidAmount += paidAmount;

                // Taksiti güncelleme
                installment.setRemainingAmount(installment.getRemainingAmount() - paidAmount);
                installment.setPaidAmount(paidAmount);  // Ödenen tutar güncelleniyor
                loanInstallmentRepository.save(installment);

                installmentsPaid++;

                // Eğer kredi tamamen ödendiyse
                if (loanInstallmentRepository.findByLoanId(loanId).stream().allMatch(i -> i.getRemainingAmount() == 0)) {
                    loanPaidCompletely = true;
                }
            }
        }

        // Kredi ödemesi tamamlandıysa, krediyi güncelle
        if (loanPaidCompletely) {
            loan.setIsPaid(true);  // Kredi durumu "Ödendi" olarak güncelleniyor
            loanRepository.save(loan);
        }

        return new PayLoanResult(installmentsPaid, totalPaidAmount, loanPaidCompletely);
    }

    // Erken/Geç ödeme durumuna göre ödemenin hesaplanması
    private double calculatePaidAmount(LoanInstallment installment, double amountToPay) {
        double installmentAmount = installment.getRemainingAmount();
        double paidAmount = 0;

        // Ödeme zamanına göre cezalar ve ödüller
        LocalDate currentDate = LocalDate.now();
        LocalDate dueDate = installment.getDueDate();
        long daysDifference = ChronoUnit.DAYS.between(dueDate, currentDate);

        if (daysDifference < 0) {
            // Erken ödeme (indirim uygulanır)
            double discount = installmentAmount * 0.001 * Math.abs(daysDifference);
            paidAmount = Math.min(amountToPay, installmentAmount - discount);
        } else if (daysDifference > 0) {
            // Geç ödeme (ceza uygulanır)
            double penalty = installmentAmount * 0.001 * daysDifference;
            paidAmount = Math.min(amountToPay, installmentAmount + penalty);
        } else {
            // Normal ödeme (ceza veya indirim yok)
            paidAmount = Math.min(amountToPay, installmentAmount);
        }

        return paidAmount;
    }
    
   
}



