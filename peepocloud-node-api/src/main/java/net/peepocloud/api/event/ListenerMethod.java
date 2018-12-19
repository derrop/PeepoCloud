package net.peepocloud.api.event;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import lombok.*;
import net.peepocloud.api.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
class ListenerMethod {
    private Object listener;
    private Method method;

    void invoke(Event event) {
        try {
            this.method.invoke(this.listener, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
