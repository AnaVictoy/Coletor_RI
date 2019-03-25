package crawler;

import crawler.test.EscalonadorSimplesTeste;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class URLAddress {
	private URL address;
	private int depth;
	public URLAddress(String url,int depth) throws MalformedURLException
	{
		
		this.address =  new URL(formatURL(url));
		this.depth = depth;
	}
	public static String formatURL(String url)
	{
		if(!url.matches("[a-zA-Z]+://.*"))
		{
			url = "http://"+url;
		}
		
		return url;
	}
	public static String getDomain(String address) throws MalformedURLException
	{
		return new URL(formatURL(address)).getHost();
	}
	public String getProtocol()
	{
		return this.address.getProtocol();
	}
	public String getDomain()
	{
	
		return address.getHost();
	}
	public String getAddress() {
		return address.getProtocol()+"://"+address.getHost()+address.getFile();
	}
	public int getDepth() {
		return depth;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return address.getPath().length()==0?"/":"";
	}
	
	
	public String toString()
	{
		return address.toString();
	}
}
