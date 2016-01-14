package picapau.contacts.onehandlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import picapau.contacts.onehandlist.Data.AccountListData;
import picapau.contacts.onehandlist.Data.ContactListData;
import picapau.contacts.onehandlist.Data.GroupListData;
import picapau.contacts.onehandlist.GroupTabActivity.StringComparator;
import picapau.contacts.onehandlist.res.CursorJoinerWithIntKey;



import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class ContactListActivity extends Activity {
	
	public int LR;
	public String GroupID;
	public String Character;
	
	// 電話番号とメールアドレスのキャッシュ
	HashMap<String, String> NumberHash = new HashMap<String, String>();	
	HashMap<String, String> AddressHash = new HashMap<String, String>();	
	HashMap<Integer, Integer> GroupHash = new HashMap<Integer, Integer>();	
	HashMap<String, Integer> AccountHash = new HashMap<String, Integer>();	
	
	public void onStart() {
		super.onStart();
//		Load_Group_Kana_asc();
	}
	
	public void onRestart() {
		super.onRestart();
//		Load_Group_Kana_asc();
	}
	
	public void onResume() {
		super.onResume();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactlistactivity);
		
 		// 右か左か
        SharedPreferences pref = getSharedPreferences("LR",Activity.MODE_PRIVATE);
 		if( pref.getInt(getString(R.string.LR), 0) == 0 )
 		{
 			LR = 0;
 		}
 		else
 		{
 			LR = 1;
 		}
		
		Caching();
		Load_Group_Kana_asc();
	}
	
	// 電話番号とメールをキャッシュ
	public void Caching()
	{
		NumberHash.clear();
		AddressHash.clear();
		GroupHash.clear();
		AccountHash.clear();
		
		boolean res;       

        //コンタクトリストのクエリを作成する
        Uri uri;
    	Cursor dataAddressTable;
    	Cursor dataNumberTable;
    	
    	
    	
//    	List<String>lis = new ArrayList<String>();
//    	List<String>lis2 = new ArrayList<String>();
//    	List<String>lis3 = new ArrayList<String>();
    	
    	
    	

        uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        dataAddressTable = managedQuery( uri, 
        					null, 
    			            ContactsContract.CommonDataKinds.Email.MIMETYPE + " = ?",
    			            new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE}, 
    			            ContactsContract.CommonDataKinds.Email._ID + " asc ");
    	
		res = dataAddressTable.moveToFirst();
		while(res)
		{
			
			
			
/* testでIDとlookupkeyの仕組みを調べました			
			lis.add(dataAddressTable.getString(
			      dataAddressTable.getColumnIndex(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID)));
			
			lis2.add(dataAddressTable.getString(
			      dataAddressTable.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
			
			lis3.add(dataAddressTable.getString(
				      dataAddressTable.getColumnIndex(ContactsContract.CommonDataKinds.Email.LOOKUP_KEY)));
*/			
			
			
			
			if( !AddressHash.containsKey(dataAddressTable.getString(dataAddressTable.getColumnIndex(ContactsContract.CommonDataKinds.Email.LOOKUP_KEY))) )
			{
			  AddressHash.put(
			    dataAddressTable.getString(
			      dataAddressTable.getColumnIndex(ContactsContract.CommonDataKinds.Email.LOOKUP_KEY)), // ID
			    dataAddressTable.getString(
			      dataAddressTable.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))); // メールアドレス
			}  
			
			res = dataAddressTable.moveToNext();
		}

		// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
//		// カーソルを閉じる
//		dataAddressTable.close();
		
		
        uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        dataNumberTable = managedQuery( uri, 
        					null, 
    			            ContactsContract.CommonDataKinds.Phone.MIMETYPE + " = ?",
    			            new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}, 
    			            ContactsContract.CommonDataKinds.Phone._ID + " asc ");
    			
		res = dataNumberTable.moveToFirst();
		while(res)
		{
			if( !NumberHash.containsKey(dataNumberTable.getString(dataNumberTable.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY))) )
			{
			  NumberHash.put(
					  dataNumberTable.getString(
							  dataNumberTable.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)), // ID
							  dataNumberTable.getString(
									  dataNumberTable.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA))); // 電話番号
			}
			  res = dataNumberTable.moveToNext();
		}

// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
//		// カーソルを閉じる
//		dataNumberTable.close();
		
		LoadGroupData();
	}
    
	// グループ検索
	private void LoadGroupData()
	{
		GroupHash.clear();
		
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("account",Activity.MODE_PRIVATE);
    	
        boolean Res1 = false; //SQLの判定
        
        Cursor cursor; 			//SQLのクエリカーソル

        List<GroupListData> ListGroupListData = new ArrayList<GroupListData>();
        
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
        	String GroupID = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
         	String accounttype = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_TYPE));
         	String accountname = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_NAME));
        	GroupHash.put( Integer.parseInt(GroupID), pref.getInt(accountname + accounttype,1));
        	Res1 = cursor.moveToNext();
        }
        
// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
//        cursor.close();

        // グループなし
        GroupHash.put( 0, 1);
	        
    }
	
	// グループ内の名前検索
	public void Load_Group_Kana_asc()
	{
        // アカウント情報のレジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("account",Activity.MODE_PRIVATE);
    	
     	Account[] accounts = AccountManager.get(this).getAccounts();  
         for (Account account : accounts)
         {  
             String type = account.name + account.type;
             
             if( pref.getInt(type, 1) == 1 )
             {
            	 AccountHash.put( type, pref.getInt(type,1));
             }
         } 
         
         // アカウント所属していないデータは無条件表示（nullnullとなったデータ）
         AccountHash.put( "nullnull", 1);
         
		List<ContactListData> ListContactListData = new ArrayList<ContactListData>();
		ListContactListData.clear();
		
        // 親インテントからGroupIDと検索文字を取得
		Intent intent = getIntent();
        if(intent != null)
        {
            GroupID = intent.getStringExtra("GroupID");
            Character = intent.getStringExtra("Character");
        }
        
		// 名前一覧検索
		Cursor c1;
		Uri uri1 = ContactsContract.Data.CONTENT_URI;
		String[] projection1 = { ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID, // これはグループデータを合致させるのに必要
								 ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
								 ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME, 
								 ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME,
								 ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY, // これはメールと電話番号を合致させるのに必要
								 ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
								 ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
				                 ContactsContract.RawContacts.ACCOUNT_TYPE,
				                 ContactsContract.RawContacts.ACCOUNT_NAME,
				                 ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID //これは電話帳データ呼び出しに必要
		                        };
		String selection1;
		String[] selectionArgs1;
		String sortOrder1 = StructuredName.CONTACT_ID;
		
		if( Character != null )
		{
			selection1 = Data.MIMETYPE + "=?" + " AND " + 
	                 "(" + 
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
				     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + 
				     ")";					     
		
			if( Character.equals("ア") )
			{
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"あ%",
						"い%",
						"う%",
						"え%",
						"お%",
						"ｱ%",
						"ｲ%",
						"ｳ%",
						"ｴ%",
						"ｵ%"
						};
			}
			else if( Character.equals("カ") )
			{
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"か%",
						"き%",
						"く%",
						"け%",
						"こ%",
						"ｶ%",
						"ｷ%",
						"ｸ%",
						"ｹ%",
						"ｺ%"
						};
			}
			else if( Character.equals("サ") )
			{
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"さ%",
						"し%",
						"す%",
						"せ%",
						"そ%",
						"ｻ%",
						"ｼ%",
						"ｽ%",
						"ｾ%",
						"ｿ%"
						};
			}
			else if( Character.equals("タ") )
			{
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"た%",
						"ち%",
						"つ%",
						"て%",
						"と%",
						"ﾀ%",
						"ﾁ%",
						"ﾂ%",
						"ﾃ%",
						"ﾄ%"
						};
			}
			else if( Character.equals("ナ") )
			{
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"な%",
						"に%",
						"ぬ%",
						"ね%",
						"の%",
						"ﾅ%",
						"ﾆ%",
						"ﾇ%",
						"ﾈ%",
						"ﾉ%"
						};
			}
			else if( Character.equals("ハ") )
			{
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"は%",
						"ひ%",
						"ふ%",
						"へ%",
						"ほ%",
						"ﾊ%",
						"ﾋ%",
						"ﾌ%",
						"ﾍ%",
						"ﾎ%"
						};
			}
			else if( Character.equals("マ") )
			{
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"ま%",
						"み%",
						"む%",
						"め%",
						"も%",
						"ﾏ%",
						"ﾐ%",
						"ﾑ%",
						"ﾒ%",
						"ﾓ%"
						};
			}
			else if( Character.equals("ヤ") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"や%",
						"ゆ%",
						"よ%",
						"ﾔ%",
						"ﾕ%",
						"ﾖ%",
						};
			}
			else if( Character.equals("ラ") )
			{
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"ら%",
						"り%",
						"る%",
						"れ%",
						"ろ%",
						"ﾗ%",
						"ﾘ%",
						"ﾙ%",
						"ﾚ%",
						"ﾛ%"
						};
			}
			else if( Character.equals("ワ") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
					     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"わ%",
						"を%",
						"ん%",
						"ﾜ%",
						"ｦ%",
						"ﾝ%",
						};
			}
			else if( Character.equals("A") )
			{
					selection1 = Data.MIMETYPE + "=?" + " AND " + 
			                 "(" + 
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + " OR " +
						     StructuredName.PHONETIC_FAMILY_NAME + " LIKE ?" + 
						     ")";					     
				
					selectionArgs1 = new String[] { 
							StructuredName.CONTENT_ITEM_TYPE,
							"a%",
							"b%",
							"c%",
							"d%",
							"e%",
							"f%",
							"g%",
							"h%",
							"i%",
							"j%",
							"k%",
							"l%",
							"m%",
							"n%",
							"o%",
							"p%",
							"q%",
							"r%",
							"s%",
							"t%",
							"u%",
							"v%",
							"w%",
							"x%",
							"y%",
							"z%"
							};			
			}
			
// ここからは海外向けの処理です ------------------------------------------------------------------------
			else if( Character.equals("ABC") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"A%",
						"B%",
						"C%",
						"a%",
						"b%",
						"c%",
						};
			}
			else if( Character.equals("DEF") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"D%",
						"E%",
						"F%",
						"d%",
						"e%",
						"f%",
						};
			}
			else if( Character.equals("GHI") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"G%",
						"H%",
						"I%",
						"g%",
						"h%",
						"i%",
						};
			}
			else if( Character.equals("JKL") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"J%",
						"K%",
						"L%",
						"j%",
						"k%",
						"l%",
						};
			}
			else if( Character.equals("MNO") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"M%",
						"N%",
						"O%",
						"m%",
						"n%",
						"o%",
						};
			}
			else if( Character.equals("PQRS") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"P%",
						"Q%",
						"R%",
						"S%",
						"p%",
						"q%",
						"r%",
						"s%",
						};
			}
			else if( Character.equals("TUV") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"T%",
						"U%",
						"V%",
						"t%",
						"u%",
						"v%",
						};
			}
			else if( Character.equals("WXYZ") )
			{
				selection1 = Data.MIMETYPE + "=?" + " AND " + 
		                 "(" + 
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + " OR " +
					     StructuredName.GIVEN_NAME + " LIKE ?" + 
					     ")";					     
				selectionArgs1 = new String[] { 
						StructuredName.CONTENT_ITEM_TYPE,
						"W%",
						"X%",
						"Y%",
						"Z%",
						"w%",
						"x%",
						"y%",
						"z%",
						};
			}
			
			else	// All
			{
				selection1 = Data.MIMETYPE + "=?";
				selectionArgs1 = new String[]{ StructuredName.CONTENT_ITEM_TYPE };
				
			}
		}
		else	// All
		{
			selection1 = Data.MIMETYPE + "=?";
			selectionArgs1 = new String[]{ StructuredName.CONTENT_ITEM_TYPE };
		}
		
		
		// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
		c1 = managedQuery(
//		c1 = getContentResolver().query(
				uri1,
				projection1,
				selection1,
				selectionArgs1,
				sortOrder1
				);
		
		
		if( c1.moveToFirst() == false )
		{
			SetNameList(ListContactListData);
			return;
		}
		
		Cursor c2;
		Uri uri2 = Data.CONTENT_URI;
		String[] projection2 = { Data.CONTACT_ID, GroupMembership.GROUP_ROW_ID, Data.DISPLAY_NAME};
		String selection2;
		String[] selectionArgs2;
		String sortOrder2 = Data.CONTACT_ID;
		
		// グループメンバー検索
		
		// 検索条件
		if( GroupID == null )//すべて
		{
			selection2 = Data.MIMETYPE + "=?";
			selectionArgs2 = new String[]{ GroupMembership.CONTENT_ITEM_TYPE };
		}
		else if( GroupID.equals("-1") )//お気に入り
		{
			selection2 = Data.MIMETYPE + "=? AND " + Data.STARRED + "=?";
			selectionArgs2 = new String[]{ Phone.CONTENT_ITEM_TYPE, String.valueOf(1) };
		}
		else if( GroupID.equals("-2") )//グループなし
		{
			selection2 = Data.MIMETYPE + "=?";
			selectionArgs2 = new String[]{ GroupMembership.CONTENT_ITEM_TYPE };			
		}
		else//特定のグループ
		{
			selection2 = Data.MIMETYPE + "=? AND " + GroupMembership.GROUP_ROW_ID + "=?";
			selectionArgs2 = new String[]{ GroupMembership.CONTENT_ITEM_TYPE,String.valueOf(GroupID) };			
		}

		
// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
		c2 = managedQuery(
//		c2 = getContentResolver().query(
				uri2,
				projection2,
				selection2,
				selectionArgs2,
				sortOrder2
				);				
		
//		boolean sta = c2.moveToFirst();
//		while(sta)
//		{
//			String a = c2.getString(2);
//			sta = c2.moveToNext();
//		}
		
		CursorJoinerWithIntKey cj = new CursorJoinerWithIntKey(
				c1, new String[]{ StructuredName.CONTACT_ID },
				c2, new String[]{ Data.CONTACT_ID });

		
		boolean AddFlg;
		int groupid;
		String account;
		for(CursorJoinerWithIntKey.Result result : cj)
		{
		
			// リストに追加されるか判定するフラグ
			AddFlg = false;
			groupid = 0;

			// 検索条件
			if( GroupID == null )//すべて
			{
				switch(result)
				{
				case BOTH:
				case LEFT:
					account = c1.getString(8) + c1.getString(7);
					if( AccountHash.containsKey(account) == false )
					{
						continue;
					}
					AddFlg = true;
					break;
				}
			}
			else if( GroupID.equals("-1") )//お気に入り
			{				
				switch(result)
				{
				case BOTH:
					account = c1.getString(8) + c1.getString(7);
					if( AccountHash.containsKey(account) == false )
					{
						continue;
					}
					AddFlg = true;
					break;
				}
			}
			else if( GroupID.equals("-2") )//グループなし
			{
				switch(result)
				{
				case LEFT:
					account = c1.getString(8) + c1.getString(7);
					if( AccountHash.containsKey(account) == false )
					{
						continue;
					}
					AddFlg = true;
					break;
				}
			}
			else//特定のグループ
			{
				switch(result)
				{
				case BOTH:
					account = c1.getString(8) + c1.getString(7);
					if( AccountHash.containsKey(account) == false )
					{
						continue;
					}
					AddFlg = true;
					break;
				}
			}
			
			
			
			if(	AddFlg == true )
			{
				ContactListData contactListdata = new ContactListData();
				contactListdata.set_ID(c1.getString(9)); // ＩＤはRAW_CONTACT_IDで
				contactListdata.set_key(c1.getString(4)); // ＬＯＯＫＵＰＫＥＹも格納しておく
//				contactListdata.setDISPNAME(c1.getString(1));
				if( c1.getString(5) != null && c1.getString(6) != null )
				{
					if( Locale.JAPAN.equals(Locale.getDefault()) )
					{
						contactListdata.setDISPNAME( c1.getString(5) + " " + c1.getString(6) );
					}
					else
					{
						contactListdata.setDISPNAME( c1.getString(6) + " " + c1.getString(5) );
					}
				}
				else if( c1.getString(5) != null && c1.getString(6) == null )
				{
					contactListdata.setDISPNAME( c1.getString(5) );
				}
				else if( c1.getString(5) == null && c1.getString(6) != null )
				{
					contactListdata.setDISPNAME( c1.getString(6) );
				}
				else
				{
					// まあこれはありえないんだけど
					contactListdata.setDISPNAME(" ");
				}
				
				if( Locale.JAPAN.equals(Locale.getDefault()) )
				{
					contactListdata.setKana( c1.getString(2) + c1.getString(3) );
				}
				else
				{
					contactListdata.setKana( c1.getString(3) + c1.getString(2) );
				}
				
				
				// ＬＯＯＫＵＰＫＥＹを使って番号とメアドを検索
				contactListdata.setNUMBER(NumberHash.get(contactListdata.get_key()));
				contactListdata.setADDRESS1(AddressHash.get(contactListdata.get_key()));
				
				ListContactListData.add(contactListdata);			
			}
	
		}
		
		
		
		
		
// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
//		c1.close();
//		c2.close();
		
		
		
		
		Collections.sort(ListContactListData, new StringComparator());
		
       // アダプタを介してデータ表示
        SetNameList(ListContactListData);
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
	    	ContactListData a = (ContactListData)arg0;
	    	ContactListData b = (ContactListData)arg1;
	    	String kana1 = a.getKana();
	    	String kana2 = b.getKana();
	    	
	    	// フリガナがないメンバーは下の方へ
	        if (kana1.equals("") && kana2.equals("")) 
	        {  
	            return 0;   // arg0 = arg1  
	        } 
	        else if (kana1.equals("")) 
	        {  
	            return 1 * sort;   // arg1 > arg2  
	        } 
	        else if (kana2.equals("")) 
	        {  
	            return -1 * sort;  // arg1 < arg2  
	        }
	                
	        return ((Comparable)kana1).compareTo((Comparable)kana2) * sort;  
	    }  
	} 
	

	// 名前表示のときに使用されるアダプター群 -------------------------------------------------------
	private void SetNameList(List<ContactListData> list)
	{
        ListView listview = (ListView)findViewById( R.id.ContactListView );      
        NameListAdapter adapter = new NameListAdapter(this, list);
        listview.setAdapter(adapter);
	}

	public class NameListAdapter extends ArrayAdapter<ContactListData> 
	{
		
	        private LayoutInflater layoutInflater_;
	        
	        public NameListAdapter(Context context, List<ContactListData> objects) 
	        {
	        	super(context, 0, objects);
	        	layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	        }
	        
			@Override
	        public View getView(final int position, View convertView, ViewGroup parent) 
	        { 
				if ( convertView == null ) 
				{ 
					if( LR == 0 )
					{
						convertView = layoutInflater_.inflate(R.layout.rowcontact_left, null);
					}
					else
					{
						convertView = layoutInflater_.inflate(R.layout.rowcontact_right, null);
					}
				}
				
				final ContactListData contactlistdata  = this.getItem(position);
				
				TextView textId; 
				textId = (TextView)convertView.findViewById(R.id.Text_ID); 
				textId.setText(contactlistdata.get_ID());
				
				// 名前
				TextView textName; 
				textName = (TextView)convertView.findViewById(R.id.TextNAME); 
				textName.setText(contactlistdata.getDISPNAME());
				textName.setOnClickListener(new View.OnClickListener()
		        {
					@Override
					public void onClick(View v)
					{
//						Intent intent = new Intent().setClassName("picapau.contacts.onehandlist","picapau.contacts.onehandlist.Info.InfoViewActivity");
//						Intent intent = new Intent("picapau.contacts.onehandlist.Info.InfoViewActivity", Uri.parse("content://contacts/people/" + contactlistdata.get_ID()));
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/" + contactlistdata.get_ID()));
						Set<String> cat = intent.getCategories();
						String sc = intent.getScheme();
						boolean b = intent.hasFileDescriptors();
						startActivity(intent);
					}
				});				          				
				
				// メールボタン
				ImageButton imageMail;
				imageMail = (ImageButton)convertView.findViewById(R.id.image_mail);
				
				// メールアドレス
				TextView textAddress; 
				textAddress = (TextView)convertView.findViewById(R.id.TextADDRESS); 
				textAddress.setText("");
				textAddress.setText(contactlistdata.getADDRESS1());

				String strAddress;
//				strAddress = AddressHash.get(contactlistdata.get_key());
				strAddress = contactlistdata.getADDRESS1();
				
				if( strAddress == null )
				{
					strAddress = "";
				}
				
				if( strAddress.length() > 0 )
				{
					imageMail.setImageResource(R.drawable.sym_action_email);
					imageMail.setOnClickListener(new View.OnClickListener()
			        {
						@Override
						public void onClick(View v)
						{
							String strAddress;
							strAddress = AddressHash.get(contactlistdata.get_key());
							OnMail(strAddress);
						}
					});
					imageMail.setOnLongClickListener(new View.OnLongClickListener()
			        {
						@Override
						public boolean onLongClick(View v)
						{
							ShowAddressDialog(getString(R.string.MailAddress), contactlistdata.get_ID());
					        return false;
						}
					});
				}
				else
				{
					imageMail.setImageResource(R.drawable.screen_background_dark);					
				}
				          				
				// 電話ボタン
				ImageButton imageCall;
				imageCall = (ImageButton)convertView.findViewById(R.id.image_call);
				
				// 電話番号
				TextView textNumber; 
				textNumber = (TextView)convertView.findViewById(R.id.TextNUMBER);
				textNumber.setText(contactlistdata.getNUMBER());
				
				String strNumber;
//				strNumber = NumberHash.get(contactlistdata.get_key());
				strNumber = contactlistdata.getNUMBER();
				
				if( strNumber == null )
				{
					strNumber = "";
				}
				
				if( strNumber.length() > 0 )
				{
					imageCall.setImageResource(R.drawable.sym_action_call);
					imageCall.setOnClickListener(new View.OnClickListener()
			        {
						@Override
						public void onClick(View v)
						{						
							String strNumber;
							strNumber = NumberHash.get(contactlistdata.get_key());
							OnCall(strNumber);
						}
					});
					imageCall.setOnLongClickListener(new View.OnLongClickListener()
			        {
						@Override
						public boolean onLongClick(View v)
						{
							ShowNumberDialog(getString(R.string.Telephone), contactlistdata.get_ID());
					        return false;
						}
					});
				}
				else
				{
					imageCall.setImageResource(R.drawable.screen_background_dark);					
				}
				
				return convertView;
	        }
	}

	private AlertDialog m_Dlg = null;
	
	// メールアドレスダイアログを表示する
	public void ShowAddressDialog(String Title, String ID)
	{
        final ArrayList<String> rows = GetAddressAll(ID);

        ListView lv = new ListView(this);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rows));
        lv.setScrollingCacheEnabled(false);
        lv.setOnItemClickListener(new OnItemClickListener(){
                public void onItemClick(AdapterView<?> items, View view, int position, long id) {
                    m_Dlg.dismiss();
                    OnMail(rows.get(position).toString());
                    //Toast.makeText(((AlphaTabActivity)getParent()).context, rows.get(position).toString(), Toast.LENGTH_LONG).show();
                }
            });

		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(((AlphaTabActivity)getParent()).context);		
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setView(lv);
        alertDialogBuilder.setCancelable(true);		
        m_Dlg = alertDialogBuilder.create();
        m_Dlg.show();
	}

	// メールアドレスダイアログを表示する
	public void ShowNumberDialog(String Title, String ID)
	{
        final ArrayList<String> rows = GetNumberAll(ID);

        ListView lv = new ListView(this);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rows));
        lv.setScrollingCacheEnabled(false);
        lv.setOnItemClickListener(new OnItemClickListener(){
                public void onItemClick(AdapterView<?> items, View view, int position, long id) {
                    m_Dlg.dismiss();
                    OnCall(rows.get(position).toString());
                    //Toast.makeText(((AlphaTabActivity)getParent()).context, rows.get(position).toString(), Toast.LENGTH_LONG).show();
                }
            });

		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(((AlphaTabActivity)getParent()).context);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setView(lv);
        alertDialogBuilder.setCancelable(true);		
        m_Dlg = alertDialogBuilder.create();
        m_Dlg.show();
	}
	
	private ArrayList<String> GetNumberAll(String strID)
	{
		ArrayList<String> rows = new ArrayList<String>();
        boolean Res2 = false;
        
    	Cursor cur;

        //コンタクトリストのクエリを作成する
        Uri uri;

    	// 登録されているすべての電話番号を取得する
        uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    	cur = managedQuery( uri, 
    			            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, 
    			            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = " + strID,
    			            null, 
    			            null);
    	Res2 = cur.moveToFirst();
        while (Res2)
        {
    		rows.add( cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) );
    		Res2 = cur.moveToNext();
    	}
    	
// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
//    	cur.close();
    	
    	return rows;
	}
	
	private ArrayList<String> GetAddressAll(String strID)
	{
		ArrayList<String> rows = new ArrayList<String>();
        boolean Res2 = false;
        
    	Cursor cur;

        //コンタクトリストのクエリを作成する
        Uri uri;

    	// 登録されているすべてのメールアドレスを取得する
        uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    	cur = managedQuery( uri, 
    			            new String[]{ContactsContract.CommonDataKinds.Email.DATA}, 
    			            ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = " + strID,
    			            null, 
    			            null);
    	Res2 = cur.moveToFirst();
    	while(Res2)
    	{    		
    		rows.add( cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)) );
    		Res2 = cur.moveToNext();
    	}
    	
// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
//    	cur.close();
    	
    	return rows;
	}
	
	public void OnCall(String strNumber)
	{
		// デバッグ中
		if(android.os.Debug.isDebuggerConnected())
		{
			// log
			Toast.makeText(((AlphaTabActivity)getParent()).context, strNumber, Toast.LENGTH_LONG).show();
			Log.d("OnCall()", strNumber);
			return;
		}

		if( strNumber.length() == 0 )
		{
			return;
		}
        // 電話番号をセットして電話をかける
		Intent intent;
		String strParse;
        strParse = "tel:" + strNumber;
        intent = new Intent(Intent.ACTION_CALL,Uri.parse(strParse));
        startActivity(intent);		
	}
	
	public void OnMail(String strAddress)
	{
		// デバッグ中
		if(android.os.Debug.isDebuggerConnected())
		{
			// log
			Toast.makeText(((AlphaTabActivity)getParent()).context, strAddress, Toast.LENGTH_LONG).show();
			Log.d("OnMail()", strAddress);
			return;
		}

		if( strAddress.length() == 0 )
		{
			return;
		}
    	// メールアドレスをセットしてメールを送る
		Intent intent;
    	String strParse;
    	strParse = "mailto:" + strAddress;
    	intent = new Intent(Intent.ACTION_SENDTO,Uri.parse(strParse));
    	startActivity(intent);
	}

}
