package im.qingtui.multiprocesssp.lib

import android.content.SharedPreferences

/**
 * SharedPreferences信息
 * Date:2020/5/20
 * @author KongMing
 */
data class SPInfo(val name: String, val sharedPreferences: SharedPreferences) {
    val edit: SharedPreferences.Editor by lazy {
        sharedPreferences.edit()
    }
}