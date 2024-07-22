package hawaiiappbuilders.omniversapp.waydirections;

public class StepsObject {
    private PolylineObject polyline;
    public StepsObject(PolylineObject polyline) {
        this.polyline = polyline;
    }
    public PolylineObject getPolyline() {
        return polyline;
    }
}
