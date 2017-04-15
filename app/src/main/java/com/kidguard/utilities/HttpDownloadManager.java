package com.kidguard.utilities;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpDownloadManager {
    private static final String TAG = HttpDownloadManager.class.getSimpleName();
    private String downloadUrl;
    private String toFile;
    private FileDownloadProgressListener listener;
    private long totalBytes;

    public void setListener(FileDownloadProgressListener listener) {
        this.listener = listener;
    }

    public HttpDownloadManager(File sourceFile, java.io.File destinationFile) {
        super();
        this.downloadUrl = sourceFile.getDownloadUrl();
        this.toFile = destinationFile.toString();
        if (sourceFile.getFileSize() != null) {
            this.totalBytes = sourceFile.getFileSize();
        }
    }

    public interface FileDownloadProgressListener {
        void downloadProgress(long bytesRead, long totalBytes);

        void downloadFinished();

        void downloadFailedWithError(Exception e);
    }

    public boolean download(Drive service) {
        HttpResponse respEntity = null;
        try {
            // URL url = new URL(urlString);
            Log.e(TAG, "downloadUrl??" + downloadUrl);
            if (downloadUrl == null) {
                return false;
            }
            respEntity = service.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl)).execute();
            HttpRequest request = respEntity.getRequest();
            request.setConnectTimeout(40 * 1000);
            InputStream in = respEntity.getContent();
            if (totalBytes == 0) {
                totalBytes = respEntity.getContentLoggingLimit();
            }

            FileOutputStream f = new FileOutputStream(toFile) {

                @Override
                public void write(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
                    super.write(buffer, byteOffset, byteCount);
                }
            };

            byte[] buffer = new byte[1024];
            int len1;
            long bytesRead = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
                if (listener != null) {
                    bytesRead += len1;
                    listener.downloadProgress(bytesRead, totalBytes);
                }
            }
            f.close();
        } catch (IOException ex) {
            if (listener != null) {
                listener.downloadFailedWithError(ex);
            }
            return false;
        } finally {
            if (respEntity != null) {
                try {
                    respEntity.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (listener != null) {
            listener.downloadFinished();
        }
        return true;
    }
}