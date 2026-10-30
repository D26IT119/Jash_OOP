import exception.BankException;
import java.util.Objects;
import model.Account;
import model.TransactionType;

public class TransactionTask implements Runnable {
    private final Account account;
    private final Account destination;
    private final TransactionType type;
    private final long amount;

    public TransactionTask(Account account, TransactionType type, long amount) {
        this(account, null, type, amount);
    }

    public TransactionTask(Account account, Account destination, TransactionType type, long amount) {
        this.account = Objects.requireNonNull(account, "account");
        this.type = Objects.requireNonNull(type, "type");
        if (type == TransactionType.TRANSFER && destination == null) {
            throw new IllegalArgumentException("Transfer tasks require a destination account.");
        }
        this.destination = destination;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            switch (type) {
                case DEPOSIT -> account.deposit(amount);
                case WITHDRAW -> account.withdraw(amount);
                case TRANSFER -> account.transfer(destination, amount);
            }
        } catch (BankException exception) {
            throw new IllegalStateException(type + " transaction failed for account "
                    + account.getAccountNumber() + ".", exception);
        }
    }
}
