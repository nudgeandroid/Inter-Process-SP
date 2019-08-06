package cn.qingtui.multiprocesssp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.qingtui.multiprocesssp.lib.RemoteSP

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(applicationContext, MyIntentService::class.java))
    }


    fun onClick(v: View) {
        val sp = RemoteSP.createMainProcessSP(this, "test", Context.MODE_PRIVATE)

        sp.edit()
            .putLong("time", System.currentTimeMillis())
            .commit()
        var strs = HashSet<String>();
        strs.add("13232")
        strs.add("1asdasd")
        strs.add("cczxczxczx")
        strs.add("3123")
        strs.add("dasdasd1asdsdfaasddasd")
        sp.edit()
            .putStringSet("set", strs)
            .putString("testnullstring", null)
            .putStringSet("testnullset", null)
            .putLong("testlong", 0)
            .putBoolean("testboolean", false)
            .putInt("testint", System.currentTimeMillis().toInt())
            .putFloat("testfloat", System.currentTimeMillis().toFloat())
            .commit()
    }
}
