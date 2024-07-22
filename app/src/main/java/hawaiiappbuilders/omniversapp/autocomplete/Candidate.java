package hawaiiappbuilders.omniversapp.autocomplete;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
   /* ObjectMapper om = new ObjectMapper();
   Root root = om.readValue(myJsonString, Root.class); */
public class Candidate {
    public String formatted_address;
    public Geometry geometry;
    public String name;
}
