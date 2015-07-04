package org.apache.cordova.amaplocation;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AmapLocation extends CordovaPlugin implements AMapLocationListener {
    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";

    CallbackContext mCallbackContext;
    LocationManagerProxy mLocationManagerProxy;

    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {

        mCallbackContext = callbackContext;

        Boolean result = false;

        if (ACTION_START.equalsIgnoreCase(action) && null == mLocationManagerProxy) {
            result = true;

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLocationManagerProxy = LocationManagerProxy.getInstance(cordova.getActivity());
                    mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 10 * 1000, 10, AmapLocation.this);
                    mLocationManagerProxy.setGpsEnable(false);
                }

            });
        } else if (ACTION_STOP.equalsIgnoreCase(action)) {
            result = true;

            terminate();
            callbackContext.success(200);
        }

        return result;
    }

    @Override
    public void onDestroy() {
        terminate();
        super.onDestroy();
    }

    private void terminate() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            try {
                JSONObject coords = new JSONObject();
                coords.put("latitude", aMapLocation.getLatitude());
                coords.put("longitude", aMapLocation.getLongitude());

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("coords", coords);
                jsonObj.put("accuracy", aMapLocation.getAccuracy());

                Log.d("AmapLocationPlugin", "run: " + jsonObj.toString());

                mCallbackContext.success(jsonObj);
            } catch (JSONException e) {
                mCallbackContext.error(e.getMessage());
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

