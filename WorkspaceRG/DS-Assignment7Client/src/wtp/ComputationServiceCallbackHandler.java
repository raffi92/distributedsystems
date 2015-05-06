
/**
 * ComputationServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package wtp;

    /**
     *  ComputationServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class ComputationServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public ComputationServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public ComputationServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for multiplication method
            * override this method for handling normal response from multiplication operation
            */
           public void receiveResultmultiplication(
                    wtp.ComputationServiceStub.MultiplicationResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from multiplication operation
           */
            public void receiveErrormultiplication(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for addition method
            * override this method for handling normal response from addition operation
            */
           public void receiveResultaddition(
                    wtp.ComputationServiceStub.AdditionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addition operation
           */
            public void receiveErroraddition(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for substraction method
            * override this method for handling normal response from substraction operation
            */
           public void receiveResultsubstraction(
                    wtp.ComputationServiceStub.SubstractionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from substraction operation
           */
            public void receiveErrorsubstraction(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for factorial method
            * override this method for handling normal response from factorial operation
            */
           public void receiveResultfactorial(
                    wtp.ComputationServiceStub.FactorialResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from factorial operation
           */
            public void receiveErrorfactorial(java.lang.Exception e) {
            }
                


    }
    