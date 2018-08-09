package org.apache.cordova.calllistener;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.json.JSONArray;
import org.json.JSONException;

public class CallListener extends CordovaPlugin {

  private TelephonyManager telephonyManager;
  private PhoneStateListener phoneStateListener;
  private CallbackContext callbackContext;
  private static final int READ_CALL_LOG = 1;
  private String mobile;
  private static final int PROCESS_OUTGOING_CALLS = 2;


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

  private void unregisterListener() {
    if (telephonyManager != null) {
      telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
      telephonyManager = null;
    }
    this.callbackContext = null;
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext;
    if ("getCallTime".equals(action)) {
      mobile = args.getString(0);
      if (PermissionHelper.hasPermission(this, Manifest.permission.READ_CALL_LOG)) {
        getCallTime(mobile);
      } else {
        PermissionHelper.requestPermission(this, READ_CALL_LOG, Manifest.permission.READ_CALL_LOG);
      }
      return true;
    } else if ("listener".equals(action)) {
      if (!PermissionHelper.hasPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) || !PermissionHelper.hasPermission(this, Manifest.permission.READ_PHONE_STATE)) {
        PermissionHelper.requestPermissions(this, PROCESS_OUTGOING_CALLS, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.READ_PHONE_STATE} );
      }
      return true;
    }
    return false;
  }


  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    super.onRequestPermissionResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case READ_CALL_LOG:
        getCallTime(mobile);
        break;
      case PROCESS_OUTGOING_CALLS:
        break;
    }
  }

  //  获取通话时长，单位秒
  private void getCallTime(String mobile) {
    try {
      int duration = 0;
      Cursor cursor = cordova.getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{
        CallLog.Calls.DATE,
        CallLog.Calls.DURATION
      }, CallLog.Calls.NUMBER + "=?", new String[]{mobile}, CallLog.Calls.DEFAULT_SORT_ORDER);
      if (cursor.moveToFirst()) {
        duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
      }
      callbackContext.success(duration);
    } catch (Exception e) {
      callbackContext.error(e.getMessage());
    }
  }

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    registerListener();
    Log.d("CallListener", "initialize");
  }

  @Override
  protected void pluginInitialize() {
    super.pluginInitialize();
    Log.d("CallListener", "pluginInitialize");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterListener();
    Log.d("CallListener", "onDestroy");
  }


  @Override
  public void onStart() {
    super.onStart();
    Log.d("CallListener", "onStart");
  }

  @Override
  public void onResume(boolean multitasking) {
    super.onResume(multitasking);
    Log.d("CallListener", "onResume");
  }

  @Override
  public void onPause(boolean multitasking) {
    super.onPause(multitasking);
    Log.d("CallListener", "onPause");
  }

  @Override
  public void onReset() {
    super.onReset();
    Log.d("CallListener", "onReset");
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.d("CallListener", "onStop");
  }

}
