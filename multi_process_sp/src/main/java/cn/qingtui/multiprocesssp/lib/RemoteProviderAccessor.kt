package cn.qingtui.multiprocesssp.lib

/**
 * 远程provider 的访问器
 * Date:2019-05-30
 *
 * @author KongMing
 */
interface RemoteProviderAccessor {
    fun <Value> put(
        key: String,
        valueClass: Class<Value>?,
        value: Value?
    )

    fun <Value> put(key: String, value: Value?)

    operator fun <Value> get(
        key: String,
        valueClass: Class<Value>,
        defaultValue: Value?
    ): Value?

    fun remove(key: String)
    fun commit(): Boolean
    fun apply()
    fun clear()
    fun getAll(): Map<String?, *>?
}