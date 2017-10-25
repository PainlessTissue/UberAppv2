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

    public void setLocation(Location location)
    {
        put("location", location);
        lastKnownLocation = location;

        ParseGeoPoint dGeo = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        put("driverGeoPoint", dGeo);
        driverGeo = dGeo;
    }

    public String getUsername() { return username; }
    public ParseGeoPoint getDriverGeo() { return driverGeo; }
    public Location getLastKnownLocation() { return lastKnownLocation; }



}
