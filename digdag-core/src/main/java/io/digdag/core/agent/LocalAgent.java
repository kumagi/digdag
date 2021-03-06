package io.digdag.core.agent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.google.common.base.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.digdag.spi.TaskRequest;
import io.digdag.spi.TaskQueueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalAgent
        implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(LocalAgent.class);

    private final AgentConfig config;
    private final AgentId agentId;
    private final TaskQueueClient queue;
    private final OperatorManager runner;
    private final ExecutorService executor;
    private volatile boolean stop = false;

    public LocalAgent(AgentConfig config, AgentId agentId,
            TaskQueueClient queue, OperatorManager runner)
    {
        this.agentId = agentId;
        this.config = config;
        this.queue = queue;
        this.runner = runner;
        if (config.getMaxThreads() > 0) {
            this.executor = Executors.newFixedThreadPool(
                    config.getMaxThreads(),
                    new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("task-thread-%d")
                    .build());
        }
        else {
            this.executor = Executors.newCachedThreadPool(
                    new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("task-thread-%d")
                    .build());
        }
    }

    public void stop()
    {
        stop = true;
    }

    public void shutdown()
    {
        executor.shutdown();
        // TODO wait for shutdown completion?
    }

    @Override
    public void run()
    {
        while (!stop) {
            try {
                // TODO implement task heartbeat that calls queue.taskHeartbeat using a background thread
                List<TaskRequest> reqs = queue.lockSharedTasks(3, agentId.toString(), config.getLockRetentionTime(), 1000);
                for (TaskRequest req : reqs) {
                    executor.submit(() -> {
                        try {
                            runner.run(req);
                        }
                        catch (Throwable t) {
                            logger.error("Uncaught exception. Task heartbeat for at-least-once task execution is not implemented yet.", t);
                        }
                    });
                }
            }
            catch (Throwable t) {
                logger.error("Uncaught exception", t);
            }
        }
    }
}
