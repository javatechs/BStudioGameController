package com.belithco.bsgamecontroller;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Utility {
    public static final String TAG = "Utility";
    public static final String fileName = "RunScripts.json";

    /**
     *
     * @param context
     */
    public static ArrayList<ScriptModel> loadFile(Activity context) {
        //Read text from file
        ArrayList<ScriptModel> fileData = new ArrayList<ScriptModel>();
        try {
            if(isExternalStorageMounted()) {
                // Check whether this app has write external storage permission or not.
                int readExternalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);

                // If do not grant write external storage permission.
                if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    // Request user to grant write external storage permission.
                    ActivityCompat.requestPermissions(
                            context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            ScriptActivity.REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION);
                }
                else {
                    // Read file from downloads folder
                    String publicDownloadsDirPath = getPublicExternalStorageBaseDir(Environment.DIRECTORY_DOWNLOADS);
                    File theFile = new File(publicDownloadsDirPath, fileName);
                    BufferedReader br = new BufferedReader(new FileReader(theFile));
                    StringBuilder text = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                    fileData = convertScriptModel(text.toString());

                    // Notify user
                    String message = context.getResources().getString(R.string.action_load_script_file);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }
            else {
                // External storage not mounted. No external store.
                String message = context.getResources().getString(R.string.NO_EXT_STORE);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(context,
                context.getResources().getString(R.string.READ_FROM_EXT_STORE) + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
        return fileData;
    }

    /**
     * Write File
     * @param context
     * @param data
     */
    public static void saveFile(Activity context, ArrayList<ScriptModel> data) {
        String json = convertScriptModel(data);
        try {
            if(isExternalStorageMounted()) {
                // Check whether this app has write external storage permission or not.
                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                // If do not grant write external storage permission.
                if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED) {
                    // Request user to grant write external storage permission.
                    ActivityCompat.requestPermissions(
                            context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            ScriptActivity.REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                }
                else {
                    // Save file to downloads folder
                    String publicDownloadsDirPath = getPublicExternalStorageBaseDir(Environment.DIRECTORY_DOWNLOADS);
                    File theFile = new File(publicDownloadsDirPath, fileName);
                    // Save file here
                    FileWriter fw = new FileWriter(theFile);
                    fw.write(json);
                    fw.flush();
                    fw.close();
                    // Notify user
                    String message = context.getResources().getString(R.string.action_save_script_file);
                    message += "\n" + theFile.getAbsolutePath();
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }
            else {
                // External storage not mounted. No external store.
                String message = context.getResources().getString(R.string.NO_EXT_STORE);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(context, context.getResources().getString(R.string.SAVE_TO_EXT_STORE) + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Convert script model from ArrayList object to String.
     * @param array
     * @return
     */
    public static String convertScriptModel(ArrayList<ScriptModel> array) {
        //  Format JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<List<ScriptModel>>() {}.getType();
        String json = "";
        try {
            json = gson.toJson(array, type);
        }
        catch (Exception e) {
            Log.e(TAG, "gson.toJson failed", e);
        }
        Log.d(TAG, json);
        return json;
    }

    /**
     * Convert script model from String to ArrayList object.
     * @param text
     * @return
     */
    public static ArrayList<ScriptModel> convertScriptModel(String text) {
        //
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ScriptModel>>(){}.getType();
        return gson.fromJson(text, listType);
    }

    /**
     * Check whether the external storage is mounted or not.
     * @return
     */
    public static boolean isExternalStorageMounted() {
        String dirState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(dirState)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get public external storage base directory.
     * @return
     */
    public static String getPublicExternalStorageBaseDir() {
        String ret = "";
        if(isExternalStorageMounted()) {
            File file = Environment.getExternalStorageDirectory();
            ret = file.getAbsolutePath();
        }
        return ret;
    }

    /**
     * Get public external storage base directory.
     * @param dirType
     * @return
     */
    public static String getPublicExternalStorageBaseDir(String dirType) {
        String ret = "";
        if(isExternalStorageMounted()) {
            File file = Environment.getExternalStoragePublicDirectory(dirType);
            ret = file.getAbsolutePath();
        }
        return ret;
    }
}
