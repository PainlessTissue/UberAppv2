package ptindustries.uberappv2;

import android.location.Location;

import com.parse.Parse;
import com.parse.ParseGeoPoint;

/**
 * Created by ryaca on 10/20/2017.
 */

public class DriverClass
{
    Location lastKnownLocation;
    Location ridersLocation;
    String username;
    ParseGeoPoint driverGeo;

    DriverClass(String newUsername)
    {
        lastKnownLocation = null;
        ridersLocation = null;
        username = newUsername;
        driverGeo = null;
    }

}
