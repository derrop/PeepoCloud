package net.peepocloud.lib.serverselector;


import net.peepocloud.lib.scheduler.Scheduler;

public interface ServerSelector<Child extends ServerSelectorChild> {

    void handleStart(Scheduler scheduler);
    void update(Child child);

    boolean isEnabled();
    void setEnabled(boolean enabled);

    default void start(Scheduler scheduler) {
        if(!this.isEnabled()) {
            this.setEnabled(true);
            this.handleStart(scheduler);
        }
    }

}
