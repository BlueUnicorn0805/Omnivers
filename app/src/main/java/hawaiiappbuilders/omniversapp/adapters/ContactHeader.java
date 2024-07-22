package hawaiiappbuilders.omniversapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;

public class ContactHeader implements ContactListItem {
   private final String name;

   public ContactHeader(String name) {
      this.name = name;
   }

   @Override
   public int getViewType() {
      return ContactArrayAdapter.RowType.HEADER_ITEM.ordinal();
   }

   @Override
   public View getView(LayoutInflater inflater, View convertView) {
      View view;
      if (convertView == null) {
         view = (View) inflater.inflate(R.layout.contact_header, null);
         // Do some initialization
      } else {
         view = convertView;
      }

      TextView text = (TextView) view.findViewById(R.id.name);
      text.setText(name);

      return view;
   }

}