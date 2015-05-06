package wtp;

import java.math.BigInteger;

public class Computation {

	public BigInteger addition(BigInteger one, BigInteger two){
		return one.add(two);
	}
	
	public BigInteger substraction(BigInteger one, BigInteger two){
		return one.subtract(two);
	}
	
	public BigInteger multiplication(BigInteger one, BigInteger two){
		return one.multiply(two);
	}
	
	public BigInteger factorial(BigInteger fac){
		BigInteger fact = BigInteger.valueOf(1);
		for (int i = 1; i <= fac.intValue(); i++) {
			fact = fact.multiply(BigInteger.valueOf(i));
		}
		return fact;
	}
}
