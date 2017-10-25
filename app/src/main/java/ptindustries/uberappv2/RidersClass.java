package ptindustries.uberappv2;

import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("RidersClass")
class RidersClass extends ParseObject
{
    private String username;
    private Location lastKnownLocation;
    private ParseGeoPoint ridersGeo;
    private Location driversLocation;
    private ParseGeoPoint driversGeo;


    RidersClass()
    {
        put("username", ParseUser.getCurrentUser().getUsername());
        username = ParseUser.getCurrentUser().getUsername();

        lastKnownLocation = null;
        ridersGeo = null;
        driversLocation = null;
        driversGeo = null;
    }

    void setLastKnownLocation(Location lastKnownLocation)
    {
        put("ridersLocation", lastKnownLocation);
        this.lastKnownLocation = lastKnownLocation;

        ParseGeoPoint rGeo = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        put("ridersGeo", rGeo);
        this.ridersGeo = rGeo;
    }

    void setGeoLocation(ParseGeoPoint rGeo)
    {
        put("ridersGeo", rGeo);
        this.ridersGeo = rGeo;
    }

    void setDriversLocation(Location driversLocation) {

        put("driversLocation", driversLocation);
        this.driversLocation = driversLocation;

        ParseGeoPoint dGeo = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        put("ridersGeo", dGeo);
        this.ridersGeo = dGeo;
    }

    String getUsername() { return username; }
    Location getLastKnownLocation() { return lastKnownLocation; }
    ParseGeoPoint getRidersGeo() { return driversGeo; }
    ParseGeoPoint getDriversGeo() {return driversGeo; }
}