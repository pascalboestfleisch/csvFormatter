import java.text.DecimalFormat;

public class Booking {

    private double bank_balance = 2133.32;
    private double fixed_cost = 165.2;
    private double joint_income = 1633.32;
    private double childSupport = 500;
    private double shoppingBudget = 400;
    private double gezTus = 92;
    private double car = 147.30;
    private double rentWlanElec = 875;

    DecimalFormat df = new DecimalFormat("0.00");

    public String getBank_balance() {
        return df.format(bank_balance);
    }

    public void setBank_balance(double bank_balance) {
        this.bank_balance = bank_balance;
    }

    public String getFixed_costs() {
        return df.format(fixed_cost);
    }

    public void setFixed_costs(double fixed_cost) {
        this.fixed_cost = fixed_cost;
    }

    public String getJoint_income() {
        return df.format(joint_income);
    }

    public void setJoint_income(double joint_income) {
        this.joint_income = joint_income;
    }

    public String getChildSupport() {
        return df.format(childSupport);
    }

    public void setChildSupport(double childSupport) {
        this.childSupport = childSupport;
    }

    public String getShoppingBudget() {
        return df.format(shoppingBudget);
    }

    public void setShoppingBudget(double shoppingBudget) {
        this.shoppingBudget = shoppingBudget;
    }

    public String getGezTus() {
        return df.format(gezTus);
    }

    public void setGezTus(double gezTus) {
        this.gezTus = gezTus;
    }

    public String getCar() {
        return df.format(car);
    }

    public void setCar(double car) {
        this.car = car;
    }

    public String getRentWlanElec() {
        return df.format(rentWlanElec);
    }

    public void setRentWlanElec(double rentWlanElec) {
        this.rentWlanElec = rentWlanElec;
    }
}
