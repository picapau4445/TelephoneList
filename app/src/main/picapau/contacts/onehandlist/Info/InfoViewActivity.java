package picapau.contacts.onehandlist.Info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import picapau.contacts.onehandlist.AlphaTabActivity;
import picapau.contacts.onehandlist.Main;
import picapau.contacts.onehandlist.R;
import picapau.contacts.onehandlist.Data.ContactData;
import picapau.contacts.onehandlist.Data.ContactInfoData;
import picapau.contacts.onehandlist.Data.ContactListData;
import picapau.contacts.onehandlist.Data.GeneralData;
import picapau.contacts.onehandlist.Data.GroupListData;
import picapau.contacts.onehandlist.Data.MailData;
import picapau.contacts.onehandlist.Data.PhoneData;
import picapau.contacts.onehandlist.db.DBGroupOrderOperation;
import picapau.contacts.onehandlist.menu.AccountSettingActivity;
import picapau.contacts.onehandlist.menu.GeneralSettingActivity;
import picapau.contacts.onehandlist.menu.GroupSortActivity;
import picapau.contacts.onehandlist.menu.TabSortActivity;
import picapau.contacts.onehandlist.menu.GeneralSettingActivity.GeneralListAdapter;
import picapau.contacts.onehandlist.menu.GroupSortActivity.GroupListAdapter;
import picapau.contacts.onehandlist.res.SortableListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.DisplayNameSources;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.StatusUpdates;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;

public class InfoViewActivity extends Activity {
	
	Uri    uri;
	String ContactID;
	
	TextView  textKana;
	TextView  textKanji;
	ListView  listInfo;
	ImageView imageStar;
	ImageView imageGroup;
	ImageView imageEdit;

	Uri m_uri;
	
	ArrayList<PhoneData> m_Phonelist = new ArrayList<PhoneData>();
	ArrayList<MailData> m_Maillist = new ArrayList<MailData>();	
	ContactData m_data = new ContactData();
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infoviewactivity);
		
        final Intent intent = getIntent();
        Uri data = intent.getData();
        String authority = data.getAuthority();
        final long rawContactId = ContentUris.parseId(data);
        
        textKana = (TextView)findViewById(R.id.KanaName);
        textKanji = (TextView)findViewById(R.id.KanjiName);
        listInfo = (ListView)findViewById(R.id.InfoView);
		imageStar = (ImageView)findViewById(R.id.imageStar);
		imageGroup = (ImageView)findViewById(R.id.imageGroup);
		imageEdit = (ImageView)findViewById(R.id.imageEdit);
		
        GetContactData(rawContactId);
        DisplayData();
        
		// お気に入り
		imageStar.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				if( m_data.Star == 0 )
				{
					imageStar.setImageResource(R.drawable.btn_star_big_on);
					m_data.Star = 1;
				}
				else
				{
					imageStar.setImageResource(R.drawable.btn_star_big_off);
					m_data.Star = 0;
				}
				
				// DB更新
/*				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
				ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
				   .withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " +	
						          ContactsContract.Data.MIMETYPE + "=?", 
						          new String[]{String.valueOf(rawContactId), 
						   		               GroupMembership.CONTENT_ITEM_TYPE })
				   .withValue(GroupMembership.GROUP_ROW_ID , String.valueOf(m_data.GroupID) )
				   .build());
				try {
					getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
				} catch (RemoteException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
*/
/*				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
				ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
				   .withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " +	
						          ContactsContract.Data.MIMETYPE + "=?", 
						          new String[]{String.valueOf(rawContactId), 
						                       ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE })
				   .withValue(ContactsContract.Data.STARRED , String.valueOf(m_data.Star) )
				   .build());
				try {
					getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
				} catch (RemoteException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
*/
			}
		});
		
		// グループ編集
		imageGroup.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				// グループのドロップダウンリストを表示する
				ShowGroupDialog("160");
			}
		});
		
		// データ編集
		imageEdit.setImageResource(R.drawable.safe_mode_background);
		imageEdit.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				
			}
		});
		
	}
	
	
	void GetContactData(long ContactID)
	{
		boolean Res1 = false; //SQLの判定
		Cursor cursor;
		Uri uri1 = ContactsContract.Data.CONTENT_URI;
		String[] projection1;
		String selection1;
		String[] selectionArgs1;
		String sortOrder1;		
		String strMimeType;
		
		
		// 初期化
		m_data.ID = 0;
		m_data.KanaName = "";
		m_data.KanjiName = "";
		m_data.Star = 0;
		m_data.GroupID = 0;
		
		selection1 = ContactsContract.Data.CONTACT_ID + "=? ";
		selectionArgs1 = new String[]{ String.valueOf(ContactID) };
		
		cursor = getContentResolver().query(
				uri1,
				null,
				selection1,
				selectionArgs1,
				null
				);
		
		Res1 = cursor.moveToFirst();
        while (Res1)
        {
        	strMimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
        	
        	if( strMimeType.equals(StructuredName.CONTENT_ITEM_TYPE) )
        	{
        		if( Locale.JAPAN.equals(Locale.getDefault()) )
				{
        			m_data.KanaName = cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME)) + " " +
        					          cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME));
        			m_data.KanjiName = cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME)) + " " +
					                    cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME));
				}
        		else
        		{
        			m_data.KanjiName = cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME)) + " " +
		                               cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME));
        		}        		
        	}
        	else if( strMimeType.equals(Phone.CONTENT_ITEM_TYPE) )
        	{
        		m_data.PhoneList = m_Phonelist;
    			PhoneData phonedata = new PhoneData();
//    			phonedata.Type = cursor.getString(cursor.getColumnIndex("data3");
    			phonedata.Number = cursor.getString(cursor.getColumnIndex("data1"));
    			phonedata.Label = cursor.getString(cursor.getColumnIndex("data3"));
    			m_Phonelist.add(phonedata);
        		m_data.Star = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.STARRED));

        	}
        	else if( strMimeType.equals(Email.CONTENT_ITEM_TYPE) )
        	{
        		m_data.MailList = m_Maillist;
    			MailData maildata = new MailData();
    			maildata.Address = cursor.getString(cursor.getColumnIndex("data1"));
    			maildata.Label = cursor.getString(cursor.getColumnIndex("data3"));
    			m_Maillist.add(maildata);

        	}
        	
        	
        	Res1 = cursor.moveToNext();
        }
        
        cursor.close();
	}

	void DisplayData()
	{
		ArrayList<ContactInfoData> ListContactInfo = new ArrayList<ContactInfoData>();
		textKana.setText(m_data.KanaName);
		textKanji.setText(m_data.KanjiName);
		
		if( m_data.PhoneList != null )
		{
			for( int i=0; i < m_data.PhoneList.size(); i++ )
			{
				ContactInfoData data = new ContactInfoData();
				data.ID = m_data.PhoneList.get(i).Index;
				data.Name = m_data.PhoneList.get(i).Number;
				data.Category = 1;
				data.Type = m_data.PhoneList.get(i).Label;
				ListContactInfo.add(data);
			}
		}
		
		if( m_data.MailList != null )
		{
			for( int i=0; i < m_data.MailList.size(); i++ )
			{
				ContactInfoData data = new ContactInfoData();
				data.ID = m_data.MailList.get(i).Index;
				data.Name = m_data.MailList.get(i).Address;
				data.Category = 2;
				data.Type = m_data.MailList.get(i).Label;
				ListContactInfo.add(data);
			}
		}
		
		SetContactList(ListContactInfo);
		
		// お気に入り
		if( m_data.Star == 1 )
		{
			imageStar.setImageResource(R.drawable.btn_star_big_on);
		}
		else
		{
			imageStar.setImageResource(R.drawable.btn_star_big_off);
		}
	}
	
	private void SetContactList(List<ContactInfoData> list)
	{
		MyAdapter adapter = new MyAdapter(this, list);
		listInfo.setAdapter(adapter);
	}
	
	public class MyAdapter extends ArrayAdapter<ContactInfoData> 
	{ 
		
        private LayoutInflater layoutInflater_;
        
        public MyAdapter(Context context, List<ContactInfoData> objects) 
        {
        	super(context, 0, objects);
        	layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        }
        
			@Override
	        public View getView(final int position, View convertView, ViewGroup parent) 
	        { 
				if ( convertView == null ) 
				{ 
					convertView = layoutInflater_.inflate(R.layout.rowcontactinfo, null);
				}
				
				final ContactInfoData Data  = this.getItem(position);
				
				ImageView imageIcon;
				imageIcon = (ImageView)convertView.findViewById(R.id.imageIcon);
				if( Data.Category == 1 )//tel
				{
					imageIcon.setImageResource(R.drawable.sym_action_call);
				}
				else if( Data.Category == 2 )//mail
				{
					imageIcon.setImageResource(R.drawable.sym_action_email);
				}
				else
				{
					imageIcon.setImageResource(R.drawable.screen_background_dark);
				}
				
				TextView textId; 
				textId = (TextView)convertView.findViewById(R.id.Text_ID); 
				textId.setText(String.valueOf(Data.ID));
				
				TextView textType; 
				textType = (TextView)convertView.findViewById(R.id.TextType); 
				textType.setText(Data.Type);
				
				TextView textName; 
				textName = (TextView)convertView.findViewById(R.id.TextNAME); 
				textName.setText(Data.Name);

				return convertView;
	        }
	}
	
	
	
	
	
	
	// グループのドロップダウンリストを表示する
	private AlertDialog m_Dlg = null;
	private ListView mListView;
	public void ShowGroupDialog(String ID)
	{
		DBGroupOrderOperation db;
		HashMap<Integer, Integer> GroupOrderHash = new HashMap<Integer, Integer>();
	    final List<GroupListData> mListGroupListData = new ArrayList<GroupListData>(); 
		ArrayList<String> rows = new ArrayList<String>();
		
        // 独自のgroupOrderDBをロード
        db = new DBGroupOrderOperation(this);
        GroupOrderHash.clear();
        GroupOrderHash = db.Load();
        
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("account",Activity.MODE_PRIVATE);
    	
        boolean Res1 = false; //SQLの判定
        
        Cursor cursor; 			//SQLのクエリカーソル

        //コンタクトリストのクエリを作成する
        Uri uri;
        
        // 検索
        uri = ContactsContract.Groups.CONTENT_URI;
        cursor = managedQuery( uri,
        		               null,
        		               ContactsContract.Groups.DELETED + "=? ",
        					   new String[]{"0"},
        					   null);
        
        Res1 = cursor.moveToFirst();
        while (Res1)
        {
        	// データの格納
        	GroupListData groupListData = new GroupListData();
        	groupListData.set_ID(cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID)));
        	groupListData.setTitle(cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE)));
        	groupListData.setAccountName(cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_NAME)));

        	// 独自に用意した並び順をハッシュから取得
        	if ( GroupOrderHash.get(Integer.parseInt(groupListData.get_ID())) != null )
        	{
        		groupListData.setOrder(GroupOrderHash.get(Integer.parseInt(groupListData.get_ID())));
        	}
        	else
        	{
        		// 新入りグループの場合
        		groupListData.setOrder(9999);
        	}
        	
        	if(pref.getInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_TYPE)),1) == 1 )
        	{
        		mListGroupListData.add(groupListData);
        	}
        	Res1 = cursor.moveToNext();
        }
        
        cursor.close();

        // グループなし
        {      
	        GroupListData noGrouprow = new GroupListData();
	        noGrouprow.set_ID("-2");
	        noGrouprow.setTitle(getString(R.string.No_Group));
	        mListGroupListData.add(noGrouprow);
	        
        	// 独自に用意した並び順をハッシュから取得
        	if ( GroupOrderHash.get(Integer.parseInt(noGrouprow.get_ID())) != null )
        	{
        		noGrouprow.setOrder(GroupOrderHash.get(Integer.parseInt(noGrouprow.get_ID())));
        	}
        	else
        	{
        		// 新入りグループの場合
        		noGrouprow.setOrder(9999);
        	}
        }
        
        Collections.sort(mListGroupListData, new StringComparator());
        
        
        final Spinner spinner = new Spinner(this);
        
        for(int i=0;i<mListGroupListData.size();i++)
        {
        	rows.add(i,mListGroupListData.get(i).getTitle());
        }
        
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, rows));

		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);		
        alertDialogBuilder.setTitle(getString(R.string.Group));
        alertDialogBuilder.setView(spinner);
        alertDialogBuilder.setCancelable(true);
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
            	Toast.makeText(getApplicationContext(), mListGroupListData.get(spinner.getSelectedItemPosition()).getTitle(), Toast.LENGTH_LONG).show();
            	// レジストリの保管
            	/*            	SharedPreferences pref = getSharedPreferences("general",Activity.MODE_PRIVATE);
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
	             	// 画面を閉じる
					dialog.dismiss();
				}
*/            	
            }
        });

        m_Dlg = alertDialogBuilder.create();
        m_Dlg.show();
	}

	private ArrayList<String> GetGroupAll()
	{
		DBGroupOrderOperation db;
		HashMap<Integer, Integer> GroupOrderHash = new HashMap<Integer, Integer>();
	    List<GroupListData> mListGroupListData = new ArrayList<GroupListData>(); 
		ArrayList<String> rows = new ArrayList<String>();
		
        // 独自のgroupOrderDBをロード
        db = new DBGroupOrderOperation(this);
        GroupOrderHash.clear();
        GroupOrderHash = db.Load();
        
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("account",Activity.MODE_PRIVATE);
    	
        boolean Res1 = false; //SQLの判定
        
        Cursor cursor; 			//SQLのクエリカーソル

        //コンタクトリストのクエリを作成する
        Uri uri;
        
        // 検索
        uri = ContactsContract.Groups.CONTENT_URI;
        cursor = managedQuery( uri,
        		               null,
        		               ContactsContract.Groups.DELETED + "=? ",
        					   new String[]{"0"},
        					   null);
        
        Res1 = cursor.moveToFirst();
        while (Res1)
        {
        	// データの格納
        	GroupListData groupListData = new GroupListData();
        	groupListData.set_ID(cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID)));
        	groupListData.setTitle(cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE)));
        	groupListData.setAccountName(cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_NAME)));

        	// 独自に用意した並び順をハッシュから取得
        	if ( GroupOrderHash.get(Integer.parseInt(groupListData.get_ID())) != null )
        	{
        		groupListData.setOrder(GroupOrderHash.get(Integer.parseInt(groupListData.get_ID())));
        	}
        	else
        	{
        		// 新入りグループの場合
        		groupListData.setOrder(9999);
        	}
        	
        	if(pref.getInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_TYPE)),1) == 1 )
        	{
        		mListGroupListData.add(groupListData);
        	}
        	Res1 = cursor.moveToNext();
        }
        
        cursor.close();

        // グループなし
        {      
	        GroupListData noGrouprow = new GroupListData();
	        noGrouprow.set_ID("-2");
	        noGrouprow.setTitle(getString(R.string.No_Group));
	        mListGroupListData.add(noGrouprow);
	        
        	// 独自に用意した並び順をハッシュから取得
        	if ( GroupOrderHash.get(Integer.parseInt(noGrouprow.get_ID())) != null )
        	{
        		noGrouprow.setOrder(GroupOrderHash.get(Integer.parseInt(noGrouprow.get_ID())));
        	}
        	else
        	{
        		// 新入りグループの場合
        		noGrouprow.setOrder(9999);
        	}
        }
        
        Collections.sort(mListGroupListData, new StringComparator());
        
        
        GroupListAdapter adapter = new GroupListAdapter(this, mListGroupListData);
        mListView = (ListView) findViewById(R.id.GroupSortList);
        mListView.setAdapter(adapter);

    	
    	return rows;
	}
	
	//Comparator実装クラス  
	@SuppressWarnings("rawtypes")
	class StringComparator implements Comparator {  
	    public static final int ASC = 1;    //昇順  
	    public static final int DESC = -1;    //降順  
	    private int sort = ASC;    //デフォルトは昇順  
	      
	    public StringComparator() 
	    {  
	          
	    }  
	      
	    /** 
	     * @param sort  StringComparator.ASC | StringComparator.DESC。昇順や降順を指定します。 
	     */  
	    public StringComparator(int sort) 
	    {  
	        this.sort = sort;  
	    }  
	      
	    @SuppressWarnings("unchecked")
		public int compare(Object arg0, Object arg1)
	    {
	    	GroupListData a = (GroupListData)arg0;
	    	GroupListData b = (GroupListData)arg1;
	    	String id1 = a.get_ID();
	    	String id2 = b.get_ID();
	    	int order1 = a.getOrder();
	    	int order2 = b.getOrder();
	    	
	    	// 並び順がない場合はグループIDで
	    	if(order1 == 9999 && order2 == 9999 )
	    	{
	    		return ((Comparable)id1).compareTo((Comparable)id2) * sort; 
	    	}
	    	else
	    	{
	    		return ((Comparable)order1).compareTo((Comparable)order2) * sort; 
	    	}	    	
	    }  
	} 
	
	public class GroupListAdapter extends ArrayAdapter<GroupListData> 
	{ 
	        private LayoutInflater layoutInflater_;
            
	        public GroupListAdapter(Context context, List<GroupListData> objects) 
	        {
	        	super(context, 0, objects);
	        	layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	        }
	        
			@Override
	        public View getView(final int position, View convertView, ViewGroup parent) 
	        { 
				if ( convertView == null ) 
				{ 
					convertView = layoutInflater_.inflate(R.layout.rowgroup, null);
				}
				                
				final GroupListData groupListData  = this.getItem(position);
				
				TextView textView; 
				textView = (TextView)convertView.findViewById(R.id.Text_ID); 
				textView.setText(groupListData.get_ID()); 
				textView = (TextView)convertView.findViewById(R.id.TextNAME); 
				textView.setText(groupListData.getTitle());
				textView = (TextView)convertView.findViewById(R.id.TextGroup); 
				textView.setText(groupListData.getAccountName());
				
				return convertView;
	        }
	}
	
}