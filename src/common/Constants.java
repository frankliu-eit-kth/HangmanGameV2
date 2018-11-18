package common;

/*
 * defines constants used by both client and server
 * whole message would look like: (length)##(msg type)##(msg body)
 * message body would look like: (game status);(player name);(player score);(player attempts);(hint word)
 *  
 */
public class Constants {
	
    /*
     * used to separate different parts( length, type, body)
     */
    public static final String MSG_DELIMETER = "##";
    
    /*
     * used to separate different fields in the msg body
     */
    public static final String GAME_MESSAGE_DELIMETER=";";
    
    /*
     * define the position of each part in the whole message
     */
    public static final int MSG_LENGTH_INDEX = 0;
   
    public static final int MSG_TYPE_INDEX = 1;
    
    public static final int MSG_BODY_INDEX = 2;
    /**
     * why 8192?
     */
    public static final int MAX_MSG_LENGTH=8192;
    
    
}
