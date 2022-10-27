package kittens.tests.kittens.core.gameRules;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import kittens.core.deck.Card;
import kittens.core.deck.DrawPile;
import kittens.core.deck.Hand;
import kittens.core.gameRules.InitGame;
import kittens.core.players.Player;
import kittens.core.players.Playerbase;

public class InitGameT {    
    
    @Test
    public void requirement2Test(){
        Playerbase playerbase = new Playerbase();
        
        //populate Playerbase
        for(int i = 1; i <= 5; i++){
            ArrayList<Card> tempHand = new ArrayList<Card>();
            Hand hand = new Hand(tempHand);

            playerbase.addPlayer(new Player(i, false, hand, null, null, null));
            if(i >= 2 && i < 5){
                DrawPile drawPile = new DrawPile(new ArrayList<Card>());
                InitGame gameInitialization = new InitGame(drawPile);
                drawPile = gameInitialization.initCardsAndDrawPile(playerbase);
                drawPile.shuffle();		//shuffle before drawing start hands for players
                gameInitialization.giveAllPlayersDefuse(playerbase, drawPile);			//give all players 7 cards
                
                for(Player player : playerbase.getPlayerbase()){
                    int count = 0;
                    for(Card card : player.getHand().getHandList()){
                        if (card.getName().equals("Defuse")){
                            count ++;
                        }
                    }
                    player.getHand().clearHand();
                    assertEquals(1, count);
                }
            }
        }
    }

    @Test
    public void requirement3Test(){
        Playerbase playerbase = new Playerbase();

        for(int i = 1; i <= 5; i++){
            ArrayList<Card> tempHand = new ArrayList<Card>();
            Hand hand = new Hand(tempHand);
            playerbase.addPlayer(new Player(i, false, hand, null, null, null));
            DrawPile drawPile = new DrawPile(new ArrayList<Card>());
            InitGame gameInitialization = new InitGame(drawPile);
            drawPile = gameInitialization.initCardsAndDrawPile(playerbase);
            int defuseCount = 0;
            int attackCount = 0;
            int favorCount = 0;
            int nopeCount = 0;
            int shuffleCount = 0;
            int skipCount = 0;
            int seeTheFutureCount = 0;
            int hairyPotatoCatCount = 0;
            int cattermelonCount = 0;
            int rainbowRalphingCatCount = 0;
            int tacoCatCount = 0;
            int overweightBikiniCatCount = 0;

            for (Card card : drawPile.getDrawPileList()) {
                switch (card.getName()) {
                    case "Defuse":
                    defuseCount += 1;
                    break;

                    case "Attack":
                    attackCount += 1;
                    break;

                    case "Favor":
                    favorCount += 1;
                    break;

                    case "Nope":
                    nopeCount += 1;
                    break;

                    case "Shuffle":
                    shuffleCount += 1;
                    break;

                    case "Skip":
                    skipCount += 1;
                    break;

                    case "See The Future":
                    seeTheFutureCount += 1;
                    break;

                    case "Hairy Potato Cat":
                    hairyPotatoCatCount += 1;
                    break;

                    case "Cattermelon":
                    cattermelonCount += 1;
                    break;

                    case "Rainbow Ralphing Cat":
                    rainbowRalphingCatCount += 1;
                    break;

                    case "Taco Cat":
                    tacoCatCount += 1;
                    break;

                    case "Overweight Bikini Cat":
                    overweightBikiniCatCount += 1;
                    break;

                    default:
                    break;
                }
            }
            if(i > 2 && i < 5){
                assertEquals(2, defuseCount);
                assertEquals(4, attackCount);
                assertEquals(4, favorCount);
                assertEquals(5, nopeCount);
                assertEquals(4, shuffleCount);
                assertEquals(4, skipCount);
                assertEquals(5, seeTheFutureCount);
                assertEquals(4, hairyPotatoCatCount);
                assertEquals(4, cattermelonCount);
                assertEquals(4, rainbowRalphingCatCount);
                assertEquals(4, tacoCatCount);
                assertEquals(4, overweightBikiniCatCount);
            } else if (i == 5){
                assertEquals(1, defuseCount);
                assertEquals(4, attackCount);
                assertEquals(4, favorCount);
                assertEquals(5, nopeCount);
                assertEquals(4, shuffleCount);
                assertEquals(4, skipCount);
                assertEquals(5, seeTheFutureCount);
                assertEquals(4, hairyPotatoCatCount);
                assertEquals(4, cattermelonCount);
                assertEquals(4, rainbowRalphingCatCount);
                assertEquals(4, tacoCatCount);
                assertEquals(4, overweightBikiniCatCount);
            }
        }
    }

    @Test
    public void requirement5Test(){
        //populate Playerbase
        for(int i = 1; i <= 5; i++){
            Playerbase playerbase = new Playerbase();
            ArrayList<Card> tempHand = new ArrayList<Card>();
            Hand hand = new Hand(tempHand);

            playerbase.addPlayer(new Player(i, false, hand, null, null, null));
            if(i >= 2 && i <= 5){
                DrawPile drawPile = new DrawPile(new ArrayList<Card>());
                InitGame gameInitialization = new InitGame(drawPile);
                drawPile = gameInitialization.initCardsAndDrawPile(playerbase);
                gameInitialization.dealAllPlayers7Cards(playerbase, drawPile);		//give all players 7 cards
                
                for(Player player : playerbase.getPlayerbase()){
                    int count = 0;
                    for(Card card : player.getHand().getHandList()){
                        count ++;
                    }
                    player.getHand().clearHand();
                    assertEquals(7, count);
                }
            }
        }
    }

    @Test
    public void requirement6Test(){
        Playerbase playerbase = new Playerbase();
        for(int i = 1; i < 5; i++){
            ArrayList<Card> tempHand = new ArrayList<Card>();
            Hand hand = new Hand(tempHand);
            playerbase.addPlayer(new Player(i, false, hand, null, null, null));

            if(i >= 2 && i <= 5){

                DrawPile dPile = new DrawPile(new ArrayList<Card>());
                InitGame gameInitialization = new InitGame(dPile);
                gameInitialization.initExplodingKittenCard(playerbase, dPile);		//initialize Exploding Kittens
                
                int count = 0;
                for(Card card : dPile.getDrawPileList()){
                    count ++;
                }
                assertEquals(i-1, count);
            }
        }
    }
}