package kittens.core.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DrawPile{

	private ArrayList<Card> drawPile;
	
	public DrawPile(ArrayList<Card> drawPile) {
		this.drawPile = drawPile;
	}
	
	public Card dealTopCard(Hand toHand) {
		Card card = getTopCard();
		giveFromDeck(toHand, card);
		drawPile.remove(card);
		return card;
	}

	public Card dealBottomCard(Hand toHand) {
		Card card = getBottomCard();
		this.giveFromDeck(toHand, card);
		drawPile.remove(card);
		return card;
	}
	
	public void remove(Card cardToRemove) {
		drawPile.remove(cardToRemove);
	}

	public void giveFromDeck(Hand toHand, Card card) {
		toHand.addCard(card);
	}

	public Card getCard(int index){
		return drawPile.get(index);
	}

	public Card getTopCard() {
		return drawPile.get(0);
	}
	
	private Card getBottomCard(){
		return drawPile.get(drawPile.size()-1);
	}
	
	public void addCard(Card card) {
		drawPile.add(0, card);
	}
	
	public void addCard(Card card, int index) {
		drawPile.add(index, card);
	}
	
	public void shuffle() {
		Collections.shuffle(drawPile, new Random());
	}

	public ArrayList<Card> getDrawPileList() {
		return drawPile;
	}

	public int getSize(){
		return drawPile.size();
	}
}
