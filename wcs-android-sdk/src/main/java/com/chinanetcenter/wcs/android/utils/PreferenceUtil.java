package com.chinanetcenter.wcs.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreferenceUtil implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences.Editor mEditor;

    private SharedPreferences mSharedPreferences;

    private static final String ARRAY_TAG = "WCS-ARRAY-TAG:";

    private final List<PreferencesListener> mPreferencesListeners = new ArrayList<PreferencesListener>();

    public synchronized static PreferenceUtil newInstance(Context context, String name) {
        return new PreferenceUtil(context, name);
    }

    public synchronized static PreferenceUtil newInstance(Context context) {
        return new PreferenceUtil(context, "wcs-default");
    }

    private PreferenceUtil(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void addListener(PreferencesListener listener) {
        if (listener != null) {
            mPreferencesListeners.add(listener);
        }
    }

    public void commit() {
        if (mEditor != null) {
            mEditor.commit();
            mEditor = null;
        }
    }

    public void putBoolean(String key, boolean value) {
        if (mEditor == null) {
            mEditor = mSharedPreferences.edit();
        }
        mEditor.putBoolean(key, value);
    }

    public void putBooleanAndCommit(String key, boolean value) {
        putBoolean(key, value);
        mEditor.commit();
        mEditor = null;
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public void putInt(String key, int value) {
        if (mEditor == null) {
            mEditor = mSharedPreferences.edit();
        }
        mEditor.putInt(key, value);
    }

    public void putIntAndCommit(String key, int value) {
        putInt(key, value);
        mEditor.commit();
        mEditor = null;
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public void putLong(String key, long value) {
        if (mEditor == null) {
            mEditor = mSharedPreferences.edit();
        }
        mEditor.putLong(key, value);
    }

    public void putLongAndCommit(String key, long value) {
        putLong(key, value);
        mEditor.commit();
        mEditor = null;
    }

    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    public void putString(String key, String value) {
        if (mEditor == null) {
            mEditor = mSharedPreferences.edit();
        }
        mEditor.putString(key, value);
    }

    public void putStringAndCommit(String key, String value) {
        putString(key, value);
        mEditor.commit();
        mEditor = null;
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public void putStringArrayAndCommit(String key, String[] strings) {
        String value = ARRAY_TAG + convertListToString(strings);
        putStringAndCommit(key, value);
    }

    public String[] getStringArray(String key, String[] defValue) {
        String persistentValue = getString(key, "");
        if (TextUtils.isEmpty(persistentValue) || !persistentValue.startsWith(ARRAY_TAG) || !persistentValue.contains(",")) {
            return defValue;
        }
        return persistentValue.substring(ARRAY_TAG.length()).split(",");
    }

    public Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    public void remove(String key) {
        if (mEditor == null) {
            mEditor = mSharedPreferences.edit();
        }
        mEditor.remove(key);
    }

    public void removeAndCommit(String key) {
        remove(key);
        mEditor.commit();
        mEditor = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (null != mPreferencesListeners) {
            for (int i = mPreferencesListeners.size() - 1; i >= 0; i--) {
                (mPreferencesListeners.get(i)).afterChanged(preferences, key);
            }
        }
    }

    public void registerChangeListener() {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void unregisterChangeListener() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public static interface PreferencesListener {
        public void afterChanged(SharedPreferences preferences, String key);
    }

    private String convertListToString(String[] contexts) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < contexts.length; i++) {
            sb.append(contexts[i]);
            if (i + 1 < contexts.length) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
