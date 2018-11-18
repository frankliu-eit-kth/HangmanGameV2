package server.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import common.Constants;
import common.Message;
import common.MessageException;
import common.MsgType;
import server.controller.GameController;



public class ClientHandler {
	private final SocketChannel clientChannel;
	private final ByteBuffer receivingBuffer = ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);
	private ByteBuffer sendingBuffer=ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);
	private GameController controller;
	
	public ClientHandler(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
       
        try {
        	this.controller=new GameController(clientChannel.getRemoteAddress().toString());
        }catch (Exception e) {
        	System.out.println("creating new game controller failed");
        }
    }
	
	public void handleMsg() throws IOException {
        receivingBuffer.clear();
        int numOfReadBytes;
        numOfReadBytes = clientChannel.read(receivingBuffer);
        if (numOfReadBytes == -1) {
            throw new IOException("Client has closed connection.");
        }
        String recvdString = extractMessageFromBuffer();
        Message msg=new Message(recvdString);
        try {
	        switch (msg.getType()) {
	        case USER:
	            String newName = msg.getBody();
	            registerMsgToSend(this.controller.changeUserName(newName));
	            break;
	        case START:
	        	registerMsgToSend(this.controller.start());
	        	break;
	        case USER_INPUT:
	        	String input=msg.getBody();
	        	registerMsgToSend(this.controller.executeRound(input));
	            break;
	        case DISCONNECT:
	            closeClientChannel();
	            break;
	        default:
	            throw new MessageException("Received corrupt message: " + recvdString);
	        }
        }catch (IOException ioe) {
	        closeClientChannel();
	        throw new MessageException(ioe);
	    }
    }
	private void registerMsgToSend(String msgBody) throws IOException {
		Message msg=new Message(MsgType.SERVERMSG,msgBody);
		this.sendingBuffer.clear();
		this.sendingBuffer=ByteBuffer.wrap(msg.getWholeMessage().getBytes());
    }
	public void sendBufferedMsg() throws IOException {
		//need testing
		System.out.println("test clear() method"+sendingBuffer.toString());
		sendingBuffer.clear();
		System.out.println("test clear() method"+sendingBuffer.toString());
        clientChannel.write(sendingBuffer);
        System.out.println("test clear() method"+sendingBuffer.toString());
        if (sendingBuffer.hasRemaining()) {
            throw new MessageException("Could not send message completely");
        }
        
    }
	public void closeClientChannel() throws IOException {
        clientChannel.close();
    }

	
	private String extractMessageFromBuffer() {
        receivingBuffer.flip();
        byte[] bytes = new byte[receivingBuffer.remaining()];
        receivingBuffer.get(bytes);
        return new String(bytes);
    }

}
