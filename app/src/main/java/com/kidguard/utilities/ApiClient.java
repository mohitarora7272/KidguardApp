package com.kidguard.utilities;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kidguard.interfaces.Constant;
import com.kidguard.interfaces.RestClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
@SuppressWarnings("all")
public class ApiClient implements Constant {
    private Retrofit retrofit = null;
    private String tag;

    public ApiClient(String tag) {
        this.tag = tag;
    }

    public RestClient getClient() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        if (retrofit == null) {
            if (tag.equals(TAG_LOGIN)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + LOGIN)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_SMS)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + SMS)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_CONTACTS)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + CONTACT)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_CALLS)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + CALL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_FILES)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + FILE)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_IMAGES)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + IMAGE)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_LIST_APPS)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + APP)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_EMAIL)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + EMAIL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_GOOGLE_DRIVE)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + DRIVE )
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_VIDEOS)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + VIDEO)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_BROWSER_HISTORY)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + BROWSER_HISTORY)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

            if (tag.equals(TAG_LOCATION)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ROOT + APPENDED_URL + LOCATION)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }

        }
        return retrofit.create(RestClient.class);
    }
}
