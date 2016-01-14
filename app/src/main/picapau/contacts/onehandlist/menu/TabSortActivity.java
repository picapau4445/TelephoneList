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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import picapau.contacts.onehandlist.AlphaTabActivity;
import picapau.contacts.onehandlist.R;
import picapau.contacts.onehandlist.Data.GroupListData;
import picapau.contacts.onehandlist.R.color;
import picapau.contacts.onehandlist.R.id;
import picapau.contacts.onehandlist.R.layout;
import picapau.contacts.onehandlist.R.string;
import picapau.contacts.onehandlist.res.SortableListView;

public class TabSortActivity extends Activity {
    
    int mDraggingPosition = -1;
    SortableListView mListView;
    int m_ItemCount = 0;
    List<GroupListData> mListGroupListData = new ArrayList<GroupListData>(); 
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabsortactivity);
        
        
        LoadGroupData();
        m_ItemCount = mListGroupListData.size();
        
        Button button_OK = (Button) findViewById(R.id.button_OK);
        button_OK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// グループ並び情報を更新する
				SharedPreferences pref = getSharedPreferences("taborder",Activity.MODE_PRIVATE);
				GroupListData data;
				String _id;
				int order;
				for(int i=0;i<mListGroupListData.size();i++)
				{
					data = mListGroupListData.get(i);
					_id = data.get_ID();
					order = i;
					Editor e = pref.edit();
					e.putInt(_id, order);
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
        
        Toast.makeText(this, getString(R.string.OrderInfo), Toast.LENGTH_LONG).show();
    }
    
	// グループ検索
	private void LoadGroupData()
	{
        // レジストリ呼び出し
    	SharedPreferences pref = getSharedPreferences("taborder",Activity.MODE_PRIVATE);

    	// データの格納
    	String str;
    	str = getString(R.string.Tab_Favorite);
    	GroupListData groupListData1 = new GroupListData();
    	groupListData1.set_ID(str);
    	groupListData1.setTitle(str);
    	groupListData1.setAccountName("");
    	groupListData1.setOrder(pref.getInt(str, 0));
    	groupListData1.setDefault(pref.getInt(str, 1));
    	mListGroupListData.add(groupListData1);
    	GroupListData groupListData2 = new GroupListData();
    	str = getString(R.string.Tab_Group);
    	groupListData2.set_ID(str);
    	groupListData2.setTitle(str);
    	groupListData2.setAccountName("");
    	groupListData2.setOrder(pref.getInt(str, 1));
    	groupListData2.setDefault(pref.getInt(str, 0));
    	mListGroupListData.add(groupListData2);
    	GroupListData groupListData3 = new GroupListData();
    	str = getString(R.string.Tab_Kana);
    	groupListData3.set_ID(str);
    	groupListData3.setTitle(str);
    	groupListData3.setAccountName("");
    	groupListData3.setOrder(pref.getInt(str, 2));
    	groupListData3.setDefault(pref.getInt(str, 0));
    	mListGroupListData.add(groupListData3);
    	GroupListData groupListData4 = new GroupListData();
    	str = getString(R.string.Tab_History);
    	groupListData4.set_ID(str);
    	groupListData4.setTitle(str);
    	groupListData4.setAccountName("");
    	groupListData4.setOrder(pref.getInt(str, 3));
    	groupListData4.setDefault(pref.getInt(str, 0));
    	mListGroupListData.add(groupListData4);
    	GroupListData groupListData5 = new GroupListData();
    	str = getString(R.string.Tab_Dial);
    	groupListData5.set_ID(str);
    	groupListData5.setTitle(str);
    	groupListData5.setAccountName("");
    	groupListData5.setOrder(pref.getInt(str, 4));
    	groupListData5.setDefault(pref.getInt(str, 0));
    	mListGroupListData.add(groupListData5);

    	Collections.sort(mListGroupListData, new StringComparator());
        
        // アダプタを介してデータ表示
        SetGroupList(mListGroupListData);
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
					convertView = layoutInflater_.inflate(R.layout.rowtabsetting, null);
				}
				                
				final GroupListData groupListData  = this.getItem(position);
				
				ImageView imageview;
				imageview = (ImageView)convertView.findViewById(R.id.imageView1);
				if( groupListData.get_ID().equals(getString(R.string.Tab_Favorite)) )
				{
					imageview.setImageResource(R.drawable.ic_menu_star);
				}
				if( groupListData.get_ID().equals(getString(R.string.Tab_Group)) )
				{
					imageview.setImageResource(R.drawable.ic_menu_allfriends);
				}
				if( groupListData.get_ID().equals(getString(R.string.Tab_Kana)) )
				{
					imageview.setImageResource(R.drawable.ic_menu_sort_alphabetically);
				}
				if( groupListData.get_ID().equals(getString(R.string.Tab_History)) )
				{
					imageview.setImageResource(R.drawable.ic_menu_recent_history);
				}
				if( groupListData.get_ID().equals(getString(R.string.Tab_Dial)) )
				{
					imageview.setImageResource(R.drawable.ic_menu_call);
				}
				
				TextView textView; 
				textView = (TextView)convertView.findViewById(R.id.Text_ID); 
				textView.setText(groupListData.get_ID()); 
				textView = (TextView)convertView.findViewById(R.id.TextNAME); 
				textView.setText(groupListData.getTitle());
//				textView = (TextView)convertView.findViewById(R.id.TextGroup); 
//				textView.setText(groupListData.getAccountName());
								
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