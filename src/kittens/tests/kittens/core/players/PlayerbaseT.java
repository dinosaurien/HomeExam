package kittens.tests.kittens.core.players;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import kittens.core.players.Player;
import kittens.core.players.Playerbase;

public class PlayerbaseT {
    
    @Test
    public void requirement1Test(){

        Playerbase playerBase = new Playerbase();
        for(int i = 0; i < 10; i++){
            if(i <= 1){ 
                assertEquals(true, playerBase.tooFewPlayers()); //amount of players is 0 or 1:
                assertEquals(false, playerBase.tooManyPlayers());
            } else if (i <= 5){
                assertEquals(false, playerBase.tooFewPlayers());
                assertEquals(false, playerBase.tooManyPlayers());
            } else {
                assertEquals(false, playerBase.tooFewPlayers());
                assertEquals(true, playerBase.tooManyPlayers());
            }
            
            playerBase.addPlayer(new Player(i, false, null, null, null, null)); //simulate that players are connecting
            
        }
    }
}
