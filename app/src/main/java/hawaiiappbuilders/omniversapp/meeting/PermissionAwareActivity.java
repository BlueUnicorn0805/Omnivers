package hawaiiappbuilders.omniversapp.meeting;

import android.app.Activity;

public interface PermissionAwareActivity {

   /** See {@link Activity#checkPermission}. */
   int checkPermission(String permission, int pid, int uid);

   /** See {@link Activity#checkSelfPermission}. */
   int checkSelfPermission(String permission);

   /** See {@link Activity#shouldShowRequestPermissionRationale}. */
   boolean shouldShowRequestPermissionRationale(String permission);

   /** See {@link Activity#requestPermissions}. */
   void requestPermissions(String[] permissions, int requestCode, PermissionListener listener);
}
