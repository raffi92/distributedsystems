package clientEx02;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class CallableImpl implements Callable<String>, Serializable{

	private int fib = 10;
	private static final long serialVersionUID = 1L;
	
	@Override
	public String call() throws Exception {
		Thread.sleep(5000);
		int res = Client.Fibonacci(fib);
		return Integer.toString(res);
	}
	
	public int getFib(){
		return fib;
	}
	
	public void setFib(int nr){
		fib = nr;
	}
}
