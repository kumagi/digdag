package io.digdag.spi;

import java.util.UUID;
import java.time.Instant;
import java.time.ZoneId;
import org.immutables.value.Value;
import com.google.common.base.Optional;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.digdag.client.config.Config;

@Value.Immutable
@JsonSerialize(as = ImmutableTaskRequest.class)
@JsonDeserialize(as = ImmutableTaskRequest.class)
public abstract class TaskRequest
{
    public abstract int getSiteId();

    public abstract int getProjectId();

    public abstract Optional<String> getProjectName();

    public abstract String getWorkflowName();

    public abstract Optional<String> getRevision();

    public abstract long getTaskId();

    public abstract long getAttemptId();

    public abstract long getSessionId();

    public abstract Optional<String> getRetryAttemptName();

    public abstract String getTaskName();

    public abstract String getQueueName();

    public abstract String getLockId();

    public abstract int getPriority();

    public abstract ZoneId getTimeZone();

    public abstract UUID getSessionUuid();

    public abstract Instant getSessionTime();

    public abstract Instant getCreatedAt();

    public abstract Config getLocalConfig();

    public abstract Config getConfig();

    public abstract Config getLastStateParams();

    public static ImmutableTaskRequest.Builder builder()
    {
        return ImmutableTaskRequest.builder();
    }

    public static TaskRequest withLockId(TaskRequest source, String lockId)
    {
        return ImmutableTaskRequest.builder()
            .from(source)
            .lockId(lockId)
            .build();
    }
}
