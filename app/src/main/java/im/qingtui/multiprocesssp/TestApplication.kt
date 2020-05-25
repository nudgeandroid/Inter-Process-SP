package im.qingtui.multiprocesssp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import im.qingtui.multiprocesssp.lib.RemoteSP

import java.util.Timer
import java.util.TimerTask

/**
 * 描述Description
 * Date:2019-05-30
 *
 * @author KongMing
 */
class TestApplication : Application(), SharedPreferences.OnSharedPreferenceChangeListener {
    internal lateinit var sharedPreferences: SharedPreferences

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        var key1 = "testnullstring";
        val value = sharedPreferences.getString(key1, null);
        Log.i("onSPChanged", "key:$key1,val:$value")

        key1 = "testnullset";
        val value1 = sharedPreferences.getStringSet(key1, null);
        Log.i("onSPChanged", "key:$key1,val:$value1")

        val value2 = sharedPreferences.getStringSet("set", null);
        Log.i("onSPChanged", "key:set,val:$value2")


        val value3 = sharedPreferences.getLong("time", -100);
        Log.i("onSPChanged", "key:time,val:$value3")

        sharedPreferences.edit().putString("pushToken", "{\"huawei\":\"123123123123123123123123123\"}").commit()
        Log.i("onSPChanged", sharedPreferences.getString("pushToken", "{}"))
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = RemoteSP.createMainProcessSP(this, "test", Context.MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        RemoteSP.createMainProcessSP(this, "test", Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this)
        RemoteSP.createMainProcessSP(this, "test", Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this)

        RemoteSP.createMainProcessSP(this, "test", Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this)


        Log.i("RemoteSP", "put test1 test2")
        sharedPreferences.edit().putString("test1", "123").putString("test2", "456").commit()
        if (RemoteSP.isMainProcess(this)) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    Log.i("RemoteSP", "put test1 test2")
                    sharedPreferences.edit().putString("test1", "22222").putString("test2", "333333").commit()
                }
            }, 1000)

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    Log.i("RemoteSP", "remove test1")
                    sharedPreferences.edit().remove("test1").commit()

                    try {
                        Thread.sleep(2000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    Log.i("RemoteSP", "clear test")
                    sharedPreferences.edit().clear().commit()
                }
            }, 3000)
        }
    }
}
