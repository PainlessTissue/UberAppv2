package ptindustries.uberappv2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class riderActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean uberCalled = false;

    public RidersClass rider;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rider = new RidersClass();
        rider.setUsername(ParseUser.getCurrentUser().getUsername());
    }

    public void callUber(View view)
    {
        final Button uberButton = (Button) findViewById(R.id.callUberButton);
        if(!uberCalled)
        {
            if(rider.getLastKnownLocation() != null)
            {
                //since rider is a parseObject we can just set its location without having to find the username to set it (like in previous branch)
                rider.setGeoLocation(new ParseGeoPoint(rider.getLastKnownLocation().getLatitude(), rider.getLastKnownLocation().getLongitude()));

                rider.saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(ParseException e)
                    {
                        if(e == null)
                        {
                            uberCalled = true;
                            uberButton.setText(R.string.cancelUber);
                        }

                        else
                            Toast.makeText(riderActivity.this, "Something happened, try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        else //uber has been called
        {
            //since rider is a parseObject instead of just a class, we can delete it right from Parse
            //instead of looping through to find it (like in previous branch)
            rider.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null)
                        Log.i("Delete", "Successful");
                    else
                        Log.i("Delete", "unsuccessful");
                }
            });

            uberCalled = false;
            uberButton.setText(R.string.callUber);
        }
    }

    public void updateMap(final Location location)
    {
        if(mMap != null)
        {
            mMap.clear();

            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

            if(rider.getDriversGeo() != null)
            {
                LatLng driversLatLng = new LatLng(rider.getDriversGeo().getLatitude(), rider.getDriversGeo().getLongitude());
                mMap.addMarker(new MarkerOptions().title("Your drivers location").position(driversLatLng));

                //all this animates the camera to fit between the two locations
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(driversLatLng);
                builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                LatLngBounds bounds = builder.build();

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                mMap.animateCamera(cu, new GoogleMap.CancelableCallback()
                {
                    @Override
                    public void onCancel() {}

                    @Override
                    public void onFinish()
                    {
                        CameraUpdate zout = CameraUpdateFactory.zoomBy(-.5f);
                        mMap.animateCamera(zout);
                    }
                });
            }
        }

    }


    public void locationShit()
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                rider.setLastKnownLocation(location);

                updateMap(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if(Build.VERSION.SDK_INT >= 23)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //make sure we have permission before continuing

            else
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                rider.setLastKnownLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

                if(rider.getLastKnownLocation() != null)
                    updateMap(rider.getLastKnownLocation());
            }
        }

        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                rider.setLastKnownLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

                updateMap(rider.getLastKnownLocation());
            }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        locationShit();
    }
}
