package hawaiiappbuilders.omniversapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;

public class ContactListData implements ContactListItem {
   private final String str1;

   public ContactListData(String text1) {
      this.str1 = text1;
   }

   @Override
   public int getViewType() {
      return ContactArrayAdapter.RowType.LIST_ITEM.ordinal();
   }

   @Override
   public View getView(LayoutInflater inflater, View convertView) {
      View view;
      if (convertView == null) {
         view = (View) inflater.inflate(R.layout.spinner_list_item, null);
         // Do some initialization
      } else {
         view = convertView;
      }

      TextView text1 = (TextView) view.findViewById(R.id.name);
      text1.setText(str1);

      return view;
   }
}