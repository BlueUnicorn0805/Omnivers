package hawaiiappbuilders.omniversapp.utils;

import android.view.View;

public class ViewUtil {
   public static void setVisible(View view) {
      view.setVisibility(View.VISIBLE);
   }

   public static void setInvisible(View view) {
      view.setVisibility(View.INVISIBLE);
   }

   public static void setGone(View view) {
      view.setVisibility(View.GONE);
   }
}
