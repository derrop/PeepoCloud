package net.peepocloud.node.api.event;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peepocloud.node.api.addon.Addon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
class ListenerMethod {
    private Object listener;
    private Method method;
    private Addon addon;
    private byte priority;

    void invoke(Event event) {
        try {
            this.method.invoke(this.listener, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    String name() {
        return addon == null ?
                listener.getClass().getName() + ":" + method.getName() :
                listener.getClass().getName() + ":" + method.getName() + "@" + addon.getAddonConfig().getName();
    }

}
