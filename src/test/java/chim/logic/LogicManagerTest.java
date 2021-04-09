package chim.logic;

import static chim.commons.core.Messages.MESSAGE_INVALID_CUSTOMER_DISPLAYED_PHONE;
import static chim.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static chim.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static chim.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static chim.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static chim.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static chim.testutil.Assert.assertThrows;
import static chim.testutil.TypicalCustomers.AMY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chim.logic.commands.AddCustomerCommand;
import chim.logic.commands.CommandResult;
import chim.logic.commands.ListCustomersCommand;
import chim.logic.commands.exceptions.CommandException;
import chim.logic.parser.exceptions.ParseException;
import chim.model.CustomerIdStub;
import chim.model.Model;
import chim.model.ModelManager;
import chim.model.ReadOnlyChim;
import chim.model.UserPrefs;
import chim.model.customer.Customer;
import chim.storage.JsonChimStorage;
import chim.storage.JsonUserPrefsStorage;
import chim.storage.StorageManager;
import chim.testutil.CustomerBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy exception");

    @TempDir
    public Path temporaryFolder;

    private final Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonChimStorage chimStorage =
                new JsonChimStorage(temporaryFolder.resolve("Chim.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(chimStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCustomerCommand = "deletecustomer p/90000989";
        assertCommandException(deleteCustomerCommand, MESSAGE_INVALID_CUSTOMER_DISPLAYED_PHONE);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCustomersCommand.COMMAND_WORD;
        String expectedMessage = String.format(
                ListCustomersCommand.SUMMARY_MESSAGE,
                model.getFilteredCustomerList().size()
        );
        assertCommandSuccess(listCommand, expectedMessage, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        // Setup LogicManager with JsonChimIoExceptionThrowingStub
        JsonChimStorage chimStorage =
                new JsonChimIoExceptionThrowingStub(temporaryFolder.resolve("ioExceptionChim.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ioExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(chimStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);

        // Execute add command
        String addCommand = AddCustomerCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY
                + ADDRESS_DESC_AMY;
        Customer expectedCustomer = new CustomerBuilder(AMY).withId(CustomerIdStub.getNextId()).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addCustomer(expectedCustomer);
        String expectedMessage = LogicManager.FILE_OPS_ERROR_MESSAGE + DUMMY_IO_EXCEPTION;
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    @Test
    public void getFilteredCustomerList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredCustomerList().remove(0));
    }

    @Test
    public void getFilteredCheeseList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredCheeseList().remove(0));
    }

    @Test
    public void getFilteredOrderList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredOrderList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getChim(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * A stub class to throw an {@code IOException} when the save method is called.
     */
    private static class JsonChimIoExceptionThrowingStub extends JsonChimStorage {
        private JsonChimIoExceptionThrowingStub(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveChim(ReadOnlyChim chim, Path filePath) throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }
    }
}