package kittens.core.gameRules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import kittens.core.deck.Card;
import kittens.core.deck.DiscardPile;
import kittens.core.deck.DrawPile;
import kittens.core.players.Player;
import kittens.core.players.Playerbase;

public class Game {
	private Player currentPlayer;
	private Playerbase playerbase;
	private int playersLeft;
	private boolean isNopedStatus;
	private DrawPile drawPile;
	private DiscardPile discardPile;
	boolean cardsAreBlind = false; //future expansion?

	public Game(Playerbase playerbase) {
		this.playerbase = playerbase;
		this.currentPlayer = playerbase.getPlayer(startAsRandomPlayer(playerbase));
		this.playersLeft = playerbase.getSize();
		this.discardPile = new DiscardPile();
	}

	public void initExplodingKittensGame(){
		ArrayList<Card> drawPileArray = new ArrayList<Card>();
		drawPile = new DrawPile(drawPileArray);
		InitGame gameInit = new InitGame(drawPile);
		initDefuseCardsForPlayers(gameInit);
		drawPile = initDrawPile(gameInit);
		dealAllPlayersAppropriateCards(gameInit);
		addExplodingKittensToDPile(gameInit);
		drawPile.shuffle();
	}

	public void initAndRunExplodingKittensGame() throws Exception {
		initExplodingKittensGame();
		ExplodingKittensGame();
	}

	public void ExplodingKittensGame() throws Exception{
		System.out.println("The game has started.\n");

		do {
			currentPlayer.getHand().sortHand();
			for (Player player : playerbase.getPlayerbase()) {
				if (player.getPlayerID() == currentPlayer.getPlayerID()) {
					player.sendMessage("It is your turn.\n");
					player.sendMessage("Your hand: " + player.getHand().handToString());
				} else {
					player.sendMessage("It is the turn of player " + currentPlayer.getPlayerID());
				}
			}

			for (int i = 0; i < currentPlayer.getNrTurnsToTake(); i++) {
				String response = "";
				while (currentPlayer.getNrTurnsToTake() > 0) {
					int turnsLeft = currentPlayer.getNrTurnsToTake();

					currentPlayer.sendMessage(
							"\nYou have " + turnsLeft + ((turnsLeft > 1) ? " turns" : " turn") + " to take");
					String options = createOptionsString();

					currentPlayer.sendMessage(options);
					response = currentPlayer.getInput(false);
					performAction(currentPlayer, response, options);
				}
			}do {
				currentPlayer.setNrTurnsToTake(1);	//make sure the player will have default 1 turns to take in the next turn
				currentPlayer.setNotAttacked();		//after a player is finished with his turn(s), the player can not still be attacked.
				int nextID = ((currentPlayer.getPlayerID()+1 < playerbase.getSize()?(currentPlayer.getPlayerID())+1: 0));
				currentPlayer = playerbase.getPlayer(nextID);
			} while (currentPlayer.isExploded() && playersLeft > 1);
		} while (playersLeft>1);
		notifyWinner();
	}

	private void notifyWinner() {
		for(Player player : playerbase.getPlayerbase()){
			player.sendMessage("Player " + gameWinner().getPlayerID() + " won the game.");
		}
	}

	private String createOptionsString() {
		Set<Card> handSet = new HashSet<Card>(currentPlayer.getHand().getHandList());

		String options = "The size of the drawpile is: " + drawPile.getSize();
		options += "\nYou have the following options:\n";
		options += "\tPass\n";

		if(!cardsAreBlind){
			for (Card card : handSet) {

				switch (card.getName()) {

					case "Attack":
						
						options += "\tAttack \n";
						break;
					case "Favor":
						options += "\tFavor (Choose target in the next step, pick a card to steal from an opponent)\n";
						break;
					case "Shuffle":
						options += "\tShuffle\n";
						break;
					case "Skip":
						options += "\tSkip\n";
						break;
					case "See The Future":
						options += "\tSee The Future\n";
						break;
					default:
						break;
				}

				int count = checkNumberOfTwosAndThrees(currentPlayer.getHand().getHandList(), card);
				if (count >= 2) {
					options += "\tTwo " + card.getName() + " (Choose target in next step, steal a random card from an opponent)\n";
				}
				if (count >= 3) {
					options += "\tThree " + card.getName() + " (Choose target in next step, pick a card to steal from an opponent)\n";
				}
			}
		}

		return options;
	}

	private void performAction(Player currentPlayer, String action, String options) throws Exception {

		if(options.contains(action)){
			if (options.contains(action.replaceAll("\\d", ""))){
			
				switch(action) {

				case "Pass":
				notifyAllPlayers("\nAction: Player " + currentPlayer.getPlayerID() + " passed.\n");
				Card drawCard = playPass();
				
				if (drawCard.getName().equals("Exploding Kitten")) {  		//Drew exploding kitten?
					drewExpolodingKitten(drawCard);
				} else {
					currentPlayer.sendMessage("You drew: " + drawCard.getName());
				}
				break;

				case "Attack":
				Card attackCard = currentPlayer.getHand().getCard("Attack");
				interactableActionPlayed(currentPlayer, attackCard.getName());		//to get NopeStatus
				playAttack();
				break;
					
				case "Favor":
				if(currentPlayer.getHand().contains("Favor")){
					Card favorCard = currentPlayer.getHand().getCard("Favor");
					boolean validOption = false;
		
					while(!validOption){
						currentPlayer.sendMessage("Which target do you wish to select? Available targets: " + printAvailableTargets(true));
						String input = currentPlayer.getInput(false);
						int targetInt = Integer.parseInt(input);
						
						for(Player player : playerbase.getPlayerbase()){
							if (targetInt == player.getPlayerID() && !player.isExploded() && targetInt != currentPlayer.getPlayerID()){
								validOption = true;
							}
						}
						if(!validOption){
							currentPlayer.sendMessage("Invalid option, try again.");
						} else {
							Player target = playerbase.getPlayer(targetInt);
							interactableActionPlayed(currentPlayer, favorCard.getName(), target);
							if (isNopedStatus == false){
								if(target.getHand().getSize() > 0){
									boolean viableCardToGive = false;
									while(!viableCardToGive){
										target.sendMessage("Your hand: " + target.getHand().handToString());
										target.sendMessage("Choose a card to give to player " + currentPlayer.getPlayerID());
										String response = target.getInput(false);
										if(target.getHand().contains(response)){
											playFavor(target, response);
										} else {
											target.sendMessage("Not a viable option, try again");
										}
									}
								} else if (target.getHand().getSize() == 0){
									currentPlayer.sendMessage("Target had no cards in his hand, you wasted your turn.");
								}
							} else{
								currentPlayer.sendMessage("You were noped.");
							}
						}
					}
				}
				
				break;
				
				case "Shuffle":
				Card shuffleCard = currentPlayer.getHand().getCard("Shuffle");
				interactableActionPlayed(currentPlayer, shuffleCard.getName());
				playShuffle();
				break;
				
				case "Skip":
				Card skipCard = currentPlayer.getHand().getCard("Skip");
				interactableActionPlayed(currentPlayer, skipCard.getName());
				playSkip();
				break;
				
				case "See The Future":
				Card seeTheFutureCard = currentPlayer.getHand().getCard("See The Future");
				interactableActionPlayed(currentPlayer, seeTheFutureCard.getName());
				playSeeTheFuture();
				break;
			
				default:	//not one of these? Check combos
				if(action.contains("Two ") && options.contains(action)){
				
					String[] args = action.split(" ", 2); //"Two" + "Card name" <- Card name can contain several spaces, such as "Rainbow Ralphing Cat"
					Card actionCard = currentPlayer.getHand().getCard(args[1]);
					discardPile.addCard(actionCard);
					discardPile.addCard(actionCard);
					currentPlayer.getHand().removeCard(actionCard);
					currentPlayer.getHand().removeCard(actionCard);
					boolean viableOption = false;

					while(!viableOption){
						currentPlayer.sendMessage("Which target do you wish to select? Available targets: " + printAvailableTargets(false));
						String input = currentPlayer.getInput(false);
						int targetInt = Integer.parseInt(input);
						for(Player player : playerbase.getPlayerbase()){
							if (targetInt == player.getPlayerID() && !player.isExploded() && targetInt != currentPlayer.getPlayerID()){
								viableOption = true;
								break;
							}
						} if(!viableOption){
							currentPlayer.sendMessage("Not a viable target, try again.");
						} else if (viableOption){
							Player target = playerbase.getPlayer(targetInt);
							interactableActionPlayed(currentPlayer, "Two " + actionCard.getName(), target);
							
							if (isNopedStatus == false){
								TwoCardCombo(action, target);
								
							} else {
								currentPlayer.sendMessage("You were noped.");
							}
						}
					}
				} else if(action.contains("Three ") && options.contains(action)){
					String[] args = action.split(" ", 2); //"Three" + "Card name" <- Card name can contain several spaces, such as "Rainbow Ralphing Cat"
					Card actionCard = currentPlayer.getHand().getCard(args[1]);
					discardPile.addCard(actionCard);
					discardPile.addCard(actionCard);
					discardPile.addCard(actionCard);
					currentPlayer.getHand().removeCard(actionCard);
					currentPlayer.getHand().removeCard(actionCard);
					currentPlayer.getHand().removeCard(actionCard);
					boolean viableOption = false;

					while(!viableOption){
						currentPlayer.sendMessage("Which target do you wish to select? Available targets: " + printAvailableTargets(false));
						String input = currentPlayer.getInput(false);
						int targetInt = Integer.parseInt(input);
						for(Player player : playerbase.getPlayerbase()){
							if (targetInt == player.getPlayerID() && !player.isExploded() && targetInt != currentPlayer.getPlayerID()){
								viableOption = true;
								break;
							}
						} if(!viableOption){
							currentPlayer.sendMessage("Not a viable target, try again.");
						} else if (viableOption){
							Player target = playerbase.getPlayer(targetInt);
							interactableActionPlayed(currentPlayer, "Three" + actionCard.getName(), target);
							
							if (isNopedStatus == false){
								currentPlayer.sendMessage("Name a card to steal, for example: [Attack] or [See The Future] or [Defuse]");
								String cardName = currentPlayer.getInput(false);
								ThreeCardCombo(action, target, cardName);	
							} else {
								currentPlayer.sendMessage("You were noped.");
							}
						}
					}
				}
				break;
				}
			}
		}
	}

	public void notifyAllPlayers(String message){
		for(Player player : playerbase.getPlayerbase()){
			player.sendMessage(message);
		}
	}

	public void notifyAllPlayersNotCurrentPlayer(String message){
		for(Player player : playerbase.getPlayerbase()){
			if(player.getPlayerID() != currentPlayer.getPlayerID()){
				player.sendMessage(message);
			}
		}
	}

	public void explode(Player playerToExplode) {
		playersLeft--;
		playerToExplode.setExploded();
		for (Card card : playerToExplode.getHand().getHandList()) {
			discardPile.addCard(card);
		}
		playerToExplode.getHand().clearHand();
		playerToExplode.sendMessage("You exploded");
		for (Player player : playerbase.getPlayerbase()){
			if(player.getPlayerID() != currentPlayer.getPlayerID()){
				player.sendMessage("Player " + currentPlayer.getPlayerID() + " exploded.");
			}
		}
	}

	//for a card, returns how many duplicates of it exist if it is a cat card.
	public int checkNumberOfTwosAndThrees(ArrayList<Card> hand, Card card){
		int count = 0;
		for(Card cardi: hand){
			if(cardi.getName().equals(card.getName())){
				count ++;
			}
		}	
		return count;
	}

	public Player getNextPlayer(Player currentPlayer){
		
		return currentPlayer;
		
	}

	public String printAvailableTargets(boolean mustHaveCards){
		String otherPlayerIDs = "";
		for (Player player : playerbase.getPlayerbase()) {
			if (player.getPlayerID() != currentPlayer.getPlayerID() && mustHaveCards == false) {
				if(player.getHand().getSize() > 0 && !player.isExploded()){
					otherPlayerIDs += (" " + Integer.toString(player.getPlayerID()));
				} else if (player.getHand().getSize() == 0 && !player.isExploded()){
					otherPlayerIDs += (" " + Integer.toString(player.getPlayerID()) + " (no cards)");
				}
				
			} else if (player.getPlayerID() != currentPlayer.getPlayerID() && mustHaveCards == true && !player.isExploded()){
				if(player.getHand().getSize() > 0){
					otherPlayerIDs += " " + Integer.toString(player.getPlayerID()) ;
				}
			}
		}
		return otherPlayerIDs;
	}

	public int checkNrNopes(){
		int i = 0;
		if(discardPile.getSize() > 0){
			for(Card card : discardPile.getDiscardPile()){
				if(card.getName() == "Nope"){
					i++;
				}
			}
		}
		return i;
	}

	public int getNextPlayerID(Player playerCurrent){
		int nextID = ((playerCurrent.getPlayerID()+1 < playerbase.getSize()?(playerCurrent.getPlayerID())+1: 0));
		if(playerbase.getPlayer(nextID).isExploded()){
			getNextPlayerID(playerbase.getPlayer(nextID));
		}
		return nextID;
	}

	//This function handles Nopes if an action occurs with no target such as Attack, Skip, See The Future, etc
	public void interactableActionPlayed(Player currentPlayer, String action) throws Exception{
		ExecutorService threadpool = Executors.newFixedThreadPool(playerbase.getSize());
		isNopedStatus = false; //if a new interactable card is played, reset nopeStatus.
		boolean isNopedAtStart = isNopedStatus;

		for (Player player: playerbase.getPlayerbase()){
			if(player.getPlayerID() != currentPlayer.getPlayerID()){		//can't nope yourself
				player.sendMessage("Action: Player " + currentPlayer.getPlayerID() + " played " + action);
				if(player.getHand().contains("Nope")){	
					player.sendMessage("Press <Enter> to play Nope");
					Runnable task = new Runnable() {
						@Override
						public void run(){
							try{
								String nextMessage = player.getInput(true);
								if(!nextMessage.equals(" ") && player.getHand().contains("Nope")) {
									discardPile.addCard(player.getHand().removeCard("Nope"));
									flipNopeStatus();
									notifyAllPlayers("Player " + player.getPlayerID() + " played Nope");
								}
							} catch (Exception e){
								System.out.println("interactableCardPlayed" + e.getMessage());
							}
						}
					};
					threadpool.execute(task);
				} else if (player.isExploded() == false){
					player.sendMessage("You do not have a nope card. Waiting to see if anyone else Nopes the action");
				}
			} else if (currentPlayer.getPlayerID() == player.getPlayerID()){
				currentPlayer.sendMessage("You played " + action + ", Waiting to see if anyone Nope's you.");
			}
		}
		threadpool.awaitTermination(5500, TimeUnit.MILLISECONDS);

		for (Player notify : playerbase.getPlayerbase()){
				notify.sendMessage("The timewindow to play Nope has passed");

		}
		if (isNopedStatus != isNopedAtStart) {
			for (Player notify : playerbase.getPlayerbase()){
				notify.sendMessage("Play another Nope? (alternate between Nope and Yup)");
			}
			recurseInteractableActionPlayed(currentPlayer, isNopedStatus);
		}
	}

	//This function handles Nopes if an interruptable Action with a target is played. Overloads interactableActionPlayed(...) with no target.
	public void interactableActionPlayed(Player currentPlayer, String action, Player target) throws Exception{
		ExecutorService threadpool = Executors.newFixedThreadPool(playerbase.getSize());
		isNopedStatus = false; //if a new interactable card with a target is played, reset nopeStatus.
		boolean isNopedAtStart = isNopedStatus;

		for (Player player: playerbase.getPlayerbase()){
			if(player.getPlayerID() != currentPlayer.getPlayerID()){		//can't nope yourself
				player.sendMessage("Action: Player " + currentPlayer.getPlayerID() + " played " + action + ", target: Player " + target.getPlayerID());
				if(player.getHand().contains("Nope")){	
					player.sendMessage("Press <Enter> to play Nope");
					Runnable task = new Runnable() {
						@Override
						public void run(){
							try{
								String nextMessage = player.getInput(true);
								if(!nextMessage.equals(" ") && player.getHand().contains("Nope")) {
									discardPile.addCard(player.getHand().removeCard("Nope"));
									flipNopeStatus();
									notifyAllPlayers("Player " + player.getPlayerID() + " played Nope");
								}
							} catch (Exception e){
								System.out.println("interactableCardPlayed" + e.getMessage());
							}
						}
					};
					threadpool.execute(task);
				} else if (player.isExploded() == false) {
					player.sendMessage("You do not have a nope card. Waiting to see if anyone else Nopes the action");
				}
			} else if (currentPlayer.getPlayerID() == player.getPlayerID()){
				currentPlayer.sendMessage("You played " + action + ", Waiting to see if anyone Nope's you.");
			}
		}
		threadpool.awaitTermination(5500, TimeUnit.MILLISECONDS);

		for (Player notify : playerbase.getPlayerbase()){
				notify.sendMessage("The timewindow to play Nope has passed");
		}
		if (isNopedStatus != isNopedAtStart) { 		//if a nope has been played:
			for (Player notify : playerbase.getPlayerbase()){
				if(!notify.isExploded()){
					notify.sendMessage("Play another Nope? (alternate between Nope and Yup)");
				}
			}
			recurseInteractableActionPlayed(currentPlayer, isNopedStatus);
		}
	}

	//Recursive function that is called to handle several nopes in row.
	public void recurseInteractableActionPlayed(Player currentPlayer, boolean nopeStatus) throws Exception{
		ExecutorService threadpool = Executors.newFixedThreadPool(playerbase.getSize());
		boolean isNopedAtStart = isNopedStatus;

		for (Player player : playerbase.getPlayerbase()){
			if(player.getHand().contains("Nope")){	
				player.sendMessage("Press <Enter> to play Nope");
				Runnable task = new Runnable() {
					@Override
					public void run(){
						try{
							String nextMessage = player.getInput(true);
							if(!nextMessage.equals(" ") && player.getHand().contains("Nope")) {
								discardPile.addCard(player.getHand().removeCard("Nope"));
								flipNopeStatus();
								notifyAllPlayers("Player " + player.getPlayerID() + " played Nope");
							}
						} catch (Exception e){
							System.out.println("interactableCardPlayed" + e.getMessage());
						}
					}
				};
				threadpool.execute(task);
			} else if (player.isExploded() == false){
				player.sendMessage("You do not have a nope card. Waiting to see if anyone else Nopes the action");
			}
		}
		threadpool.awaitTermination(5500, TimeUnit.MILLISECONDS);

		for (Player notify : playerbase.getPlayerbase()){
				notify.sendMessage("The timewindow to play Nope has passed");
		}
		if (isNopedAtStart != isNopedStatus) {		//if another nope has been played:
			for (Player notify : playerbase.getPlayerbase()){
				if(!notify.isExploded()){
					notify.sendMessage("Play another Nope? (alternate between Nope and Yup)");
					notify.sendMessage("The status for the nope ended as: " + nopeStatusString(isNopedStatus));
				}
			} 
			recurseInteractableActionPlayed(currentPlayer, nopeStatus);
		}
	}

	public void flipNopeStatus(){
		isNopedStatus = !isNopedStatus;
	}

	public Player gameWinner(){
		for (Player player : playerbase.getPlayerbase()){
			if(!player.isExploded()){
				return player;
			}
		}
		return null;
	}

	public String nopeStatusString(boolean nopeStatus){
		if(nopeStatus == true){
			return ("Nope (it will not happen)");
		} else if (nopeStatus == false){
			return ("Yup (it will happen)");
		}
		return null;
	}

	public int startAsRandomPlayer(Playerbase players){
		Random random = new Random();
		return random.nextInt(players.getSize());
	}

	public Card playPass(){
		currentPlayer.setNrTurnsToTake(currentPlayer.getNrTurnsToTake()-1);
		Card drawCard = drawPile.dealTopCard(currentPlayer.getHand());
		return drawCard;
	}

	public void playAttack() throws Exception{
		if(currentPlayer.getHand().contains("Attack")){
			Card attackCard = currentPlayer.getHand().getCard("Attack");
			discardPile.addCard(attackCard);
			currentPlayer.getHand().removeCard(attackCard);
			if (isNopedStatus == false){
				Player target = playerbase.getPlayer(getNextPlayerID(currentPlayer));
				if (currentPlayer.getAttackedStatus() == true){
					target.setAttacked();
					target.setNrTurnsToTake(currentPlayer.getNrTurnsToTake()+2);
					currentPlayer.setNrTurnsToTake(0);
					
				} else if (!currentPlayer.getAttackedStatus()){
					target.setAttacked();
					target.setNrTurnsToTake(2);
					currentPlayer.setNotAttacked();
					currentPlayer.setNrTurnsToTake(0);
				}
			}
			else{
				
			}
		}
	}

	public void drewExpolodingKitten(Card drawCard){
		currentPlayer.sendMessage("You drew " + drawCard.getName());
		if(currentPlayer.getHand().contains("Defuse")) {
			discardPile.addCard(currentPlayer.getHand().removeCard("Defuse"));
			currentPlayer.getHand().removeCard("Exploding Kitten");
			
			while(true){
				if(drawPile.getSize() > 0){
					currentPlayer.sendMessage("You defused the kitten. Where in the deck do you wish to place the Exploding kitten? [0.." + (drawPile.getSize())+ "] (0 is the top of the drawpile)");
					int indexKitten = Integer.parseInt(currentPlayer.getInput(false));
					if (indexKitten > drawPile.getSize()){
						continue;
					}
					else if (indexKitten < 0){
						continue;
					} else {
						drawPile.addCard(drawCard, indexKitten);	//place exploding kitten
						break;
					}
				} else if (drawPile.getSize() == 0){
					currentPlayer.sendMessage("You defused an exploding kitten. It was the last card in the drawpile. It has been inserterted back into the drawpile.");
					drawPile.addCard(drawCard);
					break;
				}
			}
		} else {		//No defuse card = Player lost the game
			explode(currentPlayer);
		}
	}

	public void playFavor(Player target, String cardChosenToGive) throws Exception{
		Card favorCard = currentPlayer.getHand().getCard("Favor");
		discardPile.addCard(favorCard);
		currentPlayer.getHand().removeCard(favorCard);

		Card cardToGive = target.getHand().getCard(cardChosenToGive);
		target.getHand().removeCard(cardToGive);
		currentPlayer.getHand().addCard(cardToGive);
		currentPlayer.sendMessage("Player " + target.getPlayerID() + " gave you the card: " + cardToGive.getName());
	}

	public void playShuffle() throws Exception{
		if(currentPlayer.getHand().contains("Shuffle")){
			Card shuffleCard = currentPlayer.getHand().getCard("Shuffle");
			discardPile.addCard(shuffleCard);
			currentPlayer.getHand().removeCard(shuffleCard);
			if (isNopedStatus == false){
				drawPile.shuffle();
			} else {
				currentPlayer.sendMessage("You were noped.");
			}
		}
	}

	public void playSkip() throws Exception{
		if(currentPlayer.getHand().contains("Skip")){
			Card skipCard = currentPlayer.getHand().getCard("Skip");
			discardPile.addCard(skipCard);
			currentPlayer.getHand().removeCard(skipCard);

			if (isNopedStatus == false){
				currentPlayer.setNrTurnsToTake(currentPlayer.getNrTurnsToTake() - 1);		//this turn is "over" without anything else happening.
			} else {
				currentPlayer.sendMessage("You were noped.");
			}
		}
	}

	public void playSeeTheFuture() throws Exception{
		if(currentPlayer.getHand().contains("See The Future")){
			Card seeTheFutureCard = currentPlayer.getHand().getCard("See The Future");
			discardPile.addCard(seeTheFutureCard);
			currentPlayer.getHand().removeCard(seeTheFutureCard);
			if (isNopedStatus == false){
				currentPlayer.sendMessage("The top 3 cards are (top -> 3rd from top):\n" + drawPile.getCard(0).getName() + ", " + drawPile.getCard(1).getName() + ", " + drawPile.getCard(2).getName());
			} else {
				currentPlayer.sendMessage("You were noped.");
			}
		}
	}

	public void TwoCardCombo(String action, Player target) throws Exception{
	//Two?
		Random rnd = new Random();
		if(target.getHand().getSize() > 0){
			int indexToSteal = rnd.nextInt(target.getHand().getSize());
			Card stolenCard = target.getHand().getCard(indexToSteal);
			target.getHand().removeCard(stolenCard);
			currentPlayer.getHand().addCard(stolenCard);
			currentPlayer.sendMessage("You received " + stolenCard.getName() + " from player " + target.getPlayerID());
		} else if(target.getHand().getSize() == 0) {
			currentPlayer.sendMessage("Target had no cards in his hand, you wasted your turn.");
		}
	}

	public void ThreeCardCombo(String action, Player target, String cardName) throws Exception{
		if(target.getHand().getSize() > 0 && target.getHand().contains(cardName)){
			Card stolenCard = target.getHand().getCard(cardName);
			target.getHand().removeCard(stolenCard);
			currentPlayer.getHand().addCard(stolenCard);
			currentPlayer.sendMessage("You received " + stolenCard.getName() + " from player " + target.getPlayerID());
		} else {
			currentPlayer.sendMessage("Target did not have this card in its hand, or the card you specified does not exist, you wasted your turn.");
		}
	}

	public DrawPile initDrawPile(InitGame gameInitialization){
		DrawPile localDrawPile = new DrawPile(new ArrayList<Card>());
		localDrawPile = gameInitialization.initCardsAndDrawPile(playerbase);
		return localDrawPile;
	}

	public void initDefuseCardsForPlayers(InitGame gameInitialization){
		gameInitialization.giveAllPlayersDefuse(playerbase, drawPile);
	}

	public void dealAllPlayersAppropriateCards(InitGame gameInitialization){
		drawPile.shuffle();		//shuffle before drawing start hands for players
		gameInitialization.dealAllPlayers7Cards(playerbase, drawPile);			//give all players 7 cards
	}

	public void addExplodingKittensToDPile(InitGame gameInitialization){
		gameInitialization.initExplodingKittenCard(playerbase, drawPile);		//adds appropriate number of exploding kittens to the drawpile		
	}

	public Player getCurrentPlayer(){
		return currentPlayer;
	}

	public DrawPile getDrawPile(){
		return drawPile;
	}

	public void setDrawPile(DrawPile dPile){
		drawPile = dPile;
	}
}