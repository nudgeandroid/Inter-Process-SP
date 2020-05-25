package im.qingtui.multiprocesssp.lib

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Process
import android.support.annotation.Keep
import android.text.TextUtils
import im.qingtui.multiprocesssp.lib.childprocess.RemoteSharedPreferences
import java.util.*

/**
 * 跨进程调用主进程的SP
 * Date:2019-05-30
 *
 * @author KongMing
 */
@Keep
object RemoteSP {
    var sharedPreferencesWeakHashMap =
        WeakHashMap<String, SharedPreferences>()

    @JvmStatic
    @Deprecated(replaceWith = ReplaceWith("getIPSharedPreferences"), message = "")
    fun createMainProcessSP(
        context: Context,
        name: String,
        mode: Int
    ): SharedPreferences {
        return getIPSharedPreferences(context, name, mode)
    }

    /**
     * 创建一个主进程的Sp
     *
     * @param context
     * @param name
     * @param mode
     * @return
     */
    @JvmStatic
    fun getIPSharedPreferences(
        context: Context,
        name: String,
        mode: Int
    ): SharedPreferences {
        return if (isMainProcess(context)) {
            context.getSharedPreferences(name, mode)
        } else {
            synchronized(sharedPreferencesWeakHashMap) {
                sharedPreferencesWeakHashMap.getOrPut(name) {
                    RemoteSharedPreferences(context, name, mode)
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
    private fun getCurProcessName(context: Context): String? {
        val pid = Process.myPid()
        return (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.runningAppProcesses?.find { appProcess ->
            appProcess.pid == pid
        }?.processName
    }

    /**
     * 是否为主进程
     *
     * @return
     */
    fun isMainProcess(context: Context): Boolean {
        return getCurProcessName(context) == context.packageName
    }
}