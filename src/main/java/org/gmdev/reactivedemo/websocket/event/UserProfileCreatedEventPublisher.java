package org.gmdev.reactivedemo.websocket.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

@Slf4j
@Component
public class UserProfileCreatedEventPublisher
        implements ApplicationListener<UserProfileCreatedEvent>, Consumer<FluxSink<UserProfileCreatedEvent>> {

    private final Executor executor = Executors.newFixedThreadPool(3);
    private final BlockingQueue<UserProfileCreatedEvent> queue = new LinkedBlockingDeque<>();

    @Override
    public void onApplicationEvent(@Nullable UserProfileCreatedEvent event) {
        if (event == null) return;

        log.info("Received user created event");
        queue.offer(event);
    }

    @Override
    public void accept(FluxSink<UserProfileCreatedEvent> sink) {
        executor.execute(() -> {
            while (true) {
                try {
                    UserProfileCreatedEvent event = queue.take();
                    sink.next(event);
                    log.info("Accepted user created event");
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
            }
        });
    }


}
