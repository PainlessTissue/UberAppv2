package ptindustries.uberappv2;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.parse.SaveCallback;

import java.util.List;

public class driverActivity extends FragmentActivity implements OnMapReadyCallback//, GoogleMap.CancelableCallback
{

    private GoogleMap mMap;

    //this object is used so, when the users clicks on who they want to drive, we can modify their
    //data throughout the program
    public static RidersClass rider;

    protected void confirmUber(View view)
    {
        //as much as I wouldnt like to make all these variables,
        //it makes setting the api much nicer looking
        double dLat = distanceActivity.driver.getDriverGeoPoint().getLatitude();
        double dLong = distanceActivity.driver.getDriverGeoPoint().getLongitude();
        double rLat = rider.getLastKnownLocation().getLatitude();
        double rLong = rider.getLastKnownLocation().getLongitude();

        //this starts up google maps with the direction between two points
        final Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + dLat + ", " + dLong + "&destination=" + rLat + ", " + rLong + "&travelmode=driving"));

        //this updates and tells the rider who
        rider.setDriversLocation(distanceActivity.driver.getLastKnownLocation());
/*        rider.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if(e == null)
                    startActivity(intent);

                else
                    Toast.makeText(driverActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
*/
        rider.saveInBackground();

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

        ParseQuery<RidersClass> q = new ParseQuery<>("RidersClass");
        q.whereNear("driversLocation", distanceActivity.driver.getDriverGeoPoint());

        // TODO: 10/25/2017 Look a this entire loop. May cause crashes
        q.findInBackground(new FindCallback<RidersClass>()
        {
            @Override
            public void done(List<RidersClass> objects, ParseException e)
            {
                if (e == null)
                {
                    if (objects.size() > 0)
                    {
                            //this could in the future lead to some problems
                            //my thoughts are if somebody were to join while calculating this, then the position would get changed and theyd recieve the wrong user
                            //but for now this is the best I can do
                            rider = objects.get(position);

                            LatLng driversLatLng = new LatLng(distanceActivity.driver.getDriverGeoPoint().getLatitude(), distanceActivity.driver.getDriverGeoPoint().getLongitude());
                            LatLng ridersLatLng = new LatLng(rider.getLastKnownLocation().getLatitude(), rider.getLastKnownLocation().getLongitude());

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