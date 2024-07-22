package hawaiiappbuilders.omniversapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RahulAnsari on 22-09-2018.
 */

public class Biz {
    String Resume;
    String co;
    String FN;
    String LN;
    String Dist;
    String Skills;

    public Biz(String resume, String co, String FN, String LN, String dist, String skills) {
        Resume = resume;
        this.co = co;
        this.FN = FN;
        this.LN = LN;
        Dist = dist;
        Skills = skills;
    }

    public String getResume() {
        return Resume;
    }

    public String getCo() {
        return co;
    }

    public String getFN() {
        return FN;
    }

    public String getLN() {
        return LN;
    }

    public String getDist() {
        return Dist;
    }

    public String getSkills() {
        return Skills;
    }

    private static List<Biz> bizList = new ArrayList<>();

    public static void addBizItem(Biz biz) {
        bizList.add(biz);
    }

    public static List<Biz> getBizList() {
        return bizList;
    }
}
