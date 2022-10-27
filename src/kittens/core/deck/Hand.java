package kittens.core.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Hand{
	
	private ArrayList<Card> hand;

	public Hand(ArrayList<Card> tempHand) {
		this.hand = tempHand;
	}
	
	public void giveChosenIndexFromHand(Hand receiver, int index) {
		receiver.addCard(hand.get(index));
		hand.remove(index);
	}

	public void sortHand() {
		Collections.sort(hand, Comparator.comparingLong(Card::getID));
	}
	
	public boolean contains(String name) {
		for (Card card : hand) {
			if(card.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public Card getCard(String name){
		for (Card card : hand) {
			if(card.getName().equals(name)) {
				Card thisCard = card;
				return thisCard;
			}
		}
		return null;
	}

	//used to steal a random card
	public Card getCard(int index){
		return hand.get(index);
	}

	public Card removeCard(String name) {
		for (Card card : hand) {
			if(card.getName().equals(name)) {
				hand.remove(card);
				return card;
			}
		}
		return null;
	}

	public void clearHand(){
		hand.clear();
	}
	
	public Card removeCard(Card cardToRemove) {
		for (Card card : hand) {
			if(card.getName().equals(cardToRemove.getName())) {
				hand.remove(card);
				return card;
			}
		}
		return null;
	}
	
	public ArrayList<Card> getHandList() {
		return hand;
	}

	public void addCard(Card card) {
		hand.add(card);
	}

	public String handToString() {
		String toPrint = "";
		
		for (Card card : hand) {
			toPrint += (card.getName()+ ", ");
		}
		
		return toPrint;
	}

	public int getSize() {
		return hand.size();
	}
}
