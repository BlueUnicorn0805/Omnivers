package hawaiiappbuilders.omniversapp.autocomplete;

import java.util.ArrayList;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
public class AddressComponent {
    public String long_name;
    public String short_name;
    public ArrayList<String> types;
}
