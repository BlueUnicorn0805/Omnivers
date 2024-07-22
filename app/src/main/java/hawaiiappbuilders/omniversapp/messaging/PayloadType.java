package hawaiiappbuilders.omniversapp.messaging;

public class PayloadType {
    // #####################################  Misc (1-19) #####################################
    public static final int PT_Text_Message = 1; // TXT Msg
    public static final int PT_Text_W_Pictures = 2; // TXT Msg with picture
    public static final int PT_Funds_Sent = 3; // Funds Sent
    public static final int PT_Phone_Verify_Receive_OTP = 4; // Phone Verify OTP
    public static final int PT_Share_Task = 6; // Share Task
    public static final int PT_Share_My_Info = 7; // Share My Info
    public static final int PT_Text_Message_W_Subject = 8; // TXT Msg with subject(website contact form)
    public static final int PT_Store_Total_Sale_Be_Prepared = 9; // In store, Total Sale or be prepared
    // #####################################  Location (20) #####################################
    public static final int PT_Valet_Req = 20; // Valet Req
    public static final int PT_Valet_Confirm = 21; // Valet Confirm
    public static final int PT_Valet_Decline = 22; // Valet Decline
    public static final int PT_Valet_Accept = 23; // Valet Accept
    public static final int PT_Valet_Park_Location = 25; // Valet Parked Location by
    public static final int PT_Valet_Get_Car = 27; // Valet Get Car
    // #####################################  ??? (30) #####################################
    // #####################################  ??? (40) #####################################
    // #####################################  Driver (50) #####################################
    public static final int PT_Consumer_Parcel_Req = 50; // Consumer Parcel Req
    public static final int PT_Driver_Parcel_Bid = 51; // Driver Parcel Bid
    public static final int PT_Driver_Food_Req = 52; // Driver Food Req
    public static final int PT_Driver_Food_Grab = 53; // Driver Food Grab
    // #####################################  Location (60) #####################################
    public static final int PT_Share_Location = 60; // Location Share
    public static final int PT_Follow_My_Location = 65; // Follow My Location
    // #####################################  Delivery (70-98) #####################################
    public static final int PT_Delivery_Get_Order_By_Item_Count = 70; // Delivery here, use as ID & get correct order by item count
    public static final int PT_Delivery_In_Route_To_Customer = 71; // Delivery in route to customer
    public static final int PT_Delivery_Delivery_Almost_There = 72; // Delivery almost there
    public static final int PT_Delivery_Delivery_On_Site = 73; // Delivery on site
    public static final int PT_Delivery_Delivery_Placed_Valid_Spot = 74; // Delivery placed in valid spot
    public static final int PT_Delivery_Accepted = 75; // Delivery accepted
    public static final int PT_Delivery_Failed = 77; // Delivery failed
    public static final int PT_Delivery_Headed_Next_Stop = 78; // Delivery headed to next stop
    public static final int PT_Delivery_ = 79; // Delivery
    // #####################################  ??? (99-100) #####################################
    public static final int PT_Dial_Phone = 99; // Dial with Phone
    public static final int PT_Advertisement_Industry_Offer = 100; // Advertisement with Industry & Offer
    // #####################################  ??? (101-130) #####################################
    // #####################################  Appointment (140-149) #####################################
    public static final int PT_New_Appointment = 140; // New Appointment
    public static final int PT_Cancel_Appointment = 142; // Cancel Appointment
    public static final int PT_Accept_Appointment = 143; // Accept New Appointment
    public static final int PT_Decline_New_Appointment = 144; // Accept Appointment
    public static final int PT_Propose_Reschedule_Appointment = 145; // Reschedule Appointment
    public static final int PT_Reschedule_Appointment = 146; // Reschedule Appointment
    public static final int PT_Reschedule_Accepted = 147; // Reschedule Accepted
    public static final int PT_Reschedule_Declined = 148; // Decline Reschedule

    // #####################################  ??? (150-190) #####################################
    // #####################################  Vehicle (200-299) #####################################
    public static final int PT_Set_Date_Range = 210; // Set date range??
    public static final int PT_Log_Vehicle_Door_Opened = 215; // Log Vehicle Door opened
    public static final int PT_Lock_Out = 220; // Lock out
    public static final int PT_Log_Attempt_Open_Door = 225; // Log attempt to open door
    // #####################################  ??? (300-499) #####################################
    // #####################################  Invoice (500-599) #####################################
    public static final int PT_Invoice_Sent = 500; // - Invoice Sent
    public static final int PT_Invoice_Paid = 510; // - Invoice Paid
    // #####################################  ??? (600-1999) #####################################
    public static final int PT_Emergency_Unlock = 1999; // emergency unlock
    // #####################################  Order Status (2000) #####################################
    public static final int PT_Order_Status = 2000; // Order Status
    public static final int PT_Just_Ordered = 2001; // Just Ordered, Send Order to Rest/Truck, from seat/while in line
    public static final int PT_Rest_Accepted = 2030; // Rest Accepted
    public static final int PT_Rest_Refused = 2183; // Rest Refused
    public static final int PT_Rest_Preparing = 2138; // Rest Preparing
    public static final int PT_Rest_Complete = 2070; // Rest Complete,
    public static final int PT_Rest_Table_Ready = 2057; // Rest Table Is Ready
    public static final int PT_Rest_Service_Requested = 2086; // Rest Service Requested
    public static final int PT_INCOMING_VIDEO_CALL = 310; // Log for Incoming video call
    public static final int PT_INCOMING_Response_VIDEO_CALL = 318; // Log for Incoming video call

}
