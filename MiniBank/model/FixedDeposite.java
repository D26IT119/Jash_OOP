package model;
import java.io.Serializable;

public class FixedDeposite extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    public FixedDeposite(String ownerName, long balance) {
        super(ownerName, balance);
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return false;
    }
}
