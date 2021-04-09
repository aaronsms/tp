package chim.model.customer.predicates;

import java.util.List;

import chim.commons.util.PredicateUtil;
import chim.model.customer.Customer;
import chim.model.util.predicate.SingleFieldPredicate;

/**
 * Predicate for filtering customers by their names.
 */
public class CustomerNamePredicate extends SingleFieldPredicate<Customer> {

    public static final String MESSAGE_CONSTRAINTS = "Name keywords must not be empty.";

    public CustomerNamePredicate(List<String> keywords) {
        super(keywords);
    }

    @Override
    public double getSimilarityScore(Customer customer) {
        return PredicateUtil.getWordSimilarityScoreIgnoreCase(customer.getName().fullName, getKeywords());
    }

    @Override
    public boolean test(Customer customer) {
        return PredicateUtil.containsPrefixWordIgnoreCase(customer.getName().fullName, getKeywords());
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof CustomerNamePredicate) && super.equals(other); // state check
    }

    @Override
    public String toString() {
        return String.format("name containing %s",
                super.toString());
    }
}