package net.nevercloud.node.api.events;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import net.nevercloud.node.api.event.internal.Event;
import net.nevercloud.node.api.event.internal.EventHandler;
import net.nevercloud.node.api.event.internal.EventManager;
import org.junit.Test;

public class EventManagerTest {
    @Test
    public void eventManagerTest() {
        EventManager eventManager = new EventManager();
        eventManager.registerListener(new Object() {
            @EventHandler
            public void test(SomeTestEvent event) {
                System.out.println("some test event");
            }

            @EventHandler
            public void test(TestEvent event) {
                System.out.println("test event");
            }

            @EventHandler
            public void test(Event event) {
                System.out.println("event");
            }
        });

        eventManager.callEvent(new SomeTestEvent());
        System.out.println(" ");
        eventManager.callEvent(new TestEvent());
    }
}
