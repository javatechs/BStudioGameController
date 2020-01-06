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
    public static final String PREF_MODEL_CONTROLLER_NAME = "LOCAL.MODEL.DEVICE.CONTROLLER_NAME";
    public static final String PREF_MODEL_CONTROLLER_ID = "LOCAL.MODEL.DEVICE.CONTROLLER_ID";
    public static final String PREF_SCRIPTS = "LOCAL.SCRIPTS";

    /**
     *
     * @param context Application Context
     * @param key name of the preference to retrieve
     * @return Returns the preference value if it exists, or defValue. Throws ClassCastException if there is a preference with this name that is not a String. This value may be null.
     */
    public static String getPrefStr(Context context, String key){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, null);
    }

    /**
     *
     * @param context Application Context
     * @param key String: name of the preference to retrieve
     * @param _default String: Value to return if this preference does not exist. This value may be null.
     * @return Returns the preference value if it exists, or defValue.
     * Throws ClassCastException if there is a preference with this name that is not a String. This value may be null.
     */
    public static String getPrefStr(Context context, String key, String _default){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, _default);
    }

    /**
     *
     * @param context Application Context
     * @param key name of the preference to retrieve
     * @param value new value for the preference. Passing null for this argument is equivalent to calling remove(java.lang.String) with this key. This value may be null.
     */
    public static void setPref(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     *
     * @param context Application Context
     * @param key String name of the preference to retrieve
     * @return Returns the preference value if it exists, or defValue. Throws ClassCastException if there is a preference with this name that is not a boolean.
     */
    public static boolean getPrefBool(Context context, String key){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(key, false);
    }

    /**
     *
     * @param context Application Context
     * @param key String: name of the preference to retrieve
     * @param defaultValue int: Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue. Throws ClassCastException if there is a preference with this name that is not an int.
     */
    public static int getPrefInt(Context context, String key, int defaultValue) {
        int retVal = -1;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Object value = settings.getAll().get(key);
        if (value instanceof java.lang.String) {
            retVal = Integer.parseInt((String)value);
        }
        else if (value instanceof java.lang.Integer) {
            retVal = settings.getInt(key, -1);
        }
        else {
            retVal = defaultValue;
        }
        return retVal;
    }

    /**
     *
     * @param context Application Context
     * @return Returns a map containing a list of pairs key/value representing the preferences.
     */
    public static Map<String,?> getPrefs(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getAll();
    }
}