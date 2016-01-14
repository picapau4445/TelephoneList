package picapau.contacts.onehandlist;

import java.util.ArrayList;
import java.util.Locale;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import picapau.contacts.onehandlist.res.AlphaTabContent;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;


public class AlphaTabActivity extends TabActivity {
	
	public AlphaTabActivity context; 
	public String GroupID;
	public String From;
	
	public void onStart() {
		super.onStart();
	}
	
	public void onRestart() {
		super.onRestart();
//        setDisplay();
	}
	
	@SuppressWarnings("deprecation")
	public void onResume() {
		super.onResume();
	}
	
	
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
 		// 右か左か
        SharedPreferences pref = getSharedPreferences("LR",Activity.MODE_PRIVATE);
 		if( pref.getInt(getString(R.string.LR), 0) == 0 )
 		{
 			setContentView(R.layout.alpha_left);
 		}
 		else
 		{
 			setContentView(R.layout.alpha_right);
 		}
 		
        setDisplay();
    }
    
	public void setDisplay() {
		
        
        context = this;
        
        // GroupID取得、どこから起動したのかも取得
        Intent Parent = getIntent();
        if(Parent != null)
        {
            GroupID = Parent.getStringExtra("GroupID");
            From = Parent.getStringExtra("From");
        }
        
        if( From != null )
        {
        	if( From.equals(getString(R.string.Tab_Group)))
        	{
                // レジストリ呼び出し
            	SharedPreferences pref = getSharedPreferences("taborder",Activity.MODE_PRIVATE);
         		SharedPreferences pref2 = getSharedPreferences("general",Activity.MODE_PRIVATE);
         		
         		// 画面サイズの設定
            	WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            	Display display = wm.getDefaultDisplay();
        		int DisplayHeight = pref2.getInt(getString(R.string.DisplaySize),display.getHeight() );
        		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(display.getWidth(), display.getHeight()-DisplayHeight );
        		findViewById(R.id.MovableLayout).setLayoutParams(params);
         		
                // Admob 
/*                AdView adView = new AdView(this, AdSize.BANNER, "a14f61febe33439");  
                LinearLayout layout = (LinearLayout)findViewById(R.id.MovableLayout);  
                layout.addView(adView);
                AdRequest request = new AdRequest();
        		// デバッグ中
        		if(android.os.Debug.isDebuggerConnected())
        		{
        			// admob test mode
                	request.setTesting(true);
        		}
                adView.loadAd(request);  
*/              
        	}
        }

        TabHost tabHost = getTabHost();    
        TabHost.TabSpec spec;
        
        String[] Tag;
        if( Locale.JAPAN.equals(Locale.getDefault()) )
        { 
        	Tag = new String[]{"All","ア","カ","サ","タ","ナ","ハ","マ","ヤ","ラ","ワ","A"};
        }
        else
        { 
        	Tag = new String[]{"All","ABC","DEF","GHI","JKL","MNO","PQRS","TUV","WXYZ"};
        }
        
        Intent[] intent = new Intent[Tag.length];
        
        
        int i=0;        
        
        for( i=0; i<Tag.length; i++ )
        {
	    	intent[i] = new Intent().setClassName("picapau.contacts.onehandlist","picapau.contacts.onehandlist.ContactListActivity");
	    	intent[i].putExtra("GroupID", GroupID);
	        intent[i].putExtra("Character", Tag[i]);
	        spec = tabHost.newTabSpec(Tag[i]);
	        spec.setIndicator(Tag[i],null);
//	        spec.setIndicator(new AlphaTabContent(this, Tag[i]));
	        spec.setContent(intent[i]);
	        tabHost.addTab(spec);
        }
  
        // カナ検索からなら「あ」それ以外はAll
//        if( From.equals(getString(R.string.Tab_Group)) || From.equals(getString(R.string.Tab_Favorite)) )
//        {
        	tabHost.setCurrentTab(0);
//        }
//        else
//        {
//        	tabHost.setCurrentTab(1);
//        }
        
    }

}