public class Driverr {
    public static void main(String[] args) {

        Card[] cards = new Card[5];

        int count = 0;
        boolean duplicateFound = false;

        Card card1 = new Card("Ace", "Spades");

        // Check card1 against earlier cards
        for (int i = 0; i < count; i++) {
            if (card1.equals(cards[i])) {
                System.out.println("Duplicate found: " + card1);
                duplicateFound = true;
                break;
            }
        }

        if (!duplicateFound) {
            cards[count] = card1;
            count++;
        }

        Card card2 = new Card("King", "Hearts");

        for (int i = 0; i < count; i++) {
            if (card2.equals(cards[i])) {
                System.out.println("Duplicate found: " + card2);
                duplicateFound = true;
                break;
            }
        }

        if (!duplicateFound) {
            cards[count] = card2;
            count++;
        }

        Card card3 = new Card("Queen", "Diamonds");

        for (int i = 0; i < count; i++) {
            if (card3.equals(cards[i])) {
                System.out.println("Duplicate found: " + card3);
                duplicateFound = true;
                break;
            }
        }

        if (!duplicateFound) {
            cards[count] = card3;
            count++;
        }
        Card card4 = new Card("Ace", "Spades");

        duplicateFound = false;

        for (int i = 0; i < count; i++) {
            if (card4.equals(cards[i])) {
                System.out.println("Duplicate found: " + card4);
                duplicateFound = true;
                break;
            }
        }
        if (!duplicateFound) {
            cards[count] = card4;
            count++;
        }
    }
}

