package picapau.contacts.onehandlist.Data; 

	public class ContactListData 
	{ 
	        private String str_ID; 
	        private String str_LookupKey; 
	        private String strDISP_NAME; 
	        private String strNUMBER;
	        private String strADDRESS;
	        private String strKana;
	        
	         
	        public void set_ID(String text) 
	        { 
	                str_ID = text; 
	        } 
	         
	        public String get_ID() 
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
	         
	        public void setADDRESS1(String text) 
	        { 
	                strADDRESS = text; 
	        } 
	         
	        public String getADDRESS1() 
	        { 
	                return strADDRESS; 
	        } 
	         
	        public void setKana(String text) 
	        { 
	                strKana = text; 
	        } 
	         
	        public String getKana() 
	        { 
	                return strKana; 
	        } 
	         
	} 
