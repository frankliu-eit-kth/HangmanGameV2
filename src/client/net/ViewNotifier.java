package client.net;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
/**
 * @role: separated from ServerConnector since it's becoming too large,
 * 		 this class invokes methods in communication listener to notify the view related events and messages
 * @author Liming Liu
 *
 */
public class ViewNotifier {

	public void tellViewConnectionDone(InetSocketAddress connectedAddress,CommunicationListener listener) {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                listener.connected(connectedAddress);
            }
        });
        
    }
	
	 
    public void tellViewDisconnectionDone(CommunicationListener listener) {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                listener.disconnected();
            }
        });
       
        }
   
    public void tellViewMsgReceived(String msg,CommunicationListener listener) {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                listener.gameMsg(msg);
            }
        });
    }

}
