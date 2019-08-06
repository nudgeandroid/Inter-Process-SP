package cn.qingtui.multiprocesssp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import cn.qingtui.multiprocesssp.lib.RemoteSP;

/**
 * 描述Description
 * Date:2019-05-30
 *
 * @author KongMing
 */
public class SPBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = RemoteSP.createMainProcessSP(context, "test", Context.MODE_MULTI_PROCESS);
        Log.i("SPBroadcast", intent.toString());
        sp.edit().putString("pushToken", "{\"huawei\":\"123123123123123123123123123\"}").commit();
        Log.i("SPBroadcast", sp.getString("pushToken", "{}"));
    }
}
