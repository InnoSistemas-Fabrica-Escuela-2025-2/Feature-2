package co.edu.udea.certificacion.innoSistemas.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitTime {

    static WaitTime waitTime;

    Integer millis;

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitTime.class);

    public WaitTime(Integer millis){

        try {

            Thread.sleep(millis);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Thread was interrupted during sleep", e);
        }

        this.millis=millis;

    }

    public static WaitTime putWaitTimeOf(Integer millis){

        waitTime = new WaitTime(millis);

        return waitTime;

    }
}