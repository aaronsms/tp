package seedu.address.model.cheese;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

/**
 * Represents a Cheese in the Cheese Inventory Management System (CHIM)
 * Guarantees: immutable;
 */
public class Cheese {

    // Identity fields
    // Primary key for Cheese
    private final CheeseId cheeseId;
    private final CheeseType cheeseType;

    // Data fields
    private final ManufactureDate manufactureDate;
    private final MaturityDate maturityDate;
    private final ExpiryDate expiryDate;
    private final boolean isAssigned;

    public Cheese(CheeseType cheeseType, ManufactureDate manufactureDate, MaturityDate maturityDate,
                  ExpiryDate expiryDate) {
        this(cheeseType, manufactureDate, maturityDate, expiryDate, CheeseId.getNextId(), false);
    }

    private Cheese(CheeseType cheeseType, ManufactureDate manufactureDate, MaturityDate maturityDate,
                   ExpiryDate expiryDate, CheeseId cheeseId) {
        this(cheeseType, manufactureDate, maturityDate, expiryDate, cheeseId, true);
    }

    /**
     * Every field must be present and not null.
     */
    public Cheese(CheeseType cheeseType, ManufactureDate manufactureDate, MaturityDate maturityDate,
                  ExpiryDate expiryDate, CheeseId cheeseId, boolean isAssigned) {
        requireAllNonNull(cheeseType, manufactureDate, maturityDate, expiryDate);
        this.cheeseType = cheeseType;
        this.manufactureDate = manufactureDate;
        this.maturityDate = maturityDate;
        this.expiryDate = expiryDate;
        this.cheeseId = cheeseId;
        this.isAssigned = isAssigned;
    }

    public CheeseType getCheeseType() {
        return cheeseType;
    }

    public ManufactureDate getManufactureDate() {
        return manufactureDate;
    }

    public MaturityDate getMaturityDate() {
        return maturityDate;
    }

    public ExpiryDate getExpiryDate() {
        return expiryDate;
    }

    public CheeseId getCheeseId() {
        return cheeseId;
    }

    public boolean getAssignStatus() {
        return isAssigned;
    }

    public boolean isSameType(CheeseType cheeseType) {
        return this.cheeseType.equals(cheeseType);
    }

    public Cheese assignToOrder() {
        return new Cheese(cheeseType, manufactureDate, maturityDate, expiryDate, cheeseId);
    }

    /**
     * Returns true if both cheeses have the same name.
     * This defines a weaker notion of equality between two cheeses.
     */
    public boolean isSameCheese(Cheese otherCheese) {
        if (otherCheese == this) {
            return true;
        }

        return otherCheese != null
            && otherCheese.cheeseId.equals(cheeseId);
    }

    /**
     * Returns true if both cheeses have the same identity and data fields.
     * This defines a stronger notion of equality between two cheeses.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Cheese)) {
            return false;
        }

        Cheese otherCheese = (Cheese) other;
        return otherCheese.getCheeseId().equals(getCheeseId())
            && otherCheese.getCheeseType().equals(getCheeseType())
            && otherCheese.getManufactureDate().equals(getManufactureDate())
            && otherCheese.getMaturityDate().equals(getMaturityDate())
            && otherCheese.getExpiryDate().equals(getExpiryDate());
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(getCheeseId())
            .append("; Cheese Type: ")
            .append(getCheeseType())
            .append("; Manufacture Date: ")
            .append(getManufactureDate())
            .append("; Maturity Date: ")
            .append(getMaturityDate())
            .append("; Expiry Date: ")
            .append(getExpiryDate())
            .append("; isAssigned: ")
            .append(getAssignStatus() ? "True" : "False");

        return builder.toString();
    }

}
