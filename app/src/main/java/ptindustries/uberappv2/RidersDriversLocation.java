package ptindustries.uberappv2;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * due to the way Parse works, its impossible to have more than one ParseGeoPoint per class
 * so this class is a way to bypass that; creating another class devoted specifically to
 * riders' drivers' location
 */

@ParseClassName("RidersDriversLocation")
class RidersDriversLocation extends ParseObject
{
    //the drivers location
    private ParseGeoPoint ridersDriversLocation;

    RidersDriversLocation() {}

    void setRidersDriversLocation(ParseGeoPoint location)
    {
        this.ridersDriversLocation = location;
        put("ridersDriversLocation", location);
    }

    void deleteThis()
    {
        this.deleteInBackground();
    }

    ParseGeoPoint getLocation() { return getParseGeoPoint("ridersDriversLocation"); }
}
