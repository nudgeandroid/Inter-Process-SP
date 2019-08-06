package cn.qingtui.multiprocesssp.lib;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import cn.qingtui.multiprocesssp.lib.childprocess.RemoteSharedPreferences;

import java.util.List;
import java.util.WeakHashMap;

/**
 * 跨进程调用主进程的SP
 * Date:2019-05-30
 *
 * @author KongMing
 */
public class RemoteSP {
    static WeakHashMap<String, SharedPreferences> sharedPreferencesWeakHashMap = new WeakHashMap<>();

    /**
     * 创建一个主进程的Sp
     *
     * @param context
     * @param name
     * @param mode
     * @return
     */
    public static SharedPreferences createMainProcessSP(Context context, String name, int mode) {
        if (isMainProcess(context)) {
            return context.getSharedPreferences(name, mode);
        } else {
            synchronized (sharedPreferencesWeakHashMap) {
                if (sharedPreferencesWeakHashMap.containsKey(name)) {
                    return sharedPreferencesWeakHashMap.get(name);
                } else {
                    SharedPreferences sharedPreferences = new RemoteSharedPreferences(context, name, mode);
                    sharedPreferencesWeakHashMap.put(name, sharedPreferences);
                    return sharedPreferences;
                }
            }
        }
    }


    /**
     * 获取当前进程名称
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = mActivityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcessInfoList) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return null;
    }

    /**
     * 是否为主进程
     *
     * @return
     */
    public static boolean isMainProcess(Context context) {
        String mProcessName = getCurProcessName(context);
        if (TextUtils.equals(mProcessName, context.getPackageName())) {
            return true;
        }
        return false;
    }
}
