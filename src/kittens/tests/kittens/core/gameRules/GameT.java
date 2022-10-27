package kittens.tests.kittens.core.gameRules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;

import org.junit.Test;

import kittens.core.deck.Card;
import kittens.core.deck.DrawPile;
import kittens.core.deck.Hand;
import kittens.core.gameRules.Game;
import kittens.core.gameRules.InitGame;
import kittens.core.players.Player;
import kittens.core.players.Playerbase;

public class GameT {   

    @Test
    public void requirement4Test(){
        Playerbase playerbase = new Playerbase();
        ArrayList<Card> tempHand = new ArrayList<Card>();
        Hand hand = new Hand(tempHand);
        playerbase.addPlayer(new Player(0, false, hand, null, null, null));
        
        Game game = new Game(playerbase);

        ArrayList<Card> tempDPile = new ArrayList<Card>();
        DrawPile drawPile = new DrawPile(tempDPile);
        drawPile.addCard(new Card(6, "See The Future", "description"));
        drawPile.addCard(new Card(6, "See The Future", "description"));
        drawPile.addCard(new Card(6, "See The Future", "description"));
        drawPile.addCard(new Card(6, "See The Future", "description"));
        drawPile.addCard(new Card(6, "See The Future", "description"));
        drawPile.addCard(new Card(6, "See The Future", "description"));
        drawPile.addCard(new Card(6, "See The Future", "description"));
        drawPile.addCard(new Card(6, "See The Future", "description"));
        drawPile.addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));
        drawPile.addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));
        drawPile.addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));
        drawPile.addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));
        drawPile.addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));
        drawPile.addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));
        drawPile.addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));
        drawPile.addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));


        Hand doubleCheckHand = new Hand(new ArrayList<Card>());
        for(int i = 0; i < 7; i++){
            drawPile.dealTopCard(doubleCheckHand);
        }

        //If it was not randomized, there would be 7 attack cards in the hand.
        assertEquals(false, doubleCheckHand.contains("See The Future"));

        game.setDrawPile(drawPile);
        game.dealAllPlayersAppropriateCards(new InitGame(game.getDrawPile()));
        
        //Check that the drawpile is shuffled before dealing cards. If it was not shuffled, it would be 7 attacks in the hand.
        assertNotEquals(false, game.getCurrentPlayer().getHand().contains("See The Future"));
    }

    @Test
    public void requirement7Test(){
        Playerbase playerbase = new Playerbase();
        ArrayList<Card> tempHand = new ArrayList<Card>();
        Hand hand = new Hand(tempHand);
        playerbase.addPlayer(new Player(0, false, hand, null, null, null));

        Playerbase playerbase2 = new Playerbase();
        ArrayList<Card> tempHand2 = new ArrayList<Card>();
        Hand hand2 = new Hand(tempHand2);
        playerbase2.addPlayer(new Player(0, false, hand, null, null, null));
        
        Game game = new Game(playerbase);
        Game game2 = new Game(playerbase2);

        game.initExplodingKittensGame();
        game2.initExplodingKittensGame();

        int count = 0;
        for(int i = 0; i < game.getDrawPile().getSize(); i++){
            if(game.getDrawPile().getCard(i).getName().equals(game2.getDrawPile().getCard(i).getName())){
                count++;
            }
        }
        //If not shuffled properly (or if it was seeded), all cards would have been the same, hence count would be game.getDrawPile().getSize().
        assertNotEquals(game.getDrawPile().getSize(), count);
    }

    @Test
    public void requirement8Test(){
        Playerbase playerbase = new Playerbase();
        playerbase.addPlayer(new Player(0, false, null, null, null, null));
        playerbase.addPlayer(new Player(1, false, null, null, null, null));
        playerbase.addPlayer(new Player(2, false, null, null, null, null));
        playerbase.addPlayer(new Player(3, false, null, null, null, null));
        playerbase.addPlayer(new Player(4, false, null, null, null, null));
       
        Game game = new Game(playerbase);
        
        int player1Started = 0;
        int player2Started = 0;
        int player3Started = 0;
        int player4Started = 0;
        int player5Started = 0;

        for(int i = 0; i <= 20000; i++){
            int startPlayer = game.startAsRandomPlayer(playerbase);
            if(startPlayer == 0){
                player1Started++;
            } else if(startPlayer == 1){
                player2Started++;

            } else if(startPlayer == 2){
                player3Started++;

            } else if(startPlayer == 3){
                player4Started++;

            } else if(startPlayer == 4){
                player5Started++;
            }
        }
        assertNotEquals(0, player1Started);
        assertNotEquals(0, player2Started);
        assertNotEquals(0, player3Started);
        assertNotEquals(0, player4Started);
        assertNotEquals(0, player5Started);
    }

    @Test
    public void requirement9aTest(){
        Playerbase playerbase = new Playerbase();
        ArrayList<Card> tempHand = new ArrayList<Card>();
        Hand hand = new Hand(tempHand);
        playerbase.addPlayer(new Player(0, false, hand, null, null, null));
        
        Game game = new Game(playerbase);
        game.initExplodingKittensGame();
        int handSizeBeforePlaying = game.getCurrentPlayer().getHand().getSize();
        int turnsBeforePlaying = game.getCurrentPlayer().getNrTurnsToTake();
        Card drawCard = game.playPass(); 
        int turnsAfterPlaying = game.getCurrentPlayer().getNrTurnsToTake();
        int handSizeAfterPlaying = game.getCurrentPlayer().getHand().getSize();
        
        assertEquals(handSizeAfterPlaying, handSizeBeforePlaying + 1);  //Assert one card has been added
        assertEquals(turnsBeforePlaying, turnsAfterPlaying + 1);        //Assert one turn was removed
    }

    @Test
    public void requirement9bTest() throws Exception{
        Playerbase playerbase = new Playerbase();
        ArrayList<Card> tempHand = new ArrayList<Card>();
        Hand hand = new Hand(tempHand);
        playerbase.addPlayer(new Player(0, false, hand, null, null, null));

        ArrayList<Card> tempHand2 = new ArrayList<Card>();
        Hand hand2 = new Hand(tempHand2);
        playerbase.addPlayer(new Player(1, false, hand2, null, null, null));
        
        Game game = new Game(playerbase);
        game.initExplodingKittensGame();


        game.getCurrentPlayer().getHand().addCard(new Card(1, "Attack", "Do not draw any cards. Force the next player to take two turns."));

        //Favor
        game.getCurrentPlayer().getHand().addCard( new Card(2, "Favor", "One player must give you a card of their choice."));
        int turnsBeforePlayingFavor = game.getCurrentPlayer().getNrTurnsToTake();
        Player target = playerbase.getPlayer(1);
        String cardChosenToGive = "Defuse";
        game.playFavor(target, cardChosenToGive);
        int turnsAfterPlayingFavor= game.getCurrentPlayer().getNrTurnsToTake();
        assertEquals(turnsBeforePlayingFavor, turnsAfterPlayingFavor);

        //Shuffle
        game.getCurrentPlayer().getHand().addCard(new Card(4, "Shuffle", "Shuffle the draw pile."));
        int turnsBeforePlayingShuffle = game.getCurrentPlayer().getNrTurnsToTake();
        game.playShuffle();
        int turnsAfterPlayingShuffle= game.getCurrentPlayer().getNrTurnsToTake();
        assertEquals(turnsBeforePlayingShuffle, turnsAfterPlayingShuffle);

        //SeeTheFuture
        game.getCurrentPlayer().getHand().addCard(new Card(6, "See The Future", "description"));
        game.getCurrentPlayer().getHand().addCard(new Card(5, "Skip", "End your turn without drawing a card"));
        int turnsBeforePlayingSeeTheFuture = game.getCurrentPlayer().getNrTurnsToTake();
        game.playSeeTheFuture();
        int turnsAfterPlayingSeeTheFuture= game.getCurrentPlayer().getNrTurnsToTake();
        assertEquals(turnsBeforePlayingSeeTheFuture, turnsAfterPlayingSeeTheFuture);

        //make sure there is something to steal
        for(int i = 0; i<=10; i++){
            target.getHand().addCard(new Card(7, "Hairy Potato Cat",
            "This is a cat card and is powerless on its own. Play two Hairy Potato cats as a pair to steal a random card from another player."));
        }
        
        //public void TwoCardCombo(String action, Player target)
        //public void ThreeCardCombo(String action, Player target) throws Exception{
        int turnsBeforeTwoCombo = game.getCurrentPlayer().getNrTurnsToTake();
        game.TwoCardCombo("Hairy Potato Cat", target);
        int turnsAfterTwoCombo = game.getCurrentPlayer().getNrTurnsToTake();
        assertEquals(turnsBeforeTwoCombo, turnsAfterTwoCombo);

        int turnsBeforeThreeCombo = game.getCurrentPlayer().getNrTurnsToTake();
        game.ThreeCardCombo("Hairy Potato Cat", target, "Hairy Potato Cat");
        int turnsAfterThreeCombo = game.getCurrentPlayer().getNrTurnsToTake();
        assertEquals(turnsBeforeThreeCombo, turnsAfterThreeCombo);

    }
}
