package net.bugfixers.e_commerce.database;

import android.content.Context;
import android.content.SharedPreferences;

import net.bugfixers.e_commerce.constants.AppConstants;

public class SharedPref {
    private static SharedPref yourPreference;
    private SharedPreferences sharedPreferences;

    public static SharedPref getInstance(Context context) {
        if (yourPreference == null) {
            yourPreference = new SharedPref(context);
        }
        return yourPreference;
    }

    private SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(key, value);
        prefsEditor.apply();
    }

    public String getData(String key) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getString(key, null);
        }
        return null;
    }
}
