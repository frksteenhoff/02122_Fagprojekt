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

/*
 * Author(s): Anders Sperling, Henriette Steenhoff
 * Software technology, 4th semester F15, DTU
 * TODO add code description/description of functionality  
 */

public class Index5 {

	WikiItem start, tmp;
	wikiMap wikiM = new wikiMap();
	boolean docTitle = true;

	public static void main(String[] args) {
		System.out.println("Preprocessing " + args[0]);

		Index5 i = new Index5(args[0]);
		GUI g = new GUI(i);
		Scanner console = new Scanner(System.in);
		for (;;) {
			System.out.println("Input search string:");
			String searchstr = console.nextLine();
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

	public Index5(String filename) {
		long Starttime = System.nanoTime();
		String word, currentTitle = null;
		WikiItem current, tmp;

		try {
			BufferedReader bufferR = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
			Scanner input = new Scanner(bufferR);
			word = input.next();
			if(docTitle && !word.equals(null)){
				currentTitle = word.replaceAll("[^A-Za-z0-9 ]", "");
				docTitle = false;
			}
			start = new WikiItem(word.toLowerCase().replaceAll("[^a-z0-9 ]", ""), currentTitle, null);
			current = start;

			while (input.hasNext()) {   // Read all words in input

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
				}
				if(!word.equals("---END.OF.DOCUMENT---") && 
						!stringTitleDuplicate(wikiM.get(Math.abs(word.hashCode())), word, currentTitle)){
					tmp = new WikiItem(word.toLowerCase().replaceAll("[^a-z0-9 ]", ""), currentTitle, null);
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
		System.out.println("Time spent on indexing: " + (endTime-Starttime)/1000000 + " sec. \n");
	}

	//Overall search logic
	public boolean searchLogic(String searchString){

		String searchWord = searchString;
		String[] parts = searchString.split(" ");

		System.out.println("------------------------------------");
		System.out.println("You are searching for: \"" + searchWord + "\"");

		//Limiting searches to either 1 search word or two separated by a logical operator
		if(parts.length == 1 && (!parts[0].startsWith("*") && !parts[0].endsWith("*"))){
			return search(parts[0]);

		}else if(parts.length == 1 && parts[0].endsWith("*")){
			return prefixSearch(parts);

		}else if(parts.length == 1 && parts[0].startsWith("*")){
			return suffixSearch(parts);

		//If the search is not full-text or with correct boolean operators -> error message.
		}else if(parts.length == 3 && (parts[1].equals("and") || parts[1].equals("or") || parts[1].equals("not"))){	
			String [] titles = new String[arraySearch(parts[2]).size()];
			titles = arraySearch(parts[2]).toArray(titles);
			ArrayList<String> boolResult = boolSearch(parts[0], parts[1], titles);

			System.out.println("Search words found in: \n" + boolResult);
			return true;

		}else if(!parts[1].equals("and") || !parts[1].equals("or") || !parts[1].equals("not") || parts.length < 3 
				|| !parts[parts.length-2].equals("and") || !parts[parts.length-2].equals("or") || !parts[parts.length-2].equals("not")){
			System.out.println("No full-text search allowed. \nUse OR, AND or NOT as separator in multiple word search.");
			return true;

		}else if(parts.length > 3 && parts.length % 2 == 1){		
			recursiveBool(parts);
			return true;

			// Correct single boolean search (one and/or/not operator + length 3)
		}else{
			System.out.println("Use or, and or not as separator in multiple word search.");
			return true;
		}
	}

	public boolean search(String searchstr) {
		long Starttime = System.nanoTime();
		ArrayList<WikiItem> hash = wikiM.get(Math.abs(searchstr.hashCode()));
		for(int i = 0; i < hash.size(); i++){
			if(hash.get(i).str.equals(searchstr)){
				long endTime = System.nanoTime();
				System.out.println("Time spent on Simple Search: " + (endTime-Starttime) + " nanosec. \n");
				System.out.println("Search string \"" + searchstr + "\" found in: \n"
						+ hash.get(i).title);
				return true;
			}
		}
		System.out.println("Not found.");
		return false;
	}

	private String[] recursiveBool(String[] parts){

		if(parts.length > 3){
			for(int i = (parts.length-2) ; i > 1 ; i--){
				if(parts[i].equals("and") || parts[i].equals("or") || parts[i].equals("not")){
					// Split search
					String[] first = {parts[0],parts[1]}; 
					String[] rest = Arrays.copyOfRange(parts, 2, parts.length);
					ArrayList<String> splitArray = boolSearch(first[0], first[1], recursiveBool(rest));

					String[] split = new String[splitArray.size()];
					split = splitArray.toArray(split);
					System.out.println("Search words found in: \n" + Arrays.toString(split));
					return split;
				}
				System.out.println("No full-text search allowed. \nUse OR, AND or NOT as separator in multiple word search.");
				String[] nullList = new String[0];
				return nullList;
			}

		}else if(parts.length == 3){
			// Boolean search on search words
			ArrayList<String> boolArray = boolSearch(parts[0], parts[1], arraySearch(parts[2]).toArray(new String[arraySearch(parts[2]).size()]));
			String[] recursive = new String[boolArray.size()];
			recursive = boolArray.toArray(recursive);
			return recursive;
		}
		String[] nullList = new String[0];
		return nullList;
	}

	private ArrayList<String> boolSearch(String word, String key, String[] parts) {
		long Starttime = System.nanoTime();
		ArrayList<String> found = new ArrayList<>();

		if(key.equals("and")){
			found = booleanAND(word, parts);

		}else if(key.equals("or")){
			found =  booleanOR(word, parts);

		}else if(key.equals("not")){
			found =  booleanNOT(word, parts);
		}
		long endTime = System.nanoTime();
		System.out.println("Time spent on Boolean Search: " + (endTime-Starttime) + " nanosec. \n");
		return found;
	}

	private ArrayList<String> booleanAND(String word, String[] parts) {

		ArrayList<String> union = new ArrayList<String>();
		ArrayList<String> part1 = arraySearch(word);
		ArrayList<String> part2 = new ArrayList<String>();
		if(parts != null){
			Collections.addAll(part2, parts);
		}
		if(!part1.isEmpty() && !part2.isEmpty()){

			for(String part : part1){
				if(part2.contains(part)){
					union.add(part);
				}
			}
		}
		return union;
	}

	private ArrayList<String> booleanOR(String word, String[] parts) {
		ArrayList<String> part1 = arraySearch(word);
		ArrayList<String> part2 = new ArrayList<String>(); 
		Collections.addAll(part2, parts);

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

		if(parts != null){
			Collections.addAll(part2, parts);
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
		long Starttime = System.nanoTime();
		String suffix = parts[0].substring(1);
		ArrayList<String> documents = new ArrayList<>();
		WikiItem current = start;
		while(current != null){
			if(current.str.endsWith(suffix)) {
				documents.addAll((wikiM.get(current.WikiNR)).get((wikiM.get(current.WikiNR)).indexOf(current)).title);
			}
			current = current.next;
		}
		if(documents.isEmpty()){
			System.out.println("Not found.");
			return false;
		}
		else{
			Set<String> all = new HashSet<>();
			all.addAll(documents);
			documents.clear();
			documents.addAll(all);
			long endTime = System.nanoTime();
			System.out.println("Time spent on Suffix Search: " + (endTime-Starttime) + " nanosec. \n");
			System.out.println("Search suffix \"" + suffix + "\" found in: \n" + documents);
			return true;
		}
	}

	//Finding all the words starting with the specified prefix. (can be made with regular expressions!)
	private boolean prefixSearch(String[] parts) {
		long Starttime = System.nanoTime();
		String prefix = parts[0].substring(0,parts[0].length()-1);
		ArrayList<String> documents = new ArrayList<>();
		WikiItem current = start;
		while(current != null){
			if(current.str.startsWith(prefix)) {
				documents.addAll((wikiM.get(current.WikiNR)).get((wikiM.get(current.WikiNR)).indexOf(current)).title);
			}
			current = current.next;
		}
		if(documents.isEmpty()){
			System.out.println("Words with the prefix: \"" + prefix + "\" not found.");
			return false;
		}
		else{
			Set<String> all = new HashSet<>();
			all.addAll(documents);
			documents.clear();
			documents.addAll(all);
			long endTime = System.nanoTime();
			System.out.println("Time spent on Prefix Search: " + (endTime-Starttime) + " nanosec. \n");
			System.out.println("Search prefix \"" + prefix + "\" found in: \n" + documents);
			return true;
		}
	}

	public ArrayList<String> arraySearch(String searchstr) {
		WikiItem current = start;
		while(current != null) {
			if(current.str.equals(searchstr)) {
				return (wikiM.get(current.WikiNR)).get((wikiM.get(current.WikiNR)).indexOf(current)).title;
			}
			current = current.next;
		}
		ArrayList<String> nullList = new ArrayList<String>();
		return nullList;
	}
}