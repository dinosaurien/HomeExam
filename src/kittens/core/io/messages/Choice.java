package kittens.core.io.messages;

public class Choice {

	private String selector;
	private String descriptor;

	public Choice(String selector, String descriptor) {
		this.selector = selector;
		this.descriptor = descriptor;
	}
	
	public String getSelector() {
		return selector;
	}
	
	public String getDescriptor() {
		return descriptor;
	}
}
