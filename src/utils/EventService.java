package utils;

import models.board.BoardEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by Isaac on 11/5/17.
 */
public class EventService {

    private static EventService instance;
    private Stack<Event> eventQueue;
    private ArrayList<EventListener> listeners;

    public enum EventType {
        DRAW,
        ATTACK,
        ABILITY,
        TARGET
    }

    private EventService() {
        eventQueue = new Stack<>();
        listeners = new ArrayList<>();
    }

    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }

    public Stack<Event> getQueue() {
        return eventQueue;
    }

    public void queueEvent(Event event) {
        eventQueue.push(event);
    }

    public void queueAndExecuteSingleEvent(EventType type, String message, BoardEntity ... relevantEntities) {
        queueAndExecuteSingleEvent(new Event(type, message, relevantEntities));
    }

    public void queueAndExecuteSingleEvent(EventType type, BoardEntity ... relevantEntities) {
        queueAndExecuteSingleEvent(new Event(type, relevantEntities));
    }

    public void queueAndExecuteSingleEvent(Event event) {
        queueEvent(event);
        executeEvents();
    }

    public void registerListener(BoardEntity listeningEntity, EventType type) {
        listeners.add(new EventListener(listeningEntity, type));
    }

    public void removeListenersForEntity(BoardEntity entity) {
        List<EventListener> toRemove = new ArrayList<>();
        for (EventListener listener : listeners) {
            if (entity.equals(listener.entity)) {
                toRemove.add(listener);
            }
        }
        listeners.removeAll(toRemove);
    }

    private void executeEvents() {
        Event event;
        while (!eventQueue.isEmpty()) {
            event = eventQueue.pop();
            event.execute();
            for (EventListener listener : listeners) {
                if (event.getType().equals(listener.type)) {
                    listener.entity.notify(event);
                }
            }
        }
    }

    public class Event {
        private EventType type;
        private String message;
        private List<BoardEntity> relevantEntities = new ArrayList<>();

        public Event(EventType type, BoardEntity ... entities) {
            this(type, null, entities);
        }

        public Event(EventType type, String message, BoardEntity ... entities) {
            this.type = type;
            this.message = message;
            this.relevantEntities.addAll(Arrays.asList(entities));
        }

        public EventType getType() {
            return type;
        }

        public List<BoardEntity> getRelevantEntities() {
            return relevantEntities;
        }

        public void execute() {
            if (message != null) {
                LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, message);
            }
        }
    }

    private class EventListener {
        private BoardEntity entity;
        private EventType type;

        EventListener(BoardEntity entity, EventType type) {
            this.entity = entity;
            this.type = type;
        }
    }
}
