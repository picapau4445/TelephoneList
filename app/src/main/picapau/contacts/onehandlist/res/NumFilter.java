package picapau.contacts.onehandlist.res;
import android.text.InputFilter;
import android.text.Spanned;

public class NumFilter implements InputFilter
{  
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
	{  
		if( source.toString().matches("^[0-9]+$") )
		{
			return source;  
		}else
		{
			return "";
		}  
	}
}