import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Spelling Bee
 *
 * This program accepts an input of letters. It prints to an output file
 * all English words that can be generated from those letters.
 *
 * For example: if the user inputs the letters "doggo" the program will generate:
 * do
 * dog
 * doggo
 * go
 * god
 * gog
 * gogo
 * goo
 * good
 *
 * It utilizes recursion to generate the strings, mergesort to sort them, and
 * binary search to find them in a dictionary.
 *
 * @author Zach Blick, [ADD YOUR NAME HERE]
 *
 * Written on March 5, 2023 for CS2 @ Menlo School
 *
 * DO NOT MODIFY MAIN OR ANY OF THE METHOD HEADERS.
 */
public class SpellingBee {

    private String letters;
    private ArrayList<String> words;
    public static final int DICTIONARY_SIZE = 143091;
    public static final String[] DICTIONARY = new String[DICTIONARY_SIZE];

    public SpellingBee(String letters) {
        this.letters = letters;
        words = new ArrayList<String>();
    }

    // TODO: generate all possible substrings and permutations of the letters.
    //  Store them all in the ArrayList words. Do this by calling ANOTHER method
    //  that will find the substrings recursively.
    public void generate() {
        // YOUR CODE HERE — Call your recursive method!
        create("", letters);
    }

    public void create(String group1, String group2) {
        // Base case/goes down to bottom of tree
        if(group2.equals("")) {
            words.add(group1);
            return;
        }
        // Horizontal/all letters
        for(int i=0; i<group2.length(); i++) {
            // Vertical of tree
            words.add(group1);
            words.add(group2);
            create(group1 + group2.substring(i, i+1), group2.substring(0,i) + group2.substring(i+1));
        }
    }

    // TODO: Apply mergesort to sort all words. Do this by calling ANOTHER method
    //  that will find the substrings recursively.
    public void sort() {
        // Point to returned sorted array
        words = mergeSort(words);
    }

    public ArrayList<String> mergeSort(ArrayList<String> arr) {
        // Once there is only one element in array
        if(arr.size() == 1) {
            return arr;
        }
        // Each half of array
        ArrayList<String> arr1 = new ArrayList<>();
        ArrayList<String> arr2 = new ArrayList<>();
        for(int i=0; i < arr.size()/2; i++) {
            arr1.add(arr.get(i));
        }
        for(int i=arr.size()/2; i < arr.size(); i++) {
            arr2.add(arr.get(i));
        }
        // Give new sorted arrays to merge together
        arr1 = mergeSort(arr1);
        arr2 = mergeSort(arr2);
        return merge(arr1, arr2);
    }

    public ArrayList<String> merge(List<String> arr1, List<String> arr2) {
        // Creates new empty array to add to in sorted order
        ArrayList<String> mergeList = new ArrayList<>();
        // Makes sure both arrays still have elements
        while (arr1.size() != 0 && arr2.size() != 0) {
            // Checks which strings are greater
            if (arr1.get(0).compareTo(arr2.get(0)) < 0) {
                mergeList.add(arr1.remove(0));
            }
            else {
                mergeList.add(arr2.remove(0));
            }
        }
        // If one array is empty, add what is left of the other non-empty array
        if (arr1.isEmpty() && !(arr2.isEmpty())) {
            mergeList.addAll(mergeList.size(), arr2);
        }
        else if (!(arr1.isEmpty()) && arr2.isEmpty()) {
            mergeList.addAll(mergeList.size() , arr1);
        }
        else{}
        return mergeList;
    }

    // Removes duplicates from the sorted list.
    public void removeDuplicates() {
        int i = 0;
        while (i < words.size() - 1) {
            String word = words.get(i);
            if (word.equals(words.get(i + 1)))
                words.remove(i + 1);
            else
                i++;
        }
    }

    // TODO: For each word in words, use binary search to see if it is in the dictionary.
    //  If it is not in the dictionary, remove it from words.
    public void checkWords() {
        // YOUR CODE HERE
        boolean found;
        // Each individual word in word array
        for(int i=0; i<words.size(); i++) {
            found = binarySearch(words.get(i), 0, DICTIONARY_SIZE);
            if(!found) {
               words.remove(i);
               // Since size is not a final variable, as I remove one I need to bring the index back one
               i--;
            }
        }
    }

    public boolean binarySearch(String str, int start, int end) {
        if(end < start) {
            return false;
        }

        int mid = start + (end-start)/2;
        // Checks target vs middle of sorted array and either returns true if equal or halves to one side based on if
        // target is greater or less than mid in Dictionary array
        if(DICTIONARY[mid].equals(str)) {
            return true;
        }
        else if (DICTIONARY[mid].compareTo(str) > 0) {
            return binarySearch(str, start, mid-1);
        }
        else {
            return binarySearch(str, mid+1, end);
        }
    }

    // Prints all valid words to wordList.txt
    public void printWords() throws IOException {
        File wordFile = new File("Resources/wordList.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(wordFile, false));
        for (String word : words) {
            writer.append(word);
            writer.newLine();
        }
        writer.close();
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public SpellingBee getBee() {
        return this;
    }

    public static void loadDictionary() {
        Scanner s;
        File dictionaryFile = new File("Resources/dictionary.txt");
        try {
            s = new Scanner(dictionaryFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open dictionary file.");
            return;
        }
        int i = 0;
        while(s.hasNextLine()) {
            DICTIONARY[i++] = s.nextLine();
        }
    }

    public static void main(String[] args) {

        // Prompt for letters until given only letters.
        Scanner s = new Scanner(System.in);
        String letters;
        do {
            System.out.print("Enter your letters: ");
            letters = s.nextLine();
        }
        while (!letters.matches("[a-zA-Z]+"));

        // Load the dictionary
        SpellingBee.loadDictionary();

        // Generate and print all valid words from those letters.
        SpellingBee sb = new SpellingBee(letters);
        sb.generate();
        sb.sort();
        System.out.println(sb.words);
        sb.removeDuplicates();
        System.out.println(sb.words);
        sb.checkWords();
        System.out.println(sb.words);
        try {
            sb.printWords();
        } catch (IOException e) {
            System.out.println("Could not write to output file.");
        }
        s.close();
    }
}
