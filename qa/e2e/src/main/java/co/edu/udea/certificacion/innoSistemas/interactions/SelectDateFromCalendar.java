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
        // Click on the date input to open the calendar
        actor.attemptsTo(ClickOnElement.on(dateInput));
        
        // Wait for calendar to open
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Parse the date (YYYY-MM-DD)
        String[] parts = date.split("-");
        String year = parts[0];
        String month = parts[1];
        String day = parts[2];
        
        // Remove leading zero from day if present
        int dayNum = Integer.parseInt(day);
        
        // Click on the day in the calendar
        // The calendar shows days as button elements with the day number as text
        Target dayButton = Target.the("calendar day " + dayNum)
                .located(By.xpath("//button[not(contains(@class, 'text-muted-foreground')) and text()='" + dayNum + "']"));
        
        actor.attemptsTo(ClickOnElement.on(dayButton));
        
        // Wait for calendar to close
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
