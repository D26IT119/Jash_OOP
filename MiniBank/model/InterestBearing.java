package model;

public interface InterestBearing {

    default double yearlyInterest() {
        return interestRate() * getBalance() / 100;
    }

    double interestRate();

    long getBalance();
}