package hawaiiappbuilders.omniversapp.model;

import java.util.ArrayList;

public class StateDataProvider {
    private static StateDataProvider INSTANCE;
    public static StateDataProvider getInstance() {
        if (INSTANCE == null)
            INSTANCE = new StateDataProvider();
        return INSTANCE;
    }

    public class StateInfo {
        public String abbr, name;
        public StateInfo(String abbr, String name) {
            this.abbr = abbr;
            this.name = name;
        }
    }

    private ArrayList<StateInfo> stateList = new ArrayList<>();
    public ArrayList<StateInfo> getStateList() {
        return stateList;
    }

    public StateDataProvider() {
        stateList.add(new StateInfo("AA", "Armed Forces Americas"));
        stateList.add(new StateInfo("AK", "ALASKA"));
        stateList.add(new StateInfo("AL", "ALABAMA"));
        stateList.add(new StateInfo("AP", "Armed Forces Pacific"));
        stateList.add(new StateInfo("AR", "ARKANSAS"));
        stateList.add(new StateInfo("AZ", "ARIZONA"));
        stateList.add(new StateInfo("CA", "CALIFORNIA"));
        stateList.add(new StateInfo("CO", "COLORADO"));
        stateList.add(new StateInfo("CT", "CONNECTICUT"));
        stateList.add(new StateInfo("DC", "DISTRICT OF COLUMBIA"));
        stateList.add(new StateInfo("DE", "DELAWARE"));
        stateList.add(new StateInfo("FL", "FLORIDA"));
        stateList.add(new StateInfo("GA", "GEORGIA"));
        stateList.add(new StateInfo("HI", "HAWAII"));
        stateList.add(new StateInfo("IA", "IOWA"));
        stateList.add(new StateInfo("ID", "IDAHO"));
        stateList.add(new StateInfo("IL", "ILLINOIS"));
        stateList.add(new StateInfo("IN", "INDIANA"));
        stateList.add(new StateInfo("KS", "KANSAS"));
        stateList.add(new StateInfo("KY", "KENTUCKY"));
        stateList.add(new StateInfo("LA", "LOUISIANA"));
        stateList.add(new StateInfo("MA", "MASSACHUSETTS"));
        stateList.add(new StateInfo("MD", "MARYLAND"));
        stateList.add(new StateInfo("ME", "MAINE"));
        stateList.add(new StateInfo("MI", "MICHIGAN"));
        stateList.add(new StateInfo("MN", "MINNESOTA"));
        stateList.add(new StateInfo("MO", "MISSOURI"));
        stateList.add(new StateInfo("MS", "MISSISSIPPI"));
        stateList.add(new StateInfo("MT", "MONTANA"));
        stateList.add(new StateInfo("NC", "NORTH CAROLINA"));
        stateList.add(new StateInfo("ND", "NORTH DAKOTA"));
        stateList.add(new StateInfo("NE", "NEBRASKA"));
        stateList.add(new StateInfo("NH", "NEW HAMPSHIRE"));
        stateList.add(new StateInfo("NJ", "NEW JERSEY"));
        stateList.add(new StateInfo("NM", "NEW MEXICO"));
        stateList.add(new StateInfo("NV", "NEVADA"));
        stateList.add(new StateInfo("NY", "NEW YORK"));
        stateList.add(new StateInfo("OH", "OHIO"));
        stateList.add(new StateInfo("OK", "OKLAHOMA"));
        stateList.add(new StateInfo("OR", "OREGON"));
        stateList.add(new StateInfo("PA", "PENNSYLVANIA"));
        stateList.add(new StateInfo("RI", "RHODE ISLAND"));
        stateList.add(new StateInfo("SC", "SOUTH CAROLINA"));
        stateList.add(new StateInfo("SD", "SOUTH DAKOTA"));
        stateList.add(new StateInfo("TN", "TENNESSEE"));
        stateList.add(new StateInfo("TX", "TEXAS"));
        stateList.add(new StateInfo("UT", "UTAH"));
        stateList.add(new StateInfo("VA", "VIRGINIA"));
        stateList.add(new StateInfo("VI", "VIRGIN ISLANDS"));
        stateList.add(new StateInfo("VT", "VERMONT"));
        stateList.add(new StateInfo("WA", "WASHINGTON"));
        stateList.add(new StateInfo("WI", "WISCONSIN"));
        stateList.add(new StateInfo("WV", "WEST VIRGINIA"));
        stateList.add(new StateInfo("WY", "WYOMING"));
    }
}
