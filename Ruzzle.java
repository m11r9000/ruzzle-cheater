import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Miran Ali
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * 
 * The Ruzzle class reads the board from a file and check to see if words from a
 * given dictionary (Swedish) exist in the Ruzzle board, represented by a matrix
 * of characters.
 * 
 * When checked for every word in the dictionary, the dictionary words that did
 * exist in the ruzzle board are presented to the user ordered from longest word
 * to shortest.
 * 
 * Note that the cheater does not take Multipliers or Letterscores into consideration.
 * 
 * @author mirana
 * @version 2013-10-24
 */
public class Ruzzle {

	// Constants to keep track of board dimensions
	private final static int BOARDWIDTH = 4;
	private final static int BOARDLENGTH = 4;

	// matrix that stores the letters and represents the board
	char[][] board;
	// matrix used in recursion so letters aren't revisited
	// when creating chains of letters
	boolean[][] visited;

	/*
	 * Constructor of Ruzzle. Initializes matrixes and loads the board into the
	 * board-matrix.
	 */
	public Ruzzle() throws IOException {
		board = new char[BOARDLENGTH][BOARDWIDTH];
		visited = new boolean[BOARDLENGTH][BOARDWIDTH];
		System.err.println("> Starting to read from input file <");
		BufferedReader br = new BufferedReader(new FileReader("./input2"));
		for (int i = 0; i < BOARDLENGTH; i++) {
			board[i] = br.readLine().toCharArray();
		}
		System.err.println("> Board has been loaded <");
		br.close();
	}

	/**
	 * Validates whether the parameter exists in the board.
	 * 
	 * @param word
	 * @return true if word exists in board
	 */
	public boolean contains(String word) {
		char[] wordArray = word.toCharArray();
		// Empty string is trivial
		if (word.length() == 0) {
			return true;
		}
		// Single letter words not allowed in game
		if (word.length() == 1) {
			return false;
		}
		// Find the first letter of the word in the board
		for (int i = 0; i < BOARDLENGTH; i++) {
			for (int j = 0; j < BOARDWIDTH; j++) {
				// only recurse if first letter exists in board and keep going
				// from there
				if (board[i][j] == wordArray[0] && recurse(wordArray, i, j, 1)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Recurse through the board in search of next letter in sought word
	 * 
	 * @param wordArray
	 *            the word in a char[]-representation
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 * @param index
	 *            index of the word
	 * @return true if word exists
	 */
	public boolean recurse(char[] wordArray, int i, int j, int index) {
		// The current position in the matrix has now been visited
		visited[i][j] = true;
		// reached end of word
		if (index == wordArray.length) {
			return true;
		} else {
			// loop over all neighbors
			for (int di = -1; di <= 1; di++) {
				for (int dj = -1; dj <= 1; dj++) {
					// skip cell itself and invalid cells (including visited
					// cells)
					if (!(di == 0 && dj == 0) && isValid(i + di, j + dj)) {
						if (board[i + di][j + dj] == wordArray[index]
								&& recurse(wordArray, i + di, j + dj, index + 1))
							return true;
					}
				}
			}

			return false;
		}
	}

	/**
	 * Checks to see if index values aren't out of bounds and that current
	 * position is not visited.
	 * 
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 * @return true if it is a valid position
	 */
	private boolean isValid(int i, int j) {
		if ((i >= 0 && i < 4) && (j >= 0 && j < 4) && (!visited[i][j]))
			return true;
		return false;
	}

	public static void main(String[] args) throws IOException {
		Ruzzle r = new Ruzzle();
		// Used to store words and getting rid of duplicates
		HashSet<String> set = new HashSet<String>();
		System.err.println("> Check how many swedish words are in the board <");
		Scanner sc = new Scanner(new FileReader("./bigdic"));
		StringBuilder sb = new StringBuilder();
		// Go through all the words in dictionary
		while (sc.hasNextLine()) {
			sb.append(sc.nextLine());
			if (r.contains(sb.toString())) {
				// the word existed in the board, add to set
				set.add(sb.toString());
			}
			// reset the visited-matrix so it doesn't interfere
			// with search of other words
			r.visited = new boolean[BOARDLENGTH][BOARDWIDTH];
			sb.delete(0, sb.length());
		}
		sc.close();
		// Use PriorityQueue to sort the strings based on length of strings
		PriorityQueue<String> pq = new PriorityQueue<String>(set.size(),
				new Comparator<String>() {
					public int compare(String a, String b) {
						if (a.length() > b.length())
							return -1;
						if (a.length() == b.length())
							return 0;
						return 1;
					}
				});
		System.err.println("There are " + set.size()
				+ " swedish words in the board.");
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			pq.offer(it.next());
		}
		while (pq.size() != 0) {
			System.out.println(pq.poll());
		}
	}
}
