package picapau.contacts.onehandlist.Data; 

	public class HistoryData 
	{ 
	        private int str_ID; 
	        private String str_LookupKey; 
	        private String strDISP_NAME; 
	        private String strNUMBER;
	        private int strStatus;
	        private long date;
	        private long Duration;
	        private int New;
	         
	        public void set_ID(int i) 
	        { 
	                str_ID = i; 
	        } 
	         
	        public int get_ID() 
	        { 
	        	return str_ID; 
	        } 
	         
	        public void set_key(String text) 
	        { 
	        	str_LookupKey = text; 
	        } 
	         
	        public String get_key() 
	        { 
	        	return str_LookupKey; 
	        } 
	         
	        public void setDISPNAME(String text) 
	        { 
	        	strDISP_NAME = text; 
	        } 
	         
	        public String getDISPNAME() 
	        { 
	                return strDISP_NAME; 
	        } 
	         
	        public void setNUMBER(String text) 
	        { 
	                strNUMBER = text; 
	        } 
	         
	        public String getNUMBER() 
	        { 
	                return strNUMBER; 
	        } 
	         
	        public void setType(int text) 
	        { 
	                strStatus = text; 
	        } 
	         
	        public int getType() 
	        { 
	                return strStatus; 
	        } 
	         
			public void setDate(long long1) {
				// TODO 自動生成されたメソッド・スタブ
				date = long1;
			}

	        public long getDate() 
	        { 
	                return date; 
	        } 
	         
			public void setDuration(long long1) {
				// TODO 自動生成されたメソッド・スタブ
				Duration = long1;
			}

	        public long getDuration() 
	        { 
	                return Duration; 
	        } 
	         
			public void setNew(int long1) {
				// TODO 自動生成されたメソッド・スタブ
				New = long1;
			} 
	        public int getNew() 
	        { 
	                return New; 
	        } 
	         
	         
	} 
