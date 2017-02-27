package com.kidguard.asynctask;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kidguard.interfaces.Constant;
import com.kidguard.model.Images;
import com.kidguard.orm.DatabaseHelper;
import com.kidguard.services.BackgroundDataService;
import com.kidguard.utilities.Utilities;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressWarnings("all")
public class ImagesAsyncTask extends AsyncTask<String, Void, ArrayList<Images>> implements Constant {
    private static final String TAG = ImagesAsyncTask.class.getSimpleName();

    private DatabaseHelper databaseHelper = null;
    private Dao<Images, Integer> imagesDao;
    private ArrayList lstImages;
    private Context context;

    /* ImagesAsyncTask Constructor */
    public ImagesAsyncTask(Context context) {
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
        lstImages = new ArrayList<>();
    }

    @Override
    protected ArrayList<Images> doInBackground(String... params) {
        Cursor cursor = null;
        try {
            Uri uri;
            int column_index_data, column_index_folder_name;
            String absolutePathOfImage;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            cursor = context.getContentResolver().query(uri, projection, null,
                    null, null);
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            imagesDao = getHelper().getImagesDao();
            if (imagesDao.isTableExists()) {
                long numRows = imagesDao.countOf();
                if (numRows != 0) {
                    final QueryBuilder<Images, Integer> queryBuilder = imagesDao.queryBuilder();
                    for (int i = 0; i < imagesDao.queryForAll().size(); i++) {
                        while (cursor.moveToNext()) {
                            absolutePathOfImage = cursor.getString(column_index_data);
                            List<Images> results = queryBuilder.where()
                                    .eq(Images.IMAGE_PATH, absolutePathOfImage).query();
                            if (results.size() == 0) {
                                setImagePOJO(absolutePathOfImage);
                            }
                        }
                    }

                } else {
                    while (cursor.moveToNext()) {
                        absolutePathOfImage = cursor.getString(column_index_data);
                        setImagePOJO(absolutePathOfImage);
                    }
                }
            }
            // Images Fetch With Tag
            lstImages = imagesFetchWithTag(imagesDao, params[0], params[1], params[2], params[3]);

        } catch (Exception e) {
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return lstImages;
    }

    @Override
    protected void onPostExecute(ArrayList<Images> list) {
        super.onPostExecute(list);
        // Release databaseHelper
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        if (list != null && list.size() > 0)
            Log.e("Size", "Images List???" + list.size());
        BackgroundDataService.getInstance().sendImageDataToServer(list);
    }

    /* Set Image POJO */
    private void setImagePOJO(String absolutePathOfImage) {
        try {
            Images images = new Images();
            images.setImagePath(absolutePathOfImage);
            Uri uriNew = Uri.fromFile(new File(absolutePathOfImage));

            //String path = FileUtils.getPath(context, uriNew);
            //Log.e("path", "path???" + path);

            try {
                File f = new File(uriNew.getPath());
                images.setImageName(f.getName());
                long size = f.length();
                long fileSizeInKB = size / 1024;
                long fileSizeInMB = fileSizeInKB / 1024;
                images.setDateTimeStamp(String.valueOf(f.lastModified()));
                Date lastModDate = new Date(f.lastModified());
                images.setSizeMB(String.valueOf(fileSizeInMB));
                images.setSizeKB(String.valueOf(fileSizeInKB));
                images.setDateTime(Utilities.changeDateToString(lastModDate));
                images.setImageStatus(FALSE);

            } catch (Exception e) {
                Log.e("Exception", "ImagesSize??" + e.getMessage());
            }

            imagesDao.create(images);
        } catch (SQLException e) {
            Log.e("SQLException", "SQLException??" + e.getMessage());
            e.printStackTrace();
        }
    }

    /* set Images POJO With List*/
    private void setImagePOJO(ArrayList<Images> lstImagesSorted, List<Images> results, int i) {
        final Images images = new Images();
        images.setImagePath(results.get(i).getImagePath());
        images.setImageName(results.get(i).getImageName());
        images.setSizeMB(results.get(i).getSizeMB());
        images.setSizeKB(results.get(i).getSizeKB());
        images.setDateTime(results.get(i).getDateTime());
        images.setDateTimeStamp(results.get(i).getDateTimeStamp());
        images.setImageStatus(results.get(i).getImageStatus());
        lstImagesSorted.add(images);
    }

    /* checkCount */
    private void checkCount(ArrayList<Images> lstImagesSorted, List<Images> results, String count, int countNew) {

        if (results != null && results.size() > 0) {
            if (Integer.parseInt(count) == results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) < results.size()) {
                countNew = Integer.parseInt(count);
            } else if (Integer.parseInt(count) > results.size()) {
                countNew = results.size();
            }

            for (int i = 0; i < countNew; i++) {
                setImagePOJO(lstImagesSorted, results, i);
            }
        }
    }

    /* Images Fetch With Tag */
    private ArrayList<Images> imagesFetchWithTag(Dao<Images, Integer> imagesDao, String count, String dateFrom, String dateTo, String size) {
        ArrayList<Images> lstImagesSorted = new ArrayList<>();
        int countNew = 0;
        final QueryBuilder<Images, Integer> queryBuilder = imagesDao.queryBuilder();
        //queryBuilder.orderBy(Sms.SMS_ID, false); // descending sort
        try {

            if (!count.equals("") && dateFrom.equals("") && dateTo.equals("") && size.equals("")) {
                Log.d("1", "1??");
                List<Images> results;
                results = queryBuilder.where().eq(Images.IMAGE_STATUS, FALSE).query();

                checkCount(lstImagesSorted, results, count, countNew);

                return lstImagesSorted;
            }

            if (count.equals("") && !dateFrom.equals("") && !dateTo.equals("") && size.equals("")) {
                Log.d("2", "2??");

                List<Images> resultDateFrom = queryBuilder.where().like(Images.IMAGE_DATE_TIME, dateFrom).query();
                List<Images> resultDateTo = queryBuilder.where().like(Images.IMAGE_DATE_TIME, dateTo).query();

                if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        return null;
                    }
                    dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                    dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                            .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);


                } else if (resultDateFrom.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateFrom = dateTo;

                    } else {
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                    }

                } else if (resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateTo = dateFrom;

                    } else {
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                                .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                    }
                }

                if (dateFrom.equals(dateTo)) {

                    List<Images> results;

                    results = queryBuilder.where().eq(Images.IMAGE_DATE_TIME, dateFrom)
                            .and().eq(Images.IMAGE_STATUS, FALSE).query();

                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setImagePOJO(lstImagesSorted, results, j);
                        }
                    }

                } else {
                    List<Images> results;
                    results = queryBuilder.where().between(Images.IMAGE_DATE_TIME, dateFrom, dateTo)
                            .and().eq(Images.IMAGE_STATUS, FALSE).query();

                    if (results != null && results.size() > 0) {
                        for (int j = 0; j < results.size(); j++) {
                            setImagePOJO(lstImagesSorted, results, j);
                        }
                    }
                }

                return lstImagesSorted;
            }

            if (!count.equals("") && !dateFrom.equals("") && !dateTo.equals("") && size.equals("")) {
                Log.d("3", "3??");

                List<Images> resultDateFrom = queryBuilder.where().like(Images.IMAGE_DATE_TIME, dateFrom).query();
                List<Images> resultDateTo = queryBuilder.where().like(Images.IMAGE_DATE_TIME, dateTo).query();

                if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        return null;
                    }
                    dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                    dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                            .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);


                } else if (resultDateFrom.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateFrom = dateTo;

                    } else {
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);

                    }

                } else if (resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateTo = dateFrom;

                    } else {
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                                .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);

                    }
                }

                List<Images> results;
                if (dateFrom.equals(dateTo)) {
                    results = queryBuilder.where().eq(Images.IMAGE_DATE_TIME, dateFrom)
                            .and().eq(Images.IMAGE_STATUS, FALSE).query();
                } else {
                    results = queryBuilder.where().between(Images.IMAGE_DATE_TIME, dateFrom, dateTo)
                            .and().eq(Images.IMAGE_STATUS, FALSE).query();
                }

                checkCount(lstImagesSorted, results, count, countNew);

                return lstImagesSorted;
            }

            if (!count.equals("") && dateFrom.equals("") && dateTo.equals("") && !size.equals("")) {
                Log.d("4", "4??");

                List<Images> results;
                if (Integer.parseInt(size) > 0) {
                    results = queryBuilder.where().between(Images.IMAGE_SIZE_KB, ZERO, size)
                            .and().eq(Images.IMAGE_STATUS, FALSE).query();
                } else {
                    results = queryBuilder.where().eq(Images.IMAGE_STATUS, FALSE).query();
                }

                checkCount(lstImagesSorted, results, count, countNew);

                return lstImagesSorted;
            }

            if (count.equals("") && !dateFrom.equals("") && !dateTo.equals("") && !size.equals("")) {
                Log.d("5", "5??");

                List<Images> resultDateFrom = queryBuilder.where().like(Images.IMAGE_DATE_TIME, dateFrom).query();
                List<Images> resultDateTo = queryBuilder.where().like(Images.IMAGE_DATE_TIME, dateTo).query();

                if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        return null;
                    }
                    dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                    dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                            .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);


                } else if (resultDateFrom.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateFrom = dateTo;

                    } else {
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);

                    }

                } else if (resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateTo = dateFrom;

                    } else {
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                                .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);

                    }
                }

                List<Images> results = null;
                if (dateFrom.equals(dateTo)) {

                    if (Integer.parseInt(size) > 0) {
                        results = queryBuilder.where().eq(Images.IMAGE_DATE_TIME, dateFrom).and()
                                .between(Images.IMAGE_SIZE_KB, ZERO, size).and()
                                .eq(Images.IMAGE_STATUS, FALSE).query();
                    } else {
                        results = queryBuilder.where().eq(Images.IMAGE_DATE_TIME, dateFrom).and()
                                .eq(Images.IMAGE_STATUS, FALSE).query();
                    }

                } else if (!dateFrom.equals(dateTo)) {

                    if (Integer.parseInt(size) > 0) {
                        results = queryBuilder.where().between(Images.IMAGE_DATE_TIME, dateFrom, dateTo).and()
                                .between(Images.IMAGE_SIZE_KB, ZERO, size).and().eq(Images.IMAGE_STATUS, FALSE).query();
                    } else {
                        results = queryBuilder.where().between(Images.IMAGE_DATE_TIME, dateFrom, dateTo).and()
                                .eq(Images.IMAGE_STATUS, FALSE).query();
                    }
                }

                if (results != null && results.size() > 0) {
                    for (int j = 0; j < results.size(); j++) {
                        setImagePOJO(lstImagesSorted, results, j);
                    }
                }

                return lstImagesSorted;
            }

            if (!count.equals("") && !dateFrom.equals("") && !dateTo.equals("") && !size.equals("")) {
                Log.d("6", "6??");

                List<Images> resultDateFrom = queryBuilder.where().like(Images.IMAGE_DATE_TIME, dateFrom).query();
                List<Images> resultDateTo = queryBuilder.where().like(Images.IMAGE_DATE_TIME, dateTo).query();

                if (resultDateFrom.size() == 0 && resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        return null;
                    }
                    dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);
                    dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                            .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);


                } else if (resultDateFrom.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateFrom = dateTo;

                    } else {
                        dateFrom = getResultedDateFromTo(queryBuilder, dateFrom, dateTo).get(0);

                    }

                } else if (resultDateTo.size() == 0) {
                    if (getResultedDateFromTo(queryBuilder, dateFrom, dateTo) == null
                            && getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() == 0) {
                        dateTo = dateFrom;

                    } else {
                        dateTo = getResultedDateFromTo(queryBuilder, dateFrom, dateTo)
                                .get(getResultedDateFromTo(queryBuilder, dateFrom, dateTo).size() - 1);
                    }
                }

                List<Images> results = null;
                if (dateFrom.equals(dateTo)) {

                    if (Integer.parseInt(size) > 0) {
                        results = queryBuilder.where().eq(Images.IMAGE_DATE_TIME, dateFrom).and()
                                .between(Images.IMAGE_SIZE_KB, ZERO, size).and()
                                .eq(Images.IMAGE_STATUS, FALSE).query();
                    } else {
                        results = queryBuilder.where().eq(Images.IMAGE_DATE_TIME, dateFrom).and()
                                .eq(Images.IMAGE_STATUS, FALSE).query();
                    }

                } else if (!dateFrom.equals(dateTo)) {

                    if (Integer.parseInt(size) > 0) {
                        results = queryBuilder.where().between(Images.IMAGE_DATE_TIME, dateFrom, dateTo).and()
                                .between(Images.IMAGE_SIZE_KB, ZERO, size).and().eq(Images.IMAGE_STATUS, FALSE).query();
                    } else {
                        results = queryBuilder.where().between(Images.IMAGE_DATE_TIME, dateFrom, dateTo).and()
                                .eq(Images.IMAGE_STATUS, FALSE).query();
                    }
                }

                checkCount(lstImagesSorted, results, count, countNew);

                return lstImagesSorted;
            }

            if (count.equals("") && dateFrom.equals("") && dateTo.equals("") && !size.equals("")) {
                Log.d("7", "7??");

                List<Images> results;
                if (Integer.parseInt(size) > 0) {
                    results = queryBuilder.where().between(Images.IMAGE_SIZE_KB, ZERO, size)
                            .and().eq(Images.IMAGE_STATUS, FALSE).query();
                } else {
                    results = queryBuilder.where().eq(Images.IMAGE_STATUS, FALSE).query();
                }

                if (results != null && results.size() > 0) {
                    for (int j = 0; j < results.size(); j++) {
                        setImagePOJO(lstImagesSorted, results, j);
                    }
                }

                return lstImagesSorted;
            }

            if (count.equals("") && dateFrom.equals("") && dateTo.equals("") && size.equals("")) {
                Log.d("8", "8??");

                List<Images> results;
                results = imagesDao.queryForAll();

                if (results != null && results.size() > 0) {
                    for (int j = 0; j < results.size(); j++) {
                        setImagePOJO(lstImagesSorted, results, j);
                    }
                }

                return lstImagesSorted;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lstImagesSorted;
    }

    /* Get Resulted DateFrom To Sorted */
    private List<String> getResultedDateFromTo(QueryBuilder<Images,
            Integer> queryBuilder, String dateFrom, String dateTo) {
        List<String> sortList = new ArrayList<>();
        try {
            List<Images> results;

            results = queryBuilder.where().between(Images.IMAGE_DATE_TIME, dateFrom, dateTo)
                    .and().eq(Images.IMAGE_STATUS, FALSE).query();

            if (results != null && results.size() > 0) {
                for (int i = 0; i < results.size(); i++) {
                    sortList.add(results.get(i).getDateTime());
                }
                Collections.sort(sortList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sortList;
    }
}
