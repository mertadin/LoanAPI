package com.bank.loan.result;

public class PayLoanResult {

    private int installmentsPaid;  // Ödenen taksit sayısı
    private double totalPaidAmount;  // Toplam ödeme tutarı
    private boolean loanPaidCompletely;  // Kredinin tamamen ödenip ödenmediği durumu

    // Yapıcı (Constructor)
    public PayLoanResult(int installmentsPaid, double totalPaidAmount, boolean loanPaidCompletely) {
        this.installmentsPaid = installmentsPaid;
        this.totalPaidAmount = totalPaidAmount;
        this.loanPaidCompletely = loanPaidCompletely;
    }

    // Getter ve Setter metotları
    public int getInstallmentsPaid() {
        return installmentsPaid;
    }

    public void setInstallmentsPaid(int installmentsPaid) {
        this.installmentsPaid = installmentsPaid;
    }

    public double getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(double totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public boolean isLoanPaidCompletely() {
        return loanPaidCompletely;
    }

    public void setLoanPaidCompletely(boolean loanPaidCompletely) {
        this.loanPaidCompletely = loanPaidCompletely;
    }

    @Override
    public String toString() {
        return "PayLoanResult{" +
                "installmentsPaid=" + installmentsPaid +
                ", totalPaidAmount=" + totalPaidAmount +
                ", loanPaidCompletely=" + loanPaidCompletely +
                '}';
    }
}