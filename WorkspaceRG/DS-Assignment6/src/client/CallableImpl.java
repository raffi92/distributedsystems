package client;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class CallableImpl implements Callable<String>, Serializable{

	public int fib = 4;
	private static final long serialVersionUID = 1L;

	@Override
	public String call() throws Exception {
			Thread.sleep(3000);
			int res = Client.Fibonacci(fib);
			return Integer.toString(res);
	}
	
	public int getFib(){
		return fib;
	}
}
