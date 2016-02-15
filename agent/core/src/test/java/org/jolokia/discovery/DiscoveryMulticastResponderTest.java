package org.jolokia.discovery;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import org.jolokia.restrictor.AllowAllRestrictor;
import org.jolokia.util.*;
import org.json.simple.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author roland
 * @since 04.02.14
 */
public class DiscoveryMulticastResponderTest {

    @Test
    public void simple() throws IOException, InterruptedException {
        System.out.println("=================================================");
        if (!NetworkUtil.isMulticastSupported()) {
            throw new SkipException("No multicast interface found, skipping test ");
        }
        AgentDetailsHolder holder = new TestAgentsDetailsHolder();
        DiscoveryMulticastResponder responder =
                new DiscoveryMulticastResponder(holder,new AllowAllRestrictor(),new LogHandler.StdoutLogHandler(true));
        responder.start();
        // Warming up
        Thread.sleep(1000);
        JolokiaDiscovery discovery = new JolokiaDiscovery("test",new LogHandler.StdoutLogHandler(true));
        try {
            List<JSONObject> msgs = discovery.lookupAgents();
        System.out.println("=================================================");
        assertTrue(msgs.size() > 0);
        } catch (UnknownHostException exp) {
            throw new SkipException("Skipping test because no single multicast request could be send on any interface");
        } finally {
            responder.stop();
        }

    }

    private class TestAgentsDetailsHolder implements AgentDetailsHolder {

        AgentDetails details = new AgentDetails(UUID.randomUUID().toString());

        public AgentDetails getAgentDetails() {
            return details;
        }
    }
}
