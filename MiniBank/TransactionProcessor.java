import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import model.Account;

public class TransactionProcessor {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final AccountStore accounts = new AccountStore();

    public void addAccount(Account account) {
        Objects.requireNonNull(account, "account");
        accounts.add(account);
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public void submit(Runnable task) {
        executor.execute(Objects.requireNonNull(task, "task"));
    }

    public void processBatch(List<? extends Runnable> tasks) {
        List<Future<?>> futures = new ArrayList<>();
        for (Runnable task : tasks) {
            futures.add(executor.submit(Objects.requireNonNull(task, "task")));
        }
        executor.shutdown();

        IllegalStateException failure = null;
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException exception) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while processing transactions.", exception);
            } catch (ExecutionException exception) {
                if (failure == null) {
                    failure = new IllegalStateException("A batch transaction failed.", exception.getCause());
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    public void stop() {
        executor.shutdown();
        try {
            while (!executor.awaitTermination(1, TimeUnit.DAYS)) {
                // Continue waiting until every submitted transaction has completed.
            }
        } catch (InterruptedException exception) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for transactions to finish.", exception);
        }
    }
}
