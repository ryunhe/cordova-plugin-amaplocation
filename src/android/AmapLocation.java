package org.apache.cordova.amaplocation;

import android.app.Activity;
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
    public static final String TAG = AmapLocation.class.getName();
    public static final String ACTION_GET = "getCurrentPosition";

    CallbackContext mCallbackContext;
    LocationManagerProxy mLocationManagerProxy;

    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        mCallbackContext = callbackContext;

        Boolean result = false;

        if (ACTION_GET.equalsIgnoreCase(action)) {
            getCurrentPosition();
            result = true;
        }

        return result;
    }

    @Override
    public void onDestroy() {
        if (null != mLocationManagerProxy) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
            mLocationManagerProxy = null;
        }
        super.onDestroy();
    }

    private void getCurrentPosition() {
        final Activity activity = cordova.getActivity();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mLocationManagerProxy) {
                    mLocationManagerProxy = LocationManagerProxy.getInstance(activity);
                }

                mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 0, AmapLocation.this);
                mLocationManagerProxy.setGpsEnable(true);
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            try {
                JSONObject coords = new JSONObject();
                coords.put("latitude", aMapLocation.getLatitude());
                coords.put("longitude", aMapLocation.getLongitude());
                coords.put("altitude", aMapLocation.getAltitude());
                coords.put("accuracy", aMapLocation.getAccuracy());
                coords.put("speed", aMapLocation.getSpeed());

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("coords", coords);
                jsonObj.put("timestamp", aMapLocation.getTime());

                Log.d(TAG, "onLocationChanged:" + jsonObj.toString());

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

