package org.apache.cordova.calllistener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.os.Build;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import rx.functions.Action1;

public class CallListener extends CordovaPlugin {

    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private CallbackContext callbackContext;
    private long endDate;

    @SuppressLint("MissingPermission")
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("callMobile".equals(action)) {
            callMobile(args.getString(0));
            return true;
        } else if ("addListener".equals(action)) {
            addListener(callbackContext);
            return true;
        } else if ("getCallInfo".equals(action)) {
            getCallInfo(args.getString(0),callbackContext);
            return true;
        }
        return false;
    }

    /* 拨打电话 */
    private void callMobile(final String mobile) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new RxPermissions(cordova.getActivity())
                        .request(Manifest.permission.CALL_PHONE)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse("tel:" + mobile));
                                        cordova.getActivity().startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
            }
        });
    }

    /* 添加电话状态监听 */
    private void addListener(final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new RxPermissions(cordova.getActivity())
                        .request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    CallListener.this.callbackContext = callbackContext;
                                }
                            }
                        });
            }
        });
    }

    /* 获取通话信息 */
    private void getCallInfo(final String mobile,final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new RxPermissions(cordova.getActivity())
                        .request(Manifest.permission.READ_CALL_LOG)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean) {
                                    try {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                CallInfo model = queryCallInfo(mobile);
                                                if (Build.BRAND.equalsIgnoreCase("huawei") || Build.BRAND.equalsIgnoreCase("honor") || Build.BRAND.equalsIgnoreCase("meizu")) {
                                                    if (model.duration > 0) {
                                                        if (endDate - (model.date + model.duration * 1000) < 5000) {//如果5秒内电话未接通则认为没打通
                                                            model.duration = 0;
                                                        }
                                                    }
                                                }
                                                JSONObject object = new JSONObject();
                                                try {
                                                    object.put("start", formaDatet(model.date));
                                                    object.put("end", formaDatet(model.date + model.duration*1000));
                                                    object.put("duration", model.duration);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                callbackContext.success(object);
                                                endDate = 0;
                                            }
                                        }, 1000);
                                    } catch (Exception e) {
                                        callbackContext.success(0);
                                    }
                                } else {
                                    callbackContext.success(0);
                                }
                            }
                        });
            }
        });
    }


    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        registerListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterListener();
    }

    private String formaDatet(long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 注册监听电话状态
     */
    private void registerListener() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE://空闲
                        endDate = System.currentTimeMillis();
                        success(1);
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃
                        success(2);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://通话
                        success(3);
                        break;
                }
            }
        };
        telephonyManager = (TelephonyManager) cordova.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 取消监听电话状态
     */
    private void unregisterListener() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    private void success(int message) {
        if (callbackContext != null) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    private CallInfo queryCallInfo(String mobile) {
        CallInfo model = new CallInfo();
        Cursor cursor = cordova.getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        }, CallLog.Calls.NUMBER + "=?", new String[]{mobile}, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cursor.moveToFirst()) {
            model.number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            model.type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            model.date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            model.duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
        }
        return model;
    }

    private class CallInfo {
        public String number;//呼叫号码
        public int type;//呼叫类型1来电、2去电、3未接、5拒接
        public long date;//呼出时间
        public long duration;//通话时长
    }

}