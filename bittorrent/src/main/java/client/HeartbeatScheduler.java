package client;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 * <p>
 * Brought from project3 which at the same time was brought from
 * Martin Fowler's blog
 */
public class HeartbeatScheduler {
    long heartbeatIntervalMs;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private final Runnable action;
    private ScheduledFuture<?> scheduledTask;

    HeartbeatScheduler(Runnable action, long heartbeatIntervalMs) {
        this.action = action;
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    HeartbeatScheduler start() {
        scheduledTask = executor.scheduleWithFixedDelay(
                action,
                heartbeatIntervalMs,
                heartbeatIntervalMs,
                TimeUnit.MILLISECONDS);
        return this;
    }

    void cancel() {
        if (scheduledTask != null)
            scheduledTask.cancel(true);

        executor.shutdownNow();
    }
}
