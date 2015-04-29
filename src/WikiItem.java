import java.util.ArrayList;

public class WikiItem {
    
    String str;
    ArrayList<String> title = new ArrayList<String>();
    WikiItem next;
    int WikiNR;
    
    public WikiItem(String s, String t, WikiItem n) {
        str = s;
        title.add(t);
        next = n;
        WikiNR = Math.abs(str.hashCode());
    }
}