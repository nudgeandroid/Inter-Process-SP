package cn.qingtui.multiprocesssp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cn.qingtui.multiprocesssp.lib.RemoteSP

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    fun onClick(v: View) {
        val sp = RemoteSP.createMainProcessSP(this, "test", Context.MODE_PRIVATE)

//        sp.edit()
//            .putLong("time", System.currentTimeMillis())
//            .commit()
//        var strs = HashSet<String>();
//        strs.add("13232")
//        strs.add("1asdasd")
//        strs.add("cczxczxczx")
//        strs.add("3123")
//        strs.add("dasdasd1asdsdfaasddasd")
//        sp.edit()
//            .putStringSet("set", strs)
//            .putString("testnullstring", null)
//            .putStringSet("testnullset", null)
//            .putLong("testlong", 0)
//            .putBoolean("testboolean", false)
//            .putInt("testint", System.currentTimeMillis().toInt())
//            .putFloat("testfloat", System.currentTimeMillis().toFloat())
//            .commit()


        Toast.makeText(
            applicationContext,
            "aaaaaa:${sp.getBoolean("aaaaaaaaaa", false)}",
            Toast.LENGTH_LONG
        ).show()

    }
}
