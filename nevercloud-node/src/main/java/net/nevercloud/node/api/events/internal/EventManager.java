package net.nevercloud.node.api.events.internal;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import com.google.common.base.Preconditions;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EventManager {

    private Map<Class<? extends Event>, Collection<ListenerMethod>> eventMethods = new HashMap<>();

    public EventManager registerListeners(Object... listeners) {
        for (Object listener : listeners) {
            this.registerListener(listener);
        }
        return this;
    }

    public EventManager registerListener(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                Parameter[] parameters = method.getParameters();
                Preconditions.checkArgument(parameters.length == 1, "length of parameters in event must be exactly 1");
                Parameter parameter = parameters[0];
                if (Event.class.isAssignableFrom(parameter.getType())) {
                    method.setAccessible(true);
                    Class<? extends Event> eventClass = (Class<? extends Event>) parameter.getType();
                    if (!this.eventMethods.containsKey(eventClass))
                        this.eventMethods.put(eventClass, new ArrayList<>());
                    this.eventMethods.get(eventClass).add(new ListenerMethod(listener, method));
                }
            }
        }
        return this;
    }

    public void unregisterAll() {
        this.eventMethods.clear();
    }

    public void unregister(Object listener) {
        for (Collection<ListenerMethod> value : this.eventMethods.values()) {
            Collection<ListenerMethod> remove = new ArrayList<>();
            for (ListenerMethod listenerMethod : value) {
                if (listenerMethod.getListener().equals(listener)) {
                    remove.add(listenerMethod);
                }
            }
            value.removeAll(remove);
        }
    }

    public Event callEvent(Event event) {
        if (this.eventMethods.containsKey(event.getClass())) {
            this.eventMethods.get(event.getClass()).forEach(listenerMethod -> listenerMethod.invoke(event));
        }
        if (this.eventMethods.containsKey(Event.class)) {
            this.eventMethods.get(Event.class).forEach(listenerMethod -> listenerMethod.invoke(event));
        }
        return event;
    }

}
