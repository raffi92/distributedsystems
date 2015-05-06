package wtp;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import wtp.ConverterServiceStub.CelsiusToFarenheit;
import wtp.ConverterServiceStub.CelsiusToFarenheitResponse;

public class ConverterClient {

        public static void main(String[] args) {
                try {
                        float celsiusValue = 100;
                        ConverterServiceStub stub = new ConverterServiceStub();
                        CelsiusToFarenheit c2f = new CelsiusToFarenheit();
                        c2f.setCelsius(celsiusValue);
                        CelsiusToFarenheitResponse res = stub.celsiusToFarenheit(c2f);
                        System.out.println("Celsius : "+celsiusValue+" = "+"Farenheit : "+res.getCelsiusToFarenheitReturn());
                } catch (AxisFault e) {
                        e.printStackTrace();
                } catch (RemoteException e) {
                        e.printStackTrace();
                }

        }
}