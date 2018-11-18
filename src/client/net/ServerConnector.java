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
 * @author m1339
 *
 */
public class ServerConnector implements Runnable{
	
	//private static final String FATAL_COMMUNICATION_MSG = "Lost connection.";
    //private static final String FATAL_DISCONNECT_MSG = "Could not disconnect, will leave ungracefully.";
	
    private final ByteBuffer msgFromServer = ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);
    private final Queue<ByteBuffer> msgQueueToServer=new ArrayDeque<ByteBuffer>();
    private CommunicationListener listener;
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private Selector selector;
    private boolean connected;
    private volatile boolean timeToSend = false;
    private final ViewNotifier viewNotifier=new ViewNotifier();
  //invoked by view layer
    public void startConnection(String host, int port, CommunicationListener listener) {
    	this.listener=listener;
        serverAddress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }
    
    
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
    //initiation
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
   
    
    //sending message
    public void registerSendingStart() {
    	resigerSendingMsg(MsgType.START,null);
    }
    public void registerSendingUsername(String username) {
        resigerSendingMsg(MsgType.USER, username);
    }
    
    public void registerSendingInput(String msg) {
        resigerSendingMsg(MsgType.USER_INPUT, msg);
    }
    
    public void registerDisconnect() throws IOException {
        connected = false;
        resigerSendingMsg(MsgType.DISCONNECT, null);
    }
   
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
    
    private void completeDisconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
        		this.viewNotifier.tellViewDisconnectionDone(listener);
    }
   
    
    private void resigerSendingMsg(MsgType type,String body) {
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
   
    private void recvFromServer(SelectionKey key) throws Exception  {
        int numOfReadBytes = socketChannel.read(msgFromServer);
        if (numOfReadBytes == -1) {
        	throw new IOException("receive empty message from server");
        }
        
        String recvdString = extractMessageFromBuffer();
        Message msg=new Message(recvdString);
        //for test
        System.out.println("server conncetion for test:"+ msg.getBody());
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
