package org.unicef.etools.etrips.prod.io.rest.retrofit;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.prod.db.entity.trip.ActionPointsWrapper;
import org.unicef.etools.etrips.prod.db.entity.trip.Attachment;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.io.rest.entity.ActionPointListResponse;
import org.unicef.etools.etrips.prod.io.rest.util.APIUtil;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitApiService {

    @FormUrlEncoded
    @PATCH()
    Call<Trip> updateTripReportDescription(
            @Header("Authorization") String authorization,
            @Field("report") String report,
            @Url String url
    );

    @PATCH()
    Call<Trip> changeTripStatus(
            @Header("Authorization") String authorization,
            @Body HashMap<String, Object> body,
            @Url String url
    );

    @Multipart
    @POST()
    Call<Attachment> uploadTripReportFiles(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part file,
            @Part("name") RequestBody description,
            @Part("type") RequestBody type,
            @Url String url
    );

    @GET(APIUtil.ACTION_POINTS)
    @Headers("Content-Type: application/json")
    Call<ActionPointListResponse> getActionPoints(
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("page_size") int perPage,
            @Query("sort_by") String sortBy,
            @Query("f_person_responsible") long relatedUserId
    );

    @PATCH(APIUtil.ACTION_POINT)
    @Headers("Content-Type: application/json")
    Call<ActionPoint> updateActionPoint(
            @Header("Authorization") String authorization,
            @Path("id") long id,
            @Body ActionPoint actionPoint
    );

    @PATCH(APIUtil.ADD_ACTION_POINT)
    @Headers("Content-Type: application/json")
    Call<Trip> addActionPointToTrip(
            @Header("Authorization") String authorization,
            @Path("trip_id") long tripId,
            @Body ActionPointsWrapper actionPointsWrapper
    );

    @GET(APIUtil.TRIPS)
    @Headers("Content-Type: application/json")
    Call<JsonObject> getMyTrips(
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("page_size") int perPage,
            @Query("sort_by") String sortBy,
            @Query("f_traveler") long relatedUserId
    );

    @GET(APIUtil.TRIPS)
    @Headers("Content-Type: application/json")
    Call<JsonObject> getSupervisedTrips(
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("page_size") int perPage,
            @Query("sort_by") String sortBy,
            @Query("f_supervisor") long relatedUserId
    );

    @GET(APIUtil.TRIP)
    @Headers("Content-Type: application/json")
    Call<JsonObject> getTrip(
            @Header("Authorization") String authorization,
            @Path("trip_id") long tripId
    );

    @GET(APIUtil.USERS)
    @Headers("Content-Type: application/json")
    Call<JsonArray> getUsers(
            @Header("Authorization") String authorization
    );
}
