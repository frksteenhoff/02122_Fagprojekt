import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Author(s): Anders Sperling, Henriette Steenhoff
 * Software technology, 4th semester F15, DTU
 * Modification of the Java-file Index2. The program now uses linked lists to reference between data.  
 */

public class Index3 {	 

	WikiItem start;
	titleList title;
	boolean docTitle = true;

	private class WikiItem {
		String str;
		WikiItem next;
		titleList title;

		WikiItem(String s, titleList t, WikiItem n) {
			str = s;
			title = t;
			next = n;
		}
	}

	private class titleList {
		String docTitle;
		titleList next;

		titleList(String d, titleList n){
			docTitle = d;
			next = n;
		}
	}

	public Index3(String filename) {
		long Starttime = System.nanoTime();
		String word, currentTitle = null;
		title = new titleList(currentTitle, null);
		titleList cur, temp;
		WikiItem current, tmp;
		try {
			Scanner input = new Scanner(new File(filename), "UTF-8");
			word = input.next();
			if(docTitle && !word.equals(null)){
				currentTitle = word.replaceAll("[^A-Za-z0-9 ]", "");
				docTitle = false;
			}

			start = new WikiItem(word.toLowerCase().replaceAll("[^a-z0-9 ]", ""), title, null);
			current = start;
			cur = title;
			while(input.hasNext()) {   // Read all words in input

				/* docTitle oscillates between true and false indicating whether the word  
				  is a title or a word within the document of that title*/
				if(docTitle){
					word = input.nextLine();
					if(!word.isEmpty()){
						//Removes everything but alphanumeric characters
						currentTitle = word;
						docTitle = false;
					}

				}else if(!docTitle){
					word = input.next();
					if(word.equals("---END.OF.DOCUMENT---")){
						docTitle = true;
					}
					temp = new titleList(currentTitle, null);
					tmp = new WikiItem(word.toLowerCase().replaceAll("[^a-z0-9 ]", ""), temp, null);
					current.next = tmp;
					current.next.title.next = temp;
					cur = temp;
					current = tmp;
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

	public ArrayList<String> search(String searchstr) {
		ArrayList<String> documents = new ArrayList<>();
		WikiItem current = start;
		while (current != null) {
			if (current.str.equals(searchstr)) {
				while(current.title.docTitle != null && !documents.contains(current.title.docTitle)){
					documents.add(current.title.docTitle);
				}
			}
			current = current.next;
		}
		return documents;
	}

	public static void main(String[] args) {
		System.out.println("Preprocessing " + args[0]);
		Index3 i = new Index3(args[0]);
		Scanner console = new Scanner(System.in); 
		for (;;) {
			System.out.println("Input search string or type exit to stop");
			String searchstr = console.nextLine();
			if (searchstr.equals("exit")) {
				break;
			}
			if (!i.search(searchstr).isEmpty()) {
				ArrayList<String> titles = i.search(searchstr);
				System.out.println("-----------------------------------------");
				System.out.println("You are searching for: " + searchstr);
				System.out.println("Search word exists in following documents:\n" + titles);
			} else {
				System.out.println(searchstr + " does not exist");
			}
		}
		console.close();
	}
}
