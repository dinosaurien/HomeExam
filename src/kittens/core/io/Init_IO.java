package kittens.core.io;

import java.io.IOException;
import java.util.Scanner;
import kittens.core.io.messages.Question;

public class Init_IO {
	
	Scanner scanner = new Scanner(System.in);
	
	public Init_IO() {

	}
	
	public String GetInput() throws IOException {
		String input = scanner.next();
		return input;
	}
	
	public String getInput() {
		String input = scanner.next();
		return input;
	}

	public String askQuestion(Question question) {
		String message = question.getMessage();
		System.out.println(message);
		return getInput();
	}
	
	public boolean verifyAnswer(Question question) {
		return false;
	}
}
