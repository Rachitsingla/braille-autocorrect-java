# braille-autocorrect-java
Java-based autocorrect system that converts QWERTY keyboard input to Braille text with intelligent word suggestions using Levenshtein distance algorithm. Supports real-time typing assistance for visually impaired users.

PROJECT TITLE
-------------
Braille Autocorrect and Suggestion System for QWERTY-based Braille Input

PROBLEM OVERVIEW
----------------
Visually impaired users type Braille using QWERTY keyboards where each Braille dot maps to a specific key (D=Dot1, W=Dot2, Q=Dot3, K=Dot4, O=Dot5, P=Dot6). This system converts simultaneous key presses to Braille characters, then to text, and provides intelligent autocorrect suggestions using Levenshtein distance algorithm when words are not found in the dictionary.

APPROACH / ARCHITECTURE
-----------------------
The system is built using Object-Oriented Programming principles with the following key components:

1. **BrailleAutoCorrect (Main Class)**
   - Static braille mapping initialization
   - Dictionary management with 90+ common English words
   - Main processing pipeline coordination

2. **Core Methods:**
   - `normalizeDotPattern()`: Handles input normalization (removes duplicates, sorts, validates)
   - `convertBrailleToText()`: Maps normalized dot patterns to English letters using predefined Braille alphabet
   - `calculateLevenshteinDistance()`: Implements dynamic programming algorithm for string similarity
   - `checkWordAndSuggest()`: Validates words against dictionary and generates suggestions
   - `processBrailleInput()`: Main pipeline that orchestrates the entire conversion and suggestion process

3. **Result Classes:**
   - `AutoCorrectResult`: Encapsulates word validation results and suggestions
   - `BrailleProcessResult`: Contains complete processing results including original input, converted text, and autocorrect data
   - `WordDistance`: Helper class for sorting suggestions by similarity

ALGORITHMS USED
---------------
1. **Levenshtein Distance Algorithm**: 
   - Dynamic programming approach with O(m*n) time complexity
   - Used for finding closest word matches in dictionary
   - Considers insertions, deletions, and substitutions

2. **Pattern Normalization**:
   - Removes duplicate characters from input patterns
   - Sorts characters alphabetically for consistent lookup
   - Validates against allowed Braille dot characters (D,W,Q,K,O,P)

3. **Dictionary Lookup Optimization**:
   - Uses HashSet for O(1) average case word validation
   - Filters suggestions by reasonable distance threshold (prevents irrelevant suggestions)
   - Sorts suggestions by similarity score

FILE LIST
---------
- BrailleAutoCorrect.java: Main application with complete implementation
- README.txt: This documentation file
- (Optional) words.txt: External dictionary file (currently embedded in code)

TEST CASES
----------

**Test Case 1: Valid Word**
Input: ["DK", "D", "KOQW"]
Parsed: C-A-T  
Output: Valid word: CAT

**Test Case 2: Invalid Word with Suggestions**
Input: ["DK", "D", "DOQW"]
Parsed: C-A-R
Output: Invalid word - CAR, Suggestions: CAT, CAN, etc.

**Test Case 3: Empty Input Edge Case**
Input: []
Parsed: (empty)
Output: Empty input

**Test Case 4: Unknown Dot Pattern**
Input: ["XYZ", "D", "KOQW"] 
Parsed: ?-A-T
Output: Invalid word - AT, Suggestions: based on similarity

**Test Case 5: Duplicate Dots**
Input: ["DDKK", "D", "KOQW"]
Parsed: C-A-T (normalized)
Output: Valid word: CAT

**Test Case 6: Mixed Case and Unordered**
Input: ["kd", "d", "tqowk"]
Parsed: C-A-T (normalized and case-corrected)
Output: Valid word: CAT

COMPILATION AND EXECUTION
-------------------------
Compile: javac BrailleAutoCorrect.java
Run: java BrailleAutoCorrect

PERFORMANCE CHARACTERISTICS
---------------------------
- Dictionary lookup: O(1) average case using HashSet
- Levenshtein distance: O(m*n) where m,n are string lengths
- Pattern normalization: O(k log k) where k is pattern length
- Overall system: Optimized for real-time usage with 100K+ word dictionaries

EDGE CASES HANDLED
------------------
- Empty input lists
- Null input validation
- Unknown/invalid dot patterns (represented as '?')
- Duplicate dots in single pattern
- Mixed case input
- Unordered dot patterns
- No close matches found scenario

SYSTEM REQUIREMENTS
-------------------
- Java 8 or higher
- No external dependencies
- Console-based application
- Cross-platform compatible

FUTURE ENHANCEMENTS
-------------------
- File-based dictionary loading
- Configurable suggestion count
- Multi-word sentence processing
- User-defined dictionary additions
- Phonetic similarity algorithms
