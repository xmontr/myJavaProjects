package testcentre;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

public class MyRequestHandler implements HttpRequestHandler {
	
	
	private EcasProxyTicketSSLServer ecasproxyticketsslserver;
	

	
	public MyRequestHandler(EcasProxyTicketSSLServer ecasProxyTicketSSLServer) {
		
		
		this.ecasproxyticketsslserver = ecasProxyTicketSSLServer;
		
		
	}

	public void handle(HttpRequest req, HttpResponse resp, HttpContext context)
			throws HttpException, IOException {
		
		System.out.println(" /************* handling request on ssl server ***************************/");
		System.out.println("request line :"+  req.getRequestLine()  );
		Header[] h = req.getAllHeaders();
		for (int i = 0; i < h.length; i++) {
		//	System.out.println("header :"+  h[i].getName() + " - " + h[i].getValue()  );
		}
		
		String   content = null;
		Pattern pattern;
           if (req instanceof BasicHttpRequest) {
        	   
        	   BasicHttpRequest basereq = (BasicHttpRequest )req;
        	   content  = basereq.getRequestLine().getUri();

            }
		
           if (req instanceof BasicHttpEntityEnclosingRequest) {
		
		BasicHttpEntityEnclosingRequest r = (BasicHttpEntityEnclosingRequest)req;
		   content = EntityUtils.toString(r.getEntity());
		   
           }
		// catch pgtid;
		// pgtIou=PGTIOU-14-Fkxou4AbPKmzbzS5ADHA4bmwFzXpDghnxksyzhLvZr9g3zJNGpm3o2OHh6UV4zKo12YES-fmzR0Vu2zN5GrpFC1MRiTh-UHPHyzzRVzuitrOsili5w40&pgtId=PGT-14-uRPQcsoUcmszuLSzswqRP8tb7vBXzzHIM567E2tHwmEpH2eWruml0GRxDzkBgFadzzjqbj79tEPdukT36TI5dBPa-fmzR0Vu2zN5GrpFC1MRiTh-F7UvQamfRgcmXZ67Uo2jfm&ticket=ST-1234-wkbq70GDnFzzykqRIyzs2GxThGTXWyp1ncKDnUFpfdg4-fmzR0Vu2zN5GrpFC1MRiTh-i94a97izeAPDCrNQBSlvFm
		
           if( content.startsWith("logoutRequest")){ // manage logout request
        	   
        	   ecasproxyticketsslserver.setHaslogout(true);
        	  System.out.println("--------------  received logout ------------------------------");
           }
           else {
			if( /*needsLogout*/ false )
	     pattern= Pattern.compile("pgtIou=(.*)&pgtId=(.*)&ticket=(.*)");
			else
		 pattern= Pattern.compile("pgtIou=(.*)&pgtId=(.*)");		
			
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
        	String tmp =  matcher.group(2) ;
        	 System.out.println("found pgtid " + tmp );
        	 ecasproxyticketsslserver.setPgtId(tmp);
        	
        }
           }
		
		
      
		HttpEntity returnmessage = new StringEntity("<proxySuccess xmlns=\"http://www.yale.edu/tp/casClient\"/>",Consts.UTF_8);
		
		resp.setStatusCode(HttpStatus.SC_OK);
		resp.setEntity(returnmessage);
		
		
		
		
		
		
		

			
		
	}

}
