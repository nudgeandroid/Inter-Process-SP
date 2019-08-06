package cn.qingtui.multiprocesssp.lib.childprocess;

import android.content.*;
import android.support.annotation.Nullable;
import cn.qingtui.multiprocesssp.lib.RemoteProviderAccessor;
import cn.qingtui.multiprocesssp.lib.RemoteProviderAccessorImpl;
import cn.qingtui.multiprocesssp.lib.SharedPreferencesProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 调用远程的share
 * Date:2019-05-30
 *
 * @author KongMing
 */
public class RemoteSharedPreferences extends BroadcastReceiver implements SharedPreferences {
    RemoteProviderAccessor providerAccessor;
    Editor editor;
    Set<OnSharedPreferenceChangeListener> listeners = new HashSet<>();
    Context context;
    private String spName;
    private int mode;

    public RemoteSharedPreferences(Context context, String spName, int mode) {
        this.spName = spName;
        this.mode = mode;
        this.providerAccessor = new RemoteProviderAccessorImpl(context, spName, mode);
        editor = new RemoteEditor(providerAccessor);
        this.context = context;
    }

    @Override
    public Map<String, ?> getAll() {
        return providerAccessor.getAll();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return providerAccessor.get(key, String.class, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return providerAccessor.get(key, Set.class, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return providerAccessor.get(key, Integer.class, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return providerAccessor.get(key, Long.class, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return providerAccessor.get(key, Float.class, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return providerAccessor.get(key, Boolean.class, defValue);
    }

    @Override
    public boolean contains(String key) {
        return providerAccessor.get(key, String.class, null) != null;
    }

    @Override
    public Editor edit() {
        return editor;
    }

    AtomicBoolean isRegisteredReceiver = new AtomicBoolean(false);

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.add(listener);
        if (listeners.size() > 0 && isRegisteredReceiver.compareAndSet(false, true)) {
            context.registerReceiver(this, new IntentFilter(SharedPreferencesProvider.getBroadcastAction(context)));
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.remove(listener);
        if (listeners.size() == 0 && isRegisteredReceiver.compareAndSet(true, false)) {
            context.unregisterReceiver(this);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String spName = intent.getStringExtra("spName");
        if (this.spName.equals(spName)) {
            String key = intent.getStringExtra("key");
            for (OnSharedPreferenceChangeListener listener : listeners) {
                listener.onSharedPreferenceChanged(this, key);
            }
        }
    }
}
