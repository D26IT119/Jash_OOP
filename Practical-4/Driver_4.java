public class driver_4 {
    public static void main(String[] args) {

        String[] passwords = {
            "Dhruva",
            "dhruva297",
            "Dhruva@657"
        };

        for (String pw : passwords) {
            System.out.println(pw + " -> " + PasswordChecker.strength(pw));
        }
    }
}

