package com.fde.google_drive_organizer.domain.suggest_target_folder_progress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProgressSubscribers {

    private static final Logger log = LoggerFactory.getLogger(ProgressSubscribers.class);

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void add(SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void remove(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    public boolean isEmpty() {
        return emitters.isEmpty();
    }

    public void broadcast(ProgressEvent event) {
        String data = toJson(event);
        boolean terminal = event.step() == ProgressStep.DONE || event.step() == ProgressStep.FAILED;
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("progress").data(data));
                if (terminal) {
                    emitter.complete();
                }
            } catch (IOException e) {
                log.warn("Failed to send SSE event, removing emitter: {}", e.getMessage());
                emitters.remove(emitter);
            }
        }
    }

    private String toJson(ProgressEvent event) {
        String message = event.message()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
        StringBuilder json = new StringBuilder("{\"step\":\"").append(event.step().name())
                .append("\",\"message\":\"").append(message).append("\"");
        if (event.targetFolder().name().value() != null) {
            String folder = event.targetFolder().name().value()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"");
            json.append(",\"targetFolder\":\"").append(folder).append("\"");
        }
        json.append("}");
        return json.toString();
    }
}
