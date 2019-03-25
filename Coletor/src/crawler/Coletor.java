
package crawler;

import crawler.escalonadorCurtoPrazo.Escalonador;
import crawler.escalonadorCurtoPrazo.EscalonadorSimples;
import crawler.test.EscalonadorSimplesTeste;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;


public class Coletor {
    public static void main(String[] args) throws MalformedURLException, UnknownHostException
	{
                Escalonador e = new EscalonadorSimples();
                
                URLAddress url1 = new URLAddress("https://edition.cnn.com/",1);
		URLAddress url2 = new URLAddress("http://www.openhub.net/",1);
		URLAddress url3 = new URLAddress("http://uai.com.br/",1);
		URLAddress url4 = new URLAddress("http://www.msn.com/",1);
		URLAddress url5 = new URLAddress("http://www.clarin.com/",1);
		URLAddress url6 = new URLAddress("http://www.lanacion.com.ar/",1);
		e.adicionaNovaPagina(url1);
		e.adicionaNovaPagina(url2);
		e.adicionaNovaPagina(url3);
		e.adicionaNovaPagina(url4);
		e.adicionaNovaPagina(url5);
		e.adicionaNovaPagina(url6);    
                
                PageFetcher pf = new PageFetcher(e);
                pf.run();
//                pf.run();
//                pf.run();
//                pf.run();
//                pf.run();
//                pf.run();
	}
}
