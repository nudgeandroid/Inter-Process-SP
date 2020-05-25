# 支持多进程同步读写SharedPreferences的工具库

## 使用方式

### 1.添加依赖
目前仅通过私有maven仓库提供
```
repositories {
    maven {
        url "https://dl.bintray.com/nudgeandroid/maven"
    }
}
```
添加私有仓库后，在项目依赖中增加：

```gradle
 implementation "im.qingtui.android:multi_process_sp:0.1.9"
```

### 2.获取SharedPreferences并进行操作

kotlin:
```kotlin
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
```

java:
```
    SharedPreferences sp = RemoteSP.createMainProcessSP(context, "test", Context.MODE_MULTI_PROCESS);
    sp.edit().putString("pushToken", "{\"huawei\":\"123123123123123123123123123\"}").commit();
```

## 设计原理
通过contentProvider实现跨进程数据访问，并通过广播实现数据更新后跨进程回调。
