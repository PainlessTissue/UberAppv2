package ptindustries.uberappv2;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ryaca on 10/20/2017.
 */

public class RidersClass
{
    Location lastKnownLocation;
    String username;
    Location driversLocation;

    RidersClass(String newUsername)
    {
        lastKnownLocation = null;
        username = newUsername;
        driversLocation = null;
    }
}
