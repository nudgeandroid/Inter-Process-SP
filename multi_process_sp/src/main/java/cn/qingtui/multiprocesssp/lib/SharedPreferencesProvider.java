package cn.qingtui.multiprocesssp.lib;

import android.content.*;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SP的provider，被远程调用
 * Date:2019-05-30
 *
 * @author KongMing
 */
public class SharedPreferencesProvider extends ContentProvider implements SharedPreferences.OnSharedPreferenceChangeListener {
    static String broadcastAction;
    Map<Integer, String> sharedPreferencesMap = new HashMap<>();

    @Override
    public boolean onCreate() {
        return true;
    }

    private SharedPreferences getSharedPreferences(String spName, int mode) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(spName, mode);
        sharedPreferencesMap.put(sharedPreferences.hashCode(), spName);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        return sharedPreferences;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(projection, 3);

        String spName = selectionArgs[0];
        int mode = Integer.parseInt(selectionArgs[1]);
        SharedPreferences sharedPreferences = getSharedPreferences(spName, mode);
        if (selectionArgs.length > 2) {
            String key = selectionArgs[2];
            String valueClassName = selectionArgs[3];
            String defaultValue = selectionArgs[4];
            findOne(cursor, sharedPreferences, key, valueClassName, defaultValue);
        } else {
            Map<String, ?> map = sharedPreferences.getAll();
            if (map != null && map.entrySet() != null) {
                for (Map.Entry<String, ?> stringEntry : map.entrySet()) {
                    Object value = null;
                    if (stringEntry.getValue() instanceof Set) {
                        value = StringUtils.fromSet((Set<String>) stringEntry.getValue());
                    } else {
                        value = stringEntry.getValue();
                    }
                    cursor.newRow().add(stringEntry.getKey())
                            .add(value.getClass().getName())
                            .add(String.valueOf(value));
                }
            }
        }
        return cursor;
    }

    private void findOne(MatrixCursor cursor, SharedPreferences sharedPreferences, String key, String valueClassName, String defaultValue) {
        Object value = null;
        if (Integer.class.getName().equals(valueClassName)) {
            value = sharedPreferences.getInt(key, Integer.valueOf(defaultValue));
        } else if (Long.class.getName().equals(valueClassName)) {
            value = sharedPreferences.getLong(key, Long.valueOf(defaultValue));
        } else if (Float.class.getName().equals(valueClassName)) {
            value = sharedPreferences.getFloat(key, Float.valueOf(defaultValue));
        } else if (String.class.getName().equals(valueClassName)) {
            value = sharedPreferences.getString(key, String.valueOf(defaultValue));
        } else if (Boolean.class.getName().equals(valueClassName)) {
            value = sharedPreferences.getBoolean(key, Boolean.valueOf(defaultValue));
        } else if (Set.class.getName().equals(valueClassName)) {
            Set vSet = sharedPreferences.getStringSet(key, StringUtils.toSet(defaultValue));
            value = StringUtils.fromSet(vSet);
        }
        cursor.newRow().add(key).add(valueClassName).add(value != null ? String.valueOf(value) : null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "xml/multi.process.sp";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String spName = values.getAsString("spName");
        int mode = values.getAsInteger("mode");

        String key = values.getAsString("key");
        String valueClassName = values.getAsString("valueClass");
        Object value = values.get("value");

        SharedPreferences sharedPreferences = getSharedPreferences(spName, mode);

        if (Integer.class.getName().equals(valueClassName)) {
            sharedPreferences.edit().putInt(key, (Integer) value);

        } else if (Long.class.getName().equals(valueClassName)) {
            sharedPreferences.edit().putLong(key, (Long) value);

        } else if (Float.class.getName().equals(valueClassName)) {
            sharedPreferences.edit().putFloat(key, (Float) value);

        } else if (String.class.getName().equals(valueClassName)) {
            sharedPreferences.edit().putString(key, (String) value);

        } else if (Boolean.class.getName().equals(valueClassName)) {
            sharedPreferences.edit().putBoolean(key, (Boolean) value);

        } else if (Set.class.getName().equals(valueClassName)) {
            sharedPreferences.edit().putStringSet(key, StringUtils.toSet((String) value));
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String spName = selectionArgs[0];
        int mode = Integer.parseInt(selectionArgs[1]);
        SharedPreferences sharedPreferences = getSharedPreferences(spName, mode);
        if (selectionArgs.length > 2) {
            String key = selectionArgs[2];
            sharedPreferences.edit().remove(key);
        } else {
            sharedPreferences.edit().clear();
        }
        return 1;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String spName = selectionArgs[0];
        int mode = Integer.parseInt(selectionArgs[1]);
        Boolean commit = Boolean.valueOf(selectionArgs[2]);

        SharedPreferences sharedPreferences = getSharedPreferences(spName, mode);
        int code;
        if (commit) {
            code = sharedPreferences.edit().commit() ? 1 : 0;
        } else {
            sharedPreferences.edit().apply();
            code = 1;
        }
        return code;
    }


    private void sendChangeBroadcast(String spName, String key) {
        Intent intent = new Intent(getBroadcastAction(getContext()));
        intent.putExtra("spName", spName);
        intent.putExtra("key", key);
        getContext().sendBroadcast(intent);
    }


    public static String getBroadcastAction(Context context) {
        if (broadcastAction != null) {
            return broadcastAction;
        }
        broadcastAction = StringUtils.getSPProviderAuthor(context) + ".broadcast";
        return broadcastAction;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String spName = sharedPreferencesMap.get(sharedPreferences.hashCode());
        if (spName != null) {
            sendChangeBroadcast(spName, key);
        }
    }
}
