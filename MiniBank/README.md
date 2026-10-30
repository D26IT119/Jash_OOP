# MiniBank

A small command-line banking application. Accounts and transaction logs are
stored in `data/accounts.ser` and `logs/transactions.log` in the current
working directory.

## Requirements

- Java 17 or newer
- Maven 3.8 or newer

## Build and test

From this folder, run:

```text
mvn test
mvn package
```

This runs the JUnit tests and creates the runnable
`target/minibank-1.0.0.jar`.

## Run

```text
java -jar target/minibank-1.0.0.jar
```

Choose a menu option to open accounts, make deposits or withdrawals, transfer
money, view statements, list accounts, process a transaction batch, or generate
the end-of-day report. Account data is loaded at startup and saved when exiting.
Supported account types are `SAVINGS`, `CURRENT`, and `FIXED`.
