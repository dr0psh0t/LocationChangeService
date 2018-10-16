package www.locationchange.com.locationchangeservice;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvLat, tvLng, tvCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("change");

        tvLat = findViewById(R.id.tvLat);
        tvLng = findViewById(R.id.tvLng);
        tvCounter = findViewById(R.id.tvCounter);

        Intent i = new Intent(MainActivity.this, LocationService.class);
        MainActivity.this.startService(i);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent)
                    {
                        double lat = intent.getDoubleExtra("lat", 0);
                        double lng = intent.getDoubleExtra("lng", 0);
                        double counter = intent.getIntExtra("counter", 0);

                        tvLat.setText(String.valueOf(lat));
                        tvLng.setText(String.valueOf(lng));
                        tvCounter.setText(String.valueOf(counter));
                    }
                },
                new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );

        /*
         *  REQUESTING PERMISSIONS
         *
         * */
        int accessFineLocation = ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        int accessCoarseLocation = ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED &&
                accessFineLocation != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {

        for (int x = 0; x < permissions.length; ++x)
        {
            if (grantResults[x] == -1)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("Permission " + permissions[x] + " " +
                        "was denied.\n\nThe program will exit if permissions are denied.").
                        setTitle("Permission Denied");

                builder.setPositiveButton(R.string.app_name,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }
}