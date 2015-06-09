import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Index1 {

	WikiItem start, tmp;
	wikiMap wikiM = new wikiMap();
	int x = 0;
	boolean check = true;

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
			if(!stringTitleDuplicate(wikiM.get(start.WikiNR), word, title)){
				tmp = new WikiItem(word, title, null);
				start.next = tmp;
				start = tmp;
				wikiM.add(start);
			}
		}
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
						//Removes everything but alphanumeric characters
						currentTitle = word.toLowerCase().replaceAll("[^a-z0-9 ]", "");
						x = 1;
					}
				}else if(x == 1){
					word = input.next();
					if(word.equals("---END.OF.DOCUMENT---")){
						x = 0;
					}
					//Removes everything but alphanumeric characters
					word = word.toLowerCase().replaceAll("[^a-z0-9 ]", "");
				}

				if(!word.equals("---END.OF.DOCUMENT---") && !stringTitleDuplicate(wikiM.get(Math.abs(word.hashCode())), word, currentTitle)){
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
		System.out.println("Time spent on indexing: " + (endTime-Starttime)/1000000000 + " sec.");
	}

	//Overall search logic
	public boolean searchLogic(String searchString){

		String[] parts = searchString.split(" ");

		//Limiting searches to either 1 search word or two separated by a logical operator
		if(parts.length == 1 && (!parts[0].startsWith("*") && !parts[0].endsWith("*"))){
			return search(parts[0]);

		}else if(parts.length == 1 && parts[0].endsWith("*")){
			prefixSearch(parts);

		}else if(parts.length == 1 && parts[0].startsWith("*")){
			suffixSearch(parts);

		}else if(parts.length > 3 && parts.length % 2 == 1){		
			
			recursiveBool(parts);

		}else if(parts.length == 3){		
			boolSearch(parts[0], parts[1], Arrays.copyOfRange(parts, 2, parts.length));

		}else if(parts.length < 3){
			System.out.println("No full-text search allowed. \n Use OR, AND or NOT as separator in multiple word search.");

		}else{
			System.out.println("Use or, and or not as separator in multiple word search.");
		}	
		return false;
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

	private String[] recursiveBool(String[] parts){
		
		String [] found =  new String[];

		if(parts.length > 3){
			// Split search
			String[] first = {parts[0],parts[1]}; 
			String[] rest = Arrays.copyOfRange(parts, 2, parts.length);
			boolSearch(first[0], first[1], recursiveBool(rest));
		}

		else if(parts.length == 3){
			// Boolean search on search words
			boolSearch(parts[0], parts[1], arraySearch(parts[2]).toArray(new String[arraySearch(parts[2]).size()]));
		}
		return null;
	}

	private ArrayList<String> boolSearch(String word, String key, String[] parts) {

		ArrayList<String> found = new ArrayList<>();

		if(key.equals("and")){
			found = booleanAND(word, parts);

		}else if(key.equals("or")){
			found =  booleanOR(word, parts);

		}else if(key.equals("not")){
			found =  booleanNOT(word, parts);
		}
		return found;
	}

	private ArrayList<String> booleanAND(String word, String[] parts) {
		ArrayList<String> union = new ArrayList<String>();
		ArrayList<String> part2 = new ArrayList<String>();
		ArrayList<String> part1 = arraySearch(word);
		
		if(!(parts == null)){
			part2 = arraySearch(parts[0]); 
		}
		if(!part1.isEmpty() && !part2.isEmpty()){

			for(String part : part1){
				if(part2.contains(part)){
					union.add(part);
				}
			}
			return union;
		}
		return union;
	}

	private ArrayList<String> booleanOR(String word, String[] parts) {
		ArrayList<String> part1 = arraySearch(word);
		ArrayList<String> part2 = new ArrayList<String>(); 

		for(String part : part2){
			if(!part1.contains(part)){
				part1.add(part);
			}
		}
		return part1;
	}

	private ArrayList<String> booleanNOT(String word, String[] parts) {

		ArrayList<String> part1 = arraySearch(word);
		ArrayList<String> part2 = new ArrayList<String>(); 
		ArrayList<String> exclude = new ArrayList<String>();

		if(!(parts == null)){
			part2 = arraySearch(parts[0]); 
		}
		
		for(String part : part1){
			if(!part2.contains(part)){
				exclude.add(part);
			}
		}
		return exclude;
	}

	// Finding all the words ending on the specified suffix. (can be made with regular expressions!)
	private boolean suffixSearch(String[] parts) {
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
			System.out.println("You are searching for words with the sufffix: \"" + suffix + "\"");
			System.out.println("Not found.");
			return false;
		}
		else{
			Set<String> all = new HashSet<>();
			all.addAll(documents);
			documents.clear();
			documents.addAll(all);
			System.out.println("------------------------------------");
			System.out.println("You are searching for words with the suffix: \"" + suffix + "\"");
			System.out.println("Search suffix \"" + suffix + "\" found in: \n" + documents);
			return true;
		}
	}

	//Finding all the words starting with the specified prefix. (can be made with regular expressions!)
	private boolean prefixSearch(String[] parts) {
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
}