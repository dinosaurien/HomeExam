package kittens.core.io.messages;

import java.util.ArrayList;

public class QuestionWithChoice implements Question{
	
	private ArrayList<Choice> choices;
	private String question;

	public QuestionWithChoice(String question, ArrayList<Choice> choices) {
		this.question=question;
		this.choices = choices;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(question);
		
		for(Choice choice : choices) {
			sb.append("\n" + choice.getSelector() + " : " + choice.getDescriptor());
		}
		return sb.toString();
	}
	
}
