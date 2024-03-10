public class Main {
    public static void main(String[] args) {
        Booking booking = new Booking();

        booking.setBank_balance(200);
        System.out.println("Kontostand beträgt: " + booking.getBank_balance() + " €");
    }

}
