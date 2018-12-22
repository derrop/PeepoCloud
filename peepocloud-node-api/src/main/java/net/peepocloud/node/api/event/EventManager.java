package net.peepocloud.node.api.event;
/*
 * Created by Mc_Ruben on 17.12.2018
 */

public interface EventManager {

    /**
     * Registers new Listeners to this {@link EventManager}
     *
     * @param listeners the Listeners to register
     * @return this
     */
    EventManager registerListeners(Object... listeners);

    /**
     * Registers a new Listener to this {@link EventManager}
     *
     * @param listener the Listener to register
     * @return this
     */
    EventManager registerListener(Object listener);

    /**
     * Unregisters all registered Listeners in this {@link EventManager}
     */
    void unregisterAll();

    /**
     * Unregisters a registered Listener in this {@link EventManager}
     *
     * @param listener the Listener to unregister
     */
    void unregister(Object listener);

    /**
     * Calls an Event to all Listeners that are registered in this {@link EventManager}
     *
     * @param event the Event to call in each Listener in this {@link EventManager}
     * @return this
     */
    <T extends Event> T callEvent(T event);

}
