package wtp;

import java.math.BigInteger;
import java.rmi.RemoteException;
import org.apache.axis2.AxisFault;

import wtp.ComputationServiceStub.Addition;
import wtp.ComputationServiceStub.AdditionResponse;
import wtp.ComputationServiceStub.Factorial;
import wtp.ComputationServiceStub.FactorialResponse;
import wtp.ComputationServiceStub.Multiplication;
import wtp.ComputationServiceStub.MultiplicationResponse;
import wtp.ComputationServiceStub.Substraction;
import wtp.ComputationServiceStub.SubstractionResponse;

public class ComputationClient {
	
	public static void main (String[] args){
		try {
			BigInteger value = BigInteger.valueOf(8);
			BigInteger value2 = BigInteger.valueOf(5);
			ComputationServiceStub stub = new ComputationServiceStub();
			Addition add = new Addition();
			add.setOne(value);
			add.setTwo(value2);
			AdditionResponse res = stub.addition(add);
			System.out.println("The result of the addition " + value.toString() + " + " + value2.toString() + " = " + res.getAdditionReturn());
			Substraction sub = new Substraction();
			sub.setOne(value);
			sub.setTwo(value2);
			SubstractionResponse resSub = stub.substraction(sub);
			System.out.println("The result of the substraction " + value.toString() + " - " + value2.toString() + " = " + resSub.getSubstractionReturn());
			Multiplication mul = new Multiplication();
			mul.setOne(value);
			mul.setTwo(value2);
			MultiplicationResponse resMul = stub.multiplication(mul);
			System.out.println("The result of the multiplication " + value.toString() + " * " + value2.toString() + " = " + resMul.getMultiplicationReturn());
			Factorial fac = new Factorial();
			fac.setFac(value);
			FactorialResponse resFac = stub.factorial(fac);
			System.out.println("The result of the factorial " + value.toString() + " = " + resFac.getFactorialReturn());
			
		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
