package main.gameplay;

import main.modes.Difficulty;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
* Stores and provides words for use during gameplay.
* 
* Words are loaded from CSV files at startup, organized into three separate
* lists by difficulty: easy, medium, and hard. The repository provides random
* word selection by difficulty level, which is used by {@link main.engine.GameEngine}
* to spawn {@link WordTarget} objects appropriate for the current level.
*
* @author Garv Sharma
* @see Word
*/

public class WordRepository {

    /** List of words with EASY difficulty loaded from easy.csv. */
   private List<Word> easyWords;
   /** List of words with MEDIUM difficulty loaded from medium.csv. */
   private List<Word> mediumWords;
   /** List of words with HARD difficulty loaded from hard.csv. */
   private List<Word> hardWords;

    /** Random number generator used to select a word from the appropriate list. */
    private Random random;

    /**
     * Constructs a new WordRepository and immediately loads all word lists
     * from their respective CSV files by calling {@link #init()}.
     *
     */
    public WordRepository() {
        this.easyWords = new ArrayList<>();
        this.mediumWords = new ArrayList<>();
        this.hardWords = new ArrayList<>();
        this.random = new Random();

        init();
    }

     /**
    * Initializes the repository by loading words from all three CSV files. Each file is loaded with
    * its corresponding {@link Difficulty} level.
    */
    private void init() {
        loadWords("src/main/gameplay/easy.csv", Difficulty.EASY);
        loadWords("src/main/gameplay/medium.csv", Difficulty.MEDIUM);
        loadWords("src/main/gameplay/hard.csv", Difficulty.HARD);
    }

    /**
    * Loads words from a CSV file and adds them to the appropriate difficulty list.
    * Each line of the file may contain multiple comma-separated words.
    * Lines with empty tokens are ignored.
    *
    * @param filePath   the path to the CSV file to load
    * @param difficulty the {@link Difficulty} to assign to all words in this file
    * @throws RuntimeException if the file cannot be read
    */
    private void loadWords(String filePath, Difficulty difficulty) {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                for (int i = 0; i < tokens.length; i++) {
                    String wordText = tokens[i].trim();
                    if (!wordText.isEmpty()) {
                        Word word = new Word(wordText, difficulty);
                        if (difficulty == Difficulty.EASY) {
                            easyWords.add(word);
                        } else if (difficulty == Difficulty.MEDIUM) {
                            mediumWords.add(word);
                        } else if (difficulty == Difficulty.HARD) {
                            hardWords.add(word);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading file: " + filePath, e);
        }
    }

    /**
    * Returns a random word matching the given difficulty level.
    * Returns null if no words of that difficulty are available.
    *
    * @param diff the {@link Difficulty} of the word to retrieve
    * @return a randomly selected {@link Word} of the given difficulty, or null if the list for that difficulty is empty.
    */

    public Word getRandomWord(Difficulty diff) {
        List<Word> targetList;
        if (diff == Difficulty.EASY) targetList = easyWords;
        else if (diff == Difficulty.MEDIUM) targetList = mediumWords;
        else targetList = hardWords;

        if (targetList.isEmpty()) return null;
        return targetList.get(random.nextInt(targetList.size()));
    }
}
