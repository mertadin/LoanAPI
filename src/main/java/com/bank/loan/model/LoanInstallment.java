package com.bank.loan.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Taksit ID'si

    @Column(nullable = false)
    private Double remainingAmount;  // Kalan ödeme tutarı

    @Column(nullable = false)
    private Double paidAmount;  // Ödenen miktar

    @Column(nullable = false)
    private LocalDate dueDate;  // Taksitin vade tarihi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;  // İlgili kredi (Loan nesnesi)

    // Yapıcı (Constructor)
    public LoanInstallment() {}

    public LoanInstallment(Double remainingAmount, Double paidAmount, LocalDate dueDate, Loan loan) {
        this.remainingAmount = remainingAmount;
        this.paidAmount = paidAmount;
        this.dueDate = dueDate;
        this.loan = loan;
    }

    // Getter ve Setter metotları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    @Override
    public String toString() {
        return "LoanInstallment{" +
                "id=" + id +
                ", remainingAmount=" + remainingAmount +
                ", paidAmount=" + paidAmount +
                ", dueDate=" + dueDate +
                ", loan=" + loan +
                '}';
    }
}