package crawler;

import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;
import crawler.escalonadorCurtoPrazo.Escalonador;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class PageFetcher implements Runnable{
    private final Escalonador e;
    
    public PageFetcher(Escalonador escalonador) {
        e = escalonador;
    }
    /*node.traverse(new TagNodeVisitor() {
    public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            String tagName = tag.getName();
            if ("img".equals(tagName)) {
                String src = tag.getAttributeByName("src");
                if (src != null) {
                    tag.setAttribute("src", Utils.fullUrl(siteUrl, src));
                }
            }
        } else if (htmlNode instanceof CommentNode) {
            CommentNode comment = ((CommentNode) htmlNode); 
            comment.getContent().append(" -- By HtmlCleaner");
        }
        // tells visitor to continue traversing the DOM tree
        return true;
    }
});*/

    public void encontraLinks(String html) {
        LinkedList<URLAddress> aux = new LinkedList<URLAddress>();
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(html);
        String inner=cleaner.getInnerHtml(node);
        System.out.println(inner);
    }
    
    @Override
    public void run() {
        URLAddress url = e.getURL();
        Record record = e.getRecordAllowRobots(url);
        RobotExclusion re = new RobotExclusion();
        
        if (record == null) {
            String aux = "https://"+url.getDomain()+"/roborts.txt";
            try {
                record=re.get(new URL(aux),"JAEbot");
            } catch (MalformedURLException ex) {
                Logger.getLogger(PageFetcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            e.putRecorded(url.getDomain(), record);
        }
        
        if(record.allows(url.toString())) {
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
            
            //System.out.println(html);
            encontraLinks(html);
            
            
        }
    }    
}
