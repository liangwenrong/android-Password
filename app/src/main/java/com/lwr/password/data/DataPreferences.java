package com.lwr.password.data;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import com.lwr.password.constant.Constants;
import com.lwr.password.utils.AESCrypt;
import com.lwr.password.utils.RSAUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 对内部存储Preferences的操作
 * 其中，string类型的value，存储和获取都会经过加密解密工序
 */
public class DataPreferences {

    /**
     * 获取preferencesFilename所有的键
     *
     * @param context
     * @param preferencesFilename
     * @return
     */
    public static ArrayList<String> getAllForList(Context context, String preferencesFilename) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesFilename, Activity.MODE_PRIVATE);
        Map<String, ?> allUsers = sharedPreferences.getAll();
        ArrayList<String> allList = new ArrayList<>();
        for (String account : allUsers.keySet()) {
            allList.add(account);
        }
        return allList;
    }

    /**
     * 获取preferencesFilename所有的键和值
     *
     * @param context
     * @param preferencesFilename
     * @return
     */
    public static Map<String, ?> getAllForMap(Context context, String preferencesFilename) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesFilename, Activity.MODE_PRIVATE);
        Map<String, ?> allUsers = sharedPreferences.getAll();
        return allUsers;
    }

    /**
     * 获取key对应的值（String类型）
     *
     * @param key
     * @param context
     * @param preferencesFilename
     * @return
     */
    public static String getStringByKey(String key, Context context, String preferencesFilename) {
        SharedPreferences preferences = context.getSharedPreferences(preferencesFilename, Activity.MODE_PRIVATE);
        String value = preferences.getString(key, null);
        if (value != null) {
            value = decryptValue(value);
        }
        return value;
    }

    /**
     * 不经过加密的
     */
    public static String getRawString(String key, Context context, String preferencesFileName) {
        SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE);
        return preferences.getString(key, null);
    }


    /*public static Object getObjectByKey(String key, Context context, String preferencesFilename){
        SharedPreferences preferences = context.getSharedPreferences(preferencesFilename, Activity.MODE_PRIVATE);
        return preferences.get(key, null);
    }*/


    public synchronized static void saveRawKeyValue(Map<String, ?> map, Context context, String preferencesFilename) {
        SharedPreferences.Editor edit = context.getSharedPreferences(preferencesFilename, Activity.MODE_PRIVATE).edit();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            putKeyValue(entry.getKey(), entry.getValue(), edit, false);//不加密
        }
        edit.apply();//异步提交，有序
    }

    public synchronized static void saveKeyValue(String key, Object object, Context context, String preferencesFilename) {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferencesFilename, Activity.MODE_PRIVATE).edit();
        putKeyValue(key, object, editor);
        editor.commit();
    }

    /**
     * 不经过加密的
     */
    public static void saveRawKeyValue(String key, String value, Context context, String preferencesFileName) {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    private synchronized static void putKeyValue(String key, Object object, SharedPreferences.Editor editor) {
        putKeyValue(key, object, editor, true);//默认加密字符串
    }

    /**
     * 设置一个键值对到SharedPreferences文件中（不提交）
     *
     * @param key
     * @param object
     * @param editor
     * @param encrypt true：加密
     */
    private synchronized static void putKeyValue(String key, Object object, SharedPreferences.Editor editor, boolean encrypt) {
        // 得到object的类型
        String type = object.getClass().getSimpleName();
        if ("String".equals(type)) {
            String value = (String) object;
            if (encrypt) {
                value = encryptValue(value);//加密
            }
            editor.putString(key, value);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        } else {
            if (!(object instanceof Serializable)) {
                throw new IllegalArgumentException(object.getClass().getName() + " 必须实现Serializable接口!");
            }
            // 不是基本类型则是保存序列化对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                String objectToStringUTF_8 = new String(baos.toByteArray(), "UTF-8");
                editor.putString(key, objectToStringUTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized static void clearAll(Context applicationContext, String preferencesFileName) {
        SharedPreferences.Editor editor = applicationContext.getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public synchronized static void remove(String username, Context context, String preferencesFileName) {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE).edit();
        editor.remove(username);
        editor.commit();
    }

    public synchronized void removeKey(String key, Context context, String preferencesFilename) {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferencesFilename, Activity.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.apply();
    }

    private static String encryptValue(String value) {
        try {
            value = AESCrypt.encrypt(Constants.getAesKey(), value);
            value = RSAUtils.encryptByPublicKey(value, RSAUtils.loadPublicKey(Constants.RSA_PUBLIC_KEY));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private static String decryptValue(String value) {//todo
        try {
            value = RSAUtils.decryptByPrivateKey(value, RSAUtils.loadPrivateKey(Constants.RSA_PRIVATE_KEY));
            value = AESCrypt.decrypt(Constants.getAesKey(), value);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void loadAndRefreshAESKEY(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES_FILE_NAME_CONFIG, Activity.MODE_PRIVATE);
        String key = sharedPreferences.getString(Constants.CONFIG_KEY, "");
        Constants.setAesKey(key);
    }

}
