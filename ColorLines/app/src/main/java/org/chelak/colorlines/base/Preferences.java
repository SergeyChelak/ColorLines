package org.chelak.colorlines.base;

import android.content.Context;
import android.content.SharedPreferences;

import org.chelak.colorlines.game.Location;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Sergey on 08.02.2016.
 */
public class Preferences {

    private final static String PREFERENCES_STORAGE_FILE = "colorlines.pref";

    private Context context;

    public Preferences(Context context) {
        super();
        this.context = context;
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(PREFERENCES_STORAGE_FILE,
                Context.MODE_PRIVATE);
    }

    public boolean saveCoding(String key, Coding object) {
        try {
            String value = null;
            if ( object != null )
                value = object.encode().toString();
            putString(key, value);
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public Location getLocation(String key) {
        String value = getString(key);
        if ( value == null )
            return null;
        Location location = new Location();
        if (!loadCoding(key, location))
            return null;
        return location;
    }

    public boolean loadCoding(String key, Coding object) {
        try {
            String value = getString(key);
            JSONObject jsonObject = new JSONObject(value);
            object.decode(jsonObject);
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public void putString(String key, String value) {
        SharedPreferences sharedPref = getPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        if ( value == null )
            editor.remove(key);
        else
            editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        SharedPreferences sharedPref = getPreferences();
        return sharedPref.getString(key, null);
    }

    public void putLong(String key, long value) {
        SharedPreferences sharedPref = getPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key, long defaultValue) {
        SharedPreferences sharedPref = getPreferences();
        return sharedPref.getLong(key, defaultValue);
    }

    public void putInt(String key, int value) {
        SharedPreferences sharedPref = getPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        SharedPreferences sharedPref = getPreferences();
        return sharedPref.getInt(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences sharedPref = getPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences sharedPref = getPreferences();
        return sharedPref.getBoolean(key, defaultValue);
    }

    public void putDate(String key, Date value) {
        putLong(key, value.getTime());
    }

    public Date getDate(String key) {
        long value = getLong(key, 0);
        return new Date(value);
    }

}
