package im.qingtui.multiprocesssp.lib

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONException
import java.util.*

/**
 * 远程provider 的访问器
 * Date:2019-05-30
 *
 * @author KongMing
 */
class RemoteProviderAccessorImpl(
    context: Context,
    private val spName: String,
    private val mode: Int
) : RemoteProviderAccessor {
    private val contentResolver: ContentResolver by lazy {
        context.contentResolver
    }
    private var uri: Uri? = null
    private var providerAuthor: String? = null
    private fun initProviderUri(context: Context) {
        val providerAuthorities =
            StringUtils.getSPProviderAuthor(context)
        providerAuthor = "content://$providerAuthorities/sp"
        uri = Uri.parse(providerAuthor)
    }

    override fun <Value> put(
        key: String,
        valueClass: Class<Value>?,
        value: Value?
    ) {
        val values = ContentValues()
        values.put("spName", spName)
        values.put("mode", mode)
        values.put("key", key)
        putValue(valueClass, value, values)
        val resultUri = contentResolver.insert(uri, values)
    }

    override fun <Value> put(key: String, value: Value?) {
        val values = ContentValues()
        values.put("spName", spName)
        values.put("mode", mode)
        values.put("key", key)
        putValue(null, value, values)
        val resultUri = contentResolver.insert(uri, values)
    }

    private fun putValue(
        cls: Class<*>?,
        value: Any?,
        values: ContentValues
    ) {
        val valClass = cls ?: value!!::class.java
        values.put("valueClass", valClass?.name)
        when (valClass) {
            Int::class.java, Int::class.javaPrimitiveType ->
                values.put("value", value as? Int)
            Float::class.javaPrimitiveType, Float::class.java ->
                values.put("value", value as? Float)
            Boolean::class.javaPrimitiveType, Boolean::class.java -> values.put(
                "value",
                value as? Boolean
            )
            String::class.java ->
                values.put("value", value as? String)
            Long::class.javaPrimitiveType, Long::class.java ->
                values.put("value", value as? Long)
            Set::class.java -> {
                val valueStr = StringUtils.fromSet(value as? Set<String?>)
                values.put("value", valueStr)
                values.put("valueClass", MutableSet::class.java.name)
            }
            else -> {
                values.put("value", value as? String)
                values.put("valueClass", String::class.java.name)
            }
        }
    }

    override fun <Value> get(
        key: String,
        valueClass: Class<Value>,
        defaultValue: Value?
    ): Value? {
        val cursor = contentResolver.query(
            uri,
            arrayOf("key", "valueClass", "value"),
            "spName=? and mode=? and key=? and valueClass=? and defaultValue=?",
            arrayOf(
                spName,
                mode.toString(),
                key,
                valueClass.name,
                defaultValue?.toString()
            ),
            null
        )
        var value: Value? = null
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val valueClassName = cursor.getString(1)
                val valueStr = cursor.getString(2)
                if (valueStr != null) {
                    value = getValue(valueClass, defaultValue, valueStr)
                }
                break
            }
            cursor.close()
        }
        return value ?: defaultValue
    }

    private fun <Value> getValue(
        valueClass: Class<Value>?,
        defaultValue: Value?,
        valueStr: String
    ): Value? {
        var value: Value? = null
        value = if (Int::class.java == valueClass) {
            Integer.valueOf(valueStr) as Value
        } else if (Float::class.java == valueClass) {
            java.lang.Float.valueOf(valueStr) as Value
        } else if (Boolean::class.java == valueClass) {
            java.lang.Boolean.valueOf(valueStr) as Value
        } else if (String::class.java == valueClass) {
            valueStr as Value
        } else if (Long::class.java == valueClass) {
            java.lang.Long.valueOf(valueStr) as Value
        } else if (MutableSet::class.java == valueClass) {
            try {
                val jsonArray = JSONArray(valueStr)
                val stringSet: MutableSet<String> =
                    HashSet()
                for (i in 0 until jsonArray.length()) {
                    stringSet.add(jsonArray[i] as String)
                }
                stringSet as Value
            } catch (e: JSONException) {
                e.printStackTrace()
                defaultValue
            }
        } else {
            valueStr as Value
        }
        return value
    }

    override fun remove(key: String) {
        contentResolver.delete(
            uri,
            "spName=? and mode=? and key=?",
            arrayOf(spName, mode.toString(), key)
        )
    }

    override fun commit(): Boolean {
        return 1 == contentResolver.update(
            uri,
            ContentValues(),
            "spName=? and mode=? and commit=?",
            arrayOf(spName, mode.toString(), "true")
        )
    }

    override fun apply() {
        contentResolver.update(
            uri,
            ContentValues(),
            "spName=? and mode=? and commit=?",
            arrayOf(spName, mode.toString(), "false")
        )
    }

    override fun getAll(): Map<String?, *>? {
        val cursor = contentResolver.query(
            uri, arrayOf("key", "valueClass", "value"),
            "spName=? and mode=?", arrayOf(spName, mode.toString()),
            null
        )
        var values: MutableMap<String?, Any?>? = null
        if (cursor != null) {
            values = HashMap()
            while (cursor.moveToNext()) {
                val key = cursor.getString(0)
                val valueClassName = cursor.getString(1)
                val valueStr = cursor.getString(2)
                try {
                    val value =
                        getValue(Class.forName(valueClassName), null, valueStr)!!
                    values[key] = value
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            cursor.close()
        }
        return values
    }

    override fun clear() {
        contentResolver.delete(
            uri,
            "spName=? and mode=?",
            arrayOf(spName, mode.toString())
        )
    }

    init {
        initProviderUri(context)
    }
}