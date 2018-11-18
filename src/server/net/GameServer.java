package server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import common.MessageException;
/**
 * uses a selector to operate client channels
 * 
 * @ channels:
 * 		1 server socket channel: for listening
 * 		multiple client socket channels
 * @ multiple client handlers:  stay in the same thread as this one. But when creating new game controller which needs
 * 		to read word file, the game controller will run a new non blocking thread( runs beside the main thread without blocking)
 * 		 in a thread pool by calling completable future( see in game controller)
 * @ selector:
 * 		life cycle: create-> 
 * 					select key( the keys willl be selected) ->
 * 					iterate over keys-> 
 * 					get the handeler from key attachment->
 * 					do something using handler(if it's accepting new client then need to create new thread in thread pool to read dictionary
 * 												else, still in the same thead as the server)->
 * 					wait for new keys to come( or some method calls selector.wakeup() then will immediately start another round of selecting)
 * @author Liming Liu
 *
 */
public class GameServer {
	 private static final int LINGER_TIME = 5000;
	 private int portNo = 8080;
	 private Selector selector;
	 private ServerSocketChannel listeningSocketChannel;
	
	 private void serve() {
	        try {
	            initSelector();
	            initListeningSocketChannel();
	            while (true) {
	                
	                selector.select();
	                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
	                while (iterator.hasNext()) {
	                    SelectionKey key = iterator.next();
	                    iterator.remove();
	                    if (!key.isValid()) {
	                        continue;
	                    }
	                    if (key.isAcceptable()) {
	                        startNewHandler(key);
	                    } else if (key.isReadable()) {
	                        recvByClientHandler(key);
	                    } else if (key.isWritable()) {
	                        sendByClientHandler(key);
	                    }
	                }
	            }
	        } catch (Exception e) {
	        	e.printStackTrace();
	            System.err.println("Server failure.");
	        }
	    }
	 /**
	  * the client handler attached to the key will immediately send messages in buffer
	  * @param key
	  * @throws IOException
	  */
	 private void sendByClientHandler(SelectionKey key) throws IOException {
	        ClientHandler clientHandler = (ClientHandler) key.attachment();
	        try {
	            	clientHandler.sendBufferedMsg();
	            	key.interestOps(SelectionKey.OP_READ);
	        } catch (MessageException couldNotSendAllMessages) {
	        } catch (IOException clientHasClosedConnection) {
	            removeClientHandler(key);
	        }
	    }
	 /**
	  * the client handler will immediately read from buffer and handle the meesage received
	  * @param key
	  * @throws IOException
	  */
	 private void recvByClientHandler(SelectionKey key) throws IOException {
	        ClientHandler handler = (ClientHandler)key.attachment();
	        try {
	            handler.handleMsg();
	            key.interestOps(SelectionKey.OP_WRITE);
	        } catch (IOException clientHasClosedConnection) {
	            removeClientHandler(key);
	        }
	    }
	 /**
	  * close channel->cancel key
	  * will not send disconnect message as client does
	  * @param clientKey
	  * @throws IOException
	  */
	 private void removeClientHandler(SelectionKey clientKey) throws IOException {
	        ClientHandler clientHandler = (ClientHandler) clientKey.attachment();
	        clientHandler.closeClientChannel();
	        clientKey.cancel();
	    }
	 /**
	  * start a new handler->register the channel to the selector with {key, interested operation, attachment(which is the handler)}
	  * @param key
	  * @throws IOException
	  */
	 private void startNewHandler(SelectionKey key) throws IOException {
	        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
	        SocketChannel clientChannel = serverSocketChannel.accept();
	        clientChannel.configureBlocking(false);
	        ClientHandler handler = new ClientHandler(clientChannel);
	        clientChannel.register(selector, SelectionKey.OP_READ, handler);
	        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME); //Close will probably
	        
	    }
	 private void initSelector() throws IOException {
	        selector = Selector.open();
	    }
	 
	 private void initListeningSocketChannel() throws IOException {
	        listeningSocketChannel = ServerSocketChannel.open();
	        listeningSocketChannel.configureBlocking(false);
	        listeningSocketChannel.bind(new InetSocketAddress(portNo));
	        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	    }

	 
	 public static void main(String[] args) {
	        GameServer server = new GameServer();
	        server.parseArguments(args);
	        server.serve();
	    }

	    private void parseArguments(String[] arguments) {
	        if (arguments.length > 0) {
	            try {
	                portNo = Integer.parseInt(arguments[1]);
	            } catch (NumberFormatException e) {
	                System.err.println("Invalid port number, using default.");
	            }
	        }
	    }
}
