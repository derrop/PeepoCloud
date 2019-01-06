package net.peepocloud.node.api.event;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import com.google.common.base.Preconditions;
import net.peepocloud.node.PeepoCloudNode;
import net.peepocloud.node.api.addon.Addon;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultEventManager implements EventManager {

    private Map<Class<? extends Event>, List<ListenerMethod>> eventMethods = new HashMap<>();

    public DefaultEventManager registerListeners(Object... listeners) {
        for (Object listener : listeners) {
            this.registerListener(listener);
        }
        return this;
    }

    public DefaultEventManager registerListener(Object listener) {
        this.register(null, listener);
        return this;
    }

    @Override
    public EventManager registerListeners(Addon addon, Object... listeners) {
        Preconditions.checkNotNull(addon, "addon");
        for (Object listener : listeners) {
            this.registerListener(addon, listener);
        }
        return this;
    }

    @Override
    public EventManager registerListener(Addon addon, Object listener) {
        Preconditions.checkNotNull(addon, "addon");
        this.register(addon, listener);
        return this;
    }

    private void register(Addon addon, Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                Parameter[] parameters = method.getParameters();
                Preconditions.checkArgument(parameters.length == 1, "length of parameters in an EventHandler must be exactly 1");
                Parameter parameter = parameters[0];
                if (Event.class.isAssignableFrom(parameter.getType())) {
                    EventHandler handler = method.getAnnotation(EventHandler.class);
                    byte priority = handler.priority();

                    method.setAccessible(true);
                    Class<? extends Event> eventClass = (Class<? extends Event>) parameter.getType();
                    if (!this.eventMethods.containsKey(eventClass))
                        this.eventMethods.put(eventClass, new ArrayList<>());
                    this.eventMethods.get(eventClass).add(new ListenerMethod(listener, method, addon, priority));
                    this.eventMethods.get(eventClass).sort(Comparator.comparingInt(ListenerMethod::getPriority));
                }
            }
        }
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

    @Override
    public void unregisterAll(Addon addon) {
        for (Map.Entry<Class<? extends Event>, List<ListenerMethod>> entry : new HashMap<>(this.eventMethods).entrySet()) {
            Collection<ListenerMethod> value = entry.getValue();
            value.stream().filter(listenerMethod -> listenerMethod.getAddon().getAddonConfig().getName().equals(addon.getAddonConfig().getName())).collect(Collectors.toList()).forEach(value::remove);
            if (value.isEmpty()) {
                this.eventMethods.remove(entry.getKey());
            }
        }
    }

    public <T extends Event> T callEvent(T event) {
        PeepoCloudNode.getInstance().debug("Calling event " + event.getClass().getName());
        StringBuilder builder = new StringBuilder();
        if (this.eventMethods.containsKey(event.getClass())) {
            this.eventMethods.get(event.getClass()).forEach(listenerMethod -> {
                listenerMethod.invoke(event);
                builder.append(listenerMethod.name()).append(',');
            });
        }
        Class<?> superClass = null;
        while ((superClass = superClass != null ? superClass.getSuperclass() : event.getClass().getSuperclass()) != null) {
            if (this.eventMethods.containsKey(superClass)) {
                this.eventMethods.get(superClass).forEach(listenerMethod -> {
                    listenerMethod.invoke(event);
                    builder.append(listenerMethod.name()).append(',');
                });
            }
        }
        PeepoCloudNode.getInstance().debug("Called event " + event.getClass().getName() + " to listeners: " +
                (builder.length() <= 2 ? "no listener" : builder.substring(0, builder.length() - 1)));
        return event;
    }

}
