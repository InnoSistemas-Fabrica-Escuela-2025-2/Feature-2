package co.edu.udea.certificacion.innoSistemas.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class SetDateField implements Interaction {

    private final Target target;
    private final String value;

    public SetDateField(Target target, String value) {
        this.target = target;
        this.value = value;
    }

    public static SetDateField to(Target target, String value) {
        return new SetDateField(target, value);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebElement element = target.resolveFor(actor);
        JavascriptExecutor js = (JavascriptExecutor) BrowseTheWeb.as(actor).getDriver();
        
        // Use React's native value setter to trigger proper state update
        js.executeScript(
            "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
            "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
            "var event = new Event('input', { bubbles: true });" +
            "arguments[0].dispatchEvent(event);",
            element, value
        );
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Trigger change event
        js.executeScript(
            "var event = new Event('change', { bubbles: true });" +
            "arguments[0].dispatchEvent(event);",
            element
        );
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
