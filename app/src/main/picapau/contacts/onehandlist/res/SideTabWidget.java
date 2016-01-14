package picapau.contacts.onehandlist.res;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabWidget;

public class SideTabWidget extends TabWidget {  
  
 public SideTabWidget(Context context)
 {  
	 this(context, null);  
 }  
  
 public SideTabWidget(Context context, AttributeSet attrs)
 {  
  super(context, attrs);  
  setOrientation(LinearLayout.VERTICAL);  
 }  
  
 @Override  
 public void addView(View child)
 {  
//  if (child.getLayoutParams() == null) {  
   final LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 0, 1.0f);  
   lp.setMargins(0, 0, 0, 0);  
   child.setLayoutParams(lp);  
//  }  
  super.addView(child);  
 }  
} 

/*
                android:layout_width="60dip"
                android:layout_height="fill_parent"
                android:layout_weight="1"

*/