package org.apache.cordova.calllistener;

import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class CallListener extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("show".equals(action)) {
          Toast.makeText(cordova.getActivity(), "CallListener show " + args.get(0), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

}
