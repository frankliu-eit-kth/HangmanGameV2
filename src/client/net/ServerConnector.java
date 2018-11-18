package client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import common.Constants;
import common.Message;
import common.MsgType;
/**
 * @role: handles most work in network layer
 * @author Liming Liu
 *
 */
public class ServerConnector implements Runnable{
	/**
	 * msg buffers
	 */
    private final ByteBuffer msgFromServer = ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);
    private final Queue<ByteBuffer> msgQueueToServer=new ArrayDeque<ByteBuffer>();
    /**
     * use listener to pass value to view layer, therefore avoid invoking view layer
     * use notifier to operate listener
     */
    private CommunicationListener listener;
    private final ViewNotifier viewNotifier=new ViewNotifier();
    /**
     * connection classes and params
     */
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    /**
     * selector manages{key set}  select->iterate->for each do operation
     */
    private Selector selector;
    /**
     * flags used
     */
    private boolean connected;
    private volatile boolean timeToSend = false;
    /**
     * invoked by view layer, initiate this connector working
     * @param host
     * @param port
     * @param listener
     */
    public void startConnection(String host, int port, CommunicationListener listener) {
    	this.listener=listener;
        serverAddress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }
    /**
     * use selector to manage socketChannel-> do communication activities
     */
    @Override
    public void run() {
        try {
            initChannel();
            initSelector();

            while (connected || (!msgQueueToServer.isEmpty())) {
                if (timeToSend) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }

                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        completeConnection(key);
                    } else if (key.isReadable()) {
                        recvFromServer(key);
                    } else if (key.isWritable()) {
                        sendToServer(key);
                    }
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        try {
            completeDisconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    /**
     * initialization
     * @throws IOException
     */
    private void initChannel() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        connected = true;
    }
    private void initSelector() throws IOException {
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }
   
    
    /**
     * register messages into buffer
     */
    private void queueMsgIntoBuffer(MsgType type,String body) {
    	Message msg;
    	if(body==null) {
    		msg=new Message(type);
    	}else {
    		msg=new Message(type,body);
    	}
       
        synchronized (msgQueueToServer) {
        	
        	
            msgQueueToServer.add(ByteBuffer.wrap(msg.getWholeMessage().getBytes()));
        }
        timeToSend = true;
        selector.wakeup();
        
    }
    public void registerSendingStart() {
    	queueMsgIntoBuffer(MsgType.START,null);
    }
    public void registerSendingUsername(String username) {
        queueMsgIntoBuffer(MsgType.USER, username);
    }
    
    public void registerSendingInput(String msg) {
        queueMsgIntoBuffer(MsgType.USER_INPUT, msg);
    }
    
    public void registerDisconnect() throws IOException {
        connected = false;
        queueMsgIntoBuffer(MsgType.DISCONNECT, null);
    }
    /**
     * actual sending activity through channel
     * @param key
     * @throws IOException
     */
    private void sendToServer(SelectionKey key) throws IOException {
        ByteBuffer msg;
        synchronized (msgQueueToServer) {
        	while (( msg=msgQueueToServer.peek())!=null) {
                socketChannel.write(msg);
                if (msg.hasRemaining()) {
                    return;
                }
                msgQueueToServer.remove();
            }
            key.interestOps(SelectionKey.OP_READ);
        }
    }
    /**
     * after server accepts, complete the connection and notify view layer
     * @param key
     * @throws IOException
     */
    private void completeConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                this.viewNotifier.tellViewConnectionDone(remoteAddress,listener);
        } catch (IOException couldNotGetRemAddrUsingDefaultInstead) {
        		this.viewNotifier.tellViewConnectionDone(serverAddress,listener);
        }
    }
    /**
     * after sending disconnect message, complete the disconnection
     * @throws IOException
     */
    private void completeDisconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
        		this.viewNotifier.tellViewDisconnectionDone(listener);
    }
    
   /**
    * receive from server ,extract the message and notify view
    * @param key
    * @throws Exception
    */
    private void recvFromServer(SelectionKey key) throws Exception  {
        int numOfReadBytes = socketChannel.read(msgFromServer);
        if (numOfReadBytes == -1) {
        	throw new IOException("receive empty message from server");
        }
        
        String recvdString = extractMessageFromBuffer();
        Message msg=new Message(recvdString);
        this.viewNotifier.tellViewMsgReceived(msg.getBody(),listener);
    }
    private String extractMessageFromBuffer() {
        msgFromServer.flip();
        byte[] bytes = new byte[msgFromServer.remaining()];
        msgFromServer.get(bytes);
        msgFromServer.clear();
        return new String(bytes);
    }
   

}
