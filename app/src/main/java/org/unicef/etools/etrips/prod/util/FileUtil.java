package org.unicef.etools.etrips.prod.util;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.unicef.etools.etrips.prod.BuildConfig;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    private static final String LOG_TAG = FileUtil.class.getSimpleName();

    private static final String JPG = ".jpg";
    private static final String JPEG = ".jpeg";
    private static final String PNG = ".png";

    @Nullable
    public static Uri getTempPhotoUri(Context context, String fileName) {

        // File will be saved in Android/data/packageName/files

        if (isExternalStorageWritable()) {
            // crete file dir in Android/data/packageName/files fodler
            File photoFile = new File(
                    context.getExternalFilesDir(null),
                    fileName + JPG
            );

          /*  // delete all previous files
            String[] files;
            File folderDir = context.getExternalFilesDir(null);
            if (folderDir != null && folderDir.isDirectory()) {
                files = folderDir.list();
                for (String file : files) {
                    new File(folderDir, file).delete();
                }
            }*/

            // compose uri
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                uri = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile
                );
            } else {
                uri = Uri.fromFile(photoFile);
            }
            if (BuildConfig.isDEBUG) Log.i(LOG_TAG, uri.toString());
            return uri;

        } else {
            //create temp file on internal storage
            Uri uri = null;
            try {
                File file = File.createTempFile(fileName, null, context.getCacheDir());
                uri = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return uri;
        }
    }

    private static boolean isExternalStorageWritable() {
        // Checks if external storage is available for read and write
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getFileFromUri(Context context, Uri uri) {

        if (uri.toString().contains("etrips_external_files")) {
            return new File(context.getExternalFilesDir(null), uri.getLastPathSegment());

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                String filePath = "";
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return new File(filePath);
            } else {
                String filePath = null;
                Uri selectedFileUri = uri;
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = context.getContentResolver().query(selectedFileUri, projection, null, null, null);

                int columnIndexData;

                if (cursor != null) {
                    columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    filePath = cursor.getString(columnIndexData);
                    cursor.close();
                }
                if (filePath != null) {
                    return new File(filePath);
                } else {
                    return new File(uri.getPath());
                }
            }
        }
    }

}
