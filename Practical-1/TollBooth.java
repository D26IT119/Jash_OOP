import java.util.Scanner;

public class TollBooth {

    record Vehicle(String number, String type) {}

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int totalToll = 0;
        int bikes = 0;
        int cars = 0;
        int trucks = 0;

        while (true) {

            System.out.print("Enter Vehicle Number or (DONE): ");
            String number = sc.next();

            if (number.equalsIgnoreCase("DONE")) {
                break;
            }

            System.out.print("Enter Vehicle Type (bike/car/truck): ");
            String type = sc.next().toLowerCase();

            Vehicle v = new Vehicle(number, type);

            int toll = switch (v.type()) {

                case "bike" -> {
                    bikes++;
                    yield 20;
                }

                case "car" -> {
                    cars++;
                    yield 50;
                }

                case "truck" -> {
                    trucks++;
                    yield 150;
                }

                default -> {
                    System.out.println("Invalid vehicle type!");
                    yield 0;
                }
            };

            totalToll += toll;

            System.out.println(v.number() + " pays Rs " + toll);
            System.out.println();
        }

        System.out.println("Total Toll Collected: Rs " + totalToll);
        System.out.println("Bikes  : " + bikes);
        System.out.println("Cars   : " + cars);
        System.out.println("Trucks : " + trucks);

        if (bikes > cars && bikes > trucks) {
            System.out.println("Most Frequent Vehicle: Bike");
        }
        else if (cars > bikes && cars > trucks) {
            System.out.println("Most Frequent Vehicle: Car");
        }
        else if (trucks > bikes && trucks > cars) {
            System.out.println("Most Frequent Vehicle: Truck");
        }
        else {
            System.out.println("There is no frequent vehicle.");
        }

        sc.close();
    }
}
