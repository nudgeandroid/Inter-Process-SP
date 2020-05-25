package im.qingtui.multiprocesssp.lib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

/**
 * 字符串处理工具类
 * Date:2019-05-30
 *
 * @author KongMing
 */
public class StringUtils {
    public static String fromSet(Set<String> values) {
        if (values == null) return null;
        JSONArray jsonArray = new JSONArray();
        for (String value : values) {
            jsonArray.put(value);
        }
        return jsonArray.toString();
    }

    public static <Value> Value toValue(Class<Value> valueClass, Value defaultValue, String valueStr) {
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
            value = (Value) toSet((Set) defaultValue, valueStr);
        }
        return value;
    }

    public static Set toSet(String defaultValue, String valueStr) {
        if (valueStr == null) {
            valueStr = defaultValue;
        }
        return toSet(valueStr);
    }

    public static Set toSet(String valueStr) {
        Set value = null;
        if (valueStr == null) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray(valueStr);
            Set<String> stringSet = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                stringSet.add((String) jsonArray.get(i));
            }
            value = stringSet;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Set toSet(Set defaultValue, String valueStr) {
        if (valueStr == null) {
            return defaultValue;
        }
        Set value;
        try {
            JSONArray jsonArray = new JSONArray(valueStr);
            Set<String> stringSet = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                stringSet.add((String) jsonArray.get(i));
            }
            value = stringSet;
        } catch (JSONException e) {
            e.printStackTrace();
            value = defaultValue;
        }
        return value;
    }

    private static final String DEFAULT_TOKEN_PROVIDER_AUTHOR = "im.qingtui.android.multi.provider.sp";

    public static String getSPProviderAuthor(Context context) {
        String provider_authorities = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            provider_authorities = appInfo.metaData.getString("SP_Provider_Author");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(provider_authorities)) {
            provider_authorities = DEFAULT_TOKEN_PROVIDER_AUTHOR;
        }
        return provider_authorities;
    }
}
