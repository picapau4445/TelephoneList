package picapau.contacts.onehandlist;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;

public class HistoryTabActivity extends TabActivity {
	
	public String GroupID;
	public String From;
	
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        
 		// 右か左か
        SharedPreferences pref = getSharedPreferences("LR",Activity.MODE_PRIVATE);
 		if( pref.getInt(getString(R.string.LR), 0) == 0 )
 		{
        	setContentView(R.layout.historytabactivity_left);
 		}
 		else
 		{
        	setContentView(R.layout.historytabactivity_right);
 		}
 		
        TabHost tabHost = getTabHost();          
        TabHost.TabSpec spec;
        Resources res = getResources();
        
        Intent[] intent = new Intent[4];
        String[] Tag = new String[]{"All","Outgoing","Incoming","Missed"};
        
        int i=0;

    	intent[i] = new Intent().setClassName("picapau.contacts.onehandlist","picapau.contacts.onehandlist.HistoryListActivity");
        intent[i].putExtra("Type", 0);        
        spec = tabHost.newTabSpec(Tag[i]);
        spec.setIndicator("",res.getDrawable(R.drawable.ic_menu_recent_history));
        spec.setContent(intent[i]);
        tabHost.addTab(spec);
        i++;
    	intent[i] = new Intent().setClassName("picapau.contacts.onehandlist","picapau.contacts.onehandlist.HistoryListActivity");
        intent[i].putExtra("Type", CallLog.Calls.OUTGOING_TYPE);        
        spec = tabHost.newTabSpec(Tag[i]);
        spec.setIndicator("",res.getDrawable(R.drawable.sym_call_outgoing));
        spec.setContent(intent[i]);
        tabHost.addTab(spec);
        i++;
    	intent[i] = new Intent().setClassName("picapau.contacts.onehandlist","picapau.contacts.onehandlist.HistoryListActivity");
        intent[i].putExtra("Type", CallLog.Calls.INCOMING_TYPE);        
        spec = tabHost.newTabSpec(Tag[i]);
        spec.setIndicator("",res.getDrawable(R.drawable.sym_call_incoming));
        spec.setContent(intent[i]);
        tabHost.addTab(spec);
        i++;        
    	intent[i] = new Intent().setClassName("picapau.contacts.onehandlist","picapau.contacts.onehandlist.HistoryListActivity");
        intent[i].putExtra("Type", CallLog.Calls.MISSED_TYPE);        
        spec = tabHost.newTabSpec(Tag[i]);
        spec.setIndicator("",res.getDrawable(R.drawable.sym_call_missed));
        spec.setContent(intent[i]);
        tabHost.addTab(spec);
        i++;        
       
        tabHost.setCurrentTab(0);
    }
}