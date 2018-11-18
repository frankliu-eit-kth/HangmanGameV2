package common;

import java.util.StringJoiner;

public class Message {
	private int length;
	private MsgType type;
	private String body;
	private String wholeMessage;
	//for client side sending message
	public Message(MsgType type) {
		// TODO Auto-generated constructor stub
		this.type=type;
		this.length=type.toString().length();
		this.body=null;
		StringJoiner sj1=new StringJoiner(Constants.MSG_DELIMETER);
		sj1.add(""+length);
		sj1.add(this.type.toString());
		this.wholeMessage=sj1.toString();
	}
	//for client sending input and server sending game message
	public Message(MsgType type, String body) {
		this.type=type;
		this.body=body;
		StringJoiner sj1=new StringJoiner(Constants.MSG_DELIMETER);
		sj1.add(this.type.toString());
		sj1.add(this.body);
		
		this.length=sj1.length();
		StringJoiner sj2=new StringJoiner(Constants.MSG_DELIMETER);
		sj2.add(""+this.length);
		sj2.add(sj1.toString());
		this.wholeMessage=sj2.toString();
	}
	//for receiving
	public Message(String wholeMessage) {
		this.wholeMessage=wholeMessage;
		String[] msgParts=wholeMessage.split(Constants.MSG_DELIMETER);
		this.length=Integer.parseInt(msgParts[Constants.MSG_LENGTH_INDEX]);
		if(this.length>this.wholeMessage.length()-
				msgParts[Constants.MSG_LENGTH_INDEX].length()-Constants.MSG_DELIMETER.length()) {
			System.out.println("received incomplete message");
			System.out.println(wholeMessage);
		}
		String tempType=msgParts[Constants.MSG_TYPE_INDEX];
		switch(tempType) {
		case("USER_INPUT"):
			this.type=MsgType.USER_INPUT;
			break;
		case("USER"):
			this.type=MsgType.USER;
			break;
		case("DISCONNECT"):
			this.type=MsgType.DISCONNECT;
			break;
		case("START"):
			this.type=MsgType.START;
			break;
		case("SERVERMSG"):
			this.type=MsgType.SERVERMSG;
			break;
		default:
			System.out.println("cannnot identify message type");
			System.out.println(wholeMessage);
		}
		if(msgParts.length==3) {
			this.body=msgParts[Constants.MSG_BODY_INDEX];
		}else {
			this.body=null;
		}
	}
	public String getBody() {
		return this.body;
	}
	public String getWholeMessage() {
		return this.wholeMessage;
	}
	public MsgType getType() {
		return this.type;
	}

}
