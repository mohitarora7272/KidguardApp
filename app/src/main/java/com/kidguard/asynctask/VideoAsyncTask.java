package com.kidguard.asynctask;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Video;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.utilities.Utilities;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class VideoAsyncTask extends AsyncTask<String, Void, ArrayList<Video>> implements Constant {
    private static final String TAG = VideoAsyncTask.class.getSimpleName();

    private DatabaseHelper databaseHelper = null;
    private Dao<Video, Integer> videosDao;
    private Context context;
    private ArrayList<File> fileList;
    private ArrayList<Video> lstVideos;

    /* VideoAsyncTask Constructor */
    public VideoAsyncTask(Context context) {
        this.context = context;
    }

    /* DatabaseHelper */
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        lstVideos = new ArrayList<Video>();
    }

    @Override
    protected ArrayList<Video> doInBackground(String... params) {
        fileList = new ArrayList<File>();
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        fileList = getFile(root);

        try {
            videosDao = getHelper().getVideosDao();
            if (videosDao.isTableExists()) {
                long numRows = videosDao.countOf();
                if (numRows != 0) {
                    final QueryBuilder<Video, Integer> queryBuilder = videosDao.queryBuilder();
                    for (int i = 0; i < videosDao.queryForAll().size(); i++) {
                        for (int j = 0; j < fileList.size(); j++) {
                            List<Video> results = queryBuilder.where()
                                    .eq(Video.VIDEO_PATH,
                                            fileList.get(j).getAbsolutePath()).query();
                            if (results.size() == 0) {
                                setVideoPOJO(fileList, j);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < fileList.size(); i++) {
                        setVideoPOJO(fileList, i);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /* Video Fetch With Tag */
        lstVideos = videoFetchWithTag(videosDao, params[0], params[1], params[3]);

        return lstVideos;
    }

    @Override
    protected void onPostExecute(ArrayList<Video> list) {
        super.onPostExecute(list);
        // Release databaseHelper
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        if (list != null && list.size() > 0)
            Log.e("Size", "VIDEO List???" + list.size());
        BackgroundDataService.getInstance().sendVideosDataToServer(list);

    }

    // Get Video List
    public ArrayList<File> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {

            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    //fileList.add(listFile[i]);
                    getFile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".mp4")
                            || listFile[i].getName().endsWith(".mkv")
                            || listFile[i].getName().endsWith(".3gp")
                            || listFile[i].getName().endsWith(".Wmv")
                            || listFile[i].getName().endsWith(".flv"))

                    {
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;
    }

    /* Set Video POJO With File List*/
    private void setVideoPOJO(ArrayList<File> fileList, int i) {
        Video video = new Video();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        video.setDate_time(sdf.format(fileList.get(i).lastModified()));
        video.setDate_time_stamp(String.valueOf(fileList.get(i).lastModified()));
        video.setVideoname(fileList.get(i).getName());
        video.setVideoPath(fileList.get(i).getAbsolutePath());
        video.setVideoStatus(FALSE);
        video.setVideoExt(Utilities.getExtension(fileList.get(i).getName()));
        video.setVideoSizeKB(String.valueOf(getVideoSizeKB(fileList.get(i).getAbsolutePath())));
        video.setVideoSizeMB(String.valueOf(getVideoSizeMB(fileList.get(i).getAbsolutePath())));
        try {
            videosDao.create(video);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /* Video Fetch With Tag*/
    private ArrayList<Video> videoFetchWithTag(Dao<Video, Integer> VideoDao, String count, String ext, String size) {
        ArrayList<Video> lstVideoSorted = new ArrayList<>();
        List<Video> results = null;
        int countNew = 0;
        final QueryBuilder<Video, Integer> queryBuilder = VideoDao.queryBuilder();

        if (!count.equals("") && ext.equals("") && size.equals("")) {
            try {
                results = queryBuilder.where().eq(Video.VIDEO_STATUS, FALSE).query();
                checkCount(lstVideoSorted, results, count, countNew);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return lstVideoSorted;

        }

        if (count.equals("") && !ext.equals("") && size.equals("")) {
            try {
                results = queryBuilder.where().eq(Video.VIDEO_EXT, ext).and().eq(Video.VIDEO_STATUS, FALSE).query();
                for (int i = 0; i < results.size(); i++) {
                    setVideoPOJO(lstVideoSorted, results, i);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return lstVideoSorted;
        }

        if (!count.equals("") && !ext.equals("") && size.equals("")) {
            try {
                results = queryBuilder.where().eq(Video.VIDEO_EXT, ext).and().eq(Video.VIDEO_STATUS, FALSE).query();
                checkCount(lstVideoSorted, results, count, countNew);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return lstVideoSorted;
        }

        if (count.equals("") && ext.equals("") && !size.equals("")) {
            try {

                if (Integer.parseInt(size) > 0) {

                    results = queryBuilder.where().between(Video.VIDEO_SIZE_KB, ZERO, size)
                            .and().eq(Video.VIDEO_STATUS, FALSE).query();
                } else {
                    results = queryBuilder.where().eq(Video.VIDEO_STATUS, FALSE).query();

                }

                if (results != null && results.size() > 0) {
                    for (int j = 0; j < results.size(); j++) {
                        setVideoPOJO(lstVideoSorted, results, j);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return lstVideoSorted;
        }

        if (!count.equals("") && ext.equals("") && !size.equals("")) {
            try {

                if (Integer.parseInt(size) > 0) {

                    results = queryBuilder.where().between(Video.VIDEO_SIZE_KB, ZERO, size)
                            .and().eq(Video.VIDEO_STATUS, FALSE).query();
                } else {
                    results = queryBuilder.where().eq(Video.VIDEO_STATUS, FALSE).query();
                }

                checkCount(lstVideoSorted, results, count, countNew);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return lstVideoSorted;
        }

        if (!count.equals("") && !ext.equals("") && !size.equals("")) {
            try {

                if (Integer.parseInt(size) > 0) {

                    results = queryBuilder.where().between(Video.VIDEO_SIZE_KB, ZERO, size)
                            .and().eq(Video.VIDEO_EXT, ext).and().eq(Video.VIDEO_STATUS, FALSE).query();

                } else {
                    results = queryBuilder.where().eq(Video.VIDEO_EXT, ext).and().eq(Video.VIDEO_STATUS, FALSE).query();
                }

                checkCount(lstVideoSorted, results, count, countNew);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return lstVideoSorted;
        }

        if (count.equals("") && ext.equals("") && size.equals("")) {
            try {

                results = videosDao.queryForAll();
                for (int i = 0; i < results.size(); i++) {
                    setVideoPOJO(lstVideoSorted, results, i);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return lstVideoSorted;

        }
        return lstVideoSorted;
    }

    /* Check Count */
    private void checkCount(ArrayList<Video> lstVideoSorted, List<Video> results, String count, int countNew) {

        if (results != null && results.size() > 0) {
            if (Integer.parseInt(count) == results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) > results.size()) {
                countNew = results.size();
            }

            for (int i = 0; i < countNew; i++) {
                setVideoPOJO(lstVideoSorted, results, i);
            }
        }
    }

    /* Set Video POJO */
    private void setVideoPOJO(ArrayList<Video> lstVideoSorted, List<Video> results, int i) {
        Video video = new Video();
        video.setVideoname(results.get(i).getVideoname());
        video.setDate_time(results.get(i).getDate_time());
        video.setDate_time_stamp(results.get(i).getDate_time_stamp());
        video.setVideoPath(results.get(i).getVideoPath());
        video.setVideoStatus(results.get(i).getVideoStatus());
        video.setVideoExt(Utilities.getExtension(results.get(i).getVideoExt()));
        video.setVideoSizeKB(results.get(i).getVideoSizeKB());
        video.setVideoSizeMB(results.get(i).getVideoSizeMB());
        lstVideoSorted.add(video);
    }

    private long getVideoSizeKB(String absolutePathOfImage) {
        Uri uriNew = Uri.fromFile(new File(absolutePathOfImage));
        long size = 0;
        try {
            File f = new File(uriNew.getPath());
            size = f.length();
            size = size / 1024;
        } catch (Exception e) {
            Log.e("Exception", "Size??" + e.getMessage());
        }
        return size;
    }

    private long getVideoSizeMB(String absolutePathOfImage) {
        Uri uriNew = Uri.fromFile(new File(absolutePathOfImage));
        long size = 0;
        try {
            File f = new File(uriNew.getPath());
            size = f.length();
            size = size / 1024;
            size = size / 1024;

        } catch (Exception e) {
            Log.e("Exception", "Size??" + e.getMessage());
        }
        return size;
    }
}
