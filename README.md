# 支持多进程同步读写SharedPreferences的工具库


## 使用方式

### 1.添加依赖

目前通过jcenter仓库提供

在项目依赖中增加：
```gradle
 implementation "im.qingtui.android:inter_process_sp:$latest_version"
```
### 2.获取SharedPreferences并进行操作

kotlin:
```kotlin
val sp = RemoteSP.getIPSharedPreferences(this, "test", Context.MODE_PRIVATE)
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

//注册回调，可在监听其他进程对数据的修改
sharedPreferences.registerOnSharedPreferenceChangeListener(this)

```

java:
```
    SharedPreferences sp = RemoteSP.getIPSharedPreferences(context, "test", Context.MODE_MULTI_PROCESS);
    sp.edit().putString("pushToken", "{\"huawei\":\"123123123123123123123123123\"}").commit();
```

## 设计原理
通过contentProvider将数据读写放到主进程统一操作，通过广播将改变回调给其他进程。