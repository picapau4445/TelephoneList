package picapau.contacts.onehandlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import picapau.contacts.onehandlist.Data.GroupListData;
import picapau.contacts.onehandlist.db.DBGroupOrderOperation;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GroupTabActivity extends Activity {
	
	public GroupTabActivity context; 
	
	DBGroupOrderOperation db;
	HashMap<Integer, Integer> GroupOrderHash = new HashMap<Integer, Integer>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grouptabactivity);
		
        // 独自のgroupOrderDBをロード
        db = new DBGroupOrderOperation(this);
        GroupOrderHash.clear();
        GroupOrderHash = db.Load();

		LoadGroupData();
	}
	
	// グループ検索
	private void LoadGroupData()
	{
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
        	
        	String accounttype = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.ACCOUNT_TYPE));
        	if(pref.getInt(groupListData.getAccountName() + accounttype,1) == 1 )
        	{
        		ListGroupListData.add(groupListData);
        	}
        	Res1 = cursor.moveToNext();
        }
        
// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
//        cursor.close();

        // グループなし
        {    
	        GroupListData noGrouprow = new GroupListData();
	        noGrouprow.set_ID("-2");
	        noGrouprow.setTitle(getString(R.string.No_Group));
	        ListGroupListData.add(noGrouprow);
	        
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
        
        Collections.sort(ListGroupListData, new StringComparator());
        
        // アダプタを介してデータ表示
        SetGroupList(ListGroupListData);
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
	
	// グループ表示のときに使用されるアダプター群 -------------------------------------------------------
		private void SetGroupList(List<GroupListData> list)
		{
	        ListView listview = (ListView)findViewById( R.id.GroupListView );      
	        GroupListAdapter adapter = new GroupListAdapter(this, list);
	        listview.setItemsCanFocus(false);
	        listview.setAdapter(adapter);
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
					textView.setPadding(50, 0, 0, 0);
					textView = (TextView)convertView.findViewById(R.id.TextGroup); 
					textView.setText(groupListData.getAccountName());
					textView.setPadding(50, 0, 0, 0);
					
					convertView.setFocusable(true);
					convertView.setBackgroundResource(R.color.list_item);
					
					convertView.setOnClickListener(new View.OnClickListener(){
	    				@Override
	    				public void onClick(View v)
	    				{
	    					// メンバー用のアクティビティーを呼び出す
	    					Intent intent;
	    					intent = new Intent().setClassName("picapau.contacts.onehandlist","picapau.contacts.onehandlist.AlphaTabActivity");
	    			        intent.putExtra("GroupID", groupListData.get_ID());
	    			        intent.putExtra("From", getString(R.string.Tab_Group));
	    					startActivity(intent);
	    				}
	    			});
					
					return convertView;
		        }
		}


}