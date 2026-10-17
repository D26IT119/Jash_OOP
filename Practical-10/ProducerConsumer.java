
import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumer {
    private static final int BUFFER_SIZE = 3;
    private static final int ITEMS = 10;

    private static final Queue<Integer> buffer = new LinkedList<>();

    static class Producer implements Runnable {
        public void run() {
            try {
                for (int i = 1; i <= ITEMS; i++) {
                    synchronized (buffer) {
                        while (buffer.size() == BUFFER_SIZE) {
                            buffer.wait();
                        }

                        buffer.add(i);
                        System.out.println("Produced: " + i);

                        buffer.notify();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Consumer implements Runnable {
        public void run() {
            try {
                for (int i = 1; i <= ITEMS; i++) {
                    synchronized (buffer) {
                        while (buffer.isEmpty()) {
                            buffer.wait();
                        }

                        int item = buffer.remove();
                        System.out.println("Consumed: " + item);

                        buffer.notify();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Thread producer = new Thread(new Producer());
        Thread consumer = new Thread(new Consumer());

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("All items produced and consumed.");
    }
}
