public class Main {
    public static void main(String[] args) {
        Booking booking = new Booking();
        HouseholdBudget householdBudget = new HouseholdBudget();
        booking.getBank_balance();
        householdBudget.setVisible(true);
        System.out.println("Kontostand beträgt: " + booking.getBank_balance() + "€");

    }

}
