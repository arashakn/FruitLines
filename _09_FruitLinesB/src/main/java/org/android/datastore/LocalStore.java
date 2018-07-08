package org.android.datastore;

import java.io.IOException;
import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocalStore {

    private static final String PREF_FILE_NAME = "localStore";


    public static void clear(Context context) {
        Editor editor = 
            context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static boolean putBoolean(String key, boolean value, Context context) {
	    Editor editor =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
	    editor.putBoolean(key, value);
	
	    return editor.commit(); 
    }
	public static boolean getBoolean(String key, Context context) {
	    SharedPreferences sharedPreferences =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	
	    return (sharedPreferences.getBoolean(key, false));
	}
	
	public static boolean putString(String key, String value, Context context) {
	    Editor editor =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
	    editor.putString(key, value);
	
	    return editor.commit(); 
	}
	public static String getString(String key, Context context) {
	    SharedPreferences sharedPreferences =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	
	    return (sharedPreferences.getString(key, null));
	}
	
	
	public static boolean isExisted(String customKey, Context context) {
	    SharedPreferences sharedPreferences =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	
	    return (sharedPreferences.getString(customKey, null))==null?false:true;
	}
	
	
	public static boolean putFloat(String key, float value, Context context) {
	    Editor editor =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
	    editor.putFloat(key, value);
	
	    return editor.commit(); 
	}
	public static float getFloat(String key, Context context) {
	    SharedPreferences sharedPreferences =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	
	    return (sharedPreferences.getFloat(key, 0));
	}
	
	
	public static boolean putLong(String key, long value, Context context) {
	    Editor editor =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
	    editor.putLong(key, value);
	
	    return editor.commit(); 
	}
	public static float getLong(String key, Context context) {
	    SharedPreferences sharedPreferences =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	
	    return (sharedPreferences.getLong(key, 0));
	}
	
	
	public static boolean putInt(String key, int value, Context context) {
	    Editor editor =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
	    editor.putInt(key, value);
	    return editor.commit(); 
	}
	

	public static float getInt(String key, Context context) {
	    SharedPreferences sharedPreferences =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	    return (sharedPreferences.getInt(key, 0));
	}
	
	
	
	
	
	public static boolean putObject(String objectKey, Serializable dataObj, Context context) {
	    Editor editor =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
	    try {
			editor.putString(objectKey, ObjectSerializer.serialize(dataObj) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return editor.commit(); 
	}
	
	public static Object getObject(String objectKey, Context context) {
	    SharedPreferences sharedPreferences =
	        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	
	    Object dataObject = null;
		try {
			dataObject = ObjectSerializer.deserialize(sharedPreferences.getString(objectKey, null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	    return dataObject;
	}
	
	
}
