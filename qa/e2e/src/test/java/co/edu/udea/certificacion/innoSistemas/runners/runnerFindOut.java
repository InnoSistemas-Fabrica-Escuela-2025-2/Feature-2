package co.edu.udea.certificacion.innoSistemas.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "co.edu.udea.certificacion.innoSistemas.stepDefinitions",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class runnerFindOut {}