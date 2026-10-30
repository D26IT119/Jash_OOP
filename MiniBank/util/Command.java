package util;

import model.TransactionType;

public record Command(
        TransactionType type,
        String accountNumber,
        long amount) {
}