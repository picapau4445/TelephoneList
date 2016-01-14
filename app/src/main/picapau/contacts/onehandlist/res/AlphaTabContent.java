package picapau.contacts.onehandlist.res;

import picapau.contacts.onehandlist.R;
import picapau.contacts.onehandlist.R.id;
import picapau.contacts.onehandlist.R.layout;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class AlphaTabContent extends FrameLayout {  
  
    public AlphaTabContent(Context context) {  
        super(context);  
    }  
    
    public AlphaTabContent(Context context, String title) {  
        this(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View childview1 = inflater.inflate(R.layout.alphatabwidget, null);  
        TextView tv1 = (TextView) childview1.findViewById(R.id.textview);  
        tv1.setText(title);  
        addView(childview1); 
   }  
}