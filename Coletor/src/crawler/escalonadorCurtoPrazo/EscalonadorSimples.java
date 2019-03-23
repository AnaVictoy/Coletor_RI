package crawler.escalonadorCurtoPrazo;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;

import crawler.Servidor;
import crawler.URLAddress;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EscalonadorSimples implements Escalonador {

    private LinkedHashMap<Servidor, LinkedList<URLAddress>> hashServer = new LinkedHashMap<>();
    private HashMap<Servidor, Record> robotsServidor = new HashMap<>();
    public static final int PROFUNDIDADE = 10;
    public static final int LIMITE_PAGINAS = 10;
    private HashSet<URLAddress> descobertos = new HashSet<>();
    private int paginas = 0;

    @Override
    public synchronized URLAddress getURL() {

        if (!finalizouColeta()) {
                        
            URLAddress url = null;
            Servidor s = null;
            HashSet<Servidor> excluir = new HashSet<>();
            //do while para esperar se o servidor permanecer null
            //return null se fila tiver vazios 
            //descobrir o servidor
            for(Servidor key : hashServer.keySet()){
                LinkedList<URLAddress> value = hashServer.get(key);

                if (!value.isEmpty()&&key.isAccessible()) {
                    s = key;
                    countFetchedPage();
                    break;
                }
            }
                if(s!=null){
                    LinkedList<URLAddress> value = hashServer.get(s);
                    url = value.removeFirst();
                    s.acessadoAgora(); 
                    //removo servidor se o obtido tiver vazio
                }
      

            try {
                wait(1000L);
            } catch (InterruptedException ex) {
                Logger.getLogger(EscalonadorSimples.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public boolean adicionaNovaPagina(URLAddress urlAdd) {
        LinkedList<URLAddress> aux = new LinkedList<URLAddress>();
        Servidor servidor = new Servidor(urlAdd.getDomain());
        if (descobertos.contains(urlAdd) || urlAdd.getDepth() >= PROFUNDIDADE) {
                return false;
            }
        if (hashServer.containsKey(servidor)) {
            aux = hashServer.get(servidor);

            if (descobertos.contains(urlAdd) || urlAdd.getDepth() >= PROFUNDIDADE) {
                return false;
            }

            descobertos.add(urlAdd);
            aux.add(urlAdd);
            return true;
        }

        aux.add(urlAdd);
        hashServer.put(servidor, aux);

        return true;
    }

    @Override
    public Record getRecordAllowRobots(URLAddress url) {
        Servidor servidor = new Servidor(url.getDomain());
        if (robotsServidor.containsKey(servidor)) {
            return robotsServidor.get(servidor);
        }

        return null;
    }

    @Override
    public void putRecorded(String domain, Record domainRec) {
        Servidor servidor = new Servidor(domain);
        if (!robotsServidor.containsKey(servidor)) {
            robotsServidor.put(servidor, domainRec);
        }
    }

    @Override
    public boolean finalizouColeta() {

        return (paginas > LIMITE_PAGINAS);
    }

    @Override
    public void countFetchedPage() {
        paginas++;

    }

}
