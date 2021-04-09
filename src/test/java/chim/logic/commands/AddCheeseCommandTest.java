package chim.logic.commands;

import static chim.testutil.Assert.assertThrows;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import chim.commons.core.GuiSettings;
import chim.commons.core.GuiSettings.PanelToShow;
import chim.model.Chim;
import chim.model.ReadOnlyChim;
import chim.model.ReadOnlyUserPrefs;
import chim.model.UserPrefs;
import chim.model.cheese.Cheese;
import chim.testutil.CheeseBuilder;

public class AddCheeseCommandTest {

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCheeseCommand(null));
    }

    @Test
    public void execute_cheeseAcceptedByModel_addSuccessful() throws Exception {
        Cheese validCheese = new CheeseBuilder().build();
        ModelStubAcceptingCheeseAdded modelStub = new ModelStubAcceptingCheeseAdded();

        CommandResult commandResult = new AddCheeseCommand(new Cheese[] { validCheese }).execute(modelStub);

        assertEquals(AddCheeseCommand.MESSAGE_SUCCESS + "\n" + validCheese,
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validCheese), modelStub.cheesesAdded);
        assertEquals(modelStub.getGuiSettings().getPanel(), PanelToShow.CHEESE_LIST);
    }

    @Test
    public void equals() {
        Cheese parmesan = new CheeseBuilder().withCheeseType("Parmesan").build();
        Cheese brie = new CheeseBuilder().withCheeseType("Brie").build();

        AddCheeseCommand addCheeseCommand1 = new AddCheeseCommand(new Cheese[] { parmesan });
        AddCheeseCommand addCheeseCommand2 = new AddCheeseCommand(new Cheese[] { brie });

        // same object -> returns true
        assertTrue(addCheeseCommand1.equals(addCheeseCommand1));

        // same values -> returns true
        AddCheeseCommand addCheeseCommand1Copy = new AddCheeseCommand(new Cheese[] { parmesan });
        assertTrue(addCheeseCommand1.equals(addCheeseCommand1Copy));

        // different types -> returns false
        assertFalse(addCheeseCommand1.equals(1));

        // null -> returns false
        assertFalse(addCheeseCommand1.equals(null));

        // different customer -> returns false
        assertFalse(addCheeseCommand1.equals(addCheeseCommand2));
    }


    /**
     * A Model stub that always accepts the cheese being added.
     */
    private class ModelStubAcceptingCheeseAdded extends ModelStub {
        final ArrayList<Cheese> cheesesAdded = new ArrayList<>();
        private final ReadOnlyUserPrefs userPrefs = new UserPrefs();

        @Override
        public boolean hasCheese(Cheese cheese) {
            requireNonNull(cheese);
            return cheesesAdded.stream().anyMatch(cheese::isSameCheese);
        }

        @Override
        public void addCheese(Cheese cheese) {
            requireNonNull(cheese);
            cheesesAdded.add(cheese);
        }

        @Override
        public GuiSettings getGuiSettings() {
            return userPrefs.getGuiSettings();
        }

        @Override
        public void setPanelToCheeseList() {
            getGuiSettings().setPanelToCheeseList();
        }

        @Override
        public ReadOnlyChim getChim() {
            return new Chim();
        }
    }

}