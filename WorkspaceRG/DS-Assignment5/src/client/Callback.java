package client;

public class Callback {
	private String question;
	
	public Callback(String question){
		this.question = question;
	}
	
	public void getState(String state){
		System.out.println(state);
	}
	
	public void finish(int number){
		System.out.println("The anwser to your question: " + question + " - is " + number);
	}
}
