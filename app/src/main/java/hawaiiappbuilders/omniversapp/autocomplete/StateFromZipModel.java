package hawaiiappbuilders.omniversapp.autocomplete;

import java.util.ArrayList;

class Bounds {
    public StateNortheast northeast;
    public StateSouthwest southwest;
}

class StateGeometry {
    public Bounds bounds;
    public StateLocation location;
    public String location_type;
    public StateViewport viewport;
}

class StateLocation {
    public double lat;
    public double lng;
}

class StateNortheast {
    public double lat;
    public double lng;
}

public class StateFromZipModel {
    public ArrayList<StateResult> results;
    public String status;
}

class StateSouthwest {
    public double lat;
    public double lng;
}

class StateViewport {
    public StateNortheast northeast;
    public StateSouthwest southwest;
}

