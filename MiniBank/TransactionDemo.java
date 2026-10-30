import model.Account;
import model.SavingsAccount;

public final class TransactionDemo {
    private TransactionDemo() {
    }

    public static void run() {
        Account account = new SavingsAccount("Thread Test", 0, 0);
        Thread[] workers = new Thread[10];

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Thread(new AccountWorker(account, 10_000, 1));
            workers[i].start();
        }

        try {
            for (Thread worker : workers) {
                worker.join();
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for workers.", exception);
        }

        System.out.println("Expected balance: 100000");
        System.out.println("Final balance: " + account.getBalance());
    }
}
