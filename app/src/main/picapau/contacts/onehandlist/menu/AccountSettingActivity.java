package picapau.contacts.onehandlist.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import picapau.contacts.onehandlist.R;
import picapau.contacts.onehandlist.Data.AccountListData;
import picapau.contacts.onehandlist.Data.GroupListData;
import picapau.contacts.onehandlist.R.color;
import picapau.contacts.onehandlist.R.id;
import picapau.contacts.onehandlist.R.layout;
import picapau.contacts.onehandlist.R.string;
import picapau.contacts.onehandlist.res.SortableListView;

public class AccountSettingActivity extends Activity {
    
    int mDraggingPosition = -1;
    ListView mListView;
    int m_ItemCount = 0;
    List<AccountListData> mListAccountListData = new ArrayList<AccountListData>(); 
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountsettingactivity);
        
        LoadAccountData();
        m_ItemCount = mListAccountListData.size();            	
        
        Button button_OK = (Button) findViewById(R.id.button_OK);
        button_OK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// グループ並び情報を更新する
				SharedPreferences pref = getSharedPreferences("account",Activity.MODE_PRIVATE);
				AccountListData data;
				String _id;
				int Disp;
				for(int i=0;i<mListAccountListData.size();i++)
				{
					data = mListAccountListData.get(i);
					_id = data.getAccountName() + data.getAccountType();
					Disp = data.getDisp();
					Editor e = pref.edit();
					e.putInt(_id, Disp);
					e.commit();
				}
				
				// 変更フラグをたてる
				setResult(1);
				
				// 画面を閉じる
				finish();
			}
		});
        
        Button button_Cancel = (Button) findViewById(R.id.button_Cancel);
        button_Cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 画面を閉じる
				finish();
			}
		});

    }
    
	// グループ検索
	private void LoadAccountData()
	{
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("account",Activity.MODE_PRIVATE);

    	Account[] accounts = AccountManager.get(this).getAccounts();  
        for (Account account : accounts) {  
            String name = account.name;  
            String type = account.type;  
            int hashCode = account.hashCode();  

        	AccountListData accountListData1 = new AccountListData();
        	accountListData1.set_ID(hashCode);
        	accountListData1.setAccountName(name);
        	accountListData1.setAccountType(type);
        	accountListData1.setDisp(pref.getInt(name + type, 1));
        	mListAccountListData.add(accountListData1);
        } 
        
        // アダプタを介してデータ表示
        SetGroupList(mListAccountListData);
    }
	
	// グループ表示のときに使用されるアダプター群 -------------------------------------------------------
	private void SetGroupList(List<AccountListData> list)
	{
		AccountListAdapter adapter = new AccountListAdapter(this, list);
        mListView = (ListView)findViewById(R.id.ListView1);
        mListView.setAdapter(adapter);
	}
	
	public class AccountListAdapter extends ArrayAdapter<AccountListData> 
	{ 
	        private LayoutInflater layoutInflater_;
            
	        public AccountListAdapter(Context context, List<AccountListData> objects) 
	        {
	        	super(context, 0, objects);
	        	layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	        }
	        
	        @Override
	        public int getCount() {
	            return m_ItemCount;
	        }
	        
	        @Override
	        public AccountListData getItem(int position) {
	            return mListAccountListData.get(position);
	        }
	        
	        @Override
	        public long getItemId(int position) {
	            return position;
	        }
	        
			@Override
	        public View getView(final int position, View convertView, ViewGroup parent) 
	        { 
				if ( convertView == null ) 
				{ 
					convertView = layoutInflater_.inflate(R.layout.rowcheck, null);
				}
				                
				final AccountListData accountListData  = this.getItem(position);
				
				TextView textView; 
				textView = (TextView)convertView.findViewById(R.id.Text_ID); 
				textView.setText(String.valueOf(accountListData.get_ID())); 
				textView = (TextView)convertView.findViewById(R.id.TextNAME); 
				textView.setText(accountListData.getAccountName());
				textView = (TextView)convertView.findViewById(R.id.TextType); 
				textView.setText(accountListData.getAccountType());
				
				CheckBox checkbox;
				checkbox = (CheckBox)convertView.findViewById(R.id.checkBox1);
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
					{
						if(isChecked)
						{
							accountListData.setDisp(1);
						}
						else
						{
							accountListData.setDisp(0);
						}
					}
	    		});
				
				if( accountListData.getDisp() == 1 )
				{
					checkbox.setChecked(true);
				}
				else
				{
					checkbox.setChecked(false);
				}
				
				return convertView;
	        }
	}
    
}