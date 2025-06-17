import java.util.*;

public class BrailleAutocorrect {
    // Braille to letter mapping (standard English Braille)
    private static final Map<String, Character> BRAILLE_TO_LETTER = new HashMap<>();
    static {
        BRAILLE_TO_LETTER.put("D", 'a');      // Dot 1
        BRAILLE_TO_LETTER.put("DK", 'c');     // Dots 1,4
        BRAILLE_TO_LETTER.put("DO", 'e');     // Dots 1,5
        BRAILLE_TO_LETTER.put("DKO", 'g');    // Dots 1,4,5
        BRAILLE_TO_LETTER.put("DP", 'i');     // Dots 1,6
        BRAILLE_TO_LETTER.put("DKP", 'k');    // Dots 1,4,6
        BRAILLE_TO_LETTER.put("W", 'b');      // Dot 2
        BRAILLE_TO_LETTER.put("WK", 'd');     // Dots 2,4
        BRAILLE_TO_LETTER.put("WO", 'f');     // Dots 2,5
        BRAILLE_TO_LETTER.put("WKO", 'h');    // Dots 2,4,5
        BRAILLE_TO_LETTER.put("WP", 'j');     // Dots 2,6
        BRAILLE_TO_LETTER.put("WKP", 'l');    // Dots 2,4,6
        BRAILLE_TO_LETTER.put("Q", 'm');      // Dot 3
        BRAILLE_TO_LETTER.put("QK", 'n');     // Dots 3,4
        BRAILLE_TO_LETTER.put("QO", 'o');     // Dots 3,5
        BRAILLE_TO_LETTER.put("QKO", 'p');    // Dots 3,4,5
        BRAILLE_TO_LETTER.put("QP", 'q');     // Dots 3,6
        BRAILLE_TO_LETTER.put("QKP", 'r');    // Dots 3,4,6
        BRAILLE_TO_LETTER.put("K", 's');      // Dot 4
        BRAILLE_TO_LETTER.put("KO", 't');     // Dots 4,5
        BRAILLE_TO_LETTER.put("KP", 'u');     // Dots 4,6
        BRAILLE_TO_LETTER.put("KOP", 'v');    // Dots 4,5,6
        BRAILLE_TO_LETTER.put("WQP", 'w');    // Dots 2,3,6
        BRAILLE_TO_LETTER.put("OP", 'x');     // Dots 5,6
        BRAILLE_TO_LETTER.put("QOP", 'y');    // Dots 3,5,6
        BRAILLE_TO_LETTER.put("QKP", 'z');    // Dots 3,4,6 (duplicate corrected)
    }

    // Trie node for dictionary
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isWord = false;
    }

    // Autocorrect system
    private static class AutocorrectSystem {
        private TrieNode root = new TrieNode();
        private Map<String, Integer> feedback = new HashMap<>(); // Tracks suggestion selections
        private Set<String> dictionary = new HashSet<>();

        // Initialize dictionary
        public AutocorrectSystem() {
            // Sample dictionary
            String[] words = {"call", "ball", "fall", "hall", "cake", "lake", "make", "take"};
            for (String word : words) {
                insert(word);
                dictionary.add(word);
            }
        }

        // Insert word into Trie
        private void insert(String word) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);
            }
            node.isWord = true;
        }

        // Check if word exists in dictionary
        private boolean exists(String word) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                if (!node.children.containsKey(c)) return false;
                node = node.children.get(c);
            }
            return node.isWord;
        }

        // Convert Braille input to text
        public String brailleToText(List<String> brailleInput) {
            StringBuilder result = new StringBuilder();
            for (String braille : brailleInput) {
                Character letter = BRAILLE_TO_LETTER.get(normalizeBraille(braille));
                if (letter == null) return ""; // Invalid Braille input
                result.append(letter);
            }
            return result.toString();
        }

        // Normalize Braille input (sort dots to ensure consistent mapping)
        private String normalizeBraille(String braille) {
            char[] chars = braille.toCharArray();
            Arrays.sort(chars);
            return new String(chars);
        }

        // Levenshtein distance for word similarity
        private int levenshteinDistance(String s1, String s2) {
            int[][] dp = new int[s1.length() + 1][s2.length() + 1];
            for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
            for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

            for (int i = 1; i <= s1.length(); i++) {
                for (int j = 1; j <= s2.length(); j++) {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
                }
            }
            return dp[s1.length()][s2.length()];
        }

        // Get suggestions based on Levenshtein distance
        public List<String> getSuggestions(String word, int maxSuggestions) {
            PriorityQueue<WordDistance> pq = new PriorityQueue<>(
                (a, b) -> a.distance == b.distance ? b.feedbackCount - a.feedbackCount : a.distance - b.distance
            );

            for (String dictWord : dictionary) {
                int distance = levenshteinDistance(word, dictWord);
                int feedbackCount = feedback.getOrDefault(dictWord, 0);
                pq.offer(new WordDistance(dictWord, distance, feedbackCount));
            }

            List<String> suggestions = new ArrayList<>();
            while (!pq.isEmpty() && suggestions.size() < maxSuggestions) {
                suggestions.add(pq.poll().word);
            }
            return suggestions;
        }

        // Update feedback for selected suggestion
        public void updateFeedback(String selectedWord) {
            feedback.put(selectedWord, feedback.getOrDefault(selectedWord, 0) + 1);
        }

        // Process input and return result
        public String processInput(List<String> brailleInput) {
            String word = brailleToText(brailleInput);
            if (word.isEmpty()) return "Invalid Braille input";

            if (exists(word)) {
                return "Valid word: " + word;
            } else {
                List<String> suggestions = getSuggestions(word, 3);
                if (suggestions.isEmpty()) return "No suggestions found";
                return "Did you mean: " + String.join(", ", suggestions) + "?";
            }
        }
    }

    // Helper class for word distance in suggestions
    private static class WordDistance {
        String word;
        int distance;
        int feedbackCount;

        WordDistance(String word, int distance, int feedbackCount) {
            this.word = word;
            this.distance = distance;
            this.feedbackCount = feedbackCount;
        }
    }

    // Main method with test case
    public static void main(String[] args) {
        AutocorrectSystem system = new AutocorrectSystem();

        // Test case 1: Valid word "call" (DK -> c, D -> a, WKP -> l, WKP -> l)
        List<String> input1 = Arrays.asList("DK", "D", "WKP", "WKP");
        System.out.println("Input: " + input1);
        System.out.println(system.processInput(input1));

        // Test case 2: Invalid word, expect suggestions (WK -> d, D -> a, WKP -> l, WKP -> l)
        List<String> input2 = Arrays.asList("WK", "D", "WKP", "WKP");
        System.out.println("\nInput: " + input2);
        String result = system.processInput(input2);
        System.out.println(result);

        // Simulate feedback: User selects "call" from suggestions
        if (result.contains("call")) {
            system.updateFeedback("call");
            System.out.println("Feedback updated for 'call'");
        }

        // Test case 3: Invalid Braille input
        List<String> input3 = Arrays.asList("XYZ");
        System.out.println("\nInput: " + input3);
        System.out.println(system.processInput(input3));
    }
}