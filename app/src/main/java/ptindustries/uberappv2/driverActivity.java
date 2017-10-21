package ptindustries.uberappv2;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class driverActivity extends FragmentActivity implements OnMapReadyCallback//, GoogleMap.CancelableCallback
{

    private GoogleMap mMap;
    ParseGeoPoint ridersLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        Intent intent = getIntent();

        final int position = intent.getIntExtra("location", 90);

        ParseQuery<ParseObject> q = new ParseQuery<>("Request");
        q.whereNear("location", distanceActivity.driver.driverGeo);

        q.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                if (e == null)
                {
                    Log.i("e!=", "null");
                    Log.i("size", Integer.toString(objects.size()));
                    if (objects.size() > 0)
                    {
                        //get the position of where the rider was at and assign their location
                        //ridersLocation = objects.get(position).getParseGeoPoint("location");
                        ParseObject d = objects.get(position);
                        ridersLocation = d.getParseGeoPoint("location");

                        mMap.addMarker(new MarkerOptions().position(new LatLng(ridersLocation.getLatitude(), ridersLocation.getLongitude())).title("Riders location"));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(distanceActivity.driver.driverGeo.getLatitude(), distanceActivity.driver.driverGeo.getLongitude())).title("Drivers location"));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ridersLocation.getLatitude(), ridersLocation.getLongitude()), 10));

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(ridersLocation.getLatitude(), ridersLocation.getLongitude()));
                        builder.include(new LatLng(distanceActivity.driver.driverGeo.getLatitude(), distanceActivity.driver.driverGeo.getLongitude()));
                        LatLngBounds bounds = builder.build();

                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                        mMap.animateCamera(cu, new GoogleMap.CancelableCallback()
                        {
                            @Override
                            public void onCancel()
                            {
                            }

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
        });
    }
}



        // Add a marker in Sydney and move the camera
/*        if(ridersLocation != null)
        {
            mMap.addMarker(new MarkerOptions().position(new LatLng(ridersLocation.getLatitude(), ridersLocation.getLongitude())).title("Riders location"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(distanceActivity.driver.driverGeo.getLatitude(), distanceActivity.driver.driverGeo.getLongitude())).title("Drivers location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ridersLocation.getLatitude(), ridersLocation.getLongitude()), 10));
        }
*/




