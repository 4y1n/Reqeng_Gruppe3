import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class pricing_steps {
    @When("owner sets the price for location {string} to {double}")
    public void ownerSetsThePriceForLocationTo(String arg0, int arg1, int arg2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("a location {string} exists")
    public void aLocationExists(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("owner updates the price for location {string} to {double}")
    public void ownerUpdatesThePriceForLocationTo(String arg0, int arg1, int arg2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("the price of location {string} is {double}")
    public void thePriceOfLocationIs(String arg0, int arg1, int arg2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("owner reads the price for location {string}")
    public void ownerReadsThePriceForLocation(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
