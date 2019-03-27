package crawler;

import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;
import crawler.escalonadorCurtoPrazo.Escalonador;
import crawler.escalonadorCurtoPrazo.EscalonadorSimples;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.CommentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.Utils;

public class PageFetcher implements Runnable {

    private final EscalonadorSimples e;

    public PageFetcher(EscalonadorSimples escalonador) {
        e = escalonador;
    }

    public void encontraLinks(String html, String dom, int profundidade) {
        LinkedList<URLAddress> aux = new LinkedList<URLAddress>();
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(html);
        String inner = cleaner.getInnerHtml(node);

        node.traverse(new TagNodeVisitor() {
            @Override
            public boolean visit(TagNode tn, HtmlNode htmlNode) {
                if (htmlNode instanceof TagNode) {
                    TagNode tag = (TagNode) htmlNode;
                    String tagName = tag.getName();
                    if ("a".equals(tagName)) {
                        String href = tag.getAttributeByName("href");
                        //System.out.println(href);
                        int depthFilho = 0;
                        if (href != null) {
                            
                            if (href.startsWith("/")) {
                                href = dom + href;
                                depthFilho = profundidade + 1;
                                
                            }

                            if (!(href.startsWith("javascript:") || href.equals("") || href.equals(dom + "/") || href.startsWith("#"))) {
                                try {
                                    e.adicionaNovaPagina(new URLAddress(href, depthFilho));
                                } catch (MalformedURLException ex) {

                                }
                            }
                        }
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void run() {
        while (!e.finalizouColeta()) {
            System.out.println("Rodando");
            URLAddress url = e.getURL();
            Record record = e.getRecordAllowRobots(url);
            RobotExclusion re = new RobotExclusion();

            if (record == null) {
                String aux = "https://" + url.getDomain() + "/robots.txt";
                try {
                    record = re.get(new URL(aux), "JAEbot");
                } catch (MalformedURLException ex) {
                }
                e.putRecorded(url.getDomain(), record);
            }
            if (record != null) {
                if (record.allows(url.toString())) {
                    InputStream inputStream = null;
                    String html = null;
                    try {
                        inputStream = ColetorUtil.getUrlStream("JAEbot", new URL(url.toString()));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(PageFetcher.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PageFetcher.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        html = ColetorUtil.consumeStream(inputStream);
                    } catch (IOException ex) {
                        Logger.getLogger(PageFetcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    encontraLinks(html, url.getDomain(), url.getDepth());
                    e.exibe();

                }
            }
        }
    }
}
