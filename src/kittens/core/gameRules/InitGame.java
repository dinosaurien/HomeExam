package kittens.core.gameRules;

import kittens.core.deck.Card;
import kittens.core.deck.DrawPile;
import kittens.core.players.Player;
import kittens.core.players.Playerbase;

public class InitGame {

	private DrawPile drawPile;

	public InitGame(DrawPile drawPile) {
		this.drawPile = drawPile;
	}

	public DrawPile initCardsAndDrawPile(Playerbase players) {

		if (players.getSize() < 5) {	//2-4 players: two defuse cards
			for (int i = 0; i < 2; i++) {
				drawPile.addCard(new Card(0, "Defuse", "Privately view the top three cards of the draw pile."));
			}
		} else if (players.getSize() == 5){	//5 players: one defuse card
			drawPile.addCard(new Card(0, "Defuse", "Privately view the top three cards of the draw pile."));
		}

		for (int i = 0; i < 4; i++) {
			drawPile.addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));
		}
		for (int i = 0; i < 4; i++) {
			drawPile.addCard(new Card(2, "Favor", "One player must give you a card of their choice."));
		}
		for (int i = 0; i < 5; i++) {
			drawPile.addCard(new Card(3, "Nope", "Stop the action of another player. You can play this at any time."));
		}
		for (int i = 0; i < 4; i++) {
			drawPile.addCard(new Card(4, "Shuffle", "Shuffle the draw pile."));
		}
		for (int i = 0; i < 4; i++) {
			drawPile.addCard(new Card(5, "Skip", "End your turn without drawing a card"));
		}
		for (int i = 0; i < 5; i++) {
			drawPile.addCard(new Card(6, "See The Future", "Privately view the top three cards of the drawpile"));
		}
		for (int i = 0; i < 4; i++) {
			drawPile.addCard(new Card(7, "Hairy Potato Cat",
					"This is a cat card and is powerless on its own. Play two Hairy Potato cats as a pair to steal a random card from another player."));
		}
		for (int i = 0; i < 4; i++) {
			drawPile.addCard(new Card(8, "Cattermelon",
					"This is a cat card and is powerless on its own. Play two Cattermelons as a pair to steal a random card from another player."));
		}
		for (int i = 0; i < 4; i++) {
			drawPile.addCard(new Card(9, "Rainbow Ralphing Cat",
					"This is a cat card and is powerless on its own. Play two Rainbow Ralphing cats as a pair to steal a random card from another player."));
		}
		
		for (int i = 0; i < 4; i++) {
			drawPile.addCard(new Card(10, "Taco Cat",
					"This is a cat card and is powerless on its own.Play two Taco cats as a pair to steal a random card from another player."));
		}
		for (int i = 0; i < 4; i++) {
			drawPile.addCard(new Card(11, "Overweight Bikini Cat",
					"This is a cat card and is powerless on its own. Play two Overweight Bikini cats as a pair\n to steal a random card from another player."));
		}

		return drawPile;
	}

	public void giveAllPlayersDefuse(Playerbase players, DrawPile dPile){
		for (Player player : players.getPlayerbase()){
			player.getHand().addCard(new Card(0, "Defuse", "Privately view the top three cards of the draw pile."));
		}
	}

	public void dealAllPlayers7Cards(Playerbase players, DrawPile dPile){
		for (Player player : players.getPlayerbase()) {
			for(int i=0; i<7; i++) { 
				dPile.dealTopCard(player.getHand());
			}
		}
	}

	public void initExplodingKittenCard(Playerbase players, DrawPile dPile){
		for (int i=0; i < players.getSize() - 1; i++) {		//One less exploding kittens than there are players
			dPile.addCard(new Card(12, "Exploding Kitten", "Show this card immediately"));
		}
	}
}