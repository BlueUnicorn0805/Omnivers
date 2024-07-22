package hawaiiappbuilders.omniversapp.model;

public class NearBySearchItem {

    public String id = "";
    public String place_id = "";
    public String name = "";
    public String vicinity = "";

    public double latitude;
    public double longitude;

    public String reference = "";
    public String icon = "";

    public String imageUrl = "";

    public float rating = 0.5f;

    public String description = "";
    public int icon_id = 0;
    public int importance = 0;

    public NearBySearchItem() {
    }

    public NearBySearchItem(String id, String place_id, String name, String vicinity, float rating, double latitude, double longitude, String imageUrl, String description, int icon_id, int importance) {

        this.id = id;
        this.place_id = place_id;
        this.name = name;
        this.vicinity = vicinity;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.description = description;
        this.icon_id = icon_id;
        this.importance = importance;

    }
}
