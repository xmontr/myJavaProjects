package proxyTicketSSL;

import static org.junit.Assert.*;

import org.junit.Test;

import testcentre.EcasProxyTicketSSLServer;

public class testServer {

	@Test
	public void test() {
		
		
		String  host="158.167.25.24";
		int  port =  9443;
		
			EcasProxyTicketSSLServer pserver = new EcasProxyTicketSSLServer(host, port);
			
			
			try {
				
				pserver.init();
			
			
			
			pserver.printServerSocketInfo();
			
			pserver.handleOneRequest();
			
			
			assertNotNull("no pgtid", pserver.getPgtId());
			
			
			
		pserver.end();	
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("failure with ssl server");
			
		}
		
		
		
	}

}
