package kittens.core.deck;

import java.util.ArrayList;

public class DiscardPile{
	ArrayList<Card> discardPile;
	
	public DiscardPile() {
		ArrayList<Card> dPile = new ArrayList<Card>();
		this.discardPile = dPile;
	}

	public ArrayList<Card> getDiscardPile(){
		return discardPile;
	}

	public void addCard(Card addCard) {
		discardPile.add(0, addCard);
	}

    public int getSize() {
		if (discardPile.isEmpty()){
			return 0;
		}
        return discardPile.size();
    }

	public Card getCard(int index) {
		return discardPile.get(index);
	}
}
