package ptindustries.uberappv2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class distanceActivity extends AppCompatActivity
{
    static DriverClass driver;
    LocationListener locationListener;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        driver = new DriverClass();
        driver.setUsername(ParseUser.getCurrentUser().getUsername());

        locationShit();

        if(driver.getLastKnownLocation() != null)
        {
            ParseQuery<RidersClass> q = ParseQuery.getQuery(RidersClass.class);
            q.whereNear("ridersGeo", driver.getDriverGeoPoint());

            q.findInBackground(new FindCallback<RidersClass>()
            {
                public void done(List<RidersClass> objects, ParseException exception)
                {
                    for (RidersClass obj : objects)
                    {
                        ParseGeoPoint riderGeo = obj.getParseGeoPoint("ridersGeo");
                        //double roundedNumber = Math.round(driver.getDriverGeoPoint().distanceInMilesTo(riderGeo)); //round the number so it isnt huge
                        //arrayList.add(Double.toString(roundedNumber) + " miles away");
                        arrayList.add(Double.toString(driver.getDriverGeoPoint().distanceInMilesTo(riderGeo)));
                    }
                }
            });

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(), driverActivity.class);

                intent.putExtra("location", position);
                startActivity(intent);
            }
        });
    }



    public void locationShit()
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                driver.setLastKnownLocation(location);

                //driverActivity.rider.setDriversLocation(location);
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
                driver.setLastKnownLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
        }

        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }
}
