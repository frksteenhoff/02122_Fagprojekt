import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Index1 {

	WikiItem start, tmp;
	wikiMap wikiM = new wikiMap();
	int x = 0;

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

			// Create new WikiItem, if it does not already exist
			if(!ContainsAddString(wikiM.get(start.WikiNR), word, title)){
				tmp = new WikiItem(word, title, null);
				start.next = tmp;
				start = tmp;
				wikiM.add(start);
			}
		}
	}

	// Method checking whether string to be added already exists or not
	boolean ContainsAddString(ArrayList<WikiItem> list, String string, String currentTitle){

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

	public Index1(String filename) {
		long Starttime = System.nanoTime();
		String word, currentTitle = null;
		WikiItem current, tmp;

		try {
			BufferedReader bufferR = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
			Scanner input = new Scanner(bufferR);
			word = input.next().toLowerCase();
			if(x == 0 && !word.equals(null)){
				currentTitle = word;
				x = 1;
			}

			start = new WikiItem(word, currentTitle, null);
			current = start;
			while (input.hasNext()) {   // Read all words in input

				/* x oscillates between 0 and 1 indicating whether the word  
				  is a title or a word within the document of that title*/
				if(x == 0){
					word = input.nextLine();
					if(!word.isEmpty()){
						currentTitle = word.toLowerCase().replaceAll("[^a-z0-9 ]", "");
						x = 1;
					}
				}else if(x == 1){
					word = input.next();
					if(word.equals("---END.OF.DOCUMENT---")){
						x = 0;
					}
					word = word.toLowerCase().replaceAll("[^a-z0-9 ]", "");
				}
				//System.out.println(word);

				if(!word.equals("---END.OF.DOCUMENT---") && !ContainsAddString(wikiM.get(Math.abs(word.hashCode())), word, currentTitle)){
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
		// Counter for time spent on indexing 
		long endTime = System.nanoTime();
		System.out.println((endTime-Starttime)/1000000000);
	}

	public boolean search(String searchstr) {
		ArrayList<WikiItem> hash = wikiM.get(Math.abs(searchstr.hashCode()));
		for(int i = 0; i < hash.size(); i++){
			if(hash.get(i).str.equals(searchstr)){
				System.out.println("------------------------------------");
				System.out.println("You are searching for: " + searchstr);
				System.out.println("Search string \"" + searchstr + "\" found in: \n"
						+ hash.get(i).title);
				return true;
			}
		}
		System.out.println("------------------------------------");
		System.out.println("You are searching for: " + searchstr);
		System.out.println("Not found.");
		return false;
	}

	//Method handling the boolean searches with logical AND, NOT and OR
	public boolean boolSearch(String searchString){

		String[] parts = searchString.split(" ");

		//Limiting searches to either 1 search word or two separated by a logical operator
		if(parts.length == 1 && (!parts[0].startsWith("*") && !parts[0].endsWith("*"))){
			return search(parts[0]);

			// Finding all the words starting with the specified prefix.
			// Can be made with regular expressions!
		}else if(parts.length == 1 && parts[0].endsWith("*")){
			String prefix = parts[0].substring(0,parts[0].length()-1);
			ArrayList<String> documents = new ArrayList<>();
			WikiItem current = start;
			while(current != null) {
				if(current.str.startsWith(prefix)) {
					documents.addAll((wikiM.get(current.WikiNR)).get((wikiM.get(current.WikiNR)).indexOf(current)).title);
				}
				current = current.next;
			}
			if(documents.isEmpty()){
				System.out.println("------------------------------------");
				System.out.println("You are searching for words with the prefix: \"" + prefix + "\"");
				System.out.println("Not found.");
				return false;
			}
			else{
				Set<String> all = new HashSet<>();
				all.addAll(documents);
				documents.clear();
				documents.addAll(all);
				System.out.println("------------------------------------");
				System.out.println("You are searching for words with the prefix: \"" + prefix + "\"");
				System.out.println("Search prefix \"" + prefix + "\" found in: \n" + documents);
				return true;
			}

			// Finding all the words ending on the specified suffix.
			// Can be made with regular expressions!
		}else if(parts.length == 1 && parts[0].startsWith("*")){
			String suffix = parts[0].substring(1);
			ArrayList<String> documents = new ArrayList<>();
			WikiItem current = start;
			while(current != null) {
				if(current.str.endsWith(suffix)) {
					documents.addAll((wikiM.get(current.WikiNR)).get((wikiM.get(current.WikiNR)).indexOf(current)).title);
				}
				current = current.next;
			}
			if(documents.isEmpty()){
				System.out.println("------------------------------------");
				System.out.println("You are searching for words with the prefix: \"" + suffix + "\"");
				System.out.println("Not found.");
				return false;
			}
			else{
				Set<String> all = new HashSet<>();
				all.addAll(documents);
				documents.clear();
				documents.addAll(all);
				System.out.println("------------------------------------");
				System.out.println("You are searching for words with the prefix: \"" + suffix + "\"");
				System.out.println("Search prefix \"" + suffix + "\" found in: \n" + documents);
				return true;
			}

		}else if(parts.length < 3 || parts.length > 3){
			System.out.println("No full-text search allowed. \n Use or, and or not as separator in multiple word search.");

		}else if(parts.length == 3){			
			if(parts[1].equals("and")){
				ArrayList<String> part1 = arraySearch(parts[0]);
				ArrayList<String> part2 = arraySearch(parts[2]);

				if(!part1.isEmpty() && !part2.isEmpty()){
					ArrayList<String> union = new ArrayList<String>();

					for(String part : part1){
						if(part2.contains(part)){
							union.add(part);
						}
					}
					System.out.println("------------------------------------");
					System.out.println("You are searching for: " + parts[0] + " and " + parts[2]);
					System.out.println("Search strings are both found in: \n" + union);

				}else if(!part1.isEmpty() || !part2.isEmpty()){
					System.out.println("The search words weren't found in the same document.");
				}
			}else if(parts[1].equals("or")){
				ArrayList<String> part1 = arraySearch(parts[0]);
				ArrayList<String> part2 = arraySearch(parts[2]);

				for(String part : part2){
					if(!part1.contains(part)){
						part1.add(part);
					}
				}
				System.out.println("------------------------------------");
				System.out.println("You are searching for: " + parts[0] + " or " + parts[2]);
				System.out.println("Search strings are found in: \n" + part1);

			}else if(parts[1].equals("not")){
				ArrayList<String> part1 = arraySearch(parts[0]);
				ArrayList<String> part2 = arraySearch(parts[2]);
				ArrayList<String> exclude = new ArrayList<String>();

				for(String part : part1){
					if(!part2.contains(part)){
						exclude.add(part);
					}
				}
				System.out.println("------------------------------------");
				System.out.println("You are searching for: " + parts[0] + " not " + parts[2]);
				System.out.println("Search strings are found in: \n" + exclude);

			}else{
				System.out.println("Use or, and or not as separator in multiple word search.");
			}
		}	
		return false;
	}

	public ArrayList<String> arraySearch(String searchstr) {
		WikiItem current = start;
		while(current != null) {
			if(current.str.equals(searchstr.toLowerCase())) {
				return (wikiM.get(current.WikiNR)).get((wikiM.get(current.WikiNR)).indexOf(current)).title;
			}
			current = current.next;
		}
		ArrayList<String> nullList = new ArrayList<String>();
		return nullList;
	}

	public static void main(String[] args) {
		System.out.println("Preprocessing " + args[0]);

		Index1 i = new Index1(args[0]);
		GUI g = new GUI(i);
		Scanner console = new Scanner(System.in);
		for (;;) {
			System.out.println("Input search string or type exit to stop");
			String searchstr = console.nextLine();
		}
	}
}