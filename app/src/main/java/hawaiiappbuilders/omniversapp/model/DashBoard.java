package hawaiiappbuilders.omniversapp.model;

import java.util.ArrayList;
import java.util.List;

public class DashBoard {

    public static final int MY_CALENDAR         = 101;
    public static final int BOOK_APPOINTMENT    = 102;
    public static final int PAY                 = 103;
    public static final int F2F_REQUEST         = 104;
    public static final int LOCAL_STORE_PURCHASE= 105;
    public static final int WORK_RELATED        = 106;
    public static final int MY_PROFILE          = 107;
    public static final int ROUND_UP            = 108;
    public static final int MY_DATA             = 109;
    public static final int TRANSFER_FUNDS      = 110;
    public static final int ANALYSIS            = 111;
    public static final int BUSINESS_OWNER      = 112;

    private String cardName;
    private String cardIcon;
    private List<DashBoardItemContent> dashBoardItemContents = new ArrayList<>();

    public DashBoard(String cardName, String cardIcon, List<DashBoardItemContent> dashBoardItemContents) {
        this.cardName = cardName;
        this.cardIcon = cardIcon;
        this.dashBoardItemContents.addAll(dashBoardItemContents);
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardIcon() {
        return cardIcon;
    }

    public List<DashBoardItemContent> getDashBoardItemContents() {
        return dashBoardItemContents;
    }

    public static class DashBoardItemContent {
        private String cardName;
        private String cardIcon;
        private int carItemId;

        public DashBoardItemContent(String cardName, String cardIcon, int carItemId) {
            this.cardName = cardName;
            this.cardIcon = cardIcon;
            this.carItemId = carItemId;
        }

        public String getCardName() {
            return cardName;
        }

        public String getCardIcon() {
            return cardIcon;
        }

        public int getCarItemId() {
            return carItemId;
        }
    }
}
