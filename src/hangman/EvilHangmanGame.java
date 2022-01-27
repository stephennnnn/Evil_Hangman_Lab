package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{
    Set<String> myDictionary = new HashSet<>();
    SortedSet<Character> GuessedLetters = new TreeSet<>();
    StringBuilder key = new StringBuilder();

    public EvilHangmanGame() {}

    @Override
    public void startGame(File d, int wordLength) throws IOException, EmptyDictionaryException {

//        Errors that could occur include:
//        an empty dictionary file,
//        bad command line parameters,
//        and bad characters in the dictionary file.

        // Clear previous game and initialize key
        myDictionary.clear();
        GuessedLetters.clear();

        key.append("~".repeat(Math.max(0, wordLength)));  // todo: For some reason, if I run with a dash then one of my rightmost tests fail


        // Scan in the dictionary
        if (d.length() > 0) {
            Scanner input = new Scanner(d);
            while (input.hasNext()) {
                String newWord = input.next();
                if (newWord.matches("[a-zA-Z]+") && (newWord.length() == wordLength)) {
                    myDictionary.add(newWord.toLowerCase());
                }
            }
            input.close();
            if (myDictionary.isEmpty()) {
                throw new EmptyDictionaryException();
            }
        }
        else {
            throw new EmptyDictionaryException();
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        // Check if the guess is allowed
        guess = Character.toLowerCase(guess);
        if (GuessedLetters.contains(guess)) {
            throw new GuessAlreadyMadeException();
        }
        else {
            GuessedLetters.add(guess);
        }

        // make wordPattern based on guess and sort dictionary words into their corresponding word pattern group
        HashMap<String, HashSet<String>> wordGroups = new HashMap<>();
        for (String currWord : myDictionary) {
            // make a key based on current dictionary word and character guess
            String wordPattern = WordPattern(currWord, guess);

            if (!wordGroups.containsKey(wordPattern)) {      // if there is not a corresponding wordPattern group in wordGroups, create group
                wordGroups.put(wordPattern, new HashSet<String>());
            }
            // add word to corresponding group
            wordGroups.get(wordPattern).add(currWord);
        }

        // Get the largest of the hashsets in the hashmap:

        // iterate through each key in the hashmap, get the corresponding group from the hashmap, and record the size of the largest hashmap

        String largestKey = wordGroups.keySet().iterator().next();                              // initialize to the first key in the keySet
        int largestGroupSize = wordGroups.get(wordGroups.keySet().iterator().next()).size();    // initialize to size of group corresponding to first key in keySet

        for (String currentPattern : wordGroups.keySet()) {
            if (wordGroups.get(currentPattern).size() > largestGroupSize) {     // if group size is larger, reset largestGroupSize and largestKey
                largestGroupSize = wordGroups.get(currentPattern).size();
                largestKey = currentPattern;
            }

            // TIEBREAKERS:
            // chose group/pattern where letter does not appear at all
            // if each pattern contains the letter, then choose pattern with the fewest instances
            // if still tie, choose the one with the rightmost instance of the letter
            // if still...tie, choose the next rightmost letter. Repeat this until tie is broken.

            else if (wordGroups.get(currentPattern).size() == largestGroupSize){                 // if group sizes are the same, break ties
                if (currentPattern.indexOf(guess) == -1) {
                    largestGroupSize = wordGroups.get(currentPattern).size();
                    largestKey = currentPattern;
                }
                else if ((currentPattern.indexOf(guess) != -1) && (largestKey.indexOf(guess) != -1)){
                    char myChar = guess; // had problems when not using a copy
                    int currentPatternCount = (int) currentPattern.chars().filter(ch -> ch == myChar).count();
                    int largestKeyCount = (int) largestKey.chars().filter(ch -> ch == myChar).count();
                    // if currCount < largestKeyCount, set currentPattern to key
                    if (currentPatternCount < largestKeyCount) {
                        largestKey = currentPattern;
                    }
                    // if currentPatternCount = largestKeyCount, use rightmost tiebreaker
                    if (currentPatternCount == largestKeyCount) {
                        if (currentPattern.lastIndexOf(guess) > largestKey.lastIndexOf(guess)) {
                            largestKey = currentPattern;
                        }
                        // else do nothing
                    }
                    // else do nothing
                }
                // otherwise, nothing needs to change (largestKey is the correct key)
            }
        }

        myDictionary = wordGroups.get(largestKey);
        key = new StringBuilder(largestKey);

        return wordGroups.get(largestKey);
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return GuessedLetters;
    }

    private String WordPattern(String currentWord, char charGuess) {
        StringBuilder newWordPattern = new StringBuilder();
        for (int i = 0; i < currentWord.length(); i++) {
            char tempChar = currentWord.charAt(i);
            if (tempChar == charGuess) {
                newWordPattern.append(tempChar);
            }
            else {
                newWordPattern.append(key.charAt(i));
            }
        }
        return newWordPattern.toString();
    }

}

