package kittens.core.deck;

public class Card implements Comparable<Card>{
	private String cardName;
	private String cardDescription;
	private int id;
	
	public Card(int id, String cardName, String cardDescription) {
		this.id = id;
		this.cardName=cardName;
		this.cardDescription = cardDescription;
	}
	
	public String toString() {
		return (this.cardName + ", " + this.cardDescription);
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return cardName;
	}
	
	public String getDescription() {
		return cardDescription;
	}

	@Override
	public int compareTo(Card compareCard) {
		int compareInt = ((Card) compareCard).getID();
		return this.getID() - compareInt;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Card){
			Card temp = (Card) o;
			if (this.cardName.equals(temp.cardName) && this.id == temp.id && this.cardDescription.equals(temp.cardDescription)){
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode(){
		return (this.cardName.hashCode() + this.cardDescription.hashCode());
	}
}
