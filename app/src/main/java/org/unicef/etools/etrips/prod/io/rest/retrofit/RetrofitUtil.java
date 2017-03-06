package org.unicef.etools.etrips.prod.io.rest.retrofit;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.prod.db.entity.trip.Attachment;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.rest.entity.ActionPointListResponse;
import org.unicef.etools.etrips.prod.io.rest.url_connection.RestHttpClient;
import org.unicef.etools.etrips.prod.io.rest.util.APIUtil;
import org.unicef.etools.etrips.prod.io.rest.util.HttpErrorUtil;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.NetworkUtil;
import org.unicef.etools.etrips.prod.util.Preference;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.realm.Realm;
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

import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.CHANGE_TRIP_STATUS;
import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.SUBMIT_REPORT;
import static org.unicef.etools.etrips.prod.io.rest.util.APIUtil.UPLOAD_REPORT_FILE;

public class RetrofitUtil {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = RetrofitUtil.class.getSimpleName();

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

    public static void updateTripReportDescriptionRequest(final Context context, final String subscriber, Trip trip) {

        // Report submition consist of two parts:
        // first - we send report description on server
        // second - we upload all report photos on server

        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: "
                + APIUtil.getURL(String.format(SUBMIT_REPORT, trip.getId())));

        RetrofitApiService retrofitApiService = initRetrofit();

        Call<Trip> call = retrofitApiService.updateTripReportDescription(
                RestHttpClient.TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
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
    }

    public static void uploadTripReportFileRequest(@NonNull final Context context,
                                                   final Attachment attachment,
                                                   final String subscriber,
                                                   long tripId) {

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
        Call<ResponseBody> call = retrofitApiService.uploadTripReportFiles(
                RestHttpClient.TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                MultipartBody.Part.createFormData("file", reportPhoto.getName(), requestFile),
                description,
                type,
                String.format(UPLOAD_REPORT_FILE, tripId)
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Response code: " + response.code());

                if (response.code() == HttpErrorUtil.NumericStatusCode.HTTP_CREATED) {
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
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                throwable.printStackTrace();
                handleFailedRequest(context, throwable, subscriber, call.request().body().toString(),
                        HttpErrorUtil.NumericStatusCode.CONNECTION_ERROR);
            }
        });
    }

    public static void changeTripStatusRequest(final Context context, final String subscriber, String status, final Trip trip) {

        if (BuildConfig.isDEBUG) Log.i(LOG_TAG, "Calling URL: "
                + APIUtil.getURL(String.format(SUBMIT_REPORT, trip.getId())));

        RetrofitApiService retrofitApiService = initRetrofit();

        Call<Trip> call = retrofitApiService.changeTripStatus(
                RestHttpClient.TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                trip,
                String.format(Locale.US, CHANGE_TRIP_STATUS, trip.getId(), status)
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
                                Trip fromDateBase = realm.where(Trip.class).equalTo("id", trip.getId()).findFirst();
                                if (fromDateBase != null && fromDateBase.isValid()) {
                                    loadedTrip.setMyTrip(fromDateBase.isMyTrip());
                                    loadedTrip.setTravelerName(fromDateBase.getTravelerName());
                                }
                                // update trip in db
                                realm.copyToRealmOrUpdate(loadedTrip);
                            }
                        });
                    }
                    BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.TRIP_STATUS_CHANGED,
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
    }

    public static void getActionPoints(@NonNull final Context context, final int page, final long relatedUserId,
                                       final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<ActionPointListResponse> call = retrofit.getActionPoints(
                RestHttpClient.TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                page, APIUtil.PER_PAGE_ACTION_POINTS, relatedUserId);

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

                            // TODO: it is temporary decision,
                            // before full name will be added on Server side.
                            // https://docs.google.com/document/d/1LrT3keXxudbgv725yVIdu6ZDQ6Hm5k71EeJisGKXWNw/edit - question 3.
                            AppUtil.addAssignedFullName(realm, body.actionPoints);
                            realm.insertOrUpdate(body.actionPoints);
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.ACTION_POINTS_LOADED, subscriber);
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
    }

    public static void updateActionPoint(@NonNull final Context context, @NonNull final ActionPoint actionPoint,
                                         final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<ActionPoint> call = retrofit.updateActionPoint(
                RestHttpClient.TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
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
                            // TODO: it is temporary decision,
                            // before full name will be added on Server side.
                            // https://docs.google.com/document/d/1LrT3keXxudbgv725yVIdu6ZDQ6Hm5k71EeJisGKXWNw/edit - question 3.
                            AppUtil.addAssignedFullName(realm, body);
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
    }

    // FIXME: this API was changed.
    // Now don't send whole trip to the server, just send :
    // "action_points" : [{AP_old}, {AP_old}, {AP_new}]
    public static void addActionPointToTrip(@NonNull final Context context, @NonNull Trip trip,
                                            final String subscriber) {

        final RetrofitApiService retrofit = initRetrofit();
        final Call<Trip> call = retrofit.addActionPointToTrip(
                RestHttpClient.TOKEN_VALUE + Preference.getInstance(context).getUserToken(),
                trip.getId(), trip);

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
                            // TODO: it is temporary decision,
                            // before full name will be added on Server side.
                            // https://docs.google.com/document/d/1LrT3keXxudbgv725yVIdu6ZDQ6Hm5k71EeJisGKXWNw/edit - question 3.
                            AppUtil.addAssignedFullName(realm, body.actionPoints);
                            realm.copyToRealmOrUpdate(body);
                        }
                    });
                    realm.close();

                    final ApiEvent event
                            = new ApiEvent<>(ApiEvent.EventType.Api.ACTION_POINT_ADDED, subscriber);
                    BusProvider.getInstance().post(event);
                } else {
                    // TODO: handleFailedRequest not working for 400
                    // to reproduce change date format in ActionPointActivity for actionPoint.dueDate,
                    // which is sending to server. (Method onDataSet).
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
    }

    // ===========================================================
    // Helper methods
    // ===========================================================

    public static RetrofitApiService initRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.SECONDS)
                .readTimeout(20000, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUtil.getHost())
                .client(client)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
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
