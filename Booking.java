import java.util.Scanner;
public class Booking {
    private double bank_balance = 0;

    public double getBank_balance() {
        return bank_balance;
    }

    public void setBank_balance(int bank_balance) {
        this.bank_balance = bank_balance;
    }

    public void bookingDispatch(int dispatch) {
        bank_balance = bank_balance - dispatch;
    }

    public void bookingSaldo(int saldo) {
        bank_balance = bank_balance + saldo;
    }
}
