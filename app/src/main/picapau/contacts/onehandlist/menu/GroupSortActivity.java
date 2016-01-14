package picapau.contacts.onehandlist.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import picapau.contacts.onehandlist.R;
//import picapau.contacts.onehandlist.ContactListViewActivity.StringComparator;
import picapau.contacts.onehandlist.Data.GroupListData;
import picapau.contacts.onehandlist.GroupTabActivity.GroupListAdapter;
import picapau.contacts.onehandlist.R.color;
import picapau.contacts.onehandlist.R.id;
import picapau.contacts.onehandlist.R.layout;
import picapau.contacts.onehandlist.db.DBGroupOrderOperation;
import picapau.contacts.onehandlist.res.SortableListView;

public class GroupSortActivity extends Activity {
    
	DBGroupOrderOperation db;
	HashMap<Integer, Integer> GroupOrderHash = new HashMap<Integer, Integer>();
	
    int mDraggingPosition = -1;
    SortableListView mListView;
    int m_ItemCount = 0;
    List<GroupListData> mListGroupListData = new ArrayList<GroupListData>(); 
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupsortactivity);
        
        // 独自のgroupOrderDBをロード
        db = new DBGroupOrderOperation(this);
        GroupOrderHash.clear();
        GroupOrderHash = db.Load();
        // android内のgroupDBからロード
        LoadGroupData();
        m_ItemCount = mListGroupListData.size();
        // アダプタを介してデータ表示
        SetGroupList(mListGroupListData);
        
        Button button_OK = (Button) findViewById(R.id.button_OK);
        button_OK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// グループ並び情報を更新する
				GroupListData data;
				int _id;
				int order;
				for(int i=0;i<mListGroupListData.size();i++)
				{
					data = mListGroupListData.get(i);
					_id = Integer.parseInt(data.get_ID());
					order = i;
					;
					if( GroupOrderHash.get(_id) == null )
					{
						db.add(_id, order);
					}
					else
					{
						db.update(_id, order);
					}
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

        Toast.makeText(this, getString(R.string.OrderInfo), Toast.LENGTH_LONG).show();
    }
    
	// グループ検索
	private void LoadGroupData()
	{
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

// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい        
//        cursor.close();

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
        GroupListAdapter adapter = new GroupListAdapter(this, list);
        mListView = (SortableListView) findViewById(R.id.GroupSortList);
        mListView.setDragListener(new DragListener());
        mListView.setSortable(true);
        mListView.setAdapter(adapter);
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
	        public int getCount() {
	            return m_ItemCount;
	        }
	        
	        @Override
	        public GroupListData getItem(int position) {
	            return mListGroupListData.get(position);
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
				
				convertView.setBackgroundResource(R.color.list_item);
				convertView.setVisibility(position == mDraggingPosition ? View.INVISIBLE
	                    : View.VISIBLE);
				
				return convertView;
	        }
	}
    
    class DragListener extends SortableListView.SimpleDragListener 
    {
        @Override
        public int onStartDrag(int position) {
            mDraggingPosition = position;
            mListView.invalidateViews();
            return position;
        }
        
        @Override
        public int onDuringDrag(int positionFrom, int positionTo)
        {
            if (positionFrom < 0 || positionTo < 0
                    || positionFrom == positionTo)
            {
                return positionFrom;
            }
            int i;
            if (positionFrom < positionTo)
            {
                final int min = positionFrom;
                final int max = positionTo;
                final GroupListData data = mListGroupListData.get(min);
                i = min;
                while (i < max)
                {
                	mListGroupListData.set(i, mListGroupListData.get(++i));
                }
                mListGroupListData.set(max, data);
            }
            else if (positionFrom > positionTo)
            {
                final int min = positionTo;
                final int max = positionFrom;
                final GroupListData data = mListGroupListData.get(max);
                i = max;
                while (i > min)
                {
                	mListGroupListData.set(i, mListGroupListData.get(--i));
                }
                mListGroupListData.set(min, data);
            }
            mDraggingPosition = positionTo;
            mListView.invalidateViews();
            return positionTo;
        }
        
        @Override
        public boolean onStopDrag(int positionFrom, int positionTo) {
            mDraggingPosition = -1;
            mListView.invalidateViews();
            return super.onStopDrag(positionFrom, positionTo);
        }
        
    }

}