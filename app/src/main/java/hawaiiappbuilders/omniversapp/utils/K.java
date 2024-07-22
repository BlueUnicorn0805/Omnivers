package hawaiiappbuilders.omniversapp.utils;


public class K {

    public static String gKy(String ky) {
        return ky;
//        try {
//            Crypto crypto = new Crypto();
//            return crypto.decrypt(ky);
//        } catch (Exception e) {
//            return "";
//        }
    }

    public static String eKy(String val) {
        try {
            Crypto crypto = new Crypto();
            return crypto.encrypt(val);
        } catch (Exception e) {
            return "";
        }
    }
}
