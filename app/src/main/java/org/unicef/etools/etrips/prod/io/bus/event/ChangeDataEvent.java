package org.unicef.etools.etrips.prod.io.bus.event;


public class ChangeDataEvent<T> extends Event {

    private int eventType;

    private T updateEventData;

    public ChangeDataEvent(int eventType) {
        this.eventType = eventType;
    }

    public ChangeDataEvent(int eventType, T navigationEventData) {
        this.eventType = eventType;
        this.updateEventData = navigationEventData;
    }

    public ChangeDataEvent(int eventType, String subscriber) {
        this.eventType = eventType;
        setSubscriber(subscriber);
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public T getUpdatenEventData() {
        return updateEventData;
    }

    public void setUpdateEventData(T navigationEventData) {
        this.updateEventData = navigationEventData;
    }
}