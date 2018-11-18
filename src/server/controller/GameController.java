package server.controller;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import server.model.HangmanGame;
import server.model.Player;

/*
 * @role: maintains the game status and handles the game logic for a specific game process( a game,client pair)
 * @methods provides for the net layer:
 * 		GameController(): constructor
 * 		newGame(): register the new player
 * 		start(): start the game, and returns the message for current game status
 * 		changeUserName(): change the user name, and returns the message for current game status
 * 		executeRound(): execute a round with user input, and returns the message for current game status
 */

public class GameController {
	
	private HangmanGame game;
	
	
	public GameController(String playerIp) {
		try {
			Player player=new Player(playerIp,0,"player");
			game=new HangmanGame(player);
			}catch(Exception e) {
				System.out.println("game controller create new game failed");
				e.printStackTrace();
			}
	}
	
	public String start() {
		/**
		Executor readFileThreadPool=ForkJoinPool.commonPool();
		readFileThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				
				game.initWord();
				
			}
		});*/
		game.initWord();
		System.out.println(game.toString());
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
