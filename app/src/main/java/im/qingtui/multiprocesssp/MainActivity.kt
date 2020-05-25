package im.qingtui.multiprocesssp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(applicationContext, MyIntentService::class.java))
    }


    fun onClick(v: View) {
        startActivity(Intent(this, Main2Activity::class.java))
    }
}
