package cn.qingtui.multiprocesssp.lib.childprocess;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.JsonWriter;

import cn.qingtui.multiprocesssp.lib.RemoteProviderAccessor;

import org.json.JSONArray;

import java.util.Set;

/**
 * 描述Description
 * Date:2019-05-30
 *
 * @author KongMing
 */
public class RemoteEditor implements SharedPreferences.Editor {
    RemoteProviderAccessor providerAccessor;

    public RemoteEditor(RemoteProviderAccessor providerAccessor) {
        this.providerAccessor = providerAccessor;
    }

    @Override
    public SharedPreferences.Editor putString(String key, @Nullable String value) {
        providerAccessor.put(key, String.class, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, @Nullable Set<String> values) {
        providerAccessor.put(key, Set.class, values);
        return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        providerAccessor.put(key, int.class, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        providerAccessor.put(key, long.class, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        providerAccessor.put(key, float.class, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        providerAccessor.put(key, boolean.class, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        providerAccessor.remove(key);
        return this;
    }

    @Override
    public SharedPreferences.Editor clear() {
        providerAccessor.clear();
        return this;
    }

    @Override
    public boolean commit() {
        return providerAccessor.commit();
    }

    @Override
    public void apply() {
        providerAccessor.apply();
    }
}
