import java.util.Objects;

public class TransactionBuffer {
    private final Runnable[] transactions;
    private int head;
    private int tail;
    private int size;
    private boolean producerFinished;

    public TransactionBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Buffer capacity must be greater than zero.");
        }
        transactions = new Runnable[capacity];
    }

    public synchronized void add(Runnable transaction) throws InterruptedException {
        Objects.requireNonNull(transaction, "transaction");
        while (size == transactions.length && !producerFinished) {
            wait();
        }
        if (producerFinished) {
            throw new IllegalStateException("Cannot add transactions after production has finished.");
        }

        transactions[tail] = transaction;
        tail = (tail + 1) % transactions.length;
        size++;
        notifyAll();
    }

    public synchronized Runnable take() throws InterruptedException {
        while (size == 0 && !producerFinished) {
            wait();
        }
        if (size == 0) {
            return null;
        }

        Runnable transaction = transactions[head];
        transactions[head] = null;
        head = (head + 1) % transactions.length;
        size--;
        notifyAll();
        return transaction;
    }

    public synchronized void finishProducing() {
        producerFinished = true;
        notifyAll();
    }
}
