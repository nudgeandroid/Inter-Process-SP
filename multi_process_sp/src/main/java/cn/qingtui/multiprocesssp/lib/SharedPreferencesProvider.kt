package cn.qingtui.multiprocesssp.lib

import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import java.util.*

/**
 * SP的provider，被远程调用
 * Date:2019-05-30
 *
 * @author KongMing
 */
class SharedPreferencesProvider : ContentProvider(),
    OnSharedPreferenceChangeListener {
    var spInfoSet: MutableSet<SPInfo> = HashSet()

    override fun onCreate(): Boolean {
        return true
    }

    private fun getSPInfo(spName: String, mode: Int): SPInfo {
        return spInfoSet.find { it.name == spName }
            ?: SPInfo(spName, context.getSharedPreferences(spName, mode)).also {
                spInfoSet.add(it)
                it.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
            }
    }

    private fun getSharedPreferences(spName: String, mode: Int): SharedPreferences {
        return spInfoSet.find { it.name == spName }?.sharedPreferences
            ?: context.getSharedPreferences(spName, mode).also {
                spInfoSet.add(SPInfo(spName, it))
                it.registerOnSharedPreferenceChangeListener(this)
            }
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = MatrixCursor(projection, 3)
        val spName = selectionArgs!![0]
        val mode = selectionArgs[1].toInt()
        val sharedPreferences = getSharedPreferences(spName, mode)
        if (selectionArgs.size > 2) {
            val key = selectionArgs[2]
            val valueClassName = selectionArgs[3]
            val defaultValue = selectionArgs[4]
            findOne(cursor, sharedPreferences, key, valueClassName, defaultValue)
        } else {
            val map = sharedPreferences.all
            if (map?.entries != null) {
                for ((key, value1) in map) {
                    var value: Any? = null
                    value = if (value1 is Set<*>) {
                        StringUtils.fromSet(value1 as Set<String?>?)
                    } else {
                        value1
                    }
                    cursor.newRow().add(key)
                        .add(value!!.javaClass.name)
                        .add(value.toString())
                }
            }
        }
        return cursor
    }

    private fun findOne(
        cursor: MatrixCursor,
        sharedPreferences: SharedPreferences,
        key: String,
        valueClassName: String,
        defaultValue: String
    ) {
        var value: Any? = null
        if (Int::class.java.name == valueClassName) {
            value = sharedPreferences.getInt(key, Integer.valueOf(defaultValue))
        } else if (Long::class.java.name == valueClassName) {
            value = sharedPreferences.getLong(key, java.lang.Long.valueOf(defaultValue))
        } else if (Float::class.java.name == valueClassName) {
            value = sharedPreferences.getFloat(key, java.lang.Float.valueOf(defaultValue))
        } else if (String::class.java.name == valueClassName) {
            value = sharedPreferences.getString(key, defaultValue)
        } else if (Boolean::class.java.name == valueClassName) {
            value = sharedPreferences.getBoolean(key, java.lang.Boolean.valueOf(defaultValue))
        } else if (MutableSet::class.java.name == valueClassName) {
            val vSet: Set<*>? = sharedPreferences.getStringSet(
                key,
                StringUtils.toSet(defaultValue) as? MutableSet<String>?
            )
            value = StringUtils.fromSet(vSet as? MutableSet<String>?)
        }
        cursor.newRow().add(key).add(valueClassName)
            .add(value?.toString())
    }

    override fun getType(uri: Uri): String? {
        return "xml/multi.process.sp"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val spName = values!!.getAsString("spName")
        val mode = values.getAsInteger("mode")
        val key = values.getAsString("key")
        val valueClassName = values.getAsString("valueClass")
        val value = values["value"]
        val edit = getSPInfo(spName, mode).edit

        if (Int::class.java.name == valueClassName) {
            edit.putInt(key, (value as Int))
        } else if (Long::class.java.name == valueClassName) {
            edit.putLong(key, (value as Long))
        } else if (Float::class.java.name == valueClassName) {
            edit.putFloat(key, (value as Float))
        } else if (String::class.java.name == valueClassName) {
            edit.putString(key, value as? String)
        } else if (Boolean::class.java.name == valueClassName) {
            edit.putBoolean(key, (value as Boolean))
        } else if (MutableSet::class.java.name == valueClassName) {
            edit.putStringSet(
                key,
                StringUtils.toSet(value as? String) as? MutableSet<String>?
            )
        }
        return uri
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val spName = selectionArgs!![0]
        val mode = selectionArgs[1].toInt()
        if (selectionArgs.size > 2) {
            val key = selectionArgs[2]
            getSPInfo(spName, mode).edit.remove(key)
        } else {
            getSPInfo(spName, mode).edit.clear()
        }
        return 1
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val spName = selectionArgs!![0]
        val mode = selectionArgs[1].toInt()
        val commit = java.lang.Boolean.valueOf(selectionArgs[2])
        val sharedPreferences = getSharedPreferences(spName, mode)
        val code: Int
        code = if (commit) {
            if (getSPInfo(spName, mode).edit.commit()) 1 else 0
        } else {
            getSPInfo(spName, mode).edit.apply()
            1
        }
        return code
    }

    private fun sendChangeBroadcast(spName: String, key: String) {
        val intent =
            Intent(getBroadcastAction(context))
        intent.putExtra("spName", spName)
        intent.putExtra("key", key)
        context.sendBroadcast(intent)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        spInfoSet.find { it.sharedPreferences == sharedPreferences }?.also {
            sendChangeBroadcast(it.name, key)
        }
    }

    companion object {
        var broadcastAction: String? = null

        @JvmStatic
        fun getBroadcastAction(context: Context?): String? {
            if (broadcastAction != null) {
                return broadcastAction
            }
            broadcastAction =
                StringUtils.getSPProviderAuthor(context) + ".broadcast"
            return broadcastAction
        }
    }
}