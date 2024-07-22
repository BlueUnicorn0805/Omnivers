package hawaiiappbuilders.omniversapp.orders;

import hawaiiappbuilders.omniversapp.R;

public enum OrderStatus {
    // default - statusbar0
    // 2001 - statusbar0
    // 2030 - statusbar0_acc
    // 2036 - 2069 - statusbar1
    // 2070 - 2089 - statusbar2
    // 2100 - statusbar2_3
    // 2120 - statusbar2_7
    // 2130 - 2166 - statusbar3
    JustOrdered(2001, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Just Ordered";
        }
    },
    OrderAccepted(2030, R.drawable.statusbar0_acc) {
        @Override
        public String getOrderStatus() {
            return "Order Accepted";
        }
    },
    WorkingOnOrder(2038, R.drawable.statusbar0_acc) {
        @Override
        public String getOrderStatus() {
            return "Working on Order";
        }
    },
    DriverNotified(2065, R.drawable.statusbar1) {
        @Override
        public String getOrderStatus() {
            return "Driver Notified";
        }
    },
    OrderReady(2070, R.drawable.statusbar2) {
        @Override
        public String getOrderStatus() {
            return "Order Ready";
        }
    },
    Assigned(2082, R.drawable.statusbar2) {
        @Override
        public String getOrderStatus() {
            return "Assigned";
        }
    },
    PackedReadyToShip(2085, R.drawable.statusbar2) {
        @Override
        public String getOrderStatus() {
            return "Packed Ready To Ship";
        }
    },
    Shipped(2090, R.drawable.statusbar2) {
        @Override
        public String getOrderStatus() {
            return "Shipped";
        }
    },
    MissingSomething(2094, R.drawable.statusbar2) {
        @Override
        public String getOrderStatus() {
            return "Missing Something";
        }
    },
    InRtBack(2095, R.drawable.statusbar2) {
        @Override
        public String getOrderStatus() {
            return "In Rt Back";
        }
    },
    Delivering(2100, R.drawable.statusbar2_3) {
        @Override
        public String getOrderStatus() {
            return "Delivering";
        }
    },
    Delivery75(2120, R.drawable.statusbar2_7) {
        @Override
        public String getOrderStatus() {
            return "Delivery";
        }
    },
    OnLocation(2130, R.drawable.statusbar3) {
        @Override
        public String getOrderStatus() {
            return "On Location";
        }
    },
    OrderPlacedStrategicly(2133, R.drawable.statusbar3) {
        @Override
        public String getOrderStatus() {
            return "Order Placed Strategically";
        }
    },
    CustomerAccepted(2135, R.drawable.statusbar3) {
        @Override
        public String getOrderStatus() {
            return "Customer Accepted";
        }
    },
    CustomerRefused(2140, R.drawable.statusbar3) {
        @Override
        public String getOrderStatus() {
            return "Customer Refused";
        }
    },
    timeToRateAndTip(2150, R.drawable.statusbar3) {
        @Override
        public String getOrderStatus() {
            return "Time To Rate And Tip";
        }
    },
    slammedRefused(2165, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Slammed Refused";
        }
    },
    NoShow(2167, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "No Show";
        }
    },
    FakeOrder(2172, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Fake Order";
        }
    },
    NSF(2175, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "NSF";
        }
    },
    CompletedBad(2180, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Completed Bad";
        }
    },
    DriverCanceled(2181, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Driver Canceled";
        }
    },
    StoreCanceled(2182, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Store Canceled";
        }
    },
    CustCanceled(2183, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Customer Canceled";
        }
    },
    CompletedNeedsWork(2185, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Completed Needs Work";
        }
    },
    CompletedNoTipRequested(2189, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Completed No Tip Requested";
        }
    },
    /*isCompleteBadAndNoTip(2180, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Complete Bad And No Tip";
        }
    },*/
    isCompleteGoodAndNoTip(2190, R.drawable.statusbar0) {
        @Override
        public String getOrderStatus() {
            return "Complete Good And No Tip";
        }
    };

    public abstract String getOrderStatus();

    public int statusId;

    public int imageResource;

    public static OrderStatus getOrderStatusEnum(int statusId){
        for(OrderStatus v : values()){
            if( v.statusId == statusId) {
                return v;
            }
        }
        return null;
    }

    private OrderStatus(int statusId, int imageResource) {
        this.statusId = statusId;
        this.imageResource = imageResource;
    }

}
