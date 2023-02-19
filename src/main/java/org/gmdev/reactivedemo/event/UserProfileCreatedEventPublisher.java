package org.gmdev.reactivedemo.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
@Component
public class UserProfileCreatedEventPublisher
        implements ApplicationListener<UserProfileCreatedEvent>, Consumer<FluxSink<UserProfileCreatedEvent>> {

    private final Executor executor;
    private final BlockingQueue<UserProfileCreatedEvent> queue = new LinkedBlockingDeque<>();

    @Autowired
    public UserProfileCreatedEventPublisher(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onApplicationEvent(@Nullable UserProfileCreatedEvent event) {
        if (event == null) return;

        log.info("Received user created event");
        queue.offer(event);
    }

    @Override
    public void accept(FluxSink<UserProfileCreatedEvent> sink) {
        executor.execute(() -> {
            while (true)
                try {
                    UserProfileCreatedEvent event = queue.take();
                    sink.next(event);
                    log.info("Accepted user created event");
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }


}
