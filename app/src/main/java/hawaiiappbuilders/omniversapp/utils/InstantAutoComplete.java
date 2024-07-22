package hawaiiappbuilders.omniversapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

@SuppressLint("AppCompatCustomView")
public class InstantAutoComplete extends AutoCompleteTextView {

   public InstantAutoComplete(Context context) {
      super(context);
   }

   public InstantAutoComplete(Context arg0, AttributeSet arg1) {
      super(arg0, arg1);
   }

   public InstantAutoComplete(Context arg0, AttributeSet arg1, int arg2) {
      super(arg0, arg1, arg2);
   }

   @Override
   public boolean enoughToFilter() {
      return true;
   }

   private boolean mIsKeyboardVisible;
   @Override
   protected void onFocusChanged(boolean focused, int direction,
                                 Rect previouslyFocusedRect) {
      super.onFocusChanged(focused, direction, previouslyFocusedRect);
      if (focused && getFilter() != null) {
         performFiltering(getText(), 0);
      }
      mIsKeyboardVisible = focused;
   }
   @Override
   public boolean onKeyPreIme(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
         InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(
                 Context.INPUT_METHOD_SERVICE);
         // inputManager.isAcceptingText() will not work because view is still focused.
         /*if (mIsKeyboardVisible) { // Is keyboard visible?
            // Hide keyboard.
            inputManager.hideSoftInputFromWindow(getWindowToken(), 0);
            mIsKeyboardVisible = false;

            // Consume event.
            return true;
         } else {
            // Do nothing.
         }*/

         inputManager.hideSoftInputFromWindow(getWindowToken(), 0);
         mIsKeyboardVisible = false;

         // Consume event.
         return true;
      }

      return super.onKeyPreIme(keyCode, event);
   }

}