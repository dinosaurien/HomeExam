package kittens.core.io.messages;

public class OpenQuestion implements Question{

	private String question;
	
	public OpenQuestion(String question) {
		this.question = question;
	}
	
	@Override
	public String getMessage() {
		return this.question;
	}
}
