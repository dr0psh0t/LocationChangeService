package www.locationchange.com.locationchangeservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LocationService extends Service
{
    public static final String ACTION_LOCATION_BROADCAST =
            LocationService.class.getName()+"LocationBroadcast";

    private static final String TAG = "GPS_SERVICE";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    public void sendBroadcastMessage(double lat, double lng, int locationCounter)
    {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("counter", locationCounter);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private class GPSListener implements LocationListener
    {
        Location mLastLocation;
        int locationCounter = 0;

        public GPSListener(String provider)
        {
            Log.d(TAG, "LocationListener "+provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            ++locationCounter;
            sendBroadcastMessage(location.getLatitude(), location.getLongitude(), locationCounter);
            Log.d(TAG, "onLocationChanged: "+location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.d(TAG, "onProviderDisabled: "+provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.d(TAG, "onProviderEnabled: "+provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.d(TAG, "onStatusChanged: "+provider);
        }
    }

    GPSListener[] mGpsListeners = new GPSListener[]{
            new GPSListener(LocationManager.GPS_PROVIDER),
            new GPSListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        try
        {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mGpsListeners[1]);
        }
        catch (SecurityException ex) {
            Log.e(TAG, ex.toString());
        }
        catch (IllegalArgumentException ex) {
            Log.e(TAG, ex.toString());
        }

        try
        {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mGpsListeners[0]);
        }
        catch (SecurityException ex) {
            Log.e(TAG, ex.toString());
        }
        catch (IllegalArgumentException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null)
        {
            for (int i = 0; i < mGpsListeners.length; i++)
            {
                try {
                    mLocationManager.removeUpdates(mGpsListeners[i]);
                }
                catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager()
    {
        Log.e(TAG, "initializeLocationManager");

        if (mLocationManager == null)
        {
            mLocationManager = (LocationManager)
                    getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}