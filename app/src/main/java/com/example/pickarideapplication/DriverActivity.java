package com.example.pickarideapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends AppCompatActivity {
    private LocationListener locationListener;
    private LocationManager locationManager;
    private Button requestsTextView;
    private ListView requestList;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        requestsTextView = findViewById(R.id.requests);
        requestList = findViewById(R.id.requestList);
        arrayList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter(DriverActivity.this,android.R.layout.simple_list_item_1,arrayList);
        requestList.setAdapter(arrayAdapter);
        arrayList.clear();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            } catch (Exception e){
                Toast.makeText(DriverActivity.this,e+" ",Toast.LENGTH_SHORT).show();
            }
        }
        requestsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        updateRequestList(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                if (Build.VERSION.SDK_INT >= 23){
                    if (ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DriverActivity.this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },1000);
                    } else {
                        try {
                           // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                            Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            updateRequestList(currentDriverLocation);
                        } catch (Exception e){
                            Toast.makeText(DriverActivity.this,"Error "+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void updateRequestList(Location currentDriverLocation) {
        if (currentDriverLocation != null){
            arrayList.clear();
            final ParseGeoPoint driverCurrentLocation = new ParseGeoPoint(currentDriverLocation.getLatitude(), currentDriverLocation.getLongitude());
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("RequestCar");
            parseQuery.whereNear("userLocation",driverCurrentLocation);
            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseObject distance : objects) {
                                Double distanceFromPassenger = driverCurrentLocation.distanceInKilometersTo((ParseGeoPoint) distance.get("userLocation"));
                                float roundedDistance = Math.round(distanceFromPassenger * 1000) / 1000;
                                arrayList.add(distance.get("username") + " is " + roundedDistance + " K.M. away from your location");
                            }
                        } else {
                            Toast.makeText(DriverActivity.this,"You have no request",Toast.LENGTH_SHORT).show();
                        }
                        arrayAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DriverActivity.this,"Something is wrong",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(DriverActivity.this,"Please enable your location",Toast.LENGTH_SHORT).show();

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
//                Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                updateRequestList(currentDriverLocation);
            }

        }
    }
}
