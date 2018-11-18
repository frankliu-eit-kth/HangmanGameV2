package server.model;
/*
 * @role: a beam to represent a player
 * 
 */
public class Player {
	private String ip;
	private int score;
	private String name;
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public Player(String ip, int score,String name) {
		super();
		this.ip = ip;
		this.score = score;
		this.name=name;
	}
	public void minusScore() {
		this.score--;
	}
	public void addScore() {
		this.score++;
	}
	@Override
	public String toString() {
		return "Player [ip=" + ip + ", score=" + score + ", name=" + name + "]";
	}
	
	
}
