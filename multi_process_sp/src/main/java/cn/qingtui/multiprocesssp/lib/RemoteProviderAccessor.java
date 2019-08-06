package cn.qingtui.multiprocesssp.lib;

import java.util.Map;

/**
 * 远程provider 的访问器
 * Date:2019-05-30
 *
 * @author KongMing
 */
public interface RemoteProviderAccessor {

    public <Value> void put(String key, Class<Value> valueClass, String value);

    public <Value> void put(String key, Value value);

    public <Value> Value get(String key, Class<Value> valueClass, Value defaultValue);

    void remove(String key);

    boolean commit();

    void apply();

    Map<String,?> getAll();

    void clear();
}
