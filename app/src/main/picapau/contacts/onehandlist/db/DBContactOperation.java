package picapau.contacts.onehandlist.db;

import java.util.HashMap;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBContactOperation {

	private DBOpenHelper		m_helper;

	private SQLiteDatabase	m_db;
	private static final String TBL_NAME = "GroupOrder";	

	
	public DBContactOperation( Context context )
	{
		m_helper = DBOpenHelper.getInstance( context );
		if( m_helper != null )
			m_db = m_helper.getWritableDatabase();
		else
			m_db = null;
	}
	

	public void close() {
		m_db.close();
	}

	public void add( int id, int value )
	{
		ContentValues val = new ContentValues();

		val.put("_id", id );
		val.put( "gorder", value );
		m_db.insert( TBL_NAME, null, val );

	}

	public void update( int id, int value )
	{
		ContentValues val = new ContentValues();

		val.put( "gorder", value );
		m_db.update( TBL_NAME, val, "_id=?", new String[] { Integer.toString( id ) });
	}
	
	public HashMap<Integer, Integer> Load()
	{
		Cursor					c;
		HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();

        
		if( m_db == null )
        	return null;

		boolean res;
		
        c = m_db.query( TBL_NAME,
						new String[] { "_id", "gorder" },
						null, null, null, null, null );
		
		res = c.moveToFirst();

		while(res)
		{
			hash.put(c.getInt(0), c.getInt(1));
			res = c.moveToNext();
 		}
		c.close();

		return hash;
	}
}
