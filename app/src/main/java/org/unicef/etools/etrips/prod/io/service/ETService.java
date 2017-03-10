package org.unicef.etools.etrips.prod.io.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.ActionPointStatus;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.Data;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Data2;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.db.entity.user.User;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.rest.entity.HttpConnection;
import org.unicef.etools.etrips.prod.io.rest.url_connection.HttpRequestManager;
import org.unicef.etools.etrips.prod.io.rest.url_connection.RestHttpClient;
import org.unicef.etools.etrips.prod.io.rest.util.APIUtil;
import org.unicef.etools.etrips.prod.io.rest.util.HttpErrorUtil;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.Preference;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmList;

import static com.google.common.base.Charsets.UTF_8;
import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.PROFILE;


public class ETService extends Service {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ETService.class.getSimpleName();

    private class Extra {
        static final String URL = "URL";
        static final String SUBSCRIBER = "SUBSCRIBER";
        static final String POST_ENTITY = "POST_ENTITY";
        static final String REQUEST_TYPE = "REQUEST_TYPE";
        static final String REQUEST_MODE = "REQUEST_MODE";
        static final String BUNDLE_DATA = "BUNDLE_DATA";
    }

    // ===========================================================
    // Fields
    // ===========================================================

    public static final int THREAD_POOL_SIZE = 5;
    private ExecutorService mExecutorService;

    // ===========================================================
    // Constructors
    // ===========================================================
    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Helper methods
    // ===========================================================

    public static void start(Context context, String subscriber, String url,
                             Bundle bundle, String postEntity, int requestType) {
        Intent intent = new Intent(context, ETService.class);
        intent.putExtra(Extra.SUBSCRIBER, subscriber);
        intent.putExtra(Extra.URL, url);
        intent.putExtra(Extra.BUNDLE_DATA, bundle);
        intent.putExtra(Extra.POST_ENTITY, postEntity);
        intent.putExtra(Extra.REQUEST_TYPE, requestType);
        context.startService(intent);
    }

    public static void start(Context context, String subscriber, String url, String postEntity,
                             int requestType) {
        Intent intent = new Intent(context, ETService.class);
        intent.putExtra(Extra.SUBSCRIBER, subscriber);
        intent.putExtra(Extra.URL, url);
        intent.putExtra(Extra.POST_ENTITY, postEntity);
        intent.putExtra(Extra.REQUEST_TYPE, requestType);
        context.startService(intent);
    }

    public static void start(Context context, String subscriber, String url, int requestType) {
        Intent intent = new Intent(context, ETService.class);
        intent.putExtra(Extra.SUBSCRIBER, subscriber);
        intent.putExtra(Extra.URL, url);
        intent.putExtra(Extra.REQUEST_TYPE, requestType);
        context.startService(intent);
    }

    class RunnableTask implements Runnable {

        int startId;
        Intent intent;

        RunnableTask(Intent intent, int startId) {
            this.startId = startId;
            this.intent = intent;
        }

        public void run() {

            Log.w(LOG_TAG, "run");

            Bundle bundle = intent.getExtras().getBundle(Extra.BUNDLE_DATA);
            String url = intent.getExtras().getString(Extra.URL);
            String postEntity = intent.getExtras().getString(Extra.POST_ENTITY);
            String subscriber = intent.getExtras().getString(Extra.SUBSCRIBER);
            int requestType = intent.getExtras().getInt(Extra.REQUEST_TYPE);
            if (BuildConfig.isDEBUG) Log.i(LOG_TAG, requestType + Constant.Symbol.SPACE + url);

            switch (requestType) {
                case HttpRequestManager.RequestType.LOGIN:
                    if (BuildConfig.isDEBUG) {
                        loginInStagingRequest(url, postEntity, subscriber);
                    } else {
                        loginInProdRequest(url, postEntity, subscriber);
                    }
                    break;

                case HttpRequestManager.RequestType.GET_MY_TRIPS:
                    myTripsRequest(url, subscriber);
                    break;

                case HttpRequestManager.RequestType.GET_SUPERVISED_TRIPS:
                    supervisedTripsRequest(url, subscriber);
                    break;

                case HttpRequestManager.RequestType.GET_TRIP:
                    tripRequest(url, subscriber);
                    break;

                case HttpRequestManager.RequestType.GET_STATIC_DATA:
                    staticDataRequest(url, subscriber);
                    break;

                case HttpRequestManager.RequestType.GET_STATIC_DATA_2:
                    staticData2Request(url, subscriber);
                    break;

                case HttpRequestManager.RequestType.GET_USERS:
                    usersRequest(url, subscriber);
                    break;
            }

            stopSelf(startId);
        }
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mExecutorService.execute(new RunnableTask(intent, startId));
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void loginInStagingRequest(String url, String data, String subscriber) {
        HttpConnection httpConnection = HttpRequestManager.executeRequest(
                this,
                RestHttpClient.RequestMethod.POST,
                url,
                null,
                data
        );

        if (httpConnection.isHttpConnectionSucceeded()) {
            // parse token
            try {
                JsonObject rootObject = new JsonParser().parse(
                        httpConnection.getHttpResponseBody().toString()).getAsJsonObject();
                JsonElement tokenElement = rootObject.get(Constant.Json.TOKEN_KEY);
                String token = tokenElement.getAsString();

                if (token != null) {
                    if (BuildConfig.isDEBUG) Log.i(LOG_TAG, token);

                    // drop certain user data
                    Preference.getInstance(this).setUserName(null);
                    Preference.getInstance(this).setUserId(0);

                    // save token in prefs
                    Preference.getInstance(this).setUserToken(token);

                    // retrieve profile data from server
                    profileRequest(APIUtil.getURL(PROFILE), subscriber);

                } else {
                    handleFailedConnection(subscriber, httpConnection);
                }

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                handleFailedConnection(subscriber, httpConnection);
            }

        } else {
            if (BuildConfig.isDEBUG) Log.e(LOG_TAG, httpConnection.getHttpConnectionMessage()
                    + Constant.Symbol.SPACE + httpConnection.getHttpConnectionCode());
            handleFailedConnection(subscriber, httpConnection);
        }
    }

    private void loginInProdRequest(String url, String data, String subscriber) {
        HttpConnection httpConnection = HttpRequestManager.executeRequest(
                this,
                true,
                RestHttpClient.RequestMethod.POST,
                url,
                null,
                data
        );
        if (httpConnection.isHttpConnectionSucceeded()) {
            // parse token
            try {
                String token = AppUtil.findElementFromXml(httpConnection.getHttpResponseBody().toString(), "BinarySecurityToken");

                if (token != null) {
                    if (BuildConfig.isDEBUG) Log.i(LOG_TAG, token);

                    // drop certain user data
                    Preference.getInstance(this).setUserName(null);
                    Preference.getInstance(this).setUserId(0);

                    // save token in prefs
                    Preference.getInstance(this).setUserToken(new String(Base64.decode(token, Base64.DEFAULT), UTF_8));

                    // retrieve profile data from server
                    profileRequest(APIUtil.getURL(PROFILE), subscriber);

                } else {
                    handleFailedConnection(subscriber, httpConnection);
                }

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                handleFailedConnection(subscriber, httpConnection);
            }

        } else {
            if (BuildConfig.isDEBUG) Log.e(LOG_TAG, httpConnection.getHttpConnectionMessage()
                    + Constant.Symbol.SPACE + httpConnection.getHttpConnectionCode());
            if (httpConnection.getHttpConnectionCode() == HttpErrorUtil.NumericStatusCode.HTTP_INTERNAL_ERROR) {
                BusProvider.getInstance().post(new ApiEvent<>(Event.EventType.Api.Error.BAD_REQUEST,
                        AppUtil.findElementFromXml(httpConnection.getHttpResponseBody().toString(), "Text"), subscriber));
            } else {
                handleFailedConnection(subscriber, httpConnection);
            }
        }
    }

    private void profileRequest(String url, final String subscriber) {
        HttpConnection httpConnection = HttpRequestManager.executeRequest(
                this,
                RestHttpClient.RequestMethod.GET,
                url,
                Preference.getInstance(this).getUserToken(),
                null
        );

        if (httpConnection.isHttpConnectionSucceeded()) {
            final User user = new Gson().fromJson(httpConnection.getHttpResponseBody().toString(),
                    User.class);
            if (user != null) {
                // save user name and id in prefs
                Preference.getInstance(this).setUserId(user.getId());
                Preference.getInstance(this).setUserName(user.getFirstName()
                        + Constant.Symbol.SPACE + user.getLastName());

                // add
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(user);
                        BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.PROFILE_LOADED,
                                subscriber));
                    }
                });
                Realm.getDefaultInstance().close();

            } else {
                handleFailedConnection(subscriber, httpConnection);
            }
        } else {
            if (BuildConfig.isDEBUG) Log.e(LOG_TAG, httpConnection.getHttpConnectionMessage()
                    + Constant.Symbol.SPACE + httpConnection.getHttpConnectionCode());
            handleFailedConnection(subscriber, httpConnection);
        }
    }

    private void myTripsRequest(String url, final String subscriber) {
        final HttpConnection httpConnection = HttpRequestManager.executeRequest(
                this,
                RestHttpClient.RequestMethod.GET,
                url,
                Preference.getInstance(this).getUserToken(),
                null
        );

        if (httpConnection.isHttpConnectionSucceeded()) {
            // parse trip response
            if (!httpConnection.getHttpResponseBody().toString().isEmpty()) {

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Trip.class, new Trip.TripDeserializer())
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                JsonObject rootObject = new JsonParser().parse(httpConnection.getHttpResponseBody().toString()).getAsJsonObject();
                Type listType = new TypeToken<ArrayList<Trip>>() {
                }.getType();
                final ArrayList<Trip> trips = gson.fromJson(rootObject.get(Constant.Json.TOKEN_DATA)
                        .toString(), listType);

                AppUtil.extendTripModel(true, Preference.getInstance(this).getUserId(), trips);

                if (trips != null) {
                    // insert all trips into db
                    // but before that clear all old data
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            // get all drafts
                            List<Trip> draftTrips = realm.copyFromRealm(Realm.getDefaultInstance()
                                    .where(Trip.class)
                                    .equalTo("notSynced", true)
                                    .findAll());

                            // clear
                            Realm.getDefaultInstance()
                                    .where(Trip.class)
                                    .equalTo("isMyTrip", true)
                                    .findAll()
                                    .deleteAllFromRealm();

                            // restore drafts
                            for (Trip tripDraft : draftTrips) {
                                for (int i = 0; i < trips.size(); i++) {
                                    Trip trip = trips.get(i);
                                    if (trip.getId() == tripDraft.getId()) {

                                        if (tripDraft.isNotSynced()) {
                                            trip.setNotSynced(tripDraft.isNotSynced());

                                            if (tripDraft.getReport() != null) {
                                                trip.setReport(tripDraft.getReport());
                                            }
                                            if (tripDraft.getAttachments() != null) {
                                                trip.setAttachments(tripDraft.getAttachments()/*realm.copyFromRealm(tripDraft).getAttachments()*/);
                                            }

//                                        trip.setNotSynced(tripDraft.isNotSynced());
//                                        trip.setReport(tripDraft.getReport());
//                                        trip.setAttachments(tripDraft.getAttachments());
                                            trips.set(i, trip);
                                        }
                                        break;
                                    }
                                }
                            }
                            // add
                            realm.copyToRealmOrUpdate(trips);
                        }
                    });

                    Realm.getDefaultInstance().close();

                } else {
                    handleFailedConnection(subscriber, httpConnection);
                }
            } else {
                handleFailedConnection(subscriber, httpConnection);
            }

        } else {
            if (BuildConfig.isDEBUG) Log.e(LOG_TAG, httpConnection.getHttpConnectionMessage()
                    + Constant.Symbol.SPACE + httpConnection.getHttpConnectionCode());
            handleFailedConnection(subscriber, httpConnection);
        }

        // send event in UI and retrieve trips from db
        BusProvider.getInstance().post(new ApiEvent<>(Event.EventType.Api.TRIPS_LOADED,
                subscriber));
    }

    private void supervisedTripsRequest(String url, final String subscriber) {
        final HttpConnection httpConnection = HttpRequestManager.executeRequest(
                this,
                RestHttpClient.RequestMethod.GET,
                url,
                Preference.getInstance(this).getUserToken(),
                null
        );

        if (httpConnection.isHttpConnectionSucceeded()) {
            // parse trip response
            if (!httpConnection.getHttpResponseBody().toString().isEmpty()) {

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Trip.class, new Trip.TripDeserializer())
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                JsonObject rootObject = new JsonParser().parse(httpConnection.getHttpResponseBody().toString()).getAsJsonObject();
                Type listType = new TypeToken<ArrayList<Trip>>() {
                }.getType();
                final ArrayList<Trip> trips = gson.fromJson(rootObject.get(Constant.Json.TOKEN_DATA)
                        .toString(), listType);

                AppUtil.extendTripModel(false, 0, trips);

                if (trips != null) {
                    // insert all trips into db
                    // but before that clear all old data
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // clear
                            Realm.getDefaultInstance()
                                    .where(Trip.class)
                                    .equalTo("isMyTrip", false)
                                    .findAll()
                                    .deleteAllFromRealm();
                            // add
                            realm.copyToRealmOrUpdate(trips);
                        }
                    });

                    Realm.getDefaultInstance().close();

                } else {
                    handleFailedConnection(subscriber, httpConnection);
                }
            } else {
                handleFailedConnection(subscriber, httpConnection);
            }

        } else {
            if (BuildConfig.isDEBUG) Log.e(LOG_TAG, httpConnection.getHttpConnectionMessage()
                    + Constant.Symbol.SPACE + httpConnection.getHttpConnectionCode());
            handleFailedConnection(subscriber, httpConnection);
        }

        // send event in UI and retrieve trips from db
        BusProvider.getInstance().post(new ApiEvent<>(Event.EventType.Api.TRIPS_LOADED,
                subscriber));
    }

    private void tripRequest(String url, final String subscriber) {
        final HttpConnection httpConnection = HttpRequestManager.executeRequest(
                this,
                RestHttpClient.RequestMethod.GET,
                url,
                Preference.getInstance(this).getUserToken(),
                null
        );

        if (httpConnection.isHttpConnectionSucceeded()) {
            // parse trip response
            if (!httpConnection.getHttpResponseBody().toString().isEmpty()) {

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Trip.class, new Trip.TripDeserializer())
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
                JsonObject rootObject = new JsonParser().parse(httpConnection.getHttpResponseBody().toString()).getAsJsonObject();
                final Trip trip = gson.fromJson(rootObject, Trip.class);

                if (trip != null) {
                    // update or insert single trip into db
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // first check  - is my or supervised trip and check traveler name, also check is draft
                            Trip fromDateBase = realm.where(Trip.class).equalTo("id", trip.getId()).findFirst();
                            if (fromDateBase != null && fromDateBase.isValid()) {
                                trip.setMyTrip(fromDateBase.isMyTrip());
                                trip.setTravelerName(fromDateBase.getTravelerName());
                                if (fromDateBase.isNotSynced()) {
                                    trip.setNotSynced(fromDateBase.isNotSynced());

                                    if (fromDateBase.getReport() != null) {
                                        trip.setReport(fromDateBase.getReport());
                                    }

                                    if (fromDateBase.getAttachments() != null) {
                                        trip.setAttachments(realm.copyFromRealm(fromDateBase).getAttachments());
                                    }
                                }
                            }
                            realm.copyToRealmOrUpdate(trip);
                        }
                    });

                    Realm.getDefaultInstance().close();

                    BusProvider.getInstance().post(new ApiEvent<>(Event.EventType.Api.TRIP_SYNCED,
                            subscriber));

                } else {
                    handleFailedConnection(subscriber, httpConnection);
                }
            } else {
                handleFailedConnection(subscriber, httpConnection);
            }

        } else {
            if (BuildConfig.isDEBUG) Log.e(LOG_TAG, httpConnection.getHttpConnectionMessage()
                    + Constant.Symbol.SPACE + httpConnection.getHttpConnectionCode());
            handleFailedConnection(subscriber, httpConnection);
        }

        // send event in UI and retrieve trip from db
        BusProvider.getInstance().post(new ApiEvent<>(Event.EventType.Api.TRIP_LOADED,
                subscriber));
    }

    private void staticDataRequest(String url, final String subscriber) {
        final HttpConnection httpConnection = HttpRequestManager.executeRequest(
                this,
                RestHttpClient.RequestMethod.GET,
                url,
                Preference.getInstance(this).getUserToken(),
                null
        );

        if (httpConnection.isHttpConnectionSucceeded()) {
            // parse static data response
            if (httpConnection.getHttpResponseBody() != null && !httpConnection.getHttpResponseBody().toString().isEmpty()) {
                JsonObject rootObject = new JsonParser().parse(httpConnection.getHttpResponseBody().toString()).getAsJsonObject();
                final Data data = new Gson().fromJson(rootObject, Data.class);

                if (data != null) {
                    if (data.getActionPointStatusesArray() != null) {
                        RealmList<ActionPointStatus> actionPointList = new RealmList<>();
                        for (String actionPointStatusStr : data.getActionPointStatusesArray()) {
                            actionPointList.add(new ActionPointStatus(actionPointStatusStr));
                        }
                        data.setActionPointStatuses(actionPointList);
                    }

                    // update or insert static data in db
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(data.getLocations());
                            realm.copyToRealmOrUpdate(data.getResults());
                            realm.copyToRealmOrUpdate(data.getActionPointStatuses());
                            realm.copyToRealmOrUpdate(data.getPartnerships());
                            realm.copyToRealmOrUpdate(data.getPartners());
                        }
                    });

                    Realm.getDefaultInstance().close();

                } else {
                    handleFailedConnection(subscriber, httpConnection);
                }
            } else {
                handleFailedConnection(subscriber, httpConnection);
            }

        } else {
            if (BuildConfig.isDEBUG) Log.e(LOG_TAG, httpConnection.getHttpConnectionMessage()
                    + Constant.Symbol.SPACE + httpConnection.getHttpConnectionCode());
            handleFailedConnection(subscriber, httpConnection);
        }
    }

    private void staticData2Request(String url, final String subscriber) {
        final HttpConnection httpConnection = HttpRequestManager.executeRequest(
                this,
                RestHttpClient.RequestMethod.GET,
                url,
                Preference.getInstance(this).getUserToken(),
                null
        );

        Log.i("LLL", "" +httpConnection.getHttpResponseBody());
        Log.i("LLL", "" +httpConnection.isHttpConnectionSucceeded());
        Log.i("LLL", "" +url);
        Log.i("LLL", "" +httpConnection.getHttpConnectionCode());
        Log.i("LLL", "" +Preference.getInstance(this).getUserToken());

        if (httpConnection.isHttpConnectionSucceeded()) {
            // parse static data 2 response
            if (!httpConnection.getHttpResponseBody().toString().isEmpty()) {
                JsonObject rootObject = new JsonParser().parse(httpConnection.getHttpResponseBody().toString()).getAsJsonObject();
                final Data2 data2 = new Gson().fromJson(rootObject, Data2.class);

                if (data2 != null) {
                    // update or insert static data 2 in db
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(data2.getAirlines());
                            realm.copyToRealmOrUpdate(data2.getBusinessAreas());
                            realm.copyToRealmOrUpdate(data2.getCountries());
                            realm.copyToRealmOrUpdate(data2.getCurrencies());
                            realm.copyToRealmOrUpdate(data2.getDsaRegions());
                            realm.copyToRealmOrUpdate(data2.getExpenseTypes());
                            realm.copyToRealmOrUpdate(data2.getFunds());
                            realm.copyToRealmOrUpdate(data2.getGrants());
                            realm.copyToRealmOrUpdate(data2.getWbs());
                        }
                    });

                    Realm.getDefaultInstance().close();

                } else {
                    handleFailedConnection(subscriber, httpConnection);
                }
            } else {
                handleFailedConnection(subscriber, httpConnection);
            }

        } else {
            if (BuildConfig.isDEBUG) Log.e(LOG_TAG, httpConnection.getHttpConnectionMessage()
                    + Constant.Symbol.SPACE + httpConnection.getHttpConnectionCode());
            handleFailedConnection(subscriber, httpConnection);
        }
    }

    private void usersRequest(String url, final String subscriber) {
        final HttpConnection httpConnection = HttpRequestManager.executeRequest(
                this,
                RestHttpClient.RequestMethod.GET,
                url,
                Preference.getInstance(this).getUserToken(),
                null
        );

        if (httpConnection.isHttpConnectionSucceeded()) {
            // parse users response
            if (!httpConnection.getHttpResponseBody().toString().isEmpty()) {
                Type listType = new TypeToken<ArrayList<UserStatic>>() {
                }.getType();
                final ArrayList<UserStatic> userStatics = new Gson().fromJson(httpConnection.getHttpResponseBody()
                        .toString(), listType);

                if (userStatics != null) {
                    // update or insert users in db
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            //fist delete old
                            realm.delete(UserStatic.class);
                            realm.copyToRealmOrUpdate(userStatics);
                        }
                    });

                    Realm.getDefaultInstance().close();

                } else {
                    handleFailedConnection(subscriber, httpConnection);
                }
            } else {
                handleFailedConnection(subscriber, httpConnection);
            }

        } else {
            if (BuildConfig.isDEBUG) Log.e(LOG_TAG, httpConnection.getHttpConnectionMessage()
                    + Constant.Symbol.SPACE + httpConnection.getHttpConnectionCode());
            handleFailedConnection(subscriber, httpConnection);
        }

        // send event in UI
        BusProvider.getInstance().post(new ApiEvent<>(Event.EventType.Api.USERS_LOADED,
                subscriber));
    }

    private void handleFailedConnection(String subscriber, HttpConnection httpConnection) {
        switch (httpConnection.getHttpConnectionCode()) {
            case HttpErrorUtil.NumericStatusCode.HTTP_NO_NETWORK:
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.NO_NETWORK,
                        subscriber));
                break;

            case HttpErrorUtil.NumericStatusCode.HTTP_SERVER_TIMEOUT:
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.SERVER_TIMEOUT,
                        subscriber));
                break;

            case HttpErrorUtil.NumericStatusCode.HTTP_UNKNOWN_SERVER_ERROR:
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.UNKNOWN,
                        subscriber));
                break;

            case HttpErrorUtil.NumericStatusCode.HTTP_CONNECTION_REFUSED:
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.CONNECTION_REFUSED,
                        subscriber));
                break;

            case HttpErrorUtil.NumericStatusCode.HTTP_FORBIDDEN:
            case HttpErrorUtil.NumericStatusCode.HTTP_UNAUTHORIZED:
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.UNAUTHORIZED,
                        subscriber));
                break;

            case HttpErrorUtil.NumericStatusCode.HTTP_BAD_REQUEST:
                if (httpConnection.getHttpResponseBody().toString().contains(Constant.Json.NON_FIELD_KEY)) {

                    // parse errors
                    JsonObject rootObject = new JsonParser().parse(
                            httpConnection.getHttpResponseBody().toString()).getAsJsonObject();
                    JsonArray errors = rootObject.getAsJsonArray(Constant.Json.NON_FIELD_KEY);

                    // compose errorText to show user
                    StringBuilder errorText = new StringBuilder();
                    for (JsonElement error : errors) {
                        if (!errorText.toString().isEmpty())
                            errorText.append(Constant.Symbol.COMMA);
                        errorText.append(error.getAsString());
                    }

                    BusProvider.getInstance().post(new ApiEvent<>(Event.EventType.Api.Error.BAD_REQUEST,
                            errorText.toString(), subscriber));

                } else {
                    BusProvider.getInstance().post(new ApiEvent<>(Event.EventType.Api.Error.BAD_REQUEST,
                            httpConnection.getHttpResponseBody().toString(), subscriber));
                }
                break;

            case HttpErrorUtil.NumericStatusCode.HTTP_NOT_FOUND:
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.PAGE_NOT_FOUND,
                        subscriber));
                break;

            default:
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.UNKNOWN,
                        subscriber));
                break;
        }
    }
}