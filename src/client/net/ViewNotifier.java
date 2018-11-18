package client.net;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

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
