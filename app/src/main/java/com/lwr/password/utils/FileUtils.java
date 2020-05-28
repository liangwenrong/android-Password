package com.lwr.password.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;


import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.lwr.password.MainActivity;
import com.lwr.password.constant.Constants;
import com.lwr.password.data.DataPreferences;
import com.lwr.password.data.Permissions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class FileUtils {
    // /** 算法/模式/填充 **/
    private static final String CipherMode = "AES/ECB/PKCS5Padding";
    /**
     * AES密钥长度为16/32字节
     */
    private static final byte[] key = new byte[]{60, 115, -75, -28, 56, -88, 15, -54, -36, 42, 120, -33, 112, 24, -96, -119};

    /**
     * 导出加密的密码文件
     *
     * @throws FileNotFoundException
     */
    public static boolean exportPasswordFile(final Activity activity, String relateFilePath) {
        Context applicationContext = activity.getApplicationContext();
        File dest = FileUtils.mkdirByRelateFilePath(activity, relateFilePath);
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Map<String, ?> map = DataPreferences.getAllForMap(applicationContext, Constants.PREFERENCES_FILE_NAME_PASSWORD);
        if (map.size() < 1) {
            return false;
        }
        ByteArrayOutputStream bos = null;
        ByteArrayInputStream bis = null;
        ObjectOutputStream objOut = null;
        CipherInputStream cipherInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            bos = new ByteArrayOutputStream();
            objOut = new ObjectOutputStream(bos);
            /**
             * map序列化
             */
            objOut.writeObject(map);
            objOut.flush();
            /**
             * 加密序列化内容
             */
            bis = new ByteArrayInputStream(bos.toByteArray());

            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            cipherInputStream = new CipherInputStream(bis, cipher);

            fileOutputStream = new FileOutputStream(dest);

            byte[] buffer = new byte[128 * 8];
            int n = -1;
            while ((n = cipherInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, n);
            }
            fileOutputStream.flush();
            return true;
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (cipherInputStream != null) {
                    cipherInputStream.close();
                }
                if (objOut != null) {
                    objOut.close();
                }
                bis = null;
                bos = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 导入加密的密码文件
     *
     * @param applicationContext
     * @param src
     * @throws FileNotFoundException
     */
    public static void inportPasswordFile(Context applicationContext, File src) throws FileNotFoundException {

        ByteArrayOutputStream bos = null;
        ByteArrayInputStream bis = null;
        FileInputStream fileInputStream = new FileInputStream(src);
        CipherOutputStream cipherOutputStream = null;
        ObjectInputStream ois = null;
        try {
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            bos = new ByteArrayOutputStream();
            cipherOutputStream = new CipherOutputStream(bos, cipher);
            byte[] buffer = new byte[128 * 8];
            int n = -1;
            while ((n = fileInputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, n);
            }
            cipherOutputStream.flush();
            cipherOutputStream.close();
            bis = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bis);
            Map<String, ?> map = (Map<String, ?>) ois.readObject();
            DataPreferences.saveRawKeyValue(map, applicationContext, Constants.PREFERENCES_FILE_NAME_PASSWORD);
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (cipherOutputStream != null) {
                    cipherOutputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                bis = null;
                bos = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件
     */
    public static File mkdirByRelateFilePath(final Activity activity, final String relateFilePath) {
        Permissions.verifyStoragePermissions(activity);
        String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(rootDir + relateFilePath);
        file.getParentFile().mkdirs();
        return file;
    }

    /**
     * 获取文件真实路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
//                Log.i(TAG,"isExternalStorageDocument***"+uri.toString());
//                Log.i(TAG,"docId***"+docId);
//                以下是打印示例：
//                isExternalStorageDocument***content://com.android.externalstorage.documents/document/primary%3ATset%2FROC2018421103253.wav
//                docId***primary:Test/ROC2018421103253.wav
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
//                Log.i(TAG,"isDownloadsDocument***"+uri.toString());
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
//                Log.i(TAG,"isMediaDocument***"+uri.toString());
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
