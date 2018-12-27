package net.peepocloud.lib.serverselector;


import net.peepocloud.lib.scheduler.Scheduler;

public interface ServerSelector<Child extends ServerSelectorChild> {

    void start(Scheduler scheduler);
    void update(Child child);


}
