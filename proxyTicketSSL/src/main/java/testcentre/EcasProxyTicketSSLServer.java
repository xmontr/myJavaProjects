package testcentre;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.config.SocketConfig.Builder;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;

import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerMapper;

import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.apache.http.protocol.HttpService;

public class EcasProxyTicketSSLServer {
	
	
	/**
	 * 
	 *  host that run the SSL server to receive the proxy granting ticket id
	 * 
	 */
	
	private String host; 
	
	
	/***
	 * 
	 * 
	 *  port of the SSL server
	 * 
	 */
	private int port ;
	
	
	
	public String getPgtId() {
		return pgtId;
	}




	public void setPgtId(String pgtId) {
		this.pgtId = pgtId;
	}




	private String pgtId;

	private  HttpService httpService; 
	
	private SSLServerSocket serverSocket;
	
	/***
	 * 
	 *  if ssl server receive a logout request
	 * 
	 * 
	 */
	private boolean haslogout;
	
	
	public void setHaslogout(boolean haslogout) {
		this.haslogout = haslogout;
	}
	
	
	
	public EcasProxyTicketSSLServer(String host,int port)  {
		
		this.host=host;
		this.port=port;
		
		

	}
	
	/***
	 * 
	 *   start the SSL server at the defined host:port
	 * 
	 * 
	 * @throws Exception
	 */
	
	
	public void init() throws Exception {
		
		try {
			serverSocket = createSSLserver(port);
		} catch (Exception e) {
			throw e ;
		} 
			
		
		
		httpService = createHtpService(this);	
		
		
		
		
		
	}
	
	
	
	
	public void end () throws IOException {
		
		
		serverSocket.close();
		
		
	}
	
	
	
	
	private SSLServerSocket createSSLserver(int port ) throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, URISyntaxException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
		
		
		
		
		
	
		SSLContext ctx = SSLContext.getInstance("TLS");
		SSLContext sslc = SSLContext.getInstance("SSLv3");
		  KeyStore keystore = KeyStore.getInstance("JKS");	
		  
		   KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		   TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		  
		  
		  // set the keystore	   
	 InputStream keystorein = EcasProxyTicketSSLServer.class.getClassLoader().getResourceAsStream("158.167.109.85.jks");
		  
		  keystore.load( keystorein, "changeit".toCharArray());			  



	        // Initialize the KeyManagerFactory to work with our keystore
			 kmf.init(keystore, "changeit".toCharArray());     
    
	        
		  
		  //set the trustore
	  	  InputStream truststorein = EcasProxyTicketSSLServer.class.getClassLoader().getResourceAsStream("d02di1019623dit.net1.cec.eu.int.jks");	
	  	 KeyStore truststore = KeyStore.getInstance("JKS");
	  	truststore.load(truststorein, "changeit".toCharArray());	
		// Initialize the trustManagerFactory to work with our truststore
	  tmf.init(truststore); 
	  	
	  	
	
		ctx.init(kmf.getKeyManagers() , tmf.getTrustManagers(), null);
		  
		  ServerSocketFactory ssf  = ctx.getServerSocketFactory();
		  serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
		
		  
		
	return 	serverSocket;
	}
	
	
	
	private  HttpService createHtpService(EcasProxyTicketSSLServer ecasProxyTicketSSLServer) {		
		
		/* creation service reception http */
		HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] {
                new ResponseDate(),
                new ResponseServer(),
                new ResponseContent(),
                new ResponseConnControl()
        });

		
		UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
		
		//HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();		
		
        reqistry.register("*", new MyRequestHandler(ecasProxyTicketSSLServer));
        
        
        
      Builder socketbuilder = SocketConfig.custom();
      socketbuilder.setSoTimeout(10000).setRcvBufSize(8*1024);
     SocketConfig socketConfig = socketbuilder.build();
     
 
        
		
		
  /*   	SyncBasicHttpParams paramserver = new SyncBasicHttpParams();
    	paramserver
        .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 10000)
        .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
        .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
        .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
        .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");*/
        
        
		
		HttpService service = new HttpService(
				httpproc,
                new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory(),
                reqistry);
		
		return service;
		
	}
	
	
	
	
	public void handleOneRequest() {
		
		
		if( this.serverSocket == null || this.httpService == null) {
			throw new IllegalStateException("ecasproxyticketserver not initialized");
			
		}
		
		
		// start ssl request listener
		RequestListener rql = new RequestListener(this.serverSocket, this.httpService);
		Thread set_thread = new Thread(rql);
		 System.out.println("starting listening thread " );
		set_thread.start();
		
		try {
			Thread.currentThread().sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("fin handleOneRequest " );
		
		
		
	}
	
	

	
	  public  void printServerSocketInfo() {
		
		SSLServerSocket s = this.serverSocket;
	      System.out.println("Server socket class: "+s.getClass()) ;
	      System.out.println("   Socker address = "
	         +s.getInetAddress().toString());
	      System.out.println("   Socker port = "
	         +s.getLocalPort());
	      System.out.println("   Need client authentication = "
	         +s.getNeedClientAuth());
	      System.out.println("   Want client authentication = "
	         +s.getWantClientAuth());
	      System.out.println("   Use client mode = "
	         +s.getUseClientMode());
	   }
	
	
	

}
