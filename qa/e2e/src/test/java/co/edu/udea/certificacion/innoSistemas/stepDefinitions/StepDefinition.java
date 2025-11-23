package co.edu.udea.certificacion.innoSistemas.stepDefinitions;

import co.edu.udea.certificacion.innoSistemas.tasks.OpenThe;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.googlePage;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import org.openqa.selenium.WebDriver;

public class StepDefinition {

    public final Actor student = Actor.named("student");
    @Managed(driver = "chrome", uniqueSession = true)
    public WebDriver theDriver;

    @Before
    public void config(){
        student.can(BrowseTheWeb.with(theDriver));
        OnStage.setTheStage(new OnlineCast());
        OnStage.theActorCalled("user");
    }

    @Given("This method is responsible for parameterizing the instantiation of chromedriver")    public void thisMethodIsResponsibleForParameterizingTheInstantiationOfChromedriver() {         OnStage.theActorInTheSpotlight().wasAbleTo(Open.browserOn().thePageNamed("pages.swaglabsUrl"));    }

    @Given("I am in the Google page")
    public void iAmInTheGooglePage() {
        student.attemptsTo(OpenThe.navigator(new googlePage()));
    }

    @When("I type Udea words on google")
    public void iTypeUdeaWordsOnGoogle() {
        // TODO generate actions that make this a reality
    }

    @Then("I can see the university's official page")
    public void iCanSeeTheUniversitySOfficialPage() {
        // TODO generate actions that make this a reality
    }
}
