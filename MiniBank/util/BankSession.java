package util;

public class BankSession implements AutoCloseable {
    @Override
    public void close() {
        System.out.println("Bank session closed.");
    }
}
