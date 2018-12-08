package net.nevercloud.lib.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scheduler implements Runnable {

    /**
     * ThreadPool where the async tasks are being executed on
     */

    public static final ExecutorService POOL = Executors.newCachedThreadPool();

    /**
     * Describes if this scheduler runs
     */

    private boolean enabled = false;

    /**
     * Describes how often the tasks should be executed in a second
     */

    private int ticksPerSecond;

    /**
     * The current active tasks
     */

    private List<SchedulerTask> tasks = new ArrayList<>();

    /**
     * Object where will be waited on
     */

    private Object lock = new Object();

    public Scheduler(int ticksPerSecond) {
        this.ticksPerSecond = ticksPerSecond;
    }

    /**
     * Repeats an action
     *
     * @param runner the Runnable which will be repeated
     * @param startDelay the time in ticks before the repetition starts
     * @param repeatDelay the time in ticks between repetitions
     * @param async if the runner should be executed on another thread
     * @return the new task
     */

    private SchedulerTask repeat(Runnable runner, long startDelay, long repeatDelay, boolean async) {
        SchedulerTask task = new SchedulerTask(runner, startDelay, repeatDelay, async);
        this.tasks.add(task);
        return task;
    }

    /**
     * Delays an action
     *
     * @param runner the Runnable which will be delayed
     * @param startDelay the time in ticks before the runner starts
     * @param async if the runner should be executed on another thread
     * @return the new task
     */

    private SchedulerTask delay(Runnable runner, long startDelay, boolean async) {
        SchedulerTask task = new SchedulerTask(runner, startDelay, -1, async);
        this.tasks.add(task);
        return task;
    }

    /**
     * Executes an action in the next tick of the scheduler
     *
     * @param runner the Runnable which will be executed
     * @param async if the runner should be executed on another thread
     * @return the new task
     */

    private SchedulerTask execute(Runnable runner, boolean async) {
        SchedulerTask task = new SchedulerTask(runner, 1, -1, async);
        this.tasks.add(task);
        return task;
    }

    /**
     * Removes a task from the list so it won't be executed anymore
     *
     * @param task the task
     */

    private void cancelTask(SchedulerTask task) {
        this.tasks.remove(task);
    }

    /**
     * Starts this scheduler
     */

    @Override
    public void run() {
        this.enabled = true;
        while (this.enabled) {
            try {
                this.lock.wait(TimeUnit.SECONDS.toMillis(1) / this.ticksPerSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(SchedulerTask schedulerTask : new ArrayList<>(this.tasks)) { // new list, because of concurrent modification
                if(schedulerTask.isAsync())
                    POOL.execute(schedulerTask);
                else
                    schedulerTask.run();

                if(!schedulerTask.isRepeating())
                    this.cancelTask(schedulerTask);
            }

        }
    }

    /**
     * Disables this scheduler and unlocks the {@link Scheduler#lock} if i'ts waiting
     */

    public void disable() {
        this.tasks.clear();
        this.enabled = false;
        this.lock.notify();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getTicksPerSecond() {
        return ticksPerSecond;
    }

    public List<SchedulerTask> getTasks() {
        return tasks;
    }
}
