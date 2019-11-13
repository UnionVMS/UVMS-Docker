package eu.europa.ec.fisheries.uvms.docker.validation.streamcollector;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.AuthorizationHeaderWebTarget;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.rules.CustomRulesTestHelper;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import java.util.ArrayList;
import java.util.List;

public class StreamCollectorIT extends AbstractRest {

    private static MovementHelper movementHelper;
    private static MessageHelper messageHelper;
    private static String eventStream = "";
    private static String eventNameStream = "";

    @BeforeClass
    public static void setup() throws JMSException {
        movementHelper = new MovementHelper();
        messageHelper = new MessageHelper();
    }

    @Before
    public void init(){
        eventStream = "";
        eventNameStream = "";
    }

    @AfterClass
    public static void cleanup() {
        movementHelper.close();
        messageHelper.close();
    }

    @Test(timeout = 15000)
    public void sseTest() throws Exception {
        List<String> sseMessages = new ArrayList<>();
        String movementGuid;

        try (SseEventSource source = getSseStream()) {
            source.register((inboundSseEvent) -> {
                sseMessages.add("Name: " + (inboundSseEvent.getName() == null ? null : inboundSseEvent.getName())  + " Data: " + inboundSseEvent.readData());
            });
            source.open();


            movementGuid = CustomRulesTestHelper.createRuleAndGetMovementGuid();


            while(sseMessages.size() < 5) {         // 2 welcoming messages and then 1 for creating the asset, 1 for creating the movement and then lastly one for creating the ticket
                Thread.sleep(100);
                System.out.println(sseMessages.size());
            }
        }
        assertThat(sseMessages.size(), CoreMatchers.is(5));
        assertTrue(sseMessages.stream().anyMatch(s -> s.contains("Updated Asset")));
        assertTrue(sseMessages.stream().anyMatch(s -> s.contains("Movement") && s.contains(movementGuid)));
        assertTrue(sseMessages.stream().anyMatch(s -> s.contains("Ticket") && s.contains(movementGuid)));
    }

    public static SseEventSource getSseStream() {
        WebTarget target = getWebTarget().path("stream-collector/rest/sse/subscribe");
        AuthorizationHeaderWebTarget jwtTarget = new AuthorizationHeaderWebTarget(target, getValidJwtToken());
        return SseEventSource.
                target(jwtTarget).build();
    }


}
