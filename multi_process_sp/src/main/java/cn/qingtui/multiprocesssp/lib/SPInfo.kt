package cn.qingtui.multiprocesssp.lib

import android.content.SharedPreferences

/**
 * 描述Description
 * Date:2020/5/20
 * @author KongMing
 */
data class SPInfo(val name: String, val sharedPreferences: SharedPreferences) {
    val edit: SharedPreferences.Editor by lazy {
        sharedPreferences.edit()
    }
}