package co.edu.udea.certificacion.innoSistemas.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class SelectDateFromCalendar implements Interaction {

    private final Target dateInput;
    private final String date; // Format: YYYY-MM-DD

    public SelectDateFromCalendar(Target dateInput, String date) {
        this.dateInput = dateInput;
        this.date = date;
    }

    public static SelectDateFromCalendar selectDate(String date, Target dateInput) {
        return new SelectDateFromCalendar(dateInput, date);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(ClickOnElement.on(dateInput));
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        String[] parts = date.split("-");
        String day = parts[2];
        
        int dayNum = Integer.parseInt(day);
        
        Target dayButton = Target.the("calendar day " + dayNum)
                .located(By.xpath("//button[not(contains(@class, 'text-muted-foreground')) and text()='" + dayNum + "']"));
        
        actor.attemptsTo(ClickOnElement.on(dayButton));
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
