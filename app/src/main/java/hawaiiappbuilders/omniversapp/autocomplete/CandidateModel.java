package hawaiiappbuilders.omniversapp.autocomplete;

import java.util.ArrayList;

class Northeast {
    public double lat;
    public double lng;
}

public class CandidateModel {
    public ArrayList<Candidate> candidates;
    public String status;
}

class Southwest {
    public double lat;
    public double lng;
}

class Viewport {
    public Northeast northeast;
    public Southwest southwest;
}