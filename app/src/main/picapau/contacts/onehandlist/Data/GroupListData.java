package picapau.contacts.onehandlist.Data; 

	public class GroupListData 
	{ 
	        private String str_ID; 
	        private String strTitle; 
	        private String strAccountName;
	        private String strAccountType;
	        private String strKana;
	        private int    nOrder;
	        private int    nDefault;
	        
	         
	        public void set_ID(String text) 
	        { 
	                str_ID = text; 
	        } 
	         
	        public String get_ID() 
	        { 
	        	return str_ID; 
	        } 
	         
	        public void setTitle(String text) 
	        { 
	        	strTitle = text; 
	        } 
	         
	        public String getTitle() 
	        { 
	                return strTitle; 
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
	         
	        public void setKana(String text) 
	        { 
	                strKana = text; 
	        } 
	         
	        public String getKana() 
	        { 
	                return strKana; 
	        } 
	         
	        public void setOrder(int order) 
	        { 
	                nOrder = order; 
	        } 
	        
	        // これは実質使われない関数ですが
	        public int getOrder() 
	        { 
	                return nOrder; 
	        } 
	         
	        public void setDefault(int order) 
	        { 
	                nDefault = order; 
	        } 
	        
	        // これは実質使われない関数ですが
	        public int getDefault() 
	        { 
	                return nDefault; 
	        } 
	         
	} 
