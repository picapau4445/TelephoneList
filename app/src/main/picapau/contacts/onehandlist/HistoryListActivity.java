package picapau.contacts.onehandlist;

import java.util.ArrayList;
import java.util.List;

import picapau.contacts.onehandlist.Data.HistoryData;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryListActivity extends Activity {
	
	public int LR;
	public int Type;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historylistavtivity);
		
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
 		
		LoadHistoryData();
	}
	
	// グループ検索
	private void LoadHistoryData()
	{
        boolean Res1 = false; //SQLの判定
        int count = 0;//レコード数
        
        Cursor cursor; 			//SQLのクエリカーソル

        List<HistoryData> ListHistoryData = new ArrayList<HistoryData>();
        
        // 親インテントからGroupIDと検索文字を取得
        Type = 0;
		Intent intent = getIntent();
        if(intent != null)
        {
            Type = intent.getIntExtra("Type",0);
        }
        
    	String strCACHED_NUMBER_LABEL;
    	int intCACHED_NUMBER_TYPE;
    	int int_COUNT;
    	
        //コンタクトリストのクエリを作成する
        Uri uri;
        
        // 検索
		Uri uri1 = CallLog.Calls.CONTENT_URI;
		String[] projection1 = { CallLog.Calls.TYPE, 
				                 CallLog.Calls.CACHED_NAME,
				                 CallLog.Calls.NUMBER, 
				                 CallLog.Calls.DATE,
				                 CallLog.Calls.DURATION,
				                 CallLog.Calls.NEW,
				                 CallLog.Calls._ID,
				                 CallLog.Calls.CACHED_NUMBER_LABEL,
				                 CallLog.Calls.CACHED_NUMBER_TYPE,
//				                 CallLog.Calls._COUNT
		                        };
		String selection1;
		String[] selectionArgs1;
		String sortOrder1 = CallLog.Calls.DATE + " desc";
		
		if( Type != 0 )
		{
			selection1 = CallLog.Calls.TYPE + "=? ";
			String strType;
			strType = String.valueOf(Type);
			selectionArgs1 = new String[] {strType};
		}
		else
		{
			selection1 = null;
			selectionArgs1 = null;
		}
		
        uri = CallLog.Calls.CONTENT_URI;
        cursor = managedQuery( uri1,
        		               projection1,
        		               selection1,
        		               selectionArgs1,
        					   sortOrder1);
        
        Res1 = cursor.moveToFirst();
        while (Res1)
        {
        	count++;
        	
        	// データの格納
        	HistoryData historydata = new HistoryData();
        	historydata.setType(cursor.getInt(0));
        	if( cursor.getString(1) != null )
        	{
        		historydata.setDISPNAME(cursor.getString(1));//name
        	}
        	else
        	{
        		historydata.setDISPNAME(cursor.getString(2));//number
        	}
        	historydata.setNUMBER(cursor.getString(2));
        	historydata.setDate(cursor.getLong(3));
        	historydata.setDuration(cursor.getLong(4));
        	historydata.setNew(cursor.getInt(5));
        	historydata.set_ID(cursor.getInt(6));
        	
//       	strCACHED_NUMBER_LABEL = cursor.getString(7);
//        	intCACHED_NUMBER_TYPE = cursor.getInt(8);
//        	int_COUNT = cursor.getInt(9);
        	
        	ListHistoryData.add(historydata);
        	
        	if( count >= 30 )
        	{
        		break;
        	}
        	
        	Res1 = cursor.moveToNext();
        }
        
        
// android 4.0 での既知の問題に対応 明示的にclose() すると画面遷移時にクラッシュするらしい
//        cursor.close();
        

        // アダプタを介してデータ表示
        SetHistoryList(ListHistoryData);
        
        ContentValues values = new ContentValues();
        
        for (int i = 0; i < ListHistoryData.size(); i++)
        {
        	int res;
        	HistoryData historydata = ListHistoryData.get(i);
        	
        	// 未確認の不在着信であった場合
        	if( historydata.getType() == CallLog.Calls.MISSED_TYPE && historydata.getNew() == 1 )
        	{
        		// 不在着信を確認したよ、という扱い
        		String strID;
        		values.clear();
        		strID = String.valueOf(historydata.get_ID());
        		values.put(CallLog.Calls.NEW, "0");
        		res = getContentResolver().update(CallLog.Calls.CONTENT_URI, 
        				                    values,
        				                    CallLog.Calls._ID + "=? ", new String[]{strID});
        		
        		if(res==1)
        		{
        			
        		}
        	}
    	}
        
     }
	
	// グループ表示のときに使用されるアダプター群 -------------------------------------------------------
		private void SetHistoryList(List<HistoryData> list)
		{
	        ListView listview = (ListView)findViewById( R.id.HistoryListView );      
	        HistoryListAdapter adapter = new HistoryListAdapter(this, list);
	        listview.setItemsCanFocus(false);
	        listview.setAdapter(adapter);
		}
		
		public class HistoryListAdapter extends ArrayAdapter<HistoryData> 
		{ 
		        private LayoutInflater layoutInflater_;
	            
		        public HistoryListAdapter(Context context, List<HistoryData> list) 
		        {
		        	super(context, 0, list);
		        	layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		        }
		        
				@Override
		        public View getView(final int position, View convertView, ViewGroup parent) 
		        { 
					if ( convertView == null ) 
					{ 
						if( LR == 0 )
						{
							convertView = layoutInflater_.inflate(R.layout.rowhistory_left, null);
						}
						else
						{
							convertView = layoutInflater_.inflate(R.layout.rowhistory_right, null);
						}
					}
					                
					final HistoryData historydata  = this.getItem(position);
					
					// タイプ
					ImageView imageType;
					imageType = (ImageView)convertView.findViewById(R.id.image_Type);
					if( historydata.getType() == CallLog.Calls.OUTGOING_TYPE )
					{
						imageType.setImageResource(R.drawable.sym_call_outgoing);
					}
					else if( historydata.getType() == CallLog.Calls.INCOMING_TYPE )
					{
						imageType.setImageResource(R.drawable.sym_call_incoming);						
					}
					else if( historydata.getType() == CallLog.Calls.MISSED_TYPE )
					{
						imageType.setImageResource(R.drawable.sym_call_missed);						
					}
					else
					{
						imageType.setImageResource(R.drawable.screen_background_dark);												
					}
					
					// 名前
					TextView textName; 
					textName = (TextView)convertView.findViewById(R.id.TextNAME);
//					textName.setBackgroundDrawable(R.drawable.)
					textName.setText(historydata.getDISPNAME());
					textName.setOnClickListener(new View.OnClickListener()
			        {
						@Override
						public void onClick(View v)
						{
						}
					});				          				

					// 電話番号
					TextView textNumber; 
					textNumber = (TextView)convertView.findViewById(R.id.TextNUMBER); 
					// 電話帳に登録のない相手の電話
					if( ! historydata.getDISPNAME().equals(historydata.getNUMBER())) 
					{
						textNumber.setText(historydata.getNUMBER());
					}
					else
					{
						textNumber.setText("");
					}
					
					// 着信・発信の確認
					TextView textNew; 
					textNew = (TextView)convertView.findViewById(R.id.TextNew); 
					textNew.setText(String.valueOf(historydata.getNew()));

					// 日付
					TextView textDate; 
					textDate = (TextView)convertView.findViewById(R.id.TextDate); 
					textDate.setText(DateUtils.formatDateTime(parent.getContext(),
			                 historydata.getDate(),
			                 DateUtils.FORMAT_SHOW_TIME | 
			                 DateUtils.FORMAT_SHOW_DATE)// |   
//			                 DateUtils.FORMAT_SHOW_YEAR)
			                 );

					// 経過時間
					TextView textDuration; 
					textDuration = (TextView)convertView.findViewById(R.id.TextDuration);
					int hour;
					int minute;
					int second;
					minute = (int)historydata.getDuration() / 60;
					second = (int)historydata.getDuration() % 60;
					textDuration.setText(String.format("%d:%02d", minute,second));
//					hour = (int)historydata.getDuration() / 3600;
//					minute = (int)(historydata.getDuration() % 3600)/60;
//					second = (int)historydata.getDuration() % 60;
//					textDuration.setText(String.format("%02d:%02d:%02d", hour,minute,second));

					// 電話ボタン
					ImageView imageCall;
					imageCall = (ImageView)convertView.findViewById(R.id.image_call);
					imageCall.setOnClickListener(new View.OnClickListener()
			        {
						@Override
						public void onClick(View v)
						{
							String strNumber;
							strNumber = historydata.getNUMBER();
							OnCall(strNumber);
						}
					});				          				
		
					return convertView;
		        }
		}

		public void OnCall(String strNumber)
		{
			// デバッグ中
			if(android.os.Debug.isDebuggerConnected())
			{
				// log
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
		

}