package org.unicef.etools.etrips.prod.io.rest.retrofit;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.ActionPointStatus;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.Data;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Currency;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Data2;
import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.prod.db.entity.trip.ActionPointsWrapper;
import org.unicef.etools.etrips.prod.db.entity.trip.Attachment;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.db.entity.user.User;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.rest.entity.ActionPointListResponse;
import org.unicef.etools.etrips.prod.io.rest.entity.TripListResponse;
import org.unicef.etools.etrips.prod.io.rest.util.APIUtil;
import org.unicef.etools.etrips.prod.io.rest.util.HttpErrorUtil;
import org.unicef.etools.etrips.prod.io.rest.util.PostEntityUtil;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.NetworkUtil;
import org.unicef.etools.etrips.prod.util.Preference;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.realm.Realm;
import io.realm.RealmList;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.common.base.Charsets.UTF_8;
import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.CHANGE_TRIP_STATUS;
import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.SUBMIT_REPORT;
import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.UPLOAD_REPORT_FILE;

public class RetrofitUtil {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = RetrofitUtil.class.getSimpleName();

    public static final String TOKEN_VALUE = "JWT ";

    // ===========================================================
    // Fields
    // ===========================================================

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
    // Methods
    // ===========================================================

    public static Call updateTripReportDescriptionRequest(final Context context, final String subscriber, final Trip trip) {

        // Report submition consist of two parts:
        // first - we send report description on server
        // second - we upload all report photos on server

        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: "
                + APIUtil.getURL(String.format(SUBMIT_REPORT, trip.getId())));

        RetrofitApiService retrofitApiService = initRetrofit();

        Call<Trip> call = retrofitApiService.updateTripReportDescription(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                trip.getReport(),
                String.format(SUBMIT_REPORT, trip.getId())
        );

        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, final Response<Trip> response) {
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Response code: " + response.code());

                // if description sent successfully - upload all photos
                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    // update trip in db

                    final Trip loadedTrip = response.body();

                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // first check  - is my or supervised trip and check traveler name, also check is draft
                            Trip fromDateBase = realm.where(Trip.class).equalTo("id", loadedTrip.getId()).findFirst();
                            if (fromDateBase != null && fromDateBase.isValid()) {
                                loadedTrip.setMyTrip(fromDateBase.isMyTrip());
                                loadedTrip.setTravelerName(fromDateBase.getTravelerName());
                                loadedTrip.setSupervisorName(fromDateBase.getSupervisorName());

                                AppUtil.checkDraftForTrip(realm, loadedTrip);
                            }
                            // add
                            realm.insertOrUpdate(loadedTrip);
                        }
                    });
                    realm.close();

                    BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.REPORT_SUBMITTED,
                            subscriber));

                } else {
                    handleFailedRequest(context, null, subscriber, response.raw().message(),
                            response.code());
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable throwable) {
                throwable.printStackTrace();
                handleFailedRequest(context, throwable, subscriber, call.request().body().toString(),
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call uploadTripReportFileRequest(@NonNull final Context context,
                                                   final Attachment attachment,
                                                   final String subscriber,
                                                   final long tripId) {

        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: "
                + APIUtil.getURL(String.format(UPLOAD_REPORT_FILE, tripId)));

        RetrofitApiService retrofitApiService = initRetrofit();

        // creates RequestBody instance from file
        File reportPhoto = Compressor.getDefault(context).compressToFile(new File(attachment.getFile())); // https://github.com/zetbaitsu/Compressor
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), reportPhoto);
        // creates RequestBody instance from file description
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), attachment.getName());
        // creates RequestBody instance from file type
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "Picture");

        // if description submitted we can attach files
        Call<Attachment> call = retrofitApiService.uploadTripReportFiles(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                MultipartBody.Part.createFormData("file", reportPhoto.getName(), requestFile),
                description,
                type,
                String.format(UPLOAD_REPORT_FILE, tripId)
        );

        call.enqueue(new Callback<Attachment>() {
            @Override
            public void onResponse(Call<Attachment> call, Response<Attachment> response) {
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Response code: " + response.code());

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_CREATED) {

                    final Attachment body = response.body();
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Trip trip = realm.where(Trip.class).equalTo("id", tripId).findFirst();
                            if (trip.getAttachments() != null && trip.getAttachments().isValid()) {
                                trip.getAttachments().add(body);
                            } else {
                                RealmList<Attachment> attachments = new RealmList<>();
                                attachments.add(body);
                                trip.setAttachments(attachments);
                            }
                            // add
                            realm.insertOrUpdate(trip);
                        }
                    });
                    realm.close();

                    BusProvider.getInstance().post(new ApiEvent<>(
                            Event.EventType.Api.REPORT_FILES_UPLOADED,
                            attachment.getId(),
                            subscriber));

                } else {
                    handleFailedRequest(context, null, subscriber, response.raw().message(),
                            response.code());
                }
            }

            @Override
            public void onFailure(Call<Attachment> call, Throwable throwable) {
                throwable.printStackTrace();
                handleFailedRequest(context, throwable, subscriber, call.request().body().toString(),
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call changeTripStatusRequest(final Context context, final long tripId, String status,
                                               HashMap<String, Object> body, final String subscriber) {

        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: "
                + APIUtil.getURL(String.format(SUBMIT_REPORT, tripId)));

        RetrofitApiService retrofitApiService = initRetrofit(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()));

        Call<Trip> call = retrofitApiService.changeTripStatus(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                body,
                String.format(Locale.US, CHANGE_TRIP_STATUS, tripId, status)
        );

        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, final Response<Trip> response) {
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Response code: " + response.code());

                // if description sent successfully - upload all photos
                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    final Trip loadedTrip = response.body();
                    if (loadedTrip != null) {
                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // first check  - is my or supervised trip  and check traveler name
                                Trip fromDateBase = realm.where(Trip.class).equalTo("id", tripId).findFirst();
                                if (fromDateBase != null && fromDateBase.isValid()) {
                                    loadedTrip.setMyTrip(fromDateBase.isMyTrip());
                                    loadedTrip.setTravelerName(fromDateBase.getTravelerName());
                                    loadedTrip.setSupervisorName(fromDateBase.getSupervisorName());

                                    AppUtil.checkDraftForTrip(realm, loadedTrip);
                                }
                                // update trip in db
                                realm.copyToRealmOrUpdate(loadedTrip);
                            }
                        });
                    }
                    BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.TRIP_STATUS_CHANGED,
                            subscriber));
                } else {
                    try {
                        handleFailedRequest(context, null, subscriber, response.errorBody().string(),
                                response.code());
                    } catch (IOException e) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable throwable) {
                throwable.printStackTrace();
                handleFailedRequest(context, throwable, subscriber, call.request().body().toString(),
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getActionPoints(@NonNull final Context context, final int page, final long relatedUserId,
                                       final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<ActionPointListResponse> call = retrofit.getActionPoints(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                page, APIUtil.PER_PAGE_ACTION_POINTS, APIUtil.SortBy.DUE_DATE, relatedUserId);

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<ActionPointListResponse>() {
            @Override
            public void onResponse(Call<ActionPointListResponse> call, Response<ActionPointListResponse> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    final ActionPointListResponse body = response.body();
                    if (BuildConfig.isDEBUG) {
                        Log.d(LOG_TAG, "getActionPoints: " + body.toString());
                    }

                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (page == 1) {
                                boolean deleted = realm.where(ActionPoint.class)
                                        .equalTo("personResponsible", relatedUserId)
                                        .findAll()
                                        .deleteAllFromRealm();

                                if (BuildConfig.isDEBUG && deleted) {
                                    Log.d(LOG_TAG, "Action points cleared.");
                                }
                            }

                            realm.insertOrUpdate(body.actionPoints);
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.ACTION_POINTS_LOADED, body, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    handleFailedRequest(context, null, subscriber, response.errorBody().toString(),
                            response.code());
                }
            }

            @Override
            public void onFailure(Call<ActionPointListResponse> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call updateActionPoint(@NonNull final Context context, @NonNull final ActionPoint actionPoint,
                                         final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()));
        final Call<ActionPoint> call = retrofit.updateActionPoint(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                actionPoint.getId(), actionPoint);

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<ActionPoint>() {
            @Override
            public void onResponse(Call<ActionPoint> call, Response<ActionPoint> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    final ActionPoint body = response.body();

                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(body);
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.ACTION_POINT_UPDATED, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    handleFailedRequest(context, null, subscriber, response.errorBody().toString(),
                            response.code());
                }
            }

            @Override
            public void onFailure(Call<ActionPoint> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call addActionPointToTrip(@NonNull final Context context, @NonNull Trip trip,
                                            final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<Trip> call = retrofit.addActionPointToTrip(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                trip.getId(), new ActionPointsWrapper(trip.getActionPoints()));

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    final Trip body = response.body();

                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // first check  - is my or supervised trip and check traveler name
                            Trip fromDateBase = realm.where(Trip.class).equalTo("id", body.getId()).findFirst();
                            if (fromDateBase != null && fromDateBase.isValid()) {
                                body.setMyTrip(fromDateBase.isMyTrip());
                                body.setTravelerName(fromDateBase.getTravelerName());
                                body.setSupervisorName(fromDateBase.getSupervisorName());

                                AppUtil.checkDraftForTrip(realm, body);
                            }
                            realm.insertOrUpdate(body);
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.ACTION_POINT_ADDED, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    try {
                        handleFailedRequest(context, null, subscriber, response.errorBody().string(),
                                response.code());
                    } catch (IOException e) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getMyTrips(@NonNull final Context context, final int page, final long relatedUserId,
                                  final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<JsonObject> call = retrofit.getMyTrips(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                page, APIUtil.PER_PAGE_TRIPS, APIUtil.SortBy.START_DATE, relatedUserId);

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    if (BuildConfig.isDEBUG) {
                        Log.d(LOG_TAG, "getMyTrips: " + response.body().toString());
                    }

                    // divide traveler id and traveler name
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Trip.class, new Trip.TripDeserializer())
                            .excludeFieldsWithoutExposeAnnotation()
                            .create();
                    JsonObject rootObject = response.body();

                    final TripListResponse body = gson.fromJson(rootObject, TripListResponse.class);

                    AppUtil.extendTripModel(true, Preference.getInstance(context).getUserId(), body.trips);

                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            if (page == 1) {
                                boolean deleted = realm.where(Trip.class)
                                        .equalTo("isMyTrip", true)
                                        .findAll()
                                        .deleteAllFromRealm();

                                if (BuildConfig.isDEBUG && deleted) {
                                    Log.d(LOG_TAG, "My trips cleared.");
                                }
                            }

                            // restore drafts
                            AppUtil.restoreDrafts(realm, body.trips);

                            // add
                            realm.insertOrUpdate(body.trips);
                        }
                    });
                    realm.close();

                    final ApiEvent event = new ApiEvent<>(ApiEvent.EventType.Api.TRIPS_LOADED, body, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    handleFailedRequest(context, null, subscriber, response.errorBody().toString(),
                            response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getSupervisedTrips(@NonNull final Context context, final int page, final long relatedUserId,
                                          final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<JsonObject> call = retrofit.getSupervisedTrips(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                page, APIUtil.PER_PAGE_TRIPS, APIUtil.SortBy.START_DATE, relatedUserId);

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    if (BuildConfig.isDEBUG) {
                        Log.d(LOG_TAG, "getSupervisedTrips: " + response.body().toString());
                    }

                    // divide traveler id and traveler name
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Trip.class, new Trip.TripDeserializer())
                            .excludeFieldsWithoutExposeAnnotation()
                            .create();
                    JsonObject rootObject = response.body();

                    final TripListResponse body = gson.fromJson(rootObject, TripListResponse.class);

                    AppUtil.extendTripModel(false, 0, body.trips);

                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (page == 1) {
                                boolean deleted = realm.where(Trip.class)
                                        .equalTo("isMyTrip", false)
                                        .findAll()
                                        .deleteAllFromRealm();

                                if (BuildConfig.isDEBUG && deleted) {
                                    Log.d(LOG_TAG, "Supervised trips cleared.");
                                }
                            }

                            // add
                            realm.insertOrUpdate(body.trips);
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.TRIPS_LOADED, body, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    handleFailedRequest(context, null, subscriber, response.errorBody().toString(),
                            response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getTrip(@NonNull final Context context, final long tripId,
                               final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<JsonObject> call = retrofit.getTrip(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(), tripId);

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    if (BuildConfig.isDEBUG) {
                        Log.d(LOG_TAG, "getTrip: " + response.body().toString());
                    }

                    // divide traveler id and traveler name
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Trip.class, new Trip.TripDeserializer())
                            .excludeFieldsWithoutExposeAnnotation()
                            .create();

                    final Trip trip = gson.fromJson(response.body(), Trip.class);

                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // first check  - is my or supervised trip and check traveler name, also check is draft
                            Trip fromDateBase = realm.where(Trip.class).equalTo("id", trip.getId()).findFirst();
                            if (fromDateBase != null && fromDateBase.isValid()) {
                                trip.setMyTrip(fromDateBase.isMyTrip());
                                trip.setTravelerName(fromDateBase.getTravelerName());
                                trip.setSupervisorName(fromDateBase.getSupervisorName());

                                AppUtil.checkDraftForTrip(realm, trip);
                            }
                            // add
                            realm.insertOrUpdate(trip);
                        }
                    });
                    realm.close();

                } else {
                    handleFailedRequest(context, null, subscriber, response.errorBody().toString(),
                            response.code());
                }
                final ApiEvent event
                        = new ApiEvent<>(ApiEvent.EventType.Api.TRIP_LOADED, subscriber);
                BusProvider.getInstance().post(event);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getUsers(@NonNull final Context context,
                                final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<JsonArray> call = retrofit.getUsers(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                "minimal"
        );

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    Type listType = new TypeToken<ArrayList<UserStatic>>() {
                    }.getType();
                    final ArrayList<UserStatic> userStatics = new Gson().fromJson(response.body()
                            .toString(), listType);
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            boolean deleted = realm.where(UserStatic.class)
                                    .findAll()
                                    .deleteAllFromRealm();

                            if (BuildConfig.isDEBUG && deleted) {
                                Log.d(LOG_TAG, "Users cleared.");
                            }

                            // add
                            realm.insertOrUpdate(userStatics);
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.USERS_LOADED, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    try {
                        handleFailedRequest(context, null, subscriber, response.errorBody().string(),
                                response.code());
                    } catch (IOException e) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getStaticData(@NonNull final Context context,
                                     final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<Data> call = retrofit.getStaticData(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken());

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    final Data staticData = response.body();
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (staticData.getActionPointStatusesArray() != null) {
                                RealmList<ActionPointStatus> actionPointList = new RealmList<>();
                                for (String actionPointStatusStr : staticData.getActionPointStatusesArray()) {
                                    actionPointList.add(new ActionPointStatus(actionPointStatusStr));
                                }
                                staticData.setActionPointStatuses(actionPointList);
                            }

                            // add
                            realm.insertOrUpdate(staticData.getLocations());
                            realm.insertOrUpdate(staticData.getResults());
                            realm.insertOrUpdate(staticData.getActionPointStatuses());
                            realm.insertOrUpdate(staticData.getPartnerships());
                            realm.insertOrUpdate(staticData.getPartners());
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.STATIC_DATA_LOADED, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    try {
                        handleFailedRequest(context, null, subscriber, response.errorBody().string(),
                                response.code());
                    } catch (IOException e) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getWbsGrantsFunds(@NonNull final Context context, long businessAreaId,
                                         final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<Data2> call = retrofit.getWbsGrantsFunds(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken(), businessAreaId);

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<Data2>() {
            @Override
            public void onResponse(Call<Data2> call, Response<Data2> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    final Data2 staticData2 = response.body();
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // add
                            realm.insertOrUpdate(staticData2.getFunds());
                            realm.insertOrUpdate(staticData2.getGrants());
                            realm.insertOrUpdate(staticData2.getWbs());
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.WBS_GRANTS_FUNDS_LOADED, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    try {
                        handleFailedRequest(context, null, subscriber, response.errorBody().string(),
                                response.code());
                    } catch (IOException e) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Data2> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getCurrencies(@NonNull final Context context,
                                     final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<JsonArray> call = retrofit.getCurrencies(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken());

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    Type listType = new TypeToken<ArrayList<Currency>>() {
                    }.getType();
                    final ArrayList<Currency> currencies = new Gson().fromJson(response.body()
                            .toString(), listType);
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // add
                            realm.insertOrUpdate(currencies);
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.CURRENCIES_LOADED, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    try {
                        handleFailedRequest(context, null, subscriber, response.errorBody().string(),
                                response.code());
                    } catch (IOException e) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getProfile(@NonNull final Context context,
                                  final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<User> call = retrofit.getProfile(
                TOKEN_VALUE + Preference.getInstance(context).getUserToken());

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    final User user = response.body();

                    // save user name and id in prefs
                    Preference.getInstance(context).setUserId(user.getId());
                    Preference.getInstance(context).setUserName(user.getFirstName()
                            + Constant.Symbol.SPACE + user.getLastName());

                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // add
                            realm.insertOrUpdate(user);
                        }
                    });
                    realm.close();

                    if (user.getTravel2Field() == null) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                        return;
                    }

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.PROFILE_LOADED, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    try {
                        handleFailedRequest(context, null, subscriber, response.errorBody().string(),
                                response.code());
                    } catch (IOException e) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    public static Call getStagingToken(@NonNull final Context context, final String userName,
                                       final String password, final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<JsonObject> call = retrofit.getStagingToken(userName, password);

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    final JsonObject body = response.body();
                    String token = body.get(Constant.Json.TOKEN_KEY).getAsString();

                    // drop certain user data
                    Preference.getInstance(context).setUserName(null);
                    Preference.getInstance(context).setUserId(0);

                    // save token in prefs
                    Preference.getInstance(context).setUserToken(token);

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.TOKEN_REQUEST_COMPLETED, subscriber);
                    BusProvider.getInstance().post(event);

                } else {
                    try {
                        handleFailedRequest(context, null, subscriber, response.errorBody().string(),
                                response.code());
                    } catch (IOException e) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }


    public static Call getProductionToken(@NonNull final Context context, final String userName,
                                          final String password, final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit(APIUtil.getHostLogIn());
        final Call<ResponseBody> call =
                retrofit.getProductionToken(RequestBody.create(MediaType.parse("application/soap+xml"),
                        PostEntityUtil.composeSoapSignInPostEntity(userName, password)));

        if (BuildConfig.isDEBUG) {
            Log.i(LOG_TAG, "Calling URL: " + call.request().url().toString());
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (BuildConfig.isDEBUG) {
                    Log.i(LOG_TAG, "Response code: " + response.code());
                }

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_OK) {
                    String token = null;
                    try {
                        token = AppUtil.findElementFromXml(response.body().string(), "BinarySecurityToken");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (token == null) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                        return;
                    }

                    if (BuildConfig.isDEBUG) Log.i(LOG_TAG, token);

                    // drop certain user data
                    Preference.getInstance(context).setUserName(null);
                    Preference.getInstance(context).setUserId(0);

                    // save token in prefs
                    Preference.getInstance(context).setUserToken(new String(Base64.decode(token, Base64.DEFAULT), UTF_8));

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.TOKEN_REQUEST_COMPLETED, subscriber);
                    BusProvider.getInstance().post(event);

                } else {
                    try {
                        handleFailedRequest(context, null, subscriber, response.errorBody().string(),
                                response.code());
                    } catch (IOException e) {
                        handleFailedRequest(context, null, subscriber, null, response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                handleFailedRequest(context, t, subscriber, null,
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
        return call;
    }

    // ===========================================================
    // Helper methods
    // ===========================================================

    private static RetrofitApiService initRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.SECONDS)
                .readTimeout(20000, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUtil.getHost())
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitApiService.class);
    }

    private static RetrofitApiService initRetrofit(String host) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.SECONDS)
                .readTimeout(20000, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitApiService.class);
    }

    private static RetrofitApiService initRetrofit(GsonConverterFactory gsonConverterFactory) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.SECONDS)
                .readTimeout(20000, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUtil.getHost())
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(gsonConverterFactory)
                .build();

        return retrofit.create(RetrofitApiService.class);
    }

    private static RetrofitApiService initRetrofit(String host, GsonConverterFactory gsonConverterFactory) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.SECONDS)
                .readTimeout(20000, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(gsonConverterFactory)
                .build();

        return retrofit.create(RetrofitApiService.class);
    }

    private static void handleFailedRequest(Context context, Throwable throwable, String subscriber,
                                            String responseBody, int responseCode) {
        switch (responseCode) {
            case HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR:
                if (!NetworkUtil.getInstance().isConnected(context.getApplicationContext())) {
                    if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Internet connection error");
                    BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.NO_NETWORK,
                            subscriber));

                } else if (throwable.getMessage() != null && throwable.getMessage()
                        .contains(HttpErrorUtil.NonNumericStatusCode.HTTP_SERVER_TIMEOUT)) {
                    if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection time out");
                    BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.SERVER_TIMEOUT,
                            subscriber));

                } else if (throwable.getMessage() != null && throwable.getMessage()
                        .contains(HttpErrorUtil.NonNumericStatusCode.HTTP_CONNECTION_REFUSED)) {
                    if (BuildConfig.isDEBUG) Log.e(LOG_TAG, "Connection refused by server");
                    BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.CONNECTION_REFUSED,
                            subscriber));
                } else {
                    BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.UNKNOWN,
                            subscriber));
                }
                break;

            case HttpErrorUtil.NumericStatusCode.HTTP_FORBIDDEN:
            case HttpErrorUtil.NumericStatusCode.HTTP_UNAUTHORIZED:
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.Error.UNAUTHORIZED,
                        subscriber));
                break;

            case HttpErrorUtil.NumericStatusCode.HTTP_BAD_REQUEST:
                if (responseBody.contains(Constant.Json.NON_FIELD_KEY)) {

                    // parse errors
                    JsonObject rootObject = new JsonParser().parse(responseBody).getAsJsonObject();
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
                            responseBody, subscriber));
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
