package server.controller;

import java.util.concurrent.CompletableFuture;

import server.model.Dictionary;
import server.model.HangmanGame;
import server.model.Player;

/**
 * @role: maintains the game status and handles the game logic for a specific game process( a game,client pair)
 * @methods provides for the net layer:
 * 		GameController(): constructor
 * 		newGame(): register the new player
 * 		start(): start the game, and returns the message for current game status
 * 		changeUserName(): change the user name, and returns the message for current game status
 * 		executeRound(): execute a round with user input, and returns the message for current game status
 * @author Liming Liu
 */

public class GameController {
	
	private HangmanGame game;
	
	/**
	 * @change: in version 2 change this method to implement single thread event loop model
	 * 			when creating new game controller, need to read dictionary from disk, which could be run in another thread while continue the main thread
	 * 			in this method different game controllers will run read word async, which will avoid concurrency, then initialize new game
	 * @param playerIp
	 */
	public GameController(String playerIp) {
		CompletableFuture.runAsync(()->{
			Dictionary.readWords();
		}).thenRun(()->{
			try {
				Player player=new Player(playerIp,0,"player");
				game=new HangmanGame(player);
			}catch(Exception e) {
				System.out.println("game controller create new game failed");
				e.printStackTrace();
			}
		});
	}
	/**
	 * 
	 * @return
	 */
	public String start() {
		//System.out.println("for test:" +Dictionary.dictionary.size());
		try {
			game.initWord();
		}catch(Exception e) {
			System.out.println("read dictionary not ready");
		}
		return this.game.getStateMessage();
	}
	
	public String changeUserName(String newName) {
		try {
			this.game.changePlayerName(newName);
			return this.game.getStateMessage();
			
		}catch(Exception e) {
			System.out.println("create new name failed");
			e.printStackTrace();
			return null;
		}
	}
	
	public String executeRound(String input) {
		try {
			this.game.oneRound(input);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return this.game.getStateMessage();
	}

	
	
	
}
