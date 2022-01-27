package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class EvilHangman {

    public static void main(String[] args) {
        File dictionary = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);
        int numberOfGuesses = Integer.parseInt(args[2]);
        String wordMessage = "Word length must be >= 2";
        String guessesMessage = "Number of guesses must be >=1.";

        // Check user input:
        if (wordLength < 2) {
            System.out.println(wordMessage);
        }
        if (numberOfGuesses < 1) {
            System.out.println(guessesMessage);
        }

        // Instantiate EvilHangmanGame:
        EvilHangmanGame game = new EvilHangmanGame();

        // try-catch to start the game:
        try {
            game.startGame(dictionary, wordLength);
        }
        catch (IOException error) {
            error.printStackTrace();
        }
        catch (EmptyDictionaryException error) {
            System.out.println(error.getMessage());
        }


        // GAME LOOP:
        while (numberOfGuesses > 0) {
            // Prompt user:
            userPrompt(wordLength, numberOfGuesses, game);

            // Make guess:
            try {
                // take in guess
                char userGuess = userInput();

                // make guess in game, decrement numberOfGuesses
                game.makeGuess(userGuess);

                // if the game is not finished:
                //todo: am I handling this correctly, or will I have one too many/too few?
                if (numberOfGuesses != 0) {
                    // check for incorrect guess
                    if (!game.key.toString().contains(Character.toString(userGuess))) {
                        numberOfGuesses--;
                        System.out.printf("Sorry, there are no %c\'s\n\n", userGuess);
                    }
                    // for correct guess
                    else {
                        // Count how many times the user's character occurs in the word:
                        int characterCount = 0;
                        for (char character : game.key.toString().toCharArray()) {
                            if (character == userGuess) {
                                characterCount++;
                            }
                        }

                        // Give user feedback on their guess:
                        if (characterCount < 2) {   // proper grammar
                            System.out.printf("Yes, there is %d %c\'s\n\n", characterCount, userGuess);
                        }
                        else {
                            System.out.printf("Yes, there are %d %c\'s\n\n", characterCount, userGuess);
                        }
                    }

                    boolean won = true;
                    for (char myChar : game.key.toString().toCharArray()) {
                        if (myChar == '~') {
                            won = false;
                        }
                    }
                    if (won) {
                        System.out.println("You win!!!\n");
                        System.out.printf("You Guessed the word: %s\n", game.key.toString());
                        return;
                    }
                }
            }
            catch (GuessAlreadyMadeException error) {
                System.out.println(error.getMessage());
            }
        }

        // If this point is reached, tell the user they lost
        System.out.println("You lose!!!");
        System.out.printf("The word was: %s\n", game.myDictionary.stream().findFirst());
    }

    private static void userPrompt(int wordLength, int numberOfGuesses, EvilHangmanGame game) {
        System.out.printf("You have %d guesses left\n", numberOfGuesses);
        System.out.print("Used letters: ");

        if(!game.getGuessedLetters().isEmpty()) {
            for(Character character : game.getGuessedLetters()) {
                System.out.printf("%c ", character);
            }
        }

        System.out.println();
        System.out.printf("Word: %s\n", game.key);
    }

    private static char userInput() {
        char inputChar = ' ';
        String line = "";

        // while char is not a letter:
        while (!Character.isAlphabetic(inputChar) || Character.isSpaceChar(inputChar)) {
            System.out.print("Enter guess: ");
            line = new Scanner(System.in).nextLine();

            if (line.isEmpty() || line.isBlank()) {
                inputChar = ' ';
            }
            else {
                inputChar = line.charAt(0);
            }

            if (!Character.isAlphabetic(inputChar)) {
                System.out.println("Invalid input\n");
            }
        }
        return Character.toLowerCase(inputChar);
    }

}
