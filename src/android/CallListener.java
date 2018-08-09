package org.apache.cordova.calllistener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import rx.functions.Action1;

public class CallListener extends CordovaPlugin {

  private TelephonyManager telephonyManager;
  private PhoneStateListener phoneStateListener;
  private CallbackContext callbackContext;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if ("getCallTime".equals(action)) {
      new RxPermissions(cordova.getActivity())
        .request(Manifest.permission.READ_CALL_LOG)
        .subscribe(new Action1<Boolean>() {
          @Override
          public void call(Boolean aBoolean) {
            if (aBoolean) {
              try {
                String mobile = args.getString(0);
                @SuppressLint("MissingPermission")
                Cursor cursor = cordova.getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.DATE, CallLog.Calls.DURATION}, CallLog.Calls.NUMBER + "=?", new String[]{mobile}, CallLog.Calls.DEFAULT_SORT_ORDER);
                int duration = 0;
                if (cursor.moveToFirst()) {
                  duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                }
                callbackContext.success(duration);
              } catch (Exception e) {
                e.printStackTrace();
                callbackContext.success(0);
              }
            } else {
              callbackContext.success(0);
            }
          }
        });
      return true;
    } else if ("listener".equals(action)) {
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
      return true;
    }
    return false;
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

  /**
   * 注册监听电话状态
   */
  private void registerListener() {
    phoneStateListener = new PhoneStateListener() {
      @Override
      public void onCallStateChanged(int state, String phoneNumber) {
        switch (state) {
          case TelephonyManager.CALL_STATE_IDLE://空闲
            if (callbackContext != null) {
              callbackContext.success(1);
            }
            break;
          case TelephonyManager.CALL_STATE_RINGING://响铃
            if (callbackContext != null) {
              callbackContext.success(2);
            }
            break;
          case TelephonyManager.CALL_STATE_OFFHOOK://通话
            if (callbackContext != null) {
              callbackContext.success(3);
            }
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

}
