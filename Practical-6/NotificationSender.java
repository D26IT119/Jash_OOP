// Functional interface
interface Notifier {
    void send(String message);
}
// Marker interface
interface Urgent {
}

public class NotificationSender {
    public static void main(String[] args) {
        // Email,SMS sender using lambda
        Notifier email = message ->
            System.out.println("Email: " + message);
        Notifier sms = message ->
            System.out.println("SMS: " + message);

        class UrgentEmail implements Notifier, Urgent {

            @Override
            public void send(String message) {
                System.out.println("Urgent Email: " + message);
            }
        }

        Notifier urgentEmail = new UrgentEmail();

        Notifier[] senders = {email, sms, urgentEmail};
        String message = "Class starts at 9:30 AM";

        for (Notifier sender : senders) {
            sender.send(message);

            if (sender instanceof Urgent) {
                sender.send(message);
            }
        }
    }
}

