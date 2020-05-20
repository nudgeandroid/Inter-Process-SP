package cn.qingtui.multiprocesssp.lib;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 远程provider 的访问器
 * Date:2019-05-30
 *
 * @author KongMing
 */
public class RemoteProviderAccessorImpl implements RemoteProviderAccessor {
    private String spName;
    private int mode;
    private ContentResolver contentResolver;
    private Uri uri;
    private String providerAuthor;

    public RemoteProviderAccessorImpl(Context context, String spName, int mode) {
        this.spName = spName;
        this.mode = mode;
        this.contentResolver = context.getContentResolver();
        initProviderUri(context);
    }

    private void initProviderUri(Context context) {
        String provider_authorities = StringUtils.getSPProviderAuthor(context);
        providerAuthor = "content://" + provider_authorities + "/sp";
        uri = Uri.parse(providerAuthor);
    }

    @Override
    public <Value> void put(String key, Class<Value> valueClass, String value) {
        ContentValues values = new ContentValues();
        values.put("spName", spName);
        values.put("mode", mode);
        values.put("key", key);
        values.put("valueClass", value.getClass().getName());
        values.put("value", value);
        Uri resultUri = contentResolver.insert(uri, values);
    }

    @Override
    public void put(String key, Object value) {
        ContentValues values = new ContentValues();
        values.put("spName", spName);
        values.put("mode", mode);
        values.put("key", key);
        String valueClass = value.getClass().getName();
        values.put("valueClass", valueClass);

        if (int.class.isInstance(value) || Integer.class.isInstance(value)) {
            values.put("value", (int) value);
        } else if (float.class.isInstance(value) || Float.class.isInstance(value)) {
            values.put("value", (float) value);
        } else if (boolean.class.isInstance(value) || Boolean.class.isInstance(value)) {
            values.put("value", (boolean) value);
        } else if (String.class.isInstance(value)) {
            values.put("value", (String) value);
        } else if (long.class.isInstance(value) || Long.class.isInstance(value)) {
            values.put("value", (long) value);
        } else if (Set.class.isInstance(value)) {
            String valueStr = StringUtils.fromSet((Set<String>) value);
            values.put("value", valueStr);
        } else {
            values.put("value", (String) value);
        }
        Uri resultUri = contentResolver.insert(uri, values);
    }

    @Override
    public <Value> Value get(String key, Class<Value> valueClass, Value defaultValue) {
        Cursor cursor = contentResolver.query(uri,
                new String[]{"key", "valueClass", "value"},
                "spName=? and mode=? and key=? and valueClass=? and defaultValue=?",
                new String[]{spName, String.valueOf(mode), key, valueClass.getName(), defaultValue != null ? String.valueOf(defaultValue) : null},
                null);
        Value value = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String valueClassName = cursor.getString(1);
                String valueStr = cursor.getString(2);
                if (valueStr != null) {
                    value = getValue(valueClass, defaultValue, valueStr);
                }
                break;
            }
            cursor.close();
        }
        return value != null ? value : defaultValue;
    }

    private <Value> Value getValue(Class<Value> valueClass, Value defaultValue, String valueStr) {
        Value value = null;
        if (Integer.class.equals(valueClass)) {
            value = (Value) Integer.valueOf(valueStr);
        } else if (Float.class.equals(valueClass)) {
            value = (Value) Float.valueOf(valueStr);
        } else if (Boolean.class.equals(valueClass)) {
            value = (Value) Boolean.valueOf(valueStr);
        } else if (String.class.equals(valueClass)) {
            value = (Value) String.valueOf(valueStr);
        } else if (Long.class.equals(valueClass)) {
            value = (Value) Long.valueOf(valueStr);
        } else if (Set.class.equals(valueClass)) {
            try {
                JSONArray jsonArray = new JSONArray(valueStr);
                Set<String> stringSet = new HashSet<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    stringSet.add((String) jsonArray.get(i));
                }
                value = (Value) stringSet;
            } catch (JSONException e) {
                e.printStackTrace();
                value = defaultValue;
            }
        } else {
            value = (Value) valueStr;
        }
        return value;
    }

    @Override
    public void remove(String key) {
        contentResolver.delete(uri, "spName=? and mode=? and key=?", new String[]{spName, String.valueOf(mode), key});
    }

    @Override
    public boolean commit() {
        return 1 == contentResolver.update(uri, new ContentValues(), "spName=? and mode=? and commit=?", new String[]{spName, String.valueOf(mode), "true"});
    }

    @Override
    public void apply() {
        contentResolver.update(uri, new ContentValues(), "spName=? and mode=? and commit=?", new String[]{spName, String.valueOf(mode), "false"});
    }

    @Override
    public Map<String, ?> getAll() {
        Cursor cursor = contentResolver.query(uri,
                new String[]{"key", "valueClass", "value"},
                "spName=? and mode=?",
                new String[]{spName, String.valueOf(mode)},
                null);
        Map<String, Object> values = null;
        if (cursor != null) {
            values = new HashMap<>();
            while (cursor.moveToNext()) {
                String key = cursor.getString(0);
                String valueClassName = cursor.getString(1);
                String valueStr = cursor.getString(2);
                try {
                    Object value = getValue(Class.forName(valueClassName), null, valueStr);
                    values.put(key, value);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return values;
    }

    @Override
    public void clear() {
        contentResolver.delete(uri, "spName=? and mode=?", new String[]{spName, String.valueOf(mode)});
    }
}
