package hawaiiappbuilders.omniversapp.autocomplete;

import java.util.ArrayList;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
class MainTextMatchedSubstring {
    public int length;
    public int offset;
}

class MatchedSubstring {
    public int length;
    public int offset;
}

public class Prediction {
    public String description;
    public ArrayList<MatchedSubstring> matched_substrings;
    public String place_id;
    public String reference;
    public StructuredFormatting structured_formatting;
    public ArrayList<Term> terms;
    public ArrayList<String> types;
}

class StructuredFormatting {
    public String main_text;
    public ArrayList<MainTextMatchedSubstring> main_text_matched_substrings;
    public String secondary_text;
}

class Term {
    public int offset;
    public String value;
}

