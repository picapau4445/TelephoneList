package picapau.contacts.onehandlist;


import java.util.ArrayList;
import java.util.List;

import picapau.contacts.onehandlist.menu.AccountSettingActivity;
import picapau.contacts.onehandlist.menu.GeneralSettingActivity;
import picapau.contacts.onehandlist.menu.GroupSortActivity;
import picapau.contacts.onehandlist.menu.TabSortActivity;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.view.View;


public class Main extends TabActivity {

	boolean mode;
	
	// タブの位置を覚える
	int TabPos;
	
	
	// 
	public Main context;
	
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDisplay();
        
        SharedPreferences readme1 = getSharedPreferences("Readme1",Activity.MODE_PRIVATE);
        if( readme1.getInt(getString(R.string.Readme1), 0) == 0 )
        {
			AlertDialog.Builder alertDialogBuilder;
			alertDialogBuilder = new AlertDialog.Builder(context);
	        alertDialogBuilder.setTitle(getString(R.string.Readme));
	        alertDialogBuilder.setMessage(
	        		getString(R.string.Readme1_title) + '\n' + 
	        		getString(R.string.Readme1_message) + '\n' + '\n' + 
	        		getString(R.string.Readme1_title2) + '\n' + 
	        		getString(R.string.Readme1_message2)
	        		);
	        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog,int which){}
	        });
	        final AlertDialog  Dlg = alertDialogBuilder.create();
	        Dlg.show();
        }
		Editor e = readme1.edit();;
		e.putInt(getString(R.string.Readme1), 1);
		e.commit();
    }
    
    public void setDisplay()
    {
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("taborder",Activity.MODE_PRIVATE);
 		SharedPreferences pref2 = getSharedPreferences("general",Activity.MODE_PRIVATE);
 		
 		// タブの上下
 		if( pref2.getInt(getString(R.string.TabUpDown), 0) == 0 )
 		{
 			setContentView(R.layout.main_up);
 		}
 		else
 		{
 			setContentView(R.layout.main_down);
 		}
 		
        // Admob 
        AdView adView = new AdView(this, AdSize.BANNER, "a14f61febe33439");  
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
      
        context = this;
        
 		// 画面サイズの設定
    	WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	Display display = wm.getDefaultDisplay();
		int DisplayHeight = pref2.getInt(getString(R.string.DisplaySize),display.getHeight() );
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(display.getWidth(), display.getHeight()-DisplayHeight );
		findViewById(R.id.MovableLayout).setLayoutParams(params);
		
    	// デフォルトのタブとタブの並び順
 		String Default = pref2.getString(getString(R.string.DefaultTabPosition), getString(R.string.Tab_Favorite));
        List<String> taborder = new ArrayList<String>();
        taborder.add("");
        taborder.add("");
        taborder.add("");
        taborder.add("");
        taborder.add("");
        taborder.add("");
        String str1;
        str1 = getString(R.string.Tab_Favorite);
        taborder.set(pref.getInt(str1,0), str1);
        if( Default.equals(str1) )
        {
        	TabPos = pref.getInt(str1,0);
        }
        str1 = getString(R.string.Tab_Group);
        taborder.set(pref.getInt(str1,1), str1);
        if( Default.equals(str1) )
        {
        	TabPos = pref.getInt(str1,1);
        }
        str1 = getString(R.string.Tab_Kana);
        taborder.set(pref.getInt(str1,2), str1);
        if( Default.equals(str1) )
        {
        	TabPos = pref.getInt(str1,2);
        }
        str1 = getString(R.string.Tab_History);
        taborder.set(pref.getInt(str1,3), str1);
        if( Default.equals(str1) )
        {
        	TabPos = pref.getInt(str1,3);
        }
        str1 = getString(R.string.Tab_Dial);
        taborder.set(pref.getInt(str1,4), str1);
        if( Default.equals(str1) )
        {
        	TabPos = pref.getInt(str1,4);
        }
    	
    	
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        for(int i=0;i<taborder.size();i++)
        {
        	String str = taborder.get(i);
        	
        	if(str.equals(getString(R.string.Tab_Favorite)))
        	{
                // お気に入り検索
                intent = new Intent().setClass(this, AlphaTabActivity.class);
                intent.putExtra("From", getString(R.string.Tab_Favorite));
                intent.putExtra("GroupID", "-1");        
                spec = tabHost.newTabSpec(getString(R.string.Tab_Favorite))  
                              .setIndicator(getString(R.string.Tab_Favorite),res.getDrawable(R.drawable.ic_menu_star))  
                              .setContent(intent);
                tabHost.addTab(spec);
        	}
        	else if(str.equals(getString(R.string.Tab_Group)))
        	{
                // グループ検索
                intent = new Intent().setClass(this, GroupTabActivity.class);  
                intent.putExtra("From", getString(R.string.Tab_Group));
                spec = tabHost.newTabSpec(getString(R.string.Tab_Group))  
                              .setIndicator(getString(R.string.Tab_Group),res.getDrawable(R.drawable.ic_menu_allfriends))  
                              .setContent(intent);
                tabHost.addTab(spec);          		
        	}
        	else if(str.equals(getString(R.string.Tab_Kana)))
        	{
                // カナ検索
                intent = new Intent().setClass(this, AlphaTabActivity.class);  
                intent.putExtra("From", getString(R.string.Tab_Kana));
                spec = tabHost.newTabSpec(getString(R.string.Tab_Kana))  
                              .setIndicator(getString(R.string.Tab_Kana),res.getDrawable(R.drawable.ic_menu_sort_alphabetically))  
                              .setContent(intent);  
                tabHost.addTab(spec);          		
        	}
        	else if(str.equals(getString(R.string.Tab_History)))
        	{
                // 通話履歴
                intent = new Intent().setClass(this, HistoryTabActivity.class);  
                spec = tabHost.newTabSpec(getString(R.string.Tab_History))  
                              .setIndicator(getString(R.string.Tab_History),res.getDrawable(R.drawable.ic_menu_recent_history))  
                              .setContent(intent);
                tabHost.addTab(spec);        		
        	}
        	else if(str.equals(getString(R.string.Tab_Dial)))
        	{
        	      // ダイアルパッド
                intent = new Intent().setClass(this, DialTabActivity.class);  
                spec = tabHost.newTabSpec(getString(R.string.Tab_Dial))  
                              .setIndicator(getString(R.string.Tab_Dial),res.getDrawable(R.drawable.ic_menu_call))  
                              .setContent(intent);
                tabHost.addTab(spec);        		
        	}
        }
      
        // タブ変更時
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
        	public void onTabChanged(String tabId) {
        		
        		TabHost tabHost1 = getTabHost();
        		
        		if(tabId.equals(getString(R.string.Tab_Dial)))
        		{
        			Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"));
        			startActivity(intent);
        			tabHost1.setCurrentTab(TabPos);
        		}
        		else
        		{
        			TabPos = tabHost1.getCurrentTab();
        		}
        	}
        });
        
        tabHost.setCurrentTab(TabPos);
        
        

    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{		
			if (mode==false) 
			{ //デフォルト				
				return super.onKeyDown(keyCode, event);
			} 
			else //グループ検索のグループ選択後
			{ 
				//グループ検索に戻る
				mode = false;
				
				return false;
			}
		}
		else
		{
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    super.onCreateOptionsMenu( menu );

	    MenuInflater inflater = getMenuInflater();

	    inflater.inflate( R.menu.mainmenu, menu );
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.general_goto:
			{
				Intent intent = new Intent( Main.this, GeneralSettingActivity.class );
				startActivityForResult( intent , 0 );
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if( resultCode == 1 )
		{
			// 画面の再描画処理
	        //setDisplay();
			
	        Toast.makeText(this, getString(R.string.pleaserestart), Toast.LENGTH_LONG).show();
			
	        
//	        TabHost tabHost1 = getTabHost();
//	        String str1;
//	        str1 = getString(R.string.Tab_Dial);
//	        pref.getInt(str1,4));
	        
		}
	}
	
		
}











