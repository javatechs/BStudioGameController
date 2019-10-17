package com.belithco.bsgamecontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * User Preferences
 */

public class AppPreferences {
    public static final String PREF_START_ON_BOOT = "START.ON.BOOT";
    //
    //
    public static final String PREF_MODEL_CURRENT_ROBOT  = "LOCAL.MODEL.DEVICE.CLASSIC_CONTROLLER";

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public static String getPrefStr(Context context, String key){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, null);
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public static String getPrefStr(Context context, String key, String _default){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, _default);
    }

    /**
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setPref(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getPrefBool(Context context, String key){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(key, false);
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public static int getPrefInt(Context context, String key){
        int retVal = -1;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Object value = settings.getAll().get(key);
        if (value instanceof java.lang.String) {
            retVal = Integer.parseInt((String)value);
        }
        else if (value instanceof java.lang.Integer) {
            retVal = settings.getInt(key, -1);
        }
        return retVal;
    }

    /**
     *
     * @param context
     * @return
     */
    public static Map<String,?> getPrefs(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getAll();
    }
}