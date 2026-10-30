import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import exception.BankException;
import model.Account;
import model.TransactionType;

public class MiniBank {
    private static final Path ACCOUNTS_FILE = Paths.get("data", "accounts.ser");
    private static final Path TRANSACTION_LOG = Paths.get("logs", "transactions.log");
    private static final Path REPORT_FILE = Paths.get("reports", "end-of-day.txt");

    public static void main(String[] args) throws IOException {
        Repository<Account> repository = new Repository<>();
        if (Files.exists(ACCOUNTS_FILE)) {
            for (Account account : StatePersister.load(ACCOUNTS_FILE)) {
                repository.save(account);
            }
        }

        BankService bank = new BankService(repository);
        System.out.println("Welcome to MiniBank");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1. Open account");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Transfer");
                System.out.println("5. Account statement");
                System.out.println("6. List accounts");
                System.out.println("7. Process transaction batch");
                System.out.println("8. Generate report");
                System.out.println("9. Exit");
                System.out.print("Choice: ");

                if (!scanner.hasNextLine()) {
                    break;
                }
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> openAccount(scanner, bank);
                        case "2" -> deposit(scanner, bank);
                        case "3" -> withdraw(scanner, bank);
                        case "4" -> transfer(scanner, bank);
                        case "5" -> System.out.println(bank.statement(prompt(scanner, "Account number: ")));
                        case "6" -> bank.listAccounts().forEach(System.out::println);
                        case "7" -> processBatch(scanner, bank);
                        case "8" -> System.out.println(ReportGenerator.generate(
                                TRANSACTION_LOG.getParent(), REPORT_FILE));
                        case "9" -> {
                            System.out.println("Thank you for using MiniBank.");
                            return;
                        }
                        default -> System.out.println("Invalid choice. Please choose 1 to 9.");
                    }
                } catch (BankException exception) {
                    System.out.println("Operation failed: " + exception.getMessage());
                } catch (IllegalArgumentException exception) {
                    System.out.println("Invalid input: " + exception.getMessage());
                }
            }
        } finally {
            saveAccounts(bank);
        }
    }

    private static void openAccount(Scanner scanner, BankService bank) throws BankException {
        String owner = prompt(scanner, "Owner name: ");
        System.out.print("Account type (SAVINGS/CURRENT/FIXED): ");
        String type = scanner.nextLine();
        long balance = readLong(scanner, "Opening balance: ");
        Account account = bank.openAccount(owner, type, balance);
        System.out.println("Account opened: " + account.getAccountNumber());
    }

    private static void deposit(Scanner scanner, BankService bank) throws BankException, IOException {
        String accountNumber = prompt(scanner, "Account number: ");
        long amount = readLong(scanner, "Deposit amount: ");
        Account account = bank.deposit(accountNumber, amount);
        log("DEPOSIT," + amount);
        System.out.println("Deposit complete. Balance: " + account.getBalance());
    }

    private static void withdraw(Scanner scanner, BankService bank) throws BankException, IOException {
        String accountNumber = prompt(scanner, "Account number: ");
        long amount = readLong(scanner, "Withdrawal amount: ");
        Account account = bank.withdraw(accountNumber, amount);
        log("WITHDRAW," + amount);
        System.out.println("Withdrawal complete. Balance: " + account.getBalance());
    }

    private static void transfer(Scanner scanner, BankService bank) throws BankException, IOException {
        String from = prompt(scanner, "From account: ");
        String to = prompt(scanner, "To account: ");
        long amount = readLong(scanner, "Transfer amount: ");
        bank.transfer(from, to, amount);
        log("WITHDRAW," + amount, "DEPOSIT," + amount);
        System.out.println("Transfer complete.");
    }

    private static void processBatch(Scanner scanner, BankService bank) throws BankException, IOException {
        long transactionCount = readLong(scanner, "Number of transactions: ");
        if (transactionCount < 0 || transactionCount > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Transaction count is out of range.");
        }
        int count = (int) transactionCount;

        List<BankService.BatchTransaction> transactions = new ArrayList<>();
        List<String> logEntries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.print("Transaction " + (i + 1) + " type (DEPOSIT/WITHDRAW/TRANSFER): ");
            TransactionType type = TransactionType.valueOf(scanner.nextLine().trim().toUpperCase(Locale.ROOT));
            String account = prompt(scanner, "Account number: ");
            String destination = type == TransactionType.TRANSFER
                    ? prompt(scanner, "Destination account: ")
                    : null;
            long amount = readLong(scanner, "Amount: ");
            transactions.add(new BankService.BatchTransaction(type, account, destination, amount));
            if (type == TransactionType.DEPOSIT) {
                logEntries.add("DEPOSIT," + amount);
            } else if (type == TransactionType.WITHDRAW) {
                logEntries.add("WITHDRAW," + amount);
            } else {
                logEntries.add("WITHDRAW," + amount);
                logEntries.add("DEPOSIT," + amount);
            }
        }

        bank.processBatch(transactions);
        log(logEntries.toArray(new String[0]));
        System.out.println("Batch complete: " + count + " transactions processed.");
    }

    private static String prompt(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static long readLong(Scanner scanner, String message) {
        return Long.parseLong(prompt(scanner, message));
    }

    private static void log(String... entries) throws IOException {
        for (String entry : entries) {
            TransactionLog.append(TRANSACTION_LOG, entry);
        }
    }

    private static void saveAccounts(BankService bank) throws IOException {
        StatePersister.save(bank.listAccounts().toArray(new Account[0]), ACCOUNTS_FILE);
    }
}
