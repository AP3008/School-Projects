package main.persistence;

import main.account.PlayerProfile;
import main.account.PlayerStats;
import main.account.Settings;
import main.modes.ModeType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading player profiles and high scores to/from JSON files.
 * Uses only standard Java I/O (no external libraries).
 * Artificial Intelligence Tool: Claude; Programming: helped with creating functions to parse through JSON file format. 
 *
 * @author Jaideep Singh, Adam Porbanderwalla
 */
public class PersistenceService {

    private String playersFilePath;
    private String highScoresFilePath;

    /**
     * Creates a PersistenceService with the given file paths.
     * Files are created on first save if they don't exist yet.
     *
     * @param playersFilePath    path to the JSON file that stores player profiles
     * @param highScoresFilePath path to the JSON file that stores high scores
     */
    public PersistenceService(String playersFilePath, String highScoresFilePath) {
        this.playersFilePath = playersFilePath;
        this.highScoresFilePath = highScoresFilePath;
    }

    /**
     * Loads all player profiles from players.json.
     * Returns an empty list if the file doesn't exist or has no valid data.
     *
     * @return a list of PlayerProfile objects, may be empty
     */
    public List<PlayerProfile> loadPlayers() {
        List<PlayerProfile> players = new ArrayList<>();
        String json = readFile(playersFilePath);
        if (json == null || json.trim().isEmpty()) return players;

        json = json.trim();
        if (!json.startsWith("[")) return players;
        int closingBracket = json.lastIndexOf(']');
        if (closingBracket == -1) return players;
        json = json.substring(1, closingBracket).trim();
        if (json.isEmpty()) return players;

        List<String> objects = splitJsonObjects(json);
        for (String obj : objects) {
            PlayerProfile p = parsePlayerProfile(obj);
            if (p != null) players.add(p);
        }
        return players;
    }

    /**
     * Saves all player profiles to players.json.
     *
     * @param players the list of PlayerProfile objects to save
     */
    public void savePlayers(List<PlayerProfile> players) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < players.size(); i++) {
            sb.append(playerProfileToJson(players.get(i)));
            if (i < players.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        writeFile(playersFilePath, sb.toString());
    }


    /**
     * Loads the high score table from highscores.json.
     * Returns an empty table if the file doesn't exist or has no valid data.
     *
     * @return a HighScoreTable populated with saved entries
     */
    public HighScoreTable loadHighScores() {
        HighScoreTable table = new HighScoreTable();
        String json = readFile(highScoresFilePath);
        if (json == null || json.trim().isEmpty()) return table;

        json = json.trim();
        if (!json.startsWith("[")) return table;
        int closingBracket = json.lastIndexOf(']');
        if (closingBracket == -1) return table;
        json = json.substring(1, closingBracket).trim();
        if (json.isEmpty()) return table;

        List<String> objects = splitJsonObjects(json);
        for (String obj : objects) {
            HighScoreEntry entry = parseHighScoreEntry(obj);
            if (entry != null) table.record(entry.getModeType(), entry);
        }
        return table;
    }

    /**
     * Saves the entire high score table to highscores.json.
     *
     * @param table the HighScoreTable to save
     */
    public void saveHighScores(HighScoreTable table) {
        List<HighScoreEntry> entries = table.getAllEntries();
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < entries.size(); i++) {
            sb.append(highScoreEntryToJson(entries.get(i)));
            if (i < entries.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        writeFile(highScoresFilePath, sb.toString());
    }

    /**
     * Converts a PlayerProfile (including stats, unlocked levels, and settings)
     * into a JSON object string.
     *
     * @param p the PlayerProfile to serialize
     * @return a JSON object string representing the profile
     */
    private String playerProfileToJson(PlayerProfile p) {
        PlayerStats s = p.getStats();
        Settings st = p.getSettings();
        List<Integer> levels = p.getUnlockedLevels();

        StringBuilder sb = new StringBuilder();
        sb.append("  {\n");
        sb.append("    \"username\": \"").append(escapeJson(p.getUsername())).append("\",\n");
        sb.append("    \"password\": \"").append(escapeJson(p.getPassword())).append("\",\n");

        sb.append("    \"stats\": {\n");
        sb.append("      \"averageWPM\": ").append(s.getAverageWPM()).append(",\n");
        sb.append("      \"peakWPM\": ").append(s.getPeakWPM()).append(",\n");
        sb.append("      \"overallAccuracy\": ").append(s.getOverallAccuracy()).append(",\n");
        sb.append("      \"totalWordsCorrect\": ").append(s.getTotalWordsCorrect()).append(",\n");
        sb.append("      \"totalSessions\": ").append(s.getTotalSessions()).append(",\n");
        sb.append("      \"totalErrors\": ").append(s.getTotalErrors()).append(",\n");
        sb.append("      \"totalTimePlayed\": ").append(s.getTotalTimePlayed()).append(",\n");
        sb.append("      \"highScore\": ").append(s.getHighScore()).append(",\n");
        sb.append("      \"highScoreNormal\": ").append(s.getHighScoreNormal()).append(",\n");
        sb.append("      \"highScoreTimed\": ").append(s.getHighScoreTimed()).append(",\n");
        sb.append("      \"highScoreEndless\": ").append(s.getHighScoreEndless()).append(",\n");
        sb.append("      \"highestLevel\": ").append(s.getHighestLevel()).append("\n");
        sb.append("    },\n");

        sb.append("    \"unlockedLevels\": [");
        for (int i = 0; i < levels.size(); i++) {
            sb.append(levels.get(i));
            if (i < levels.size() - 1) sb.append(", ");
        }
        sb.append("],\n");

        sb.append("    \"settings\": {\n");
        sb.append("      \"volume\": ").append(st.getVolume()).append(",\n");
        sb.append("      \"musicEnabled\": ").append(st.isMusicEnabled()).append(",\n");
        sb.append("      \"soundEffectsEnabled\": ").append(st.isSoundEffectsEnabled()).append("\n");
        sb.append("    }\n");
        sb.append("  }");
        return sb.toString();
    }

    /**
     * Converts a HighScoreEntry into a JSON object string.
     *
     * @param e the HighScoreEntry to serialize
     * @return a JSON object string representing the entry
     */
    private String highScoreEntryToJson(HighScoreEntry e) {
        StringBuilder sb = new StringBuilder();
        sb.append("  {\n");
        sb.append("    \"username\": \"").append(escapeJson(e.getUsername())).append("\",\n");
        sb.append("    \"score\": ").append(e.getScore()).append(",\n");
        sb.append("    \"modeType\": \"").append(e.getModeType().name()).append("\",\n");
        sb.append("    \"timestampMillis\": ").append(e.getTimestampMillis()).append("\n");
        sb.append("  }");
        return sb.toString();
    }

    /**
     * Parses a JSON object string into a PlayerProfile.
     * Returns null if the username or password fields are missing.
     *
     * @param json a JSON object string for a single player
     * @return a populated PlayerProfile, or null if required fields are missing
     */
    private PlayerProfile parsePlayerProfile(String json) {
        String username = getJsonString(json, "username");
        String password = getJsonString(json, "password");
        if (username == null || password == null) return null;

        PlayerProfile p = new PlayerProfile(username, password);

        String statsJson = getJsonObject(json, "stats");
        if (statsJson != null) {
            PlayerStats stats = p.getStats();
            stats.setAverageWPM(getJsonDouble(statsJson, "averageWPM"));
            stats.setPeakWPM(getJsonDouble(statsJson, "peakWPM"));
            stats.setOverallAccuracy(getJsonDouble(statsJson, "overallAccuracy"));
            stats.setTotalWordsCorrect(getJsonInt(statsJson, "totalWordsCorrect"));
            stats.setTotalSessions(getJsonInt(statsJson, "totalSessions"));
            stats.setTotalErrors(getJsonInt(statsJson, "totalErrors"));
            stats.setTotalTimePlayed(getJsonLong(statsJson, "totalTimePlayed"));
            stats.setHighScore(getJsonInt(statsJson, "highScore"));
            stats.setHighScoreNormal(getJsonInt(statsJson, "highScoreNormal"));
            stats.setHighScoreTimed(getJsonInt(statsJson, "highScoreTimed"));
            stats.setHighScoreEndless(getJsonInt(statsJson, "highScoreEndless"));
            stats.setHighestLevel(getJsonInt(statsJson, "highestLevel"));
        }

        String levelsJson = getJsonArray(json, "unlockedLevels");
        if (levelsJson != null) {
            List<Integer> levels = new ArrayList<>();
            String[] parts = levelsJson.split(",");
            for (String part : parts) {
                part = part.trim();
                if (!part.isEmpty()) levels.add(Integer.parseInt(part));
            }
            p.setUnlockedLevels(levels);
        }

        String settingsJson = getJsonObject(json, "settings");
        if (settingsJson != null) {
            int volume = getJsonInt(settingsJson, "volume");
            boolean music = getJsonBoolean(settingsJson, "musicEnabled");
            boolean sfx = getJsonBoolean(settingsJson, "soundEffectsEnabled");
            p.setSettings(new Settings(volume, music, sfx));
        }

        return p;
    }

    /**
     * Parses a JSON object string into a HighScoreEntry.
     * Returns null if required fields are missing or the mode type is unrecognized.
     *
     * @param json a JSON object string for a single high score entry
     * @return a HighScoreEntry, or null if the data is invalid
     */
    private HighScoreEntry parseHighScoreEntry(String json) {
        String username = getJsonString(json, "username");
        int score = getJsonInt(json, "score");
        String modeStr = getJsonString(json, "modeType");
        long timestamp = getJsonLong(json, "timestampMillis");

        if (username == null || modeStr == null) return null;
        ModeType modeType;
        try {
            modeType = ModeType.valueOf(modeStr);
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown mode type: " + modeStr + " - skipping entry");
            return null;
        }
        return new HighScoreEntry(username, score, modeType, timestamp);
    }

    /**
     * Extracts a string value for the given key from a JSON object string.
     * Returns null if the key is not found.
     *
     * @param json the JSON string to search
     * @param key  the key to look up
     * @return the unescaped string value, or null if not found
     */
    private String getJsonString(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + search.length());
        if (colon == -1) return null;
        int start = json.indexOf('"', colon + 1);
        if (start == -1) return null;
        int end = start + 1;
        while (end < json.length()) {
            char ch = json.charAt(end);
            if (ch == '\\') { end += 2; continue; }
            if (ch == '"') break;
            end++;
        }
        if (end >= json.length()) return null;
        return unescapeJson(json.substring(start + 1, end));
    }

    /**
     * Extracts an integer value for the given key from a JSON object string.
     * Returns 0 if the key is not found or cannot be parsed.
     *
     * @param json the JSON string to search
     * @param key  the key to look up
     * @return the integer value, or 0 on failure
     */
    private int getJsonInt(String json, String key) {
        String val = getJsonRawValue(json, key);
        if (val == null) return 0;
        try { return Integer.parseInt(val); }
        catch (NumberFormatException e) { return 0; }
    }

    /**
     * Extracts a long value for the given key from a JSON object string.
     * Returns 0 if the key is not found or cannot be parsed.
     *
     * @param json the JSON string to search
     * @param key  the key to look up
     * @return the long value, or 0 on failure
     */
    private long getJsonLong(String json, String key) {
        String val = getJsonRawValue(json, key);
        if (val == null) return 0L;
        try { return Long.parseLong(val); }
        catch (NumberFormatException e) { return 0L; }
    }

    /**
     * Extracts a double value for the given key from a JSON object string.
     * Returns 0.0 if the key is not found or cannot be parsed.
     *
     * @param json the JSON string to search
     * @param key  the key to look up
     * @return the double value, or 0.0 on failure
     */
    private double getJsonDouble(String json, String key) {
        String val = getJsonRawValue(json, key);
        if (val == null) return 0.0;
        try { return Double.parseDouble(val); }
        catch (NumberFormatException e) { return 0.0; }
    }

    /**
     * Extracts a boolean value for the given key from a JSON object string.
     * Returns false if the key is not found.
     *
     * @param json the JSON string to search
     * @param key  the key to look up
     * @return the boolean value, or false if not found
     */
    private boolean getJsonBoolean(String json, String key) {
        String val = getJsonRawValue(json, key);
        if (val == null) return false;
        return Boolean.parseBoolean(val);
    }

    /**
     * Extracts the raw unparsed value string for the given key.
     * Returns null if the key is not found.
     *
     * @param json the JSON string to search
     * @param key  the key to look up
     * @return the trimmed raw value string, or null if not found
     */
    private String getJsonRawValue(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + search.length());
        if (colon == -1) return null;
        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == ',' || c == '}' || c == ']' || c == '\n') break;
            end++;
        }
        return json.substring(start, end).trim();
    }

    /**
     * Extracts a nested JSON object string for the given key (including braces).
     * Returns null if the key or opening brace is not found.
     *
     * @param json the JSON string to search
     * @param key  the key to look up
     * @return the full nested JSON object string, or null if not found
     */
    private String getJsonObject(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int braceStart = json.indexOf('{', idx + search.length());
        if (braceStart == -1) return null;
        int depth = 0;
        for (int i = braceStart; i < json.length(); i++) {
            if (json.charAt(i) == '{') depth++;
            else if (json.charAt(i) == '}') depth--;
            if (depth == 0) return json.substring(braceStart, i + 1);
        }
        return null;
    }

    /**
     * Extracts the inner content of a JSON array for the given key (without brackets).
     * Only handles flat, non-nested arrays.
     *
     * @param json the JSON string to search
     * @param key  the key to look up
     * @return the trimmed inner array content, or null if not found
     */
    private String getJsonArray(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int bracketStart = json.indexOf('[', idx + search.length());
        if (bracketStart == -1) return null;
        int bracketEnd = json.indexOf(']', bracketStart);
        if (bracketEnd == -1) return null;
        return json.substring(bracketStart + 1, bracketEnd).trim();
    }

    /**
     * Splits a JSON array body into individual object strings.
     */
    private List<String> splitJsonObjects(String json) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    objects.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }


    /**
     * Escapes backslashes and double quotes for safe JSON embedding.
     *
     * @param s the string to escape
     * @return the escaped string, or empty string if null
     */
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Reverses JSON escape sequences after reading from a JSON file.
     *
     * @param s the string to unescape
     * @return the unescaped string, or empty string if null
     */
    private String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\\\", "\\").replace("\\\"", "\"");
    }

    /**
     * Reads the entire contents of a file as a string.
     * Returns null if the file doesn't exist or cannot be read.
     *
     * @param path the file path to read
     * @return the file contents, or null on failure
     */
    private String readFile(String path) {
        File file = new File(path);
        if (!file.exists()) return null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + path + " - " + e.getMessage());
            return null;
        }
        return sb.toString();
    }

    /**
     * Atomically writes a string to a file using a temp file to prevent corruption.
     * Creates any missing parent directories automatically.
     *
     * @param path    the file path to write to
     * @param content the content to write
     */
    private void writeFile(String path, String content) {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                System.err.println("Failed to create directory: " + parent.getAbsolutePath());
                return;
            }
        }
        File tmpFile = new File(path + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Error writing file: " + path + " - " + e.getMessage());
            return;
        }
        try {
            Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error moving temp file: " + path + " - " + e.getMessage());
        }
    }
}
