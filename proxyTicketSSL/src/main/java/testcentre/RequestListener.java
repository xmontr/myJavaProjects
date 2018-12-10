package testcentre;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.xml.ws.spi.http.HttpContext;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.impl.DefaultBHttpServerConnection;


import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpService;

public class RequestListener implements Runnable{
	
	private SSLServerSocket server ;
	private HttpService httpService;
	//private HttpParams paramserver;
	
	
	public RequestListener ( SSLServerSocket s, HttpService  h) {
		this.server=s ;
		this.httpService = h ; 
		
		
	}
	
	
	
	public void run() {
		 System.out.println("waiting reception ");
		
   
    	
    	BasicHttpContext context = new BasicHttpContext(null);
		
		
		
        SSLSocket c = null ;
		try {
			c = (SSLSocket) server.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        
        
        
        
        DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(1024);

			try {
				conn.bind(c);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
			
			//if (conn.isOpen() ) {
				httpService.handleRequest(conn, context);
           // }
			
			} catch (ConnectionClosedException ex) {
                System.out.println("Client closed connection");
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			finally{ try {
				conn.shutdown() ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		
			 System.out.println("fin reception ");
		

	}
	
	
}