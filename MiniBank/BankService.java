import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import model.Account;
import model.CurrentAccount;
import model.FixedDeposite;
import model.SavingsAccount;
import model.TransactionType;
import util.StatementFormat;
import util.Validator;

public class BankService {
    private final Repository<Account> repository;

    public BankService(Repository<Account> repository) {
        this.repository = repository;
    }

    public Account openAccount(String ownerName, String accountType, long initialBalance) throws BankException {
        if (!Validator.isValidOwnerName(ownerName)) {
            throw new BankException("Owner name must contain 1 to 50 characters.");
        }
        if (!Validator.isValidOpeningBalance(initialBalance)) {
            throw new InvalidAmountException("Initial balance cannot be negative.");
        }
        if (accountType == null) {
            throw new BankException("Account type must be SAVINGS, CURRENT, or FIXED.");
        }

        String name = ownerName.trim();
        String type = accountType.trim().toUpperCase(Locale.ROOT);
        while (true) {
            Account account = switch (type) {
                case "SAVINGS" -> new SavingsAccount(name, initialBalance, 0);
                case "CURRENT" -> new CurrentAccount(name, initialBalance, 3_000);
                case "FIXED" -> new FixedDeposite(name, initialBalance);
                default -> throw new BankException("Account type must be SAVINGS, CURRENT, or FIXED.");
            };
            if (repository.findById(account.getAccountNumber()) == null) {
                repository.save(account);
                return account;
            }
        }
    }

    public Account deposit(String accountNumber, long amount) throws BankException {
        validateAmount(amount);
        Account account = findAccount(accountNumber);
        account.deposit(amount);
        return account;
    }

    public Account withdraw(String accountNumber, long amount) throws BankException {
        validateAmount(amount);
        Account account = findAccount(accountNumber);
        if (!account.canWithdraw(amount)) {
            throw new InsufficientFundsException(Math.max(1, amount - account.getBalance()));
        }
        account.withdraw(amount);
        return account;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, long amount) throws BankException {
        validateAmount(amount);
        Account from = findAccount(fromAccountNumber);
        Account to = findAccount(toAccountNumber);
        from.transfer(to, amount);
    }

    public String statement(String accountNumber) throws AccountNotFoundException {
        return StatementFormat.buildStatement(findAccount(accountNumber));
    }

    public ArrayList<Account> listAccounts() {
        return repository.findAll();
    }

    public void processBatch(List<BatchTransaction> transactions) throws BankException {
        TransactionProcessor processor = new TransactionProcessor();
        List<TransactionTask> tasks = new ArrayList<>();
        for (Account account : repository.findAll()) {
            processor.addAccount(account);
        }

        for (BatchTransaction transaction : transactions) {
            if (transaction.type() == null) {
                throw new BankException("Transaction type is required.");
            }
            validateAmount(transaction.amount());
            Account account = processor.getAccount(transaction.accountNumber());
            if (account == null) {
                throw new AccountNotFoundException("Account not found: " + transaction.accountNumber());
            }
            Account destination = null;
            if (transaction.type() == TransactionType.TRANSFER) {
                if (transaction.destinationAccountNumber() == null
                        || transaction.destinationAccountNumber().isBlank()) {
                    throw new BankException("A destination account is required for transfers.");
                }
                destination = processor.getAccount(transaction.destinationAccountNumber());
                if (destination == null) {
                    throw new AccountNotFoundException(
                            "Account not found: " + transaction.destinationAccountNumber());
                }
            }
            tasks.add(new TransactionTask(account, destination, transaction.type(), transaction.amount()));
        }

        try {
            processor.processBatch(tasks);
        } catch (IllegalStateException exception) {
            Throwable cause = exception.getCause();
            String message = cause == null ? exception.getMessage() : cause.getMessage();
            throw new BankException("Batch transaction failed: " + message, exception);
        }
    }

    private Account findAccount(String accountNumber) throws AccountNotFoundException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        return account;
    }

    private static void validateAmount(long amount) throws InvalidAmountException {
        if (!Validator.isValidAmount(amount)) {
            throw new InvalidAmountException("Transaction amount must be greater than zero.");
        }
    }

    public record BatchTransaction(
            TransactionType type,
            String accountNumber,
            String destinationAccountNumber,
            long amount) {
        public BatchTransaction(TransactionType type, String accountNumber, long amount) {
            this(type, accountNumber, null, amount);
        }
    }
}
