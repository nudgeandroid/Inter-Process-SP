package im.qingtui.multiprocesssp

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import im.qingtui.multiprocesssp.lib.RemoteSP
import java.util.*

class Main2Activity : AppCompatActivity() {
    val sp: SharedPreferences by lazy {
        RemoteSP.createMainProcessSP(this, "test", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    fun onClick(v: View) {
        sp.edit().apply {
            putStringSet("testSet", setOf("wo", "hahaha", "kkkkk", UUID.randomUUID().toString()))
        }.commit()

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

    fun onClick2(v: View) {
        sp.getStringSet("testSet", setOf("default")).also {
            Toast.makeText(
                applicationContext,
                "testSet:$it",
                Toast.LENGTH_LONG
            ).show()
        }

    }
}
