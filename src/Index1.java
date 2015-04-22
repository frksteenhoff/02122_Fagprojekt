import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Index1 {

	WikiItem start, tmp;
	wikiMap wikiM = new wikiMap();
    int x = 0;

	private class WikiItem {
		String str;
		String title;
		WikiItem next;
		int WikiNR;
		//String filename = "WestburyLab.wikicorp.201004_100KB.txt";
		WikiItem(String s, String t, WikiItem n) {
			str = s;
			title = t;
			next = n;
			WikiNR = Math.abs(str.hashCode());
		}
	}

	public class wikiMap{
		ArrayList<ArrayList<WikiItem>> mapList = new ArrayList<ArrayList<WikiItem>>();
        
		wikiMap(){

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

	public ArrayList<String> sectionPreprocessing(String str){
		String section, line = null;
		ArrayList<String> sections = new ArrayList<String>();
		try {
			Scanner input = new Scanner(new File(str), "UTF-8");
			section = input.nextLine();
			while(input.hasNext()){
				line = input.nextLine();
				if(line.contains("---END.OF.DOCUMENT---")){
					section += line;
					sections.add(section);
					section = null;
				}
				section += line;
			}
			input.close();
			return sections;

		} catch (FileNotFoundException e) {
			System.out.println("Error reading file " + str);
		}
		return null;
	}

	public void sectionIndexing(String str){
		Scanner input = new Scanner(str);
		String title = input.next();
		String word = title;
		start = new WikiItem(word, title, null);

		while (input.hasNext()) {   // Read all words in section
			word = input.next();
			
			if(!ContainsAddString(wikiM.get(start.WikiNR), word, title)){
				tmp = new WikiItem(word, title, null);
				start.next = tmp;
				start = tmp;
				wikiM.add(start);
			}
		}
	}

	boolean ContainsAddString(ArrayList<WikiItem> list, String string, String currentTitle){
        
        for(int i = 0; i < list.size(); i++){
			if(list.get(i).str.equals(string)){
                if(!list.get(i).title.contains(currentTitle)){
					list.get(i).title += "\n" + currentTitle;
                }
                    return true;
				}else{
					return false;
				}
			}
		return false;
	}

	public Index1(String filename) {
		long Starttime = System.nanoTime();


		String word, currentTitle = null;
	        WikiItem current, tmp;
	        try {
	            Scanner input = new Scanner(new File(filename), "UTF-8");
	            word = input.next().toLowerCase();
	            if(x == 0 && !word.equals(null)){
	            	currentTitle = word;
	            	x = 1;
	            }

	            start = new WikiItem(word, currentTitle, null);
	            current = start;
	            while (input.hasNext()) {   // Read all words in input
	                word = input.next();
	                if(x == 0 && !word.equals(null)){
		            	currentTitle = word;
		            	x = 1;
		            }else if(x == 1 && word.equals("---END.OF.DOCUMENT---")){
		            	x = 0;
		            }
	                //System.out.println(word);

	                if(!ContainsAddString(wikiM.get(Math.abs(word.hashCode())), word, currentTitle)){
	                	tmp = new WikiItem(word, currentTitle, null);
	                	current.next = tmp;
	                	current = tmp;
	                	wikiM.add(current);
	                }
	            }
	            input.close();
	        } catch (FileNotFoundException e) {
	            System.out.println("Error reading file " + filename);
	        }
        
		/*ArrayList<String> test = this.sectionPreprocessing(filename);
        
		for(int i = 0; i < test.size(); i++){
			this.sectionIndexing(test.get(i));
		}*/
        
		long endTime = System.nanoTime();
		System.out.println((endTime-Starttime)/1000000000);
	}

	public boolean search(String searchstr) {
		WikiItem current = start;
		while(current != null) {
			if(current.str.equals(searchstr)) {
				System.out.println("------------------------------------");
				System.out.println("You are searching for:              " + searchstr);
				//System.out.println("Subsequent word is:                 " + current.next.str);
				System.out.println("Search string \"" + searchstr + "\" found in: \n" + (wikiM.get(current.WikiNR)).get((wikiM.get(current.WikiNR)).indexOf(current)).title);

				/*while(!current.next.str.equals("---END.OF.DOCUMENT-----")){
	            		current = current.next;
	            	}*/
				current = current.next;

				if(current.next == null){
					return true;
				}
				return true;
			}
			current = current.next;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println("Preprocessing " + args[0]);
        
		Index1 i = new Index1(args[0]);
		GUI g = new GUI(i);
		Scanner console = new Scanner(System.in);
		for (;;) {
            System.out.println("Input search string or type exit to stop");
            String searchstr = console.nextLine();
	            if (searchstr.equals("exit")) {
	                break;
	            }
	            if (i.search(searchstr)) {
	                System.out.println(searchstr + " exists");
	            } else {
	                System.out.println(searchstr + " does not exist");
	            }
	        }
	}
}