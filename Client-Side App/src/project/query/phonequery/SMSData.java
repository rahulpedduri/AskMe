
package project.query.phonequery;
class SMSData{
		String body;
		String number;
		String name;
		
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}	
		
		public void setName(String name)
		{
			this.name=name;
		}
		
		public String getdatainStringFormat()
		{
			String data="";
			if(this.name!=null)
			data="From: "+this.name+" --> Message is: "+this.body;
			else
			data="From: "+this.number+" --> Message is: "+this.body;	
			return data; 
		}
	}