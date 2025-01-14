package io.github.orionlibs.orion_websocket_tools;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Utils
{
    public static void applyDelayInSeconds(int numberOfSeconds)
    {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        CompletableFuture<Void> delayedTask = CompletableFuture.runAsync(() -> {
                            System.out.println("Starting task...");
                        }).thenCompose(ignored -> delay(numberOfSeconds, TimeUnit.SECONDS, scheduler))
                        .thenRun(() -> System.out.println("Task completed after delay!"));
        delayedTask.join(); // Wait for the task to complete
        scheduler.shutdown();
    }


    private static CompletableFuture<Void> delay(long delay, TimeUnit unit, ScheduledExecutorService scheduler)
    {
        CompletableFuture<Void> future = new CompletableFuture<>();
        scheduler.schedule(() -> future.complete(null), delay, unit);
        return future;
    }
}
