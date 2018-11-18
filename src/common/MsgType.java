package common;

/**
 * Defines all message types that can be sent between client and server
 * used by the network layer on both server and client side to identify the operations needed
 * server side: used by client handler thread
 * client side: used by message listener in ServerConnection
 */
public enum MsgType {
	/*
	 * send by client
	 * user's input to the console
	 * single letter or whole word
	 */
    USER_INPUT,
    /*
     * send by client
     */
    DISCONNECT,
    /*
     * send by client
     * to start a new game
     * before start the server will not create new game controller for this client
     */
    START,
    /*
     * send by client
     * to change user name
     */
    USER,
    /*
     * send by server
     * to tell the player current game status
     */
    SERVERMSG
}
