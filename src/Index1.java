import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Index1 implements KeyListener{

	WikiItem start, tmp;
	wikiMap wikiM = new wikiMap();
	GUI g = null;

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
			WikiNR = str.length()-1;
		}
	}

	public class wikiMap{
		ArrayList<ArrayList<WikiItem>> mapList = new ArrayList<ArrayList<WikiItem>>();
        
		wikiMap(){

			for(int i = 0; i <25; i++){
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
			//str = str.replace(word, "");
			
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
					list.get(i).title += "\n" + currentTitle;
					return true;
				}else{
					return false;
				}
			}
		return false;
	}

	public Index1(String filename) {
		long Start = System.nanoTime();


		/*String word, currentTitle = null;
	        WikiItem current, tmp;
            long Start = System.nanoTime();
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

	                if(!ContainsAddString(wikiM.get(current.WikiNR), word, currentTitle)){
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
	        
		for(int i = 0; i < test.size(); i++){
			this.sectionIndexing(test.get(i));
		}*/
        
		ArrayList<String> test = this.sectionPreprocessing(filename);
        
		for(int i = 0; i < test.size(); i++){
			this.sectionIndexing(test.get(i));
		}
        
		long endTime = System.nanoTime();
		System.out.println((endTime-Start)/1000000000);
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
	            	}
				current = current.next;

				if(current.next == null){
					return true;
				}
				return true;*/
			}
			current = current.next;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println("Preprocessing " + args[0]);
		Index1 i = new Index1(args[0]);
        
		g = new GUI(i);
		Scanner console = new Scanner(System.in);
        System.out.println("Input search string or type exit to stop");
        String searchstr = console.next();
		for (;;) {
            
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
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			//System.out.println(editText.getText());
			this.search(g.editText.getText().toLowerCase());
			g.editText.setText(null);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			g.editText.setText(null);
		}
	}
}