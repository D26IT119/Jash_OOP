import java.util.Scanner;

public class VendingMachine {

    enum Coin { ONE, TWO, FIVE, TEN }
    public static void main(String[] args) {
        final int Price = 15;
        int Total = 0;

        Scanner sc = new Scanner(System.in);
        System.out.println("Snack price: " + Price);
        System.out.println("Insert Coins (ONE, TWO, FIVE, TEN)");

        while (Total < Price) {

            System.out.print("Coin: ");
            String input = sc.next().toUpperCase();
            Coin coin = Coin.valueOf(input);

            // Switch the enum variable
            int value = switch (coin) {
                case ONE -> 1;
                case TWO -> 2;
                case FIVE -> 5;
                case TEN -> 10;
            };

            Total += value;

            System.out.println("Total paid: " + Total);
        }

        System.out.println("Paid. Change: " + (Total - Price));
        sc.close();
    }
}
