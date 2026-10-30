import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.InsufficientFundsException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import model.Account;
import org.junit.jupiter.api.Test;

class BankServiceTest {
    @Test
    void depositIncreasesBalance() throws Exception {
        BankService bank = new BankService(new Repository<>());
        Account account = bank.openAccount("Aaryan", "SAVINGS", 500);

        bank.deposit(account.getAccountNumber(), 200);

        assertEquals(700, account.getBalance());
    }

    @Test
    void withdrawalAboveBalanceThrows() throws Exception {
        BankService bank = new BankService(new Repository<>());
        Account account = bank.openAccount("Aaryan", "SAVINGS", 500);

        assertThrows(InsufficientFundsException.class,
                () -> bank.withdraw(account.getAccountNumber(), 600));
        assertEquals(500, account.getBalance());
    }

    @Test
    void concurrentDepositsKeepTheCorrectBalance() throws Exception {
        BankService bank = new BankService(new Repository<>());
        Account account = bank.openAccount("Aaryan", "SAVINGS", 0);
        int workerCount = 8;
        int depositsPerWorker = 1_000;
        ExecutorService workers = Executors.newFixedThreadPool(workerCount);
        List<Future<?>> results = new ArrayList<>();

        try {
            for (int worker = 0; worker < workerCount; worker++) {
                results.add(workers.submit(() -> {
                    for (int deposit = 0; deposit < depositsPerWorker; deposit++) {
                        try {
                            bank.deposit(account.getAccountNumber(), 1);
                        } catch (Exception exception) {
                            throw new IllegalStateException(exception);
                        }
                    }
                }));
            }
            for (Future<?> result : results) {
                result.get();
            }
        } finally {
            workers.shutdownNow();
        }

        assertEquals((long) workerCount * depositsPerWorker, account.getBalance());
    }
}
