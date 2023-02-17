package org.gmdev.reactivedemo.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.*;
import java.util.function.Consumer;

@Component
public class UserProfileCreatedEventPublisher
        implements ApplicationListener<UserProfileCreatedEvent>, Consumer<FluxSink<UserProfileCreatedEvent>> {

    private final Executor executor;
    private final BlockingQueue<UserProfileCreatedEvent> queue = new LinkedBlockingDeque<>();

    public UserProfileCreatedEventPublisher(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onApplicationEvent(UserProfileCreatedEvent event) {
        queue.offer(event);
    }

    @Override
    public void accept(FluxSink<UserProfileCreatedEvent> sink) {
        executor.execute(() -> {
            while (true)
                try {
                    UserProfileCreatedEvent event = queue.take();
                    sink.next(event);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }


}
