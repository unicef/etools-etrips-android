package org.unicef.etools.etrips.prod.io.bus.event;

public abstract class Event {

    private String subscriber;

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public static class EventType {

        public static class Api {
            public static final int LOGIN_COMPLETED = 100;
            public static final int TRIPS_LOADED = 101;
            public static final int REPORT_SUBMITTED = 102;
            public static final int PROFILE_LOADED = 103;
            public static final int REPORT_FILES_UPLOADED = 104;
            public static final int TRIP_LOADED = 105;
            public static final int STATIC_DATA_LOADED = 106;
            public static final int STATIC_DATA_2_LOADED = 107;
            public static final int USERS_LOADED = 108;
            public static final int TRIP_STATUS_CHANGED = 109;
            public static final int TRIP_SYNCED = 110;
            public static final int ACTION_POINTS_LOADED = 111;
            public static final int ACTION_POINT_UPDATED = 112;
            public static final int ACTION_POINT_ADDED = 113;

            public class Error {
                public static final int UNKNOWN = 200;
                public static final int NO_NETWORK = 201;
                public static final int PAGE_NOT_FOUND = 202;
                public static final int BAD_REQUEST = 203;
                public static final int UNAUTHORIZED = 204;
                public static final int SERVER_TIMEOUT = 205;
                public static final int CONNECTION_REFUSED = 206;
            }
        }

        public static class Network {
            public static final int CONNECTED = 300;
            public static final int DISCONNECTED = 301;
        }

    }

}
