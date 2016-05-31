package main;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class SpellChecker {
	HashMap<String, Integer> counts; // indexed  training data for spelling correction

	@SuppressWarnings("unchecked")
	public SpellChecker(int params){
		 super();
		 if (params == 0){
			 try {
	        	 	// TODO switcher
					File file = new File("resources/index.txt");
				    FileInputStream f = new FileInputStream(file);
				    ObjectInputStream s = new ObjectInputStream(f);
				    counts = (HashMap<String, Integer>) s.readObject();
				    s.close();
				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
         
	}
	
	public String[] correctDocument(String[] input){
		for (int i = 0; i < input.length; i++){
			if (!input[i].equals(" ")){
				input[i] = correctWord(input[i]);
			}
		}
		return input;
	}
	
	/**
     * corrects the spelling of the given sentence using Levenshtein distance
     * and a training data with probabilities
     * @param input
     * @return
     */
    public String correctSentence(String input){
    	String result = new String();
    	for (String word : input.split(" ")) {
    		result = result + " " + correctWord(word);
    	}
    		
    	result = result.replaceFirst(" ", "");
    	return result;
    }
    
    
    /**
     * corrects the spelling of the given word using Levenshtein distance
     * and a training data with probabilities
     * @param input
     * @return
     */
    public String correctWord(String input){
    	input = input.toLowerCase();
    	int levenshteinParam = input.length() - 4;
    	if (levenshteinParam <= 0)
    		levenshteinParam = 1;
    	levenshteinParam = 1;
    	
    	if (counts.containsKey(input))
    		return input;
    	
    	int prob = 0;
    	String endCandidate = new String();
    	for (Map.Entry<String, Integer> entry : counts.entrySet()){
    		String key = entry.getKey();
            int value = entry.getValue();
    		int distance = editdist(input, key);
    		
    		// always consider the most probable string
    		if (distance <= levenshteinParam && prob < value){
    			prob = value;
    			endCandidate = key;
    			System.out.println(input + " " + key + " " + value);
    		}
    	}
    	
    	if (!endCandidate.equals(""))
    	{
    		return endCandidate;
    	}
    		
    	return input;
    }
    

    /**
     * computes the Levenshtein distance between two Strings
     * @param S1
     * @param S2
     * @return Levenshtein distance
     */
    private int editdist(String S1, String S2) {
    	int m = S1.length(), n = S2.length();
    	int[] D1;
    	int[] D2 = new int[n + 1];

    	for(int i = 0; i <= n; i ++)
    		D2[i] = i;

    	for(int i = 1; i <= m; i ++) {
    		D1 = D2;
    		D2 = new int[n + 1];
    		for(int j = 0; j <= n; j ++) {
    			if(j == 0) D2[j] = i;
    			else {
    				int cost = (S1.charAt(i - 1) != S2.charAt(j - 1)) ? 1 : 0;
    				if(D2[j - 1] < D1[j] && D2[j - 1] < D1[j - 1] + cost)
    					D2[j] = D2[j - 1] + 1;
    				else if(D1[j] < D1[j - 1] + cost)
    					D2[j] = D1[j] + 1;
    				else
    					D2[j] = D1[j - 1] + cost;
    			}
    		}
    	}
    	return D2[n];
    }
    
    
    /**
     * Parses a dictionary and creates the HashMap with number of 
     * occurrences of each occurred word in the file.
     * Must be run each time when new words are added to the dictionary or
     * when a new dictionary is going to be used.
     * Dictionary can be any text
     * @param fileName directory of the dictionary
     * @return HashMap with word and number of occurrences
     * @throws IOException 
     */
    private HashMap<String, Integer> countStringsInFile(String fileName) throws IOException{
    	@SuppressWarnings("resource")
		Scanner file = new Scanner(new File(fileName)).useDelimiter("[^a-zA-Z]+");
    	   HashMap<String, Integer> map = new HashMap<>();

    	   while (file.hasNext()){
    	        String word = file.next().toLowerCase();
    	        if (map.containsKey(word)) {
    	            map.put(word, map.get(word) + 1);
    	        } else {
    	            map.put(word, 0);
    	        }
    	    }
    	   file.close();
    	    ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
    	    Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {

    	        @Override
    	        public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
    	            return a.getValue().compareTo(b.getValue());
    	        }
    	    });
    	        File fileOutput = new File("resources/index.txt");
    	        FileOutputStream f = new FileOutputStream(fileOutput);
    	        ObjectOutputStream s = new ObjectOutputStream(f);
    	        s.writeObject(map);
    	        s.close();

    	    return map;
    	}
    public static void main(String argv[]) {
    	/*
    	SpellChecker sc = new SpellChecker(0);
    	try {
			sc.countStringsInFile("resources/dictionary.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    	SpellChecker sc = new SpellChecker(0);
    	System.out.println(System.currentTimeMillis());
    	System.out.println(sc.correctSentence("imag is a vry god colection"));
    	System.out.println(System.currentTimeMillis());

	}
}
