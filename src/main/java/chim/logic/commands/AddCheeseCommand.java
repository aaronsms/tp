package chim.logic.commands;

import static chim.logic.parser.CliSyntax.PREFIX_CHEESE_TYPE;
import static chim.logic.parser.CliSyntax.PREFIX_EXPIRY_DATE;
import static chim.logic.parser.CliSyntax.PREFIX_MANUFACTURE_DATE;
import static chim.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import chim.logic.commands.exceptions.CommandException;
import chim.model.Model;
import chim.model.cheese.Cheese;

/**
 * Adds a cheese to CHIM.
 */
public class AddCheeseCommand extends AddCommand {

    public static final String COMMAND_WORD = "addcheese";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a cheese to CHIM.\n"
            + "Parameters: "
            + PREFIX_CHEESE_TYPE + "CHEESE TYPE "
            + PREFIX_QUANTITY + "QUANTITY "
            + "[" + PREFIX_MANUFACTURE_DATE + "MANUFACTURE_DATE] "
            + "[" + PREFIX_EXPIRY_DATE + "EXPIRY_DATE]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CHEESE_TYPE + "Parmesan "
            + PREFIX_QUANTITY + "5 "
            + PREFIX_MANUFACTURE_DATE + "2020-12-30 "
            + PREFIX_EXPIRY_DATE + "2021-02-30";

    public static final String MESSAGE_SUCCESS = "New cheeses added: ";

    private final Cheese[] toAddCheeses;

    /**
     * Creates an AddOrderCommand to add the specified {@code Order}
     */
    public AddCheeseCommand(Cheese[] cheeses) {
        requireNonNull(cheeses);
        toAddCheeses = cheeses;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        for (Cheese toAddCheese : toAddCheeses) {
            model.addCheese(toAddCheese);
        }
        model.setPanelToCheeseList();

        StringBuilder sb = new StringBuilder(MESSAGE_SUCCESS);
        for (Cheese toAddCheese : toAddCheeses) {
            sb.append("\n");
            sb.append(toAddCheese);
        }

        return new CommandResult(sb.toString());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddCheeseCommand // instanceof handles nulls
                && Arrays.equals(toAddCheeses, ((AddCheeseCommand) other).toAddCheeses));
    }
}