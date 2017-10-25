package ptindustries.uberappv2;

import android.location.Location;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("DriverClass")
class DriverClass extends ParseObject
{
    private String username;
    private Location lastKnownLocation;
    private ParseGeoPoint driverGeo;

    DriverClass()
    {
        put("username", ParseUser.getCurrentUser().getUsername());
        username = ParseUser.getCurrentUser().getUsername();
        lastKnownLocation = null;
        driverGeo = null;
    }

    void setLastKnownLocation(Location location)
    {
        put("lastKnownLocation", location);
        lastKnownLocation = location;

        ParseGeoPoint dGeo = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        put("driversGeoPoint", dGeo);
        driverGeo = dGeo;
    }

    void setParseGeo(ParseGeoPoint dGeo)
    {
        put("driversGeoPoint", dGeo);
        driverGeo = dGeo;
    }

    String getUsername() { return username; }
    ParseGeoPoint getDriverGeoPoint() { return driverGeo; }
    Location getLastKnownLocation() { return lastKnownLocation; }



}
