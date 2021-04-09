package chim.logic.commands;

import static chim.logic.commands.CommandTestUtil.assertCommandFailure;
import static chim.logic.commands.CommandTestUtil.assertCommandSuccess;
import static chim.testutil.TypicalModels.getTypicalChim;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chim.model.Model;
import chim.model.ModelManager;
import chim.model.OrderIdStub;
import chim.model.UserPrefs;
import chim.model.cheese.Cheese;
import chim.model.customer.Customer;
import chim.model.customer.Phone;
import chim.model.order.Order;
import chim.testutil.CheeseBuilder;
import chim.testutil.CustomerBuilder;
import chim.testutil.OrderBuilder;
import chim.testutil.TypicalOrder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalChim(), new UserPrefs());
    }

    @Test
    public void execute_newCustomer_success() {
        Customer validCustomer = new CustomerBuilder().build();

        Model expectedModel = new ModelManager(model.getChim(), new UserPrefs());
        expectedModel.addCustomer(validCustomer);
        expectedModel.setPanelToCustomerList();

        assertCommandSuccess(new AddCustomerCommand(validCustomer), model,
                String.format(AddCustomerCommand.MESSAGE_SUCCESS, validCustomer), expectedModel);
    }

    @Test
    public void execute_duplicateCustomer_throwsCommandException() {
        Customer customerInList = model.getChim().getCustomerList().get(0);
        assertCommandFailure(new AddCustomerCommand(customerInList), model,
                AddCustomerCommand.MESSAGE_DUPLICATE_CUSTOMER);
    }

    @Test
    public void execute_newCheese_success() {
        Cheese validCheese = new CheeseBuilder().build();

        Model expectedModel = new ModelManager(model.getChim(), new UserPrefs());
        expectedModel.addCheese(validCheese);
        expectedModel.setPanelToCheeseList();

        String successMessage = AddCheeseCommand.MESSAGE_SUCCESS + "\n" + validCheese;
        assertCommandSuccess(new AddCheeseCommand(new Cheese[] { validCheese }), model, successMessage, expectedModel);
    }

    @Test
    public void execute_newCheeses_success() {
        Cheese validCheese1 = new CheeseBuilder().build();
        Cheese validCheese2 = new CheeseBuilder().build();
        Cheese validCheese3 = new CheeseBuilder().build();

        Model expectedModel = new ModelManager(model.getChim(), new UserPrefs());
        expectedModel.addCheese(validCheese1);
        expectedModel.addCheese(validCheese2);
        expectedModel.addCheese(validCheese3);
        expectedModel.setPanelToCheeseList();

        String successMessage = AddCheeseCommand.MESSAGE_SUCCESS + "\n" + validCheese1 + "\n" + validCheese2
                + "\n" + validCheese3;
        assertCommandSuccess(new AddCheeseCommand(new Cheese[] { validCheese1, validCheese2, validCheese3 }),
                model, successMessage, expectedModel);
    }

    @Test
    public void execute_newOrderWithCustomerPresent_success() {
        Phone customerPhoneInList = new Phone(model.getChim().getCustomerList().get(0).getPhone().value);

        Order validOrder = new OrderBuilder(TypicalOrder.ORDER_CAMEMBERT)
                .withCompletedDate(null).withOrderId(OrderIdStub.getNextId()).withCheeses(new HashSet<>()).build();

        Model expectedModel = new ModelManager(model.getChim(), new UserPrefs());
        expectedModel.addOrder(validOrder);
        expectedModel.setPanelToOrderList();

        assertCommandSuccess(new AddOrderCommand(validOrder.getCheeseType(), customerPhoneInList,
                validOrder.getQuantity(), validOrder.getOrderDate()), model,
                String.format(AddOrderCommand.MESSAGE_SUCCESS, validOrder), expectedModel);
    }

    @Test
    public void execute_newOrderWithCustomerAbsent_throwsCommandException() {
        Order validOrder = new OrderBuilder(TypicalOrder.ORDER_CAMEMBERT)
                .withCompletedDate(null).withCheeses(Set.of())
                .withOrderId(OrderIdStub.getNextId()).build();

        Model expectedModel = new ModelManager(model.getChim(), new UserPrefs());
        expectedModel.addOrder(validOrder);
        expectedModel.setPanelToOrderList();

        assertCommandFailure(new AddOrderCommand(validOrder.getCheeseType(), new Phone("55555555"),
                validOrder.getQuantity(), validOrder.getOrderDate()), model,
                AddOrderCommand.MESSAGE_NO_CUSTOMERS_FOUND);
    }
}