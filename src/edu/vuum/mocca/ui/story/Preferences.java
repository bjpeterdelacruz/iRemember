package edu.vuum.mocca.ui.story;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Preferences {

  private final Context context;

  public Preferences(Context context) {
    this.context = context;
  }

  public void saveString(String key, String value) {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    sharedPrefs.edit().putString(key, value).commit();
  }

  public boolean hasValue(String key) {
    return !getString(key).isEmpty();
  }

  public String getString(String key) {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    return sharedPrefs.getString(key, "");
  }

  public void clearPreferences() {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    sharedPrefs.edit().clear().commit();
  }

}
