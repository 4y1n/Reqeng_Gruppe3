import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class charging_steps {
    @And("a location {string} exists with price {double}")
    public void aLocationExistsWithPrice(String arg0, int arg1, int arg2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("a customer {string} with credit {double} exists")
    public void aCustomerWithCreditExists(String arg0, int arg1, int arg2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("a charger {string} at location {string} is available")
    public void aChargerAtLocationIsAvailable(String arg0, String arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("customer {string} starts charging at {string} for {int} minutes")
    public void customerStartsChargingAtForMinutes(String arg0, String arg1, int arg2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("the charging session for {string} at {string} is completed")
    public void theChargingSessionForAtIsCompleted(String arg0, String arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("customer {string} credit is reduced according to consumed energy")
    public void customerCreditIsReducedAccordingToConsumedEnergy(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("charger {string} status is {string}")
    public void chargerStatusIs(String arg0, String arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("customer {string} has credit {double}")
    public void customerHasCredit(String arg0, int arg1, int arg2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("an error")
    public void anError() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
