package kittens.core;

import java.util.ArrayList;
import java.util.Arrays;

import kittens.core.gameRules.Game;
import kittens.core.io.Init_IO;
import kittens.core.io.messages.*;
import kittens.core.io.network.*;
import kittens.core.players.Playerbase;

public class Run {
	
	Init_IO scanner = new Init_IO();

	public static void main(String[] args) throws Exception {
		Run run = new Run();
		run.runExplodingKittens();
	}

	private void runExplodingKittens() throws Exception {
		
		ArrayList<Choice> numberOfPlayersChoice = new ArrayList<Choice>();

		for(int i = 2; i <= 5; i++){
			numberOfPlayersChoice.add(new Choice(String.valueOf(i), i + " players"));
		}

		Question numberOfPlayersQuestion = new QuestionWithChoice(
				"How many players (including you) do you want in the game?", numberOfPlayersChoice);

		String answer = scanner
				.askQuestion(new QuestionWithChoice("Do you wish to start as Host or Client?",
						new ArrayList<Choice>(Arrays.asList(new Choice("H", "Host"), new Choice("C", "Client")))))
				.toUpperCase();

		// Host
		if (answer.contentEquals("H")) {
			String botAnswer = scanner
					.askQuestion(new QuestionWithChoice("Do you wish to play with bots",
							new ArrayList<Choice>(Arrays.asList(new Choice("Y", "Yes"), new Choice("N", "No")))))
					.toUpperCase();

			// Bots: Yes
			if (botAnswer.contentEquals("Y")) {
				int numberOfBots = 0;
				int numberOfPlayers = Integer.parseInt(scanner.askQuestion(numberOfPlayersQuestion));
				if(numberOfPlayers > 5 || numberOfPlayers < 2){
					invalid();
				}
				ArrayList<Choice> howManyBots = new ArrayList<Choice>();

				for (int i = 1; i < numberOfPlayers; i++) {
					howManyBots.add(new Choice(String.valueOf(i), String.valueOf(i) + " bot(s)"));
				}

				numberOfBots = Integer.parseInt(scanner.askQuestion(new QuestionWithChoice(
						"How many out of the " + String.valueOf(numberOfPlayers) + " players do you want to be bots?",
						howManyBots)));

				// Start Server
				numberOfPlayers = numberOfPlayers - numberOfBots;
				if(playersInsideBoundaries(numberOfPlayers, numberOfBots)){
					startServer(numberOfPlayers, numberOfBots);
				} else {
					invalid();
				}
				// Bots: No
			} else if (botAnswer.contentEquals("N")) {
				int numberOfPlayers = Integer.parseInt(scanner.askQuestion(numberOfPlayersQuestion));
				if(playersInsideBoundaries(numberOfPlayers, 0)){
					startServer(numberOfPlayers, 0);
				} else {
					invalid();
				}
			} else {
				invalid();
			}

			// Client:
		} else if (answer.contentEquals("C")) {
			startClient();
			System.out.println("Connected");
		} else {
			invalid();
		}
	}

	public void startServer(int numberOfPlayers, int numberOfBots) throws Exception {
		int port = Integer.parseInt(scanner.askQuestion(new OpenQuestion("Enter PORT: ")));
		
		NetworkServer server = new NetworkServer(port);
		Playerbase players = server.establishConnections(numberOfPlayers, numberOfBots);

		Game game = new Game(players);
		game.initAndRunExplodingKittensGame();
	}

	public NetworkClient startClient() {
		String ipAddress = scanner.askQuestion(new OpenQuestion("Enter server IP address: "));
		int port = Integer.parseInt(scanner.askQuestion(new OpenQuestion("Enter PORT: ")));
		NetworkClient client = new NetworkClient(ipAddress, port);
		return client;
	}

	public boolean playersInsideBoundaries(int numberOfPlayers, int numberOfBots){
		if(numberOfPlayers + numberOfBots > 5 || numberOfBots + numberOfPlayers < 2){
			return false;
		}
		else { 
			return true;
		}
	}

	public void invalid() throws Exception {
		System.out.println("Invalid answer, rebooting.\n");
		runExplodingKittens();
	}
}