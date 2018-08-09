package org.apache.cordova.calllistener;

import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class CallListener extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Toast.makeText(cordova.getActivity(), "CallListener", Toast.LENGTH_LONG).show();
        if ("show".equals(action)) {
          Toast.makeText(cordova.getActivity(), "show", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

}
