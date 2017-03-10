package org.unicef.etools.etrips.prod.io.rest.retrofit;


import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.prod.db.entity.trip.ActionPointsWrapper;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.io.rest.entity.ActionPointListResponse;
import org.unicef.etools.etrips.prod.io.rest.util.APIUtil;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitApiService {

    @FormUrlEncoded
    @PUT()
    Call<Trip> updateTripReportDescription(
            @Header("Authorization") String authorization,
            @Field("report") String report,
            @Url String url
    );

    @PATCH()
    Call<Trip> changeTripStatus(
            @Header("Authorization") String authorization,
            @Url String url
    );

    @Multipart
    @POST()
    Call<ResponseBody> uploadTripReportFiles(
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
}
