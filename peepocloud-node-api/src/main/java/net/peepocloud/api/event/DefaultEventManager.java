package net.peepocloud.api.event;
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

public class DefaultEventManager implements EventManager {

    private Map<Class<? extends Event>, Collection<ListenerMethod>> eventMethods = new HashMap<>();

    public DefaultEventManager registerListeners(Object... listeners) {
        for (Object listener : listeners) {
            this.registerListener(listener);
        }
        return this;
    }

    public DefaultEventManager registerListener(Object listener) {
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

    public <T extends Event> T callEvent(T event) {
        if (this.eventMethods.containsKey(event.getClass())) {
            this.eventMethods.get(event.getClass()).forEach(listenerMethod -> listenerMethod.invoke(event));
        }
        Class<?> superClass = null;
        while ((superClass = superClass != null ? superClass.getSuperclass() : event.getClass().getSuperclass()) != null) {
            if (this.eventMethods.containsKey(superClass)) {
                this.eventMethods.get(superClass).forEach(listenerMethod -> listenerMethod.invoke(event));
            }
        }
        return event;
    }

}
