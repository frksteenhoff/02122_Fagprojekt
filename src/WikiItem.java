import java.util.ArrayList;

/*
 * Author(s): Anders Sperling, Henriette Steenhoff
 * Software technology, 4th semester F15, DTU  
 */

public class WikiItem {
    
    String str;
    WikiItem next;
    ArrayList<String> title = new ArrayList<String>();
    int WikiNR;
    
    public WikiItem(String s, String t, WikiItem n) {
        str = s;
        title.add(t);
        next = n;
        WikiNR = str.hashCode();
    }
}