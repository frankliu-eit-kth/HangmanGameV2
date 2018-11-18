package server.model;

import java.util.Arrays;
/*
 * @role: a beam to represend a word
 */
public class Word {
	private String word;
	private char[] letters;
	private int numLetters;
	
	public Word(String word) {
		this.word=word;
		this.letters=word.toCharArray();
		this.numLetters=letters.length;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	
	
	public int getNumLetters() {
		return numLetters;
	}

	public void setNumLetters(int numLetters) {
		this.numLetters = numLetters;
	}

	@Override
	public String toString() {
		return "Word [word=" + word + ", letters=" + Arrays.toString(letters) + ", numLetters=" + numLetters + "]";
	}
	
	
	
}
