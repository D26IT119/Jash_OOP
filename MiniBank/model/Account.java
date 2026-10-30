package model;
import java.io.Serializable;
import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;
public abstract class Account implements Transactable, InterestBearing, Serializable, Comparable<Account> {

    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;

    @MaxLength(50)
    private String ownerName;

    @Positive
    private long balance;

    private boolean active;

    private static long AccountCounter = 0;

      private static String generateAccountNumber() {
        AccountCounter++;
        return String.format("AC%04d", AccountCounter);
    }

    public Account(String ownerName, long initialBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.active = true;
    }

    public Account(String ownerName) {
     
        this(ownerName, 0);
    }
    
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        balance += amount;
    }

    public synchronized void withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException(Math.max(1, amount - balance));
        }
        balance -= amount;
    }

    public void transfer(Account to, long amount) throws BankException {
        try {
            if (to == null) {
                throw new AccountNotFoundException("Destination account not found.");
            }

            Account firstLock = accountNumber.compareTo(to.accountNumber) <= 0 ? this : to;
            Account secondLock = firstLock == this ? to : this;

            synchronized (firstLock) {
                synchronized (secondLock) {
                    withdraw(amount);
                    to.deposit(amount);
                }
            }
        } catch (BankException exception) {
            throw exception;
        } finally {
            System.out.println("Transfer attempt completed.");
        }
    }

      public String getAccountNumber() {
        return accountNumber;
    }

      @Override
      public int compareTo(Account other) {
          return accountNumber.compareTo(other.accountNumber);
      }

      public String getOwnerName() {
          return ownerName;
      }

    public synchronized long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
public synchronized String toString() {
    return "Account Number: " + accountNumber + ", Owner Name: " + ownerName + ", Balance: " + balance + ", Active: " + active;
}

   @Override
public boolean equals(Object o) {

    if (!(o instanceof Account)) {
        return false;
    }

    Account other = (Account) o;

    return this.accountNumber.equals(other.accountNumber);
}

@Override
public int hashCode() {
    return accountNumber.hashCode();
}

public abstract double interestRate();

public abstract boolean canWithdraw(long amount);

}
