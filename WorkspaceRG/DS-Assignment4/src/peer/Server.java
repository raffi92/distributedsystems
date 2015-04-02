package peer;

import java.io.IOException;
import java.net.ServerSocket;
/*
 * This class act like a server and listens to new incomming nodes.
 */

public class Server extends Peer implements Runnable{
	private ServerSocket sockert;
	@Override
	public void run() {
		try {
			sockert = new ServerSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}

}
