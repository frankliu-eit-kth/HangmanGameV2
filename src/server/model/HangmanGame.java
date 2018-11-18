package server.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

import common.Constants;
import common.Message;
import common.MsgType;
/*
 * @role: model for executing the hangman game logic
 * @methods provide for the upper level controller
 * 		HangmanGame(): constructor
 * 		getters and setters
 * 		updateStateMessage()
 * 		initWord(): read a new word and update the game state
 * 		oneRound(): execute one round and return the current game state
 * @ notice:
 * 		game state is the state of win/lost/continue
 * 		game status:states of the whole game
 * 		could be confusing
 * 		
 */
public class HangmanGame {
	private Player player;
	private HintWord hintWord;
	private Word word;
	private int attempts;
	private String stateMessage;
	private String gameStatus;
	
	public HangmanGame(Player player) {
		this.player=player;
	}
	public void changePlayerName(String newName) {
		this.player.setName(newName);
		updateStateMessage();
	}
	public void initWord() {
		String randomWord=this.readRandomWord();
		this.word=new Word(randomWord);		
		int wordLength=word.getNumLetters();
		this.hintWord=new HintWord(wordLength);
		this.attempts=wordLength;
		updateStateMessage();
	}
	
	public void updateStateMessage() {
		StringJoiner sj=new StringJoiner(Constants.GAME_MESSAGE_DELIMETER);
		sj.add(gameStatus);
		sj.add(this.getPlayer().getName());
		sj.add(new String(""+this.getPlayer().getScore()));
		sj.add(new String(""+this.getAttempts()));
		sj.add(new String(this.getHintWord().toStringWord()));
		this.stateMessage=sj.toString();
		
	}
	
	public int oneRound(String input) {
		if(input.length()>1) {
			if(checkWholeWord(input)) {
				hintWord.setLetters(input);
				win();
				return 1;
			}else {
				attempts--;
				if(attempts==0) {
					lose();
					return -1;
				}
				continueGame();
				return 0;
			}
		}else {
			char inputChar=input.toCharArray()[0];
			if(checkLetter(inputChar)) {
				for(char c:hintWord.getLetters()) {
					if(c=='_') {
						continueGame();
						return 0;
					}
				}
				win();
				return 1;
			}else {
				attempts--;
				if(attempts==0) {
					lose();
					return -1;
				}
				continueGame();
				return 0;
			}
		}
	}
	private void continueGame() {
		this.gameStatus="continue";
		updateStateMessage();
	}
	private String readRandomWord() {
		String wordFile="words.txt";
		ArrayList<String> wordList=new ArrayList<String>();
		
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(wordFile))) {
                String s;
                while ((s = br.readLine()) != null) {
                    wordList.add(s);
                }
            }
            int randomNum = ThreadLocalRandom.current().nextInt(0, wordList.size());
            return wordList.get(randomNum);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }	
	}

	
	private boolean checkLetter(char input) {
		boolean flag=false;
		char[] wordLetters=word.getWord().toCharArray();
		for(int i=0;i<word.getNumLetters();i++) {
			if(input==wordLetters[i]) {
				hintWord.getLetters()[i]=input;
				flag=true;
			}
		}
		return flag;
	}
	
	private boolean checkWholeWord(String input) {
		return input.equals(word.getWord());
	}
	
	private void lose() {
		player.minusScore();
		this.gameStatus="lose";
		updateStateMessage();
		initWord();
	}
	private void win(){
		player.addScore();
		this.gameStatus="win";
		updateStateMessage();
		initWord();
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public HintWord getHintWord() {
		return hintWord;
	}

	public void setHintWord(HintWord hintWord) {
		this.hintWord = hintWord;
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
	
	public String getStateMessage() {
		return stateMessage;
	}
	public void setStateMessage(String stateMessage) {
		this.stateMessage = stateMessage;
	}

	@Override
	public String toString() {
		return "HangmanGame [player=" + player + ", hintWord=" + hintWord + ", word=" + word + ", attempts=" + attempts
				+ ", stateMessage=" + stateMessage + ", gameStatus=" + gameStatus + "]";
	}

	
	
	
}
