package hawaiiappbuilders.omniversapp.appointment;

import static hawaiiappbuilders.omniversapp.ConnectionActivity.REQUEST_ADD_AS_NEW_CONTACT;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import java.util.Calendar;

import hawaiiappbuilders.omniversapp.ActivityAddNewContact;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.dialog.util.DialogUtil;
import hawaiiappbuilders.omniversapp.dialog.util.OnDialogViewListener;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.CalendarData;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.utils.ViewUtil;

public class ApptUtil {

    public static final int NEW_APPOINTMENT = 2220; // 2200
    public static final int RESCHEDULE_APPOINTMENT = 2225; // update the date and time
    public static final int ACCEPT_APPOINTMENT = 2230;
    public static final int CANCEL_APPOINTMENT = 2235;
    public static final int DECLINE_APPOINTMENT = 2240; // ????

    public interface OnUpdateApptListener {
        void onAppointmentDeclined(int newStatusId);

        void onAppointmentAccepted(int newStatusId);

        void onAppointmentRescheduled(int newStatusId);

        void onAppointmentCancelled(int newStatusId);
    }

    public interface OnAddNewContactForApptListener {
        void onSuccessAddNewContact(ContactInfo newContactInfo);

        void onErrorAddNewContact(String message);
    }

    Context context;
    private OnUpdateApptListener listener;
    private OnAddNewContactForApptListener addNewContactForApptListener;

    public ApptUtil(Context context) {
        this.context = context;
    }

    /**
     * From doctor to patient.LOGGED_IN
     */
    public boolean isApptForPatient(CalendarData.Data data) {
        AppSettings appSettings = new AppSettings(context);
        return data.getCalSetById() != appSettings.getUserId() && data.getAttendeeMLID() == appSettings.getUserId();
    }

    /**
     * From doctor.LOGGED_IN to other user
     */
    public boolean isApptCreatedByDoctor(CalendarData.Data data) {
        AppSettings appSettings = new AppSettings(context);
        return data.getCalSetById() == appSettings.getUserId() && (data.getAttendeeMLID() != appSettings.getUserId() && data.getAttendeeMLID() != 0);
        //                     doctor                                            not user                                        set
    }

    /**
     * For user.LOGGED_IN is the doctor and patient
     */
    public boolean isApptCreatedByDoctorAndYouAreTheAttendee(CalendarData.Data data) {
        AppSettings appSettings = new AppSettings(context);
        return data.getCalSetById() == appSettings.getUserId() && data.getAttendeeMLID() == appSettings.getUserId();
    }

    /**
     * Load page buttons according to appointment status and attendeeMLID value
     *
     * If attendeeMLID != 0, show Cancel, hide Delete
     * Otherwise, hide Cancel, show Delete
     */
    public void initializeActionButtons(CalendarData.Data data,
                                        Button buttonAddAsNewContact,
                                        Button accept,
                                        Button cancel,
                                        Button decline,
                                        View update,
                                        View edit,
                                        View menu) {

        // Button "Add as New Contact"
        if(data.getAttendeeMLID() != 0) {
            buttonAddAsNewContact.setVisibility(View.GONE);
            // Update, Edit, Menu
            if(isApptCreatedByDoctor(data)) {
                //ViewUtil.setVisible(update);
                ViewUtil.setVisible(edit);
                ViewUtil.setVisible(menu);
            } else if(isApptForPatient(data)) {
                //ViewUtil.setGone(update);
                ViewUtil.setGone(edit);
                ViewUtil.setGone(menu);
            } else if(isApptCreatedByDoctorAndYouAreTheAttendee(data)) {
                //ViewUtil.setVisible(update);
                ViewUtil.setVisible(edit);
                ViewUtil.setVisible(menu);
            }

            // Accept, Cancel, Decline
            if(isApptCreatedByDoctor(data)) {
                switch (data.getApptStatusID()) {
                    case NEW_APPOINTMENT:
                    case RESCHEDULE_APPOINTMENT:
                    case ACCEPT_APPOINTMENT:
                    case DECLINE_APPOINTMENT:
                        ViewUtil.setGone(accept);
                        ViewUtil.setVisible(cancel);
                        ViewUtil.setGone(decline);
                        break;
                    case CANCEL_APPOINTMENT:
                        ViewUtil.setGone(accept);
                        ViewUtil.setGone(cancel);
                        ViewUtil.setGone(decline);
                        break;
                }
            } else if(isApptForPatient(data)) {
                switch (data.getApptStatusID()) {
                    case NEW_APPOINTMENT:
                    case RESCHEDULE_APPOINTMENT:
                        ViewUtil.setVisible(accept);
                        ViewUtil.setVisible(cancel);
                        ViewUtil.setVisible(decline);
                        break;
                    case ACCEPT_APPOINTMENT:
                        ViewUtil.setGone(accept);
                        ViewUtil.setVisible(cancel);
                        ViewUtil.setGone(decline);
                        break;
                    case CANCEL_APPOINTMENT:
                    case DECLINE_APPOINTMENT:
                        ViewUtil.setGone(accept);
                        ViewUtil.setGone(cancel);
                        ViewUtil.setGone(decline);
                        break;
                }
            } else if(isApptCreatedByDoctorAndYouAreTheAttendee(data)) {
                ViewUtil.setGone(accept);
                ViewUtil.setGone(cancel);
                ViewUtil.setGone(decline);
            }
        } else {
            buttonAddAsNewContact.setVisibility(View.VISIBLE);
            ViewUtil.setGone(accept);
            ViewUtil.setGone(cancel);
            ViewUtil.setGone(decline);
            //ViewUtil.setVisible(update);
            ViewUtil.setVisible(edit);
            ViewUtil.setVisible(menu);
        }
    }

    /**
     * This will color the appointment itemView in the calendarView based on the returned appointment status
     */
    public static void colorItemByStatus(Context context, int status, TextView textView, CalendarData.Data calData) {
        ApptUtil apptUtil = new ApptUtil(context);
        if (calData.getAttendeeMLID() == 0) {
            if (status == ACCEPT_APPOINTMENT) {
                textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_green));
            } else if(status == CANCEL_APPOINTMENT) {
                textView.setTextColor(Color.WHITE);
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_red));
            } else if(status == DECLINE_APPOINTMENT) {
                textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_red));
            } else {
                textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_default));
            }
        } else {
            if(apptUtil.isApptForPatient(calData)) {
                switch (status) {
                    case NEW_APPOINTMENT:
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_yellow));
                        break;
                    case RESCHEDULE_APPOINTMENT:
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_orange));
                        break;
                    case ACCEPT_APPOINTMENT:
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_green));
                        break;
                    case CANCEL_APPOINTMENT:
                        textView.setTextColor(Color.WHITE);
                        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_red));
                        break;
                    case DECLINE_APPOINTMENT:
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_red));
                        break;
                }
            } else if(apptUtil.isApptCreatedByDoctor(calData)) {
                switch (status) {
                    case NEW_APPOINTMENT:
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_default));
                        break;
                    case RESCHEDULE_APPOINTMENT:
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_orange));
                        break;
                    case ACCEPT_APPOINTMENT:
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_green));
                        break;
                    case CANCEL_APPOINTMENT:
                        textView.setTextColor(Color.WHITE);
                        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_red));
                        break;
                    case DECLINE_APPOINTMENT:
                        textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_red));
                        break;
                }
            } else {
                if (status == ACCEPT_APPOINTMENT) {
                    textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_green));
                } else if(status == CANCEL_APPOINTMENT) {
                    textView.setTextColor(Color.WHITE);
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_red));
                } else if(status == DECLINE_APPOINTMENT) {
                    textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_red));
                } else {
                    textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.status_default));
                }
            }
        }
    }

    /*public String getProposalTime(CalendarData.Data apptData) {
        Calendar calStartTime = Calendar.getInstance();
        Calendar calEndTime = Calendar.getInstance();

        calStartTime.setTimeInMillis(DateUtil.parseDataFromFormat20(apptData.getStartDate()).getTime());
        calEndTime.setTimeInMillis(DateUtil.parseDataFromFormat20(apptData.getEndDate()).getTime());
        return String.format("%s - %s",
                DateUtil.toStringFormat_37(calStartTime.getTime()),
                DateUtil.toStringFormat_37(calEndTime.getTime()));

    }*/

    public boolean isDateTimeUpdated(Calendar start, Calendar end, Calendar dStart, Calendar dEnd) {
        return start.getTimeInMillis() != dStart.getTimeInMillis() || end.getTimeInMillis() != dEnd.getTimeInMillis();
    }

    /**
     * This method will be called when [Accept, Cancel] will be clicked.
     * It opens up a new dialog asking for confirmation
     */
    public void setNewAppointmentStatus(CalendarData.Data data, int newStatusID, OnUpdateApptListener listener) {
        this.listener = listener;
        if (isApptForPatient(data)) {
            switch (newStatusID) {
                case RESCHEDULE_APPOINTMENT:
                    reviewProposal();
                    break;
                case ACCEPT_APPOINTMENT:
                    if (data.getApptStatusID() == RESCHEDULE_APPOINTMENT) {
                        acceptRescheduledAppointment();
                    } else {
                        accept();
                    }
                    break;
                case CANCEL_APPOINTMENT:
                    cancel();
                    break;
                case DECLINE_APPOINTMENT:
                    if (data.getApptStatusID() == RESCHEDULE_APPOINTMENT) {
                        declineRescheduledAppointment();
                    } else {
                        decline();
                    }
                    break;
            }
        } else if (isApptCreatedByDoctor(data)) {
            switch (newStatusID) {
                case RESCHEDULE_APPOINTMENT:
                    reschedule();
                    break;
                case CANCEL_APPOINTMENT:
                    cancelAttendeeAppointment();
                    break;
            }
        } else if (isApptCreatedByDoctorAndYouAreTheAttendee(data)) {
            // NA
        }
    }

    //#region Decline
    private void decline() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Decline Appointment", "Are you sure?")
                .setPositiveButton("Decline")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentDeclined(DECLINE_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    private void declineRescheduledAppointment() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Decline Rescheduled Appointment", "Are you sure you want to decline proposed new time?")
                .setPositiveButton("Yes")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentDeclined(DECLINE_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    private void declineAttendeeReschedule() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Decline Reschedule", "Are you sure you want to decline attendee's proposed new time?")
                .setPositiveButton("Yes")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentDeclined(DECLINE_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    //#endregion

    //#region Accept
    private void accept() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Accept Appointment", "Are you sure?")
                .setPositiveButton("Accept")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentAccepted(ACCEPT_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    private void acceptAttendeeReschedule() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Accept Rescheduled Appointment", "Are you sure you want to accept attendee's proposed new time?")
                .setPositiveButton("Accept")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentAccepted(ACCEPT_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    private void acceptRescheduledAppointment() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Accept Rescheduled Appointment", "Are you sure you want to accept proposed new time?")
                .setPositiveButton("Yes")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentAccepted(ACCEPT_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    //#endregion

    //#region Reschedule
    private void reviewProposal() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Reschedule", "You proposed a new date and time.  Please hit Reschedule to confirm")
                .setPositiveButton("Reschedule")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentAccepted(RESCHEDULE_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {
                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    private void reschedule() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Reschedule", "You proposed a new date and time for attendee.  Please hit Reschedule to confirm")
                .setPositiveButton("Reschedule")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentRescheduled(RESCHEDULE_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    //#endregion

    //#region Cancel
    private void cancel() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Cancel Appointment", "Are you sure you want to cancel appointment?")
                .setPositiveButton("Cancel")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentCancelled(CANCEL_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    private void cancelAttendeeAppointment() {
        new DialogUtil(context)
                .setCancellable(true)
                .setTitleAndMessage("Cancel Attendee's Appointment", "Are you sure you want to cancel attendee's appointment?")
                .setPositiveButton("Cancel")
                .setNegativeButton("Close").createDialog(new OnDialogViewListener() {
                    @Override
                    public void onPositiveClick() {
                        listener.onAppointmentCancelled(CANCEL_APPOINTMENT);
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }
                }).show();
    }
    //#endregion

    /**
     * This method will open AddNewContact activity
     * Adding a new contact's result will be handled by the calling activity (ActivityAddMeet or ViewMeetingActivity)
     */
    public void addNewContactForAttendee(BaseActivity activity, String company, String email, String address, String cp, String meetingUrl) {
        ContactInfo newContact = new ContactInfo();
        newContact.setCo(company);
        newContact.setFname("");
        newContact.setLname("");
        newContact.setEmail(email);
        newContact.setTitle("");
        newContact.setCp(cp);
        newContact.setDob("");
        newContact.setAddress("");
        newContact.setStreetNum("");
        newContact.setStreet("");
        newContact.setSuite("");
        newContact.setZip(String.valueOf(0));
        newContact.setState("");
        newContact.setCity("");
        newContact.setVideoMeetingUrl(meetingUrl);
        Intent intent = new Intent(context, ActivityAddNewContact.class);
        intent.putExtra("contact", newContact);
        intent.putExtra("mode", 1); // add as new contact
        activity.startActivityForResult(intent, REQUEST_ADD_AS_NEW_CONTACT);
    }


}
