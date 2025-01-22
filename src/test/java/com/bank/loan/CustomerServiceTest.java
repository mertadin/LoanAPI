package com.bank.loan;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.bank.loan.model.Customer;
import com.bank.loan.model.Loan;
import com.bank.loan.model.LoanInstallment;
import com.bank.loan.repository.CustomerRepository;
import com.bank.loan.repository.LoanRepository;
import com.bank.loan.service.CustomerService;
import com.bank.loan.repository.LoanInstallmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    public void testCustomerLoanAccess() {
        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setCreditLimit(10000.0);
        mockCustomer.setUsedCreditLimit(2000.0);

        Loan mockLoan = new Loan();
        mockLoan.setCustomer(mockCustomer);

        lenient().when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        lenient().when(loanRepository.findByCustomerId(mockCustomer.getId())).thenReturn(Arrays.asList(mockLoan)); 

        List<Loan> loans = customerService.getLoansForCustomer(1L);
        assertEquals(1, loans.size());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAdminLoanAccess() {
        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);
        mockCustomer.setCreditLimit(10000.0);
        mockCustomer.setUsedCreditLimit(2000.0);

        Loan mockLoan = new Loan();
        mockLoan.setCustomer(mockCustomer);

        lenient().when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
        lenient().when(loanRepository.findByCustomerId(mockCustomer.getId())).thenReturn(Arrays.asList(mockLoan));

        List<Loan> loans = customerService.getLoansForCustomer(1L);
        assertEquals(1, loans.size());
    }

    @Test
    void testCreateLoan() {
        // Mock müşteri
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setCreditLimit(10000.0);
        customer.setUsedCreditLimit(2000.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Mock loanRepository ve loan installment repository
        Loan loan = new Loan();
        loan.setLoanAmount(5000.0);
        loan.setNumberOfInstallments(12);

        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        // Loan installment oluşturma
        List<LoanInstallment> installments = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setRemainingAmount(416.67);  // Taksit tutarı
            installment.setPaidAmount(0.0);  // Henüz ödeme yapılmadı
            installment.setDueDate(LocalDate.now().plusMonths(i + 1));  // Taksit tarihi
            installment.setLoan(loan);  // İlgili kredi ile ilişkilendir
            installments.add(installment);
        }

        // Installment repository mock'lama
        when(loanInstallmentRepository.save(any(LoanInstallment.class))).thenReturn(new LoanInstallment());

        // Create loan service test
        Loan createdLoan = customerService.createLoan(1L, 5000.0, 0.2, 12);

        // Assertions
        assertNotNull(createdLoan);  // Loan null olmamalı
        assertEquals(5000.0, createdLoan.getLoanAmount(), 0.01);  // Loan tutarı doğru olmalı
        assertEquals(12, createdLoan.getNumberOfInstallments());  // Taksit sayısı doğru olmalı

        // Loan installment'ların doğru şekilde oluşturulmuş ve kaydedilmiş olması gerektiğini kontrol et
        assertEquals(12, installments.size());  // 12 taksit oluşturulmuş olmalı

        // Repository methodlarının doğru çağrıldığını kontrol et
        verify(customerRepository, times(1)).save(customer);
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(loanInstallmentRepository, times(12)).save(any(LoanInstallment.class));  // 12 taksit kaydedildiği kontrol ediliyor
    }

    @Test
    void testCreateInstallments() {
        // Mock müşteri
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setCreditLimit(10000.0);
        customer.setUsedCreditLimit(2000.0);

        lenient().when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Mock loan
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setLoanAmount(5000.0);
        loan.setNumberOfInstallments(12);
        loan.setCreateDate(LocalDate.now().toString());

        lenient().when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        // Loan installment oluşturma
        List<LoanInstallment> installments = new ArrayList<>();
        Double installmentAmount = loan.getLoanAmount() / loan.getNumberOfInstallments();
        for (int i = 0; i < 12; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setRemainingAmount(installmentAmount);  // Kalan ödeme tutarı
            installment.setPaidAmount(0.0);  // Henüz ödeme yapılmadı
            installment.setDueDate(LocalDate.now().plusMonths(i + 1));  // Taksit tarihi
            installment.setLoan(loan);  // İlgili kredi ile ilişkilendir
            installments.add(installment);
        }

        // Loan installment repository mock'lama
        when(loanInstallmentRepository.save(any(LoanInstallment.class))).thenReturn(new LoanInstallment());

        // Create loan service test
        List<LoanInstallment> createdInstallments = customerService.createInstallments(loan, loan.getNumberOfInstallments(), loan.getLoanAmount());

        // Assertions
        assertNotNull(createdInstallments);  // Installments null olmamalı
        assertEquals(12, createdInstallments.size());  // 12 taksit oluşturulmuş olmalı
        assertEquals(installmentAmount, createdInstallments.get(0).getRemainingAmount(), 0.01);  // İlk taksitin tutarı doğru olmalı
        assertEquals("0.0", createdInstallments.get(0).getPaidAmount().toString());  // İlk taksitin ödenen miktarı 0.0 olmalı

        // Loan installment'ların doğru şekilde kaydedildiğini kontrol et
        verify(loanInstallmentRepository, times(12)).save(any(LoanInstallment.class));  // 12 taksit kaydedildiği kontrol ediliyor
    }
}