package hawaiiappbuilders.omniversapp.ldb;

import android.provider.BaseColumns;

public final class LocalTables {

    private LocalTables() {
    }

    public static class MessageHistoryTable implements BaseColumns {

        //container for all const
        public static final String TABLE_NAME = "qix_messages";
        public static final String TABLE_COL_SVRID = "type";
        public static final String TABLE_COL_STATUSID = "statusid";
        public static final String TABLE_COL_FROMID = "fromid";
        public static final String TABLE_COL_TOID = "toid";
        public static final String TABLE_COL_EMPLOYERID = "employerid";
        public static final String TABLE_COL_MSG = "msg";
        public static final String TABLE_COL_NAME = "name";
        public static final String TABLE_COL_CREATEDATE = "createdate";
        public static final String TABLE_COL_CHANNEL = "channel";
    }

    public static class ContactTable implements BaseColumns {
        //add column here
        public static final String TABLE_NAME = "qix_users";
        public static final String TABLE_COL_EMAIL = "email";
        public static final String TABLE_COL_MLID = "mlid";
        public static final String TABLE_COL_FN = "fn";
        public static final String TABLE_COL_HANDLE = "handle";
        public static final String TABLE_COL_LN = "ln";
        public static final String TABLE_COL_ADDR = "addr";

        public static final String TABLE_COL_SUITE = "suite";
        public static final String TABLE_COL_ZIP = "zip";
        public static final String TABLE_COL_STATE = "state";
        public static final String TABLE_COL_CITY = "city";

        public static final String TABLE_COL_CP = "cp";
        public static final String TABLE_COL_DOB = "dob";
        public static final String TABLE_COL_SHARELOC = "shareloc";

        public static final String TABLE_COL_YOUTUBE = "youtube";
        public static final String TABLE_COL_FB = "fb";
        public static final String TABLE_COL_TWITTER = "twitter";
        public static final String TABLE_COL_LINKEDIN = "linkedin";
        public static final String TABLE_COL_PINTEREST = "pinterest";
        public static final String TABLE_COL_SNAPCHAT = "snapchat";
        public static final String TABLE_COL_INSTAGRAM = "instagram";
        public static final String TABLE_COL_WHATSAPP = "whatsapp";

        public static final String TABLE_COL_CO = "co";
        public static final String TABLE_COL_TITLE = "title";
        public static final String TABLE_COL_WORKADD = "workadd";
        public static final String TABLE_COL_WEBSITE = "website";
        public static final String TABLE_COL_WP = "wp";
        public static final String TABLE_COL_CREATEDATE = "createdate";

        // Additional Items
        public static final String TABLE_COL_PRI = "pri";
        public static final String TABLE_COL_LOCALDBOWNER = "localdbownermlid";

        public static final String TABLE_COL_FRIENDLEVEL = "friendlevel";
        public static final String TABLE_COL_GENDER = "gender";
        public static final String TABLE_COL_INITIAL = "initial";
        public static final String TABLE_COL_STREETNUM = "streetnum";
        public static final String TABLE_COL_STREET = "street";
        public static final String TABLE_COL_STE = "ste";
        public static final String TABLE_COL_UTC = "utc";
        public static final String TABLE_COL_MARITAL = "marital";

        public static final String TABLE_COL_VERIFIED = "verified";
        public static final String TABLE_COL_RATING = "rating";
        public static final String TABLE_COL_COA = "coa";

        public static final String TABLE_COL_PERSONAL = "personal";
        public static final String TABLE_COL_BUSINESS = "business";
        public static final String TABLE_COL_FAMILY = "family";

        public static final String TABLE_COL_BLOCKED = "blocked";
        public static final String TABLE_COL_ARCHIVED = "archived";
        public static final String TABLE_COL_LON = "lon";
        public static final String TABLE_COL_LAT = "lat";

        public static final String TABLE_COL_EDITDATE = "editdate";
        public static final String TABLE_COL_INDUSTRYID = "indusid";

        public static final String TABLE_COL_GROUPIDS = "groupids";

        public static final String TABLE_COL_VIDEO_MEETING_URL = "videoMeetingURL";

    }

    public static class GroupTable implements BaseColumns {

        //container for all const
        public static final String TABLE_NAME = "qix_group";
        public static final String TABLE_COL_GRPNAME = "grpname";
        public static final String TABLE_COL_PRI = "pri";
        public static final String TABLE_COL_SORTBY = "sortby";
        public static final String TABLE_COL_CREATEDAT = "createdat";
        public static final String TABLE_COL_MORE = "more";
    }

    public static class FamilyMemberTable implements BaseColumns {

        //container for all const
        public static final String TABLE_NAME = "family_member";
        public static final String TABLE_COL_FNAME = "family_member_firstname";
        public static final String TABLE_COL_LNAME = "family_member_lastname";
        public static final String TABLE_COL_BIRTHDATE = "family_member_birthdate";
        public static final String TABLE_COL_AVATAR = "family_member_avatar";
        public static final String TABLE_COL_TITLE = "family_member_title";
        public static final String TABLE_COL_MOM_ID = "family_mom_id";
        public static final String TABLE_COL_DAD_ID = "family_dad_id";
        public static final String TABLE_COL_SPOUSE_ID = "family_spouse_id";
        public static final String TABLE_COL_SETTINGS = "family_settings";
        public static final String TABLE_COL_TITLE_ID = "family_title_id";

        public static final String TABLE_COL_CHILDREN = "family_children";

    }

    public static class ChildrenTable implements BaseColumns {
        public static final String TABLE_NAME = "childrens";
        public static final String TABLE_COL_CHILD_ID = "children_id";
        public static final String TABLE_COL_DAD_ID = "children_dad_id";
        public static final String TABLE_COL_MOM_ID = "children_mom_id";
    }

    public static class CallLogTable implements BaseColumns {

        //container for all const
        public static final String TABLE_NAME = "qix_calllogs";
        public static final String TABLE_COL_LDBID = "ldbid";
        public static final String TABLE_COL_PHONE = "phone";
        public static final String TABLE_COL_STATUSID = "statusid";
        public static final String TABLE_COL_INOUT = "inout";
        public static final String TABLE_COL_CALLSECS = "callsecs";
        public static final String TABLE_COL_CREATEDATE = "createdate";
    }

    public static class CheckTable implements BaseColumns {
        public static final String TABLE_NAME = "checktable";
        public static final String TABLE_COL_TRANSACTION_ID = "transactionId";
        public static final String TABLE_COL_TRANSACTION_DATE = "transactionDate";
        public static final String TABLE_COL_BANK_NAME = "bankName";
        public static final String TABLE_COL_NAME = "name";
        public static final String TABLE_COL_MEMO = "memo";
        public static final String TABLE_COL_ADDRESS = "address";
        public static final String TABLE_COL_CHECK_NUMBER = "checkNumber";
        public static final String TABLE_COL_ROUTING_NUMBER = "routingNumber";
        public static final String TABLE_COL_ACCOUNT_NUMBER = "accountNumber";
        public static final String TABLE_COL_FRONT_IMAGE = "frontImage";
        public static final String TABLE_COL_BACK_IMAGE = "backImage";
        public static final String TABLE_COL_AMOUNT = "amount";
    }
}

