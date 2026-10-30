package util;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern MOBILE =
            Pattern.compile("^[6-9][0-9]{9}$");

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PAN =
            Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");

    private static final Pattern IFSC =
            Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

    public static boolean isValidOwnerName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() <= 50;
    }

    public static boolean isValidAmount(long amount) {
        return amount > 0;
    }

    public static boolean isValidOpeningBalance(long amount) {
        return amount >= 0;
    }

    public static boolean isValidMobile(String mobile) {
        return MOBILE.matcher(mobile).matches();
    }

    public static boolean isValidEmail(String email) {
        return EMAIL.matcher(email).matches();
    }

    public static boolean isValidPan(String pan) {
        return PAN.matcher(pan).matches();
    }

    public static boolean isValidIfsc(String ifsc) {
        return IFSC.matcher(ifsc).matches();
    }
}