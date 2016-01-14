package picapau.contacts.onehandlist;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

// ダイアル画面を表示させるためのダミーアクティビティーです
public class DialTabActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textview = new TextView(this);
		setContentView(textview);		
		}	
}