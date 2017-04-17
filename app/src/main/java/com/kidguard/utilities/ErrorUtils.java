package com.kidguard.utilities;

import com.kidguard.exceptions.APIError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ErrorUtils {
    public static APIError parseError(Response<?> response, Retrofit retrofit) throws IOException {
        Converter<ResponseBody, APIError> converter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
        return converter.convert(response.errorBody());
    }
}