package models;

public enum AccountType {
    ASSET,
    LIABILITY,
    EXPENSE,
    REVENUE;

    public boolean isDebitAccount() {
        return this.equals(ASSET) || this.equals(EXPENSE);
    }
}
