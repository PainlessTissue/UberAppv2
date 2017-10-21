package ptindustries.uberappv2;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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


    protected void confirmUber(View view)
    {
        double dLat = distanceActivity.driver.driverGeo.getLatitude();
        double dLong = distanceActivity.driver.driverGeo.getLongitude();
        double rLat = ridersLocation.getLatitude();
        double rLong = ridersLocation.getLongitude();

        //this starts up google maps with the direction between two points
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + dLat + ", " + dLong + "&destination=" + rLat + ", " + rLong + "&travelmode=driving"));
        startActivity(intent);
    }



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
                        //this could in the future lead to some problems
                        //my thoughts are if somebody were to join while calculating this, then the position would get changed and theyd recieve the wrong user
                        //but for now this is the best I can do
                        ParseObject d = objects.get(position);
                        ridersLocation = d.getParseGeoPoint("location");

                        LatLng driversLatLng = new LatLng(distanceActivity.driver.driverGeo.getLatitude(), distanceActivity.driver.driverGeo.getLongitude());
                        LatLng ridersLatLng = new LatLng(ridersLocation.getLatitude(), ridersLocation.getLongitude());

                        mMap.addMarker(new MarkerOptions().position(ridersLatLng).title("Riders location"));
                        mMap.addMarker(new MarkerOptions().position(driversLatLng).title("Drivers location"));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ridersLocation.getLatitude(), ridersLocation.getLongitude()), 10));

                        //all this animates the camera to fit between the two locations
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(driversLatLng);
                        builder.include(ridersLatLng);
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