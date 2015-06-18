import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Author(s): Anders Sperling, Henriette Steenhoff
 * Software technology, 4th semester F15, DTU
 * Using hash table in stead of linked list to search through wikiItems.
 */

public class Index4 {	 

	WikiItem start, tmp;
	wikiMap wikiM = new wikiMap();
	boolean docTitle = true;

	private class WikiItem {
		String str;
		WikiItem next;
		ArrayList<String> title = new ArrayList<String>();
		int WikiNR;

		WikiItem(String s, String t, WikiItem n) {
			str = s;
			title.add(t);
			next = n;
			WikiNR = Math.abs(str.hashCode());
		}
	}

	public class wikiMap {
	    
	    ArrayList<ArrayList<WikiItem>> mapList = new ArrayList<ArrayList<WikiItem>>();

	    public wikiMap(){
	            for(int i = 0; i < 8000; i++){
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
	
	public Index4(String filename) {
		long Starttime = System.nanoTime();
		String word, currentTitle = null;
		WikiItem current, tmp;
		try {
			Scanner input = new Scanner(new File(filename), "UTF-8");
			word = input.next();
			if(docTitle && !word.equals(null)){
				currentTitle = word.toLowerCase().replaceAll("[^a-z0-9 ]", "");
				docTitle = false;
			}

			start = new WikiItem(word.toLowerCase().replaceAll("[^a-z0-9 ]", ""), currentTitle, null);
			current = start;
			while(input.hasNext()) {   // Read all words in input

				/* docTitle oscillates between true and false indicating whether the word  
				  is a title or a word within the document of that title*/
				if(docTitle){
					word = input.nextLine();

					if(!word.isEmpty()){
						currentTitle = word.replaceAll("[^A-Za-z0-9 ]", "");
						docTitle = false;
					}

				}else if(!docTitle){
					word = input.next();
					if(word.equals("---END.OF.DOCUMENT---")){
						docTitle = true;
					}
		
					if(!word.equals("---END.OF.DOCUMENT---") && 
							!stringTitleDuplicate(wikiM.get(Math.abs(word.hashCode())), word, currentTitle)){
					tmp = new WikiItem(word.toLowerCase().replaceAll("[^a-z0-9 ]", ""), currentTitle, null);
					current.next = tmp;
					current = tmp;
					wikiM.add(current);
				}
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error reading file " + filename);
		}
		// Counter for processing time
		long endTime = System.nanoTime();
		System.out.println("Time spent on indexing: " + (endTime-Starttime)/1000000000 + " sec. \n");
	}
	// Method checking whether string to be added already exists or not
	boolean stringTitleDuplicate(ArrayList<WikiItem> list, String string, String currentTitle){

		for(int i = 0; i < list.size(); i++){
			if(list.get(i).str.equals(string)){
				if(!list.get(i).title.contains(currentTitle)){
					list.get(i).title.add(currentTitle);
					return true;
				}
				return true;
			}
		}
		return false;
	}

	public boolean search(String searchstr) {
		ArrayList<WikiItem> hash = wikiM.get(Math.abs(searchstr.hashCode()));
		for(int i = 0; i < hash.size(); i++){
			if(hash.get(i).str.equals(searchstr)){
				System.out.println("-----------------------------------------");
				System.out.println("You are searching for: " + searchstr);
				System.out.println("Search string \"" + searchstr + "\" found in: \n"
						+ hash.get(i).title);
				return true;
			}
		}
		System.out.println("Not found.");
		return false;
	}

	public static void main(String[] args) {
		System.out.println("Preprocessing " + args[0]);
		Index4 i = new Index4(args[0]);
		Scanner console = new Scanner(System.in); 
		for (;;) {
			System.out.println("Input search string or type exit to stop");
			String searchstr = console.nextLine();
			if (searchstr.equals("exit")) {
				break;
			}
			else if (i.search(searchstr)) {
			}
		}
		console.close();
	}
}
