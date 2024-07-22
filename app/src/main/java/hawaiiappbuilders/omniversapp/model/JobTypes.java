package hawaiiappbuilders.omniversapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RahulAnsari on 22-09-2018.
 */

public class JobTypes {
    String ID;
    String Title;
    String sortid;
    String remote;
    boolean checked;

    public JobTypes(String ID, String title, String sortid, String remote) {
        this.ID = ID;
        Title = title;
        this.sortid = sortid;
        this.remote = remote;
        this.checked = false;
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return Title;
    }

    public String getSortid() {
        return sortid;
    }

    public String getRemote() {
        return remote;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private static List<JobTypes> jobTypesList = new ArrayList<>();

    public static void addJobTypes(JobTypes jobTypes) {
        jobTypesList.add(jobTypes);
    }

    public static List<JobTypes> getJobTypesList() {
        return jobTypesList;
    }

    public static void clearJobList() {
        jobTypesList.clear();;
    }
}
