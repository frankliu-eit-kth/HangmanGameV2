package server.model;
/*
 * @role: a beam model to represent the hint word
 */
public class HintWord {
	
	private char[] letters;
	private int numLetters;
	
	public HintWord(int numLetters) {
		this.letters=new char[numLetters];
		for (int i=0;i<numLetters;i++) {
			letters[i]='_';
		}
		this.numLetters=numLetters;
	}
	
	public HintWord(char[] letters) {
		this.letters=letters;
		this.numLetters=letters.length;
	}
	
	public char[] getLetters() {
		return letters;
	}
	
	public void setLetters(String input) {
		if(input.length()==numLetters) {
			letters=input.toCharArray();
		}
		else {
			System.out.println("setLetters in hintword does not match");
			return;
		}
	}


	public String toStringWord() {
		StringBuilder sb=new StringBuilder();
		for(char c:letters) {
			sb.append(c);
		}
		return sb.toString();
	}
	
}
