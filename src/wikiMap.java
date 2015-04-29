import java.util.ArrayList;

public class wikiMap {
    
    ArrayList<ArrayList<WikiItem>> mapList = new ArrayList<ArrayList<WikiItem>>();

    public wikiMap(){
            for(int i = 0; i <2000; i++){
                ArrayList<WikiItem> list = new ArrayList<WikiItem>();
                mapList.add(list);
            }
        }
        public void add(WikiItem w){
            mapList.get(w.WikiNR % mapList.size()).add(w);
        }
        public ArrayList<WikiItem> get(int i){
            return mapList.get(i % mapList.size());
        }
}