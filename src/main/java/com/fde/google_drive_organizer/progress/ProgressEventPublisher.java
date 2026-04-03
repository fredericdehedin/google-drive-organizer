package com.fde.google_drive_organizer.progress;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProgressEventPublisher {

    private final ConcurrentHashMap<FileId, ProgressSubscribers> subscribers = new ConcurrentHashMap<>();

    public void subscribe(FileId fileId, SseEmitter emitter) {
        ProgressSubscribers subs = subscribers.computeIfAbsent(fileId, id -> new ProgressSubscribers());
        subs.add(emitter);
        emitter.onCompletion(() -> unsubscribe(fileId, emitter));
        emitter.onTimeout(() -> unsubscribe(fileId, emitter));
        emitter.onError(e -> unsubscribe(fileId, emitter));
    }

    public void publish(FileId fileId, ProgressStep step, String message) {
        ProgressSubscribers subs = subscribers.get(fileId);
        if (subs != null) {
            subs.broadcast(new ProgressEvent(fileId, step, message, Instant.now()));
        }
    }

    private void unsubscribe(FileId fileId, SseEmitter emitter) {
        ProgressSubscribers subs = subscribers.get(fileId);
        if (subs != null) {
            subs.remove(emitter);
            if (subs.isEmpty()) {
                subscribers.remove(fileId);
            }
        }
    }
}
