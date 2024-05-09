import java.util.Scanner;

public class Booking {
    private double bank_balance = 0;
    private double fixed_cost = 0;
    private double joint_income = 0;

    public double getBank_balance() {

        return bank_balance;
    }

    public void setBank_balance(double bank_balance) {

        this.bank_balance = bank_balance;
    }

    public void bookingDispatch(int dispatch) {

        bank_balance = bank_balance - dispatch;
    }

    public void bookingSaldo(int saldo) {

        bank_balance = bank_balance + saldo;
    }

    public double getFixed_costs() {
        return fixed_cost;
    }

    public void setFixed_cost(double fixed_cost) {
        this.fixed_cost = fixed_cost;
    }

    public double getJoint_income() {
        return joint_income;
    }

    public void setJoint_income(double joint_income) {
        this.joint_income = joint_income;
    }
}
