package picapau.contacts.onehandlist.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import picapau.contacts.onehandlist.AlphaTabActivity;
import picapau.contacts.onehandlist.R;
import picapau.contacts.onehandlist.Data.AccountListData;
import picapau.contacts.onehandlist.Data.GeneralData;
import picapau.contacts.onehandlist.Data.GroupListData;
import picapau.contacts.onehandlist.Main;
import picapau.contacts.onehandlist.R.color;
import picapau.contacts.onehandlist.R.id;
import picapau.contacts.onehandlist.R.layout;
import picapau.contacts.onehandlist.R.string;
import picapau.contacts.onehandlist.res.NumFilter;
import picapau.contacts.onehandlist.res.SortableListView;

public class GeneralSettingActivity extends Activity {
    
    int mDraggingPosition = -1;
    ListView mListView;
    int m_ItemCount = 0;
    List<GeneralData> mListGeneralData = new ArrayList<GeneralData>();
    
    boolean bSettingChaged;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generalsettingactivity);
        
        LoaddData();
        m_ItemCount = mListGeneralData.size();            	
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if( bSettingChaged == true )
			{
				// 設定変更したことをメイン画面へ伝える
				setResult(1);
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	// 設定の画面
	private void LoaddData()
	{
    	GeneralData ListData2 = new GeneralData();
    	ListData2.set_ID(getString(R.string.DisplaySize));
    	ListData2.setDISPNAME(getString(R.string.DisplaySize));
    	mListGeneralData.add(ListData2);
    	
    	GeneralData ListData4 = new GeneralData();
    	ListData4.set_ID(getString(R.string.AccountChoice));
    	ListData4.setDISPNAME(getString(R.string.AccountChoice));
    	mListGeneralData.add(ListData4);

    	GeneralData ListData1 = new GeneralData();
    	ListData1.set_ID(getString(R.string.DefaultTabPosition));
    	ListData1.setDISPNAME(getString(R.string.DefaultTabPosition));
    	mListGeneralData.add(ListData1);
    	
    	GeneralData ListData3 = new GeneralData();
    	ListData3.set_ID(getString(R.string.TabUpDown));
    	ListData3.setDISPNAME(getString(R.string.TabUpDown));
    	mListGeneralData.add(ListData3);

    	GeneralData ListData5 = new GeneralData();
    	ListData5.set_ID(getString(R.string.TabOrder));
    	ListData5.setDISPNAME(getString(R.string.TabOrder));
    	mListGeneralData.add(ListData5);

    	GeneralData ListData6 = new GeneralData();
    	ListData6.set_ID(getString(R.string.GroupOrder));
    	ListData6.setDISPNAME(getString(R.string.GroupOrder));
    	mListGeneralData.add(ListData6);

    	GeneralData ListData7 = new GeneralData();
    	ListData7.set_ID(getString(R.string.LR));
    	ListData7.setDISPNAME(getString(R.string.LR));
    	mListGeneralData.add(ListData7);

        // アダプタを介してデータ表示
        SetGroupList(mListGeneralData);
    }
	
	// グループ表示のときに使用されるアダプター群 -------------------------------------------------------
	private void SetGroupList(List<GeneralData> list)
	{
		GeneralListAdapter adapter = new GeneralListAdapter(this, list);
        mListView = (ListView)findViewById(R.id.ListView1);
        mListView.setAdapter(adapter);
	}
	
	public class GeneralListAdapter extends ArrayAdapter<GeneralData> 
	{ 
	        private LayoutInflater layoutInflater_;
            
	        public GeneralListAdapter(Context context, List<GeneralData> objects) 
	        {
	        	super(context, 0, objects);
	        	layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	        }
	        
	        @Override
	        public int getCount() {
	            return m_ItemCount;
	        }
	        
	        @Override
	        public GeneralData getItem(int position) {
	            return mListGeneralData.get(position);
	        }
	        
	        @Override
	        public long getItemId(int position) {
	            return position;
	        }
	        
			@Override
	        public View getView(final int position, View convertView, ViewGroup parent) 
	        { 
				final GeneralData Data  = this.getItem(position);
				
				if ( convertView == null ) 
				{ 
					if( Data.get_ID().equals(getString(R.string.DefaultTabPosition)))
					{
						convertView = layoutInflater_.inflate(R.layout.rowexpand, null);
						convertView.setOnClickListener(new View.OnClickListener()
				        {
							@Override
							public void onClick(View v)
							{						
								ShowDialog1(getString(R.string.DefaultTabPosition));
							}
						});
					}
					
					if( Data.get_ID().equals(getString(R.string.DisplaySize)))
					{
						convertView = layoutInflater_.inflate(R.layout.rowexpand, null);
						convertView.setOnClickListener(new View.OnClickListener()
				        {
							@Override
							public void onClick(View v)
							{						
								ShowDialog2(getString(R.string.DisplaySize));
							}
						});
					}
					
					if( Data.get_ID().equals(getString(R.string.TabUpDown)))
					{
						convertView = layoutInflater_.inflate(R.layout.rowexpand, null);
						convertView.setOnClickListener(new View.OnClickListener()
				        {
							@Override
							public void onClick(View v)
							{						
								ShowDialog3(getString(R.string.TabUpDown));
							}
						});
					}
					
					if( Data.get_ID().equals(getString(R.string.AccountChoice)))
					{
						convertView = layoutInflater_.inflate(R.layout.rowexpand, null);
						convertView.setOnClickListener(new View.OnClickListener()
				        {
							@Override
							public void onClick(View v)
							{						
								Intent intent = new Intent( GeneralSettingActivity.this, AccountSettingActivity.class );
								startActivityForResult( intent, 0 );
							}
						});
					}
					
					if( Data.get_ID().equals(getString(R.string.TabOrder)))
					{
						convertView = layoutInflater_.inflate(R.layout.rowexpand, null);
						convertView.setOnClickListener(new View.OnClickListener()
				        {
							@Override
							public void onClick(View v)
							{						
								Intent intent = new Intent( GeneralSettingActivity.this, TabSortActivity.class );
								startActivityForResult( intent, 0 );
							}
						});
					}
					
					if( Data.get_ID().equals(getString(R.string.GroupOrder)))
					{
						convertView = layoutInflater_.inflate(R.layout.rowexpand, null);
						convertView.setOnClickListener(new View.OnClickListener()
				        {
							@Override
							public void onClick(View v)
							{						
								Intent intent = new Intent( GeneralSettingActivity.this, GroupSortActivity.class );
								startActivityForResult( intent, 0 );
							}
						});
					}
					
					if( Data.get_ID().equals(getString(R.string.LR)))
					{
						convertView = layoutInflater_.inflate(R.layout.rowexpand, null);
						convertView.setOnClickListener(new View.OnClickListener()
				        {
							@Override
							public void onClick(View v)
							{						
								ShowDialog4(getString(R.string.LR));
							}
						});
					}
					
				}
				                
				TextView textView; 
				textView = (TextView)convertView.findViewById(R.id.Text_ID);
				textView.setText(Data.get_ID());
				textView = (TextView)convertView.findViewById(R.id.TextNAME);
				textView.setText(Data.getDISPNAME());				
								
				return convertView;
	        }
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if( resultCode == 1 )
		{
			// 変更フラグをたてる
			bSettingChaged = true;			
		}
	}
	

	// タブポジションダイアログを表示する
	public void ShowDialog1(String Title)
	{
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("general",Activity.MODE_PRIVATE);
    	String name = pref.getString(getString(R.string.DefaultTabPosition),getString(R.string.Tab_Favorite));
    	
        final CharSequence[] items = { getString(R.string.Tab_Favorite),
        							   getString(R.string.Tab_Group),
        							   getString(R.string.Tab_Kana),
        							   getString(R.string.Tab_History),};
//        							   getString(R.string.TabItem5)};
        
        int pos=0;
        
        if(items[0].equals(name))
        {
        	pos = 0;
        }
        if(items[1].equals(name))
        {
        	pos = 1;
        }
        if(items[2].equals(name))
        {
        	pos = 2;
        }
        if(items[3].equals(name))
        {
        	pos = 3;
        }
//        if(items[4].equals(name))
//        {
//        	pos = 4;
//        }
        		
		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Title);
        
        alertDialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
             	// 画面を閉じる
				dialog.dismiss();
            }
          });
        
        alertDialogBuilder.setSingleChoiceItems(
          items, 
          pos,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
             	// レジストリ保存
         		SharedPreferences pref = getSharedPreferences("general",Activity.MODE_PRIVATE);
				Editor e = pref.edit();
				e.putString(getString(R.string.DefaultTabPosition), (String) items[which]);
				e.commit();
				
				// 変更フラグをたてる
				bSettingChaged = true;
						
				dialog.dismiss();
            }
          });
        alertDialogBuilder.show();
	}
	
	// 画面サイズ調節のダイアログを表示する
	Display display;
	public void ShowDialog2(String Title)
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.editdialog, null);
		final EditText editText = (EditText)view.findViewById(R.id.editText1);
      
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("general",Activity.MODE_PRIVATE);
    	WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	display = wm.getDefaultDisplay();
    	editText.setText( String.valueOf(pref.getInt(getString(R.string.DisplaySize), display.getHeight())) );
    	editText.setFilters(new InputFilter[]{ new NumFilter()});
        		
		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Title + "( 0 - " + String.valueOf(display.getHeight()) + " )");
        alertDialogBuilder.setView(view);
        
        alertDialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
             	// 画面を閉じる
				dialog.dismiss();
            }
        });

        alertDialogBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
            	// レジストリの保管
            	SharedPreferences pref = getSharedPreferences("general",Activity.MODE_PRIVATE);
				Editor e = pref.edit();
				
				if( editText.getText().toString().length() > 0)
				{
					int size = Integer.parseInt(editText.getText().toString());
					
					// dhisplayのサイズは超えない
					if( size > display.getHeight() )
					{
						size = display.getHeight();
					}
					e.putInt(getString(R.string.DisplaySize), size);
					e.commit();
					
					// 変更フラグをたてる
					bSettingChaged = true;
							
	             	// 画面を閉じる
					dialog.dismiss();
				}
            	
            }
        });

        alertDialogBuilder.show();
	}
	
	// タブ上下を設定するダイアログを表示する
	public void ShowDialog3(String Title)
	{
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("general",Activity.MODE_PRIVATE);
    	int updown = pref.getInt(getString(R.string.TabUpDown),0);
    	
        final CharSequence[] items = { getString(R.string.TabUp),
        							   getString(R.string.TabDown),};        

        		
		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Title);
        
        alertDialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
             	// 画面を閉じる
				dialog.dismiss();
            }
          });
        
        alertDialogBuilder.setSingleChoiceItems(
          items, 
          updown,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
             	// レジストリ保存
         		SharedPreferences pref = getSharedPreferences("general",Activity.MODE_PRIVATE);
				Editor e = pref.edit();
				e.putInt(getString(R.string.TabUpDown), which);
				e.commit();
				
				// 変更フラグをたてる
				bSettingChaged = true;
						
				dialog.dismiss();
            }
          });
        alertDialogBuilder.show();
	}
	
	
	// 右手左手を設定するダイアログを表示する
	public void ShowDialog4(String Title)
	{
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("LR",Activity.MODE_PRIVATE);
    	int updown = pref.getInt(getString(R.string.LR),0);
    	
        final CharSequence[] items = { getString(R.string.LR_left),
        							   getString(R.string.LR_right),};        

        		
		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Title);
        
        alertDialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
             	// 画面を閉じる
				dialog.dismiss();
            }
          });
        
        alertDialogBuilder.setSingleChoiceItems(
          items, 
          updown,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
             	// レジストリ保存
         		SharedPreferences pref = getSharedPreferences("LR",Activity.MODE_PRIVATE);
				Editor e = pref.edit();
				e.putInt(getString(R.string.LR), which);
				e.commit();
				
				// 変更フラグをたてる
				bSettingChaged = true;
						
				dialog.dismiss();
            }
          });
        alertDialogBuilder.show();
	}
	
	
}