package picapau.contacts.onehandlist.Data; 

	public class AccountListData 
	{ 
	        private int str_ID; 
	        private String strAccountName;
	        private String strAccountType;
	        private int    nDisplay;
	         
	        public void set_ID(int text) 
	        { 
	                str_ID = text; 
	        } 
	         
	        public int get_ID() 
	        { 
	        	return str_ID; 
	        } 
	         
	        public void setAccountName(String text) 
	        { 
	        	strAccountName = text; 
	        } 
	         
	        public String getAccountName() 
	        { 
	                return strAccountName; 
	        } 
	         
	        public void setAccountType(String text) 
	        { 
	        	strAccountType = text; 
	        } 
	         
	        public String getAccountType() 
	        { 
	                return strAccountType; 
	        } 
	         
	        public void setDisp(int val) 
	        { 
	        	nDisplay = val;
	        } 
	        
	        public int getDisp() 
	        { 
	                return nDisplay; 
	        } 
	         
	} 
