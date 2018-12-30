package net.peepocloud.lib.scheduler;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scheduler implements Runnable {

    /**
     * ThreadPool where the async tasks are being executed on
     */

    private ExecutorService threadPool;

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

    private final Object lock = new Object();


    public Scheduler() {
        this(20);
    }

    public Scheduler(int ticksPerSecond) {
        this(ticksPerSecond,  Executors.newCachedThreadPool());
    }

    public Scheduler(ExecutorService threadPool) {
        this(20, threadPool);
    }

    public Scheduler(int ticksPerSecond, ExecutorService threadPool) {
        this.ticksPerSecond = ticksPerSecond;
        this.threadPool = threadPool;
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

    public SchedulerTask repeat(Runnable runner, long startDelay, long repeatDelay, boolean async) {
        Preconditions.checkArgument(repeatDelay > 0, "repeatDelay has to be at least 1");
        SchedulerTask task = new SchedulerTask(this.threadPool, runner, startDelay, repeatDelay, async);
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

    public SchedulerTask delay(Runnable runner, long startDelay, boolean async) {
        Preconditions.checkArgument(startDelay > 0, "startDelay has to be at least 1");
        SchedulerTask task = new SchedulerTask(this.threadPool, runner, startDelay, -1, async);
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

    public SchedulerTask execute(Runnable runner, boolean async) {
        SchedulerTask task = new SchedulerTask(this.threadPool, runner, 1, -1, async);
        this.tasks.add(task);
        return task;
    }

    /**
     * Removes a task from the list so it won't be executed anymore
     *
     * @param task the task
     */

    public void cancelTask(SchedulerTask task) {
        this.tasks.remove(task);
    }

    /**
     * Removes all tasks from the list so they won't be executed anymore
     */

    public void cancelAllTasks() {
        this.tasks.clear();
    }

    /**
     * Starts this scheduler
     */

    @Override
    public void run() {
        this.enabled = true;
        while (this.enabled) {
            try {
                synchronized (this.lock) {
                    this.lock.wait(TimeUnit.SECONDS.toMillis(1) / this.ticksPerSecond);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(this.tasks.isEmpty())
                continue;

            for(SchedulerTask schedulerTask : new ArrayList<>(this.tasks)) { // new list because of concurrent modification
                schedulerTask.run();

                if(!schedulerTask.isRepeating() && schedulerTask.hasBeenExecuted())
                    this.cancelTask(schedulerTask);
            }

        }
    }

    /**
     * Disables this scheduler and unlocks the {@link Scheduler#lock} if it's waiting
     */

    public void disable() {
        this.tasks.clear();
        this.enabled = false;
        this.threadPool.shutdown();
        synchronized (this.lock) {
            this.lock.notify();
        }
    }

    public ExecutorService getThreadPool() {
        return threadPool;
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
