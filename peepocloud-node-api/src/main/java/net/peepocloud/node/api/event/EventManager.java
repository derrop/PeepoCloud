package net.peepocloud.node.api.event;
/*
 * Created by Mc_Ruben on 17.12.2018
 */

import net.peepocloud.node.api.addon.Addon;

public interface EventManager {

    /**
     * Registers new Listeners to this {@link EventManager}
     *
     * @param addon the addon with which the Listeners are identified
     * @param listeners the Listeners to register
     * @return this
     */
    EventManager registerListeners(Addon addon, Object... listeners);

    /**
     * Registers a new Listener to this {@link EventManager}
     *
     * @param addon the addon with which the Listener is identified
     * @param listener the Listener to register
     * @return this
     */
    EventManager registerListener(Addon addon, Object listener);

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
     * Unregisters all registered Listeners in this {@link EventManager} which were registered by the given {@link Addon}
     *
     * @param addon the addon to remove the Listeners from
     */
    void unregisterAll(Addon addon);

    /**
     * Calls an Event to all Listeners that are registered in this {@link EventManager}
     *
     * @param event the Event to call in each Listener in this {@link EventManager}
     * @return this
     */
    <T extends Event> T callEvent(T event);

}
