package classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import logic.WinConditionLogic;

//TODO :Separate display and logic for PokerTable

public class PokerTable {
	protected List<Player> playerList;
	protected List<Player> currentlyPlaying;
	protected DealerHand dealer;
	protected Deck deck;
	protected int totalBets;
	protected int highestBet;
	protected int numberOfTurns;
	protected Blind bigBlind;
	protected Blind smallBlind;
	protected Blind donor;
	protected final int defaultBlind = 5;
	protected final int turnsForBlindIncrease = 5;
	protected Scanner scanner = new Scanner(System.in);

	protected List<Pot> pots = new ArrayList<>();

	public PokerTable() {
		this.playerList = new ArrayList<>();
		this.currentlyPlaying = new ArrayList<>();
		this.deck = new Deck();
		this.dealer = new DealerHand(deck);
		totalBets = 0;
		this.highestBet = 0;
	}

	public PokerTable(Player player) {
		this();
		this.playerList.add(player);
		this.currentlyPlaying.add(player);
	}

	public PokerTable(List<Player> players) {
		this.playerList = players;
		this.deck = new Deck();
		this.dealer = new DealerHand(deck);
		this.currentlyPlaying= new ArrayList<>();
		for (Player player : this.playerList) {
			if (player.getChipStack()>0) {
				this.currentlyPlaying.add(player);
			}
		}
	}

	public int howManyAreStillPlaying() {
		return this.currentlyPlaying.size();
	}

	public void initializeBlinds() {
		int n = this.currentlyPlaying.size();
		if (n <= 1) {
			return;
		}
		// last player at the table will be first big blind, the one before him
		// will be small blind, and the one before that will be donor.
		// if there's only two players, one player will always be donor and small blind.
		if (this.bigBlind == null) {
			this.bigBlind = new Blind(this.defaultBlind, this.currentlyPlaying.get(n - 1));
			this.smallBlind = new Blind(this.defaultBlind / 2, this.currentlyPlaying.get(n - 2));
			this.donor = new Blind(0, this.currentlyPlaying.get(Math.max(0, n - 3)));
		}

	}

	/**
	 * Switches each blind to the next player in the table
	 */
	public void switchBlinds() {
		int n = this.currentlyPlaying.size();
		int bigBlindIndex = 0, smallBlindIndex = 0, donorIndex = 0;
		// find the index of players that hold the blinds
		for (int i = 0; i < n; i++) {
			if (this.currentlyPlaying.get(i) == this.bigBlind.getPlayer()) {
				bigBlindIndex = i;
			} else if (this.currentlyPlaying.get(i) == this.smallBlind.getPlayer()) {
				smallBlindIndex = i;
			} else if (this.currentlyPlaying.get(i) == this.donor.getPlayer()) {
				donorIndex = i;
			}
		}
		// increment the blinds mod n (if last player has blind, it goes to the first)
		bigBlindIndex = (bigBlindIndex + 1) % n;
		smallBlindIndex = (smallBlindIndex + 1) % n;
		donorIndex = (donorIndex + 1) % n;
		// set players to the blinds
		this.bigBlind.setPlayer(this.currentlyPlaying.get(bigBlindIndex));
		this.smallBlind.setPlayer(this.currentlyPlaying.get(smallBlindIndex));
		this.donor.setPlayer(this.currentlyPlaying.get(donorIndex));
	}

	/**
	 * Kicks every <Player> with no money left
	 */
	public void kickBrokePlayers() {
		for (Player player : this.playerList) {
			if (player.getChipStack() == 0) {
				player.setPlaying(false);
				this.currentlyPlaying.remove(player);
			}
		}
	}

	/**
	 * Adds a player to the current <PokerTable>
	 * 
	 * @param player
	 */
	public void addPlayer(Player player) {
		this.playerList.add(player);
		if (player.getChipStack() > 0) {
			this.currentlyPlaying.add(player);
		}
	}

	/**
	 * Deals 2 new cards to every player from the current deck
	 */
	public void giveCards() {
		for (Player player : this.currentlyPlaying) {
			player.setHand(new PlayerHand(this.deck.getRandomCards(2)));
		}
	}

	/**
	 * Asks players to raise,call,or fold. Need to implement big blind small blind.
	 */
	public int askForBets(int playersInRound) {
		boolean everyoneCalled = false;
		// Implementation of support for constant raising : while
		// not everyone has called/folded (i.e. there's still a player raising)
		// We ask every other player for a call/fold/raise
		List<Boolean> playersCalled = new ArrayList<>();
		while (!everyoneCalled) {
			playersCalled.clear();
			for (Player player : this.currentlyPlaying) {
				// if there's more than one player to ask, player hasn't folded, isn't all in
				// and isn't the one currently raising,
				// ask him for bet
				if (playersInRound > 1 && player.hasNotFolded() && !player.isAllIn() && !player.isCurrentlyRaising()) {
					System.out.println("Current highest bet is " + this.highestBet);
					System.out.println(player.getName() + ", you are currently betting " + player.getBet());
					player.printHand();
					System.out.println("Press 1 to call, 2 to fold, 3 to raise");
					int answer = scanner.nextInt();
					while (answer != 1 && answer != 2 && answer != 3) {
						System.out.println("Wrong command ! Try again: ");
						answer = scanner.nextInt();
					}

					switch (answer) {
					case 1:
						player.call(this.highestBet - player.getBet());
						player.setCurrentlyRaising(false);
						break;
					case 2:
						player.fold();
						player.setCurrentlyRaising(false);
						playersInRound--;
						break;
					// if a player raises, we set him to currently raising, and all the other
					// players to not currently raising
					case 3:
						System.out.println("How much do you want to raise by? (negative will call!)");
						int x = scanner.nextInt();
						if (x > 0) {
							player.bet(highestBet - player.getBet() + x);
							if (player.isAllIn()) {
								this.makePotForAllInPlayer(player);
							}
							for (Player aPlayer : this.currentlyPlaying) {
								// we shouldn't have any weird behaviour
								// if we use != for comparison, at least for now
								aPlayer.setCurrentlyRaising(false);
							}
							player.setCurrentlyRaising((true));
							playersCalled.add(false);
						} else {
							player.call(this.highestBet - player.getBet());
							player.setCurrentlyRaising(false);
							playersCalled.add(true);
						}
						break;
					}
				}
				if (player.isAllIn()) {
					playersInRound--;
				}
				this.findHighestBet();
			}
			everyoneCalled = true;
			for (Boolean playerCalled : playersCalled) {
				if (!playerCalled) {
					everyoneCalled = false;
				}
			}
		}
		// reset player raise state for next dealer card
		this.resetPlayersRaise();
		return playersInRound;
	}

	/**
	 * Checks what <Player> won the current round. Also supports draws
	 * 
	 * @param players
	 * @return
	 */
	public List<Player> checkWhoWins(List<Player> players) {
		List<Player> playersThatWon = new ArrayList<>();
		for (Player player : players) {
			player.setWinCombination(WinConditionLogic.findWinningCombination(dealer, player.getPlayerHand()));
		}
		Collections.sort(players);
		int index = players.size() - 1;
		Player wonForSure = players.get(index);
		// Look for the strongest player that hasn't folded
		while (index > 0 && !wonForSure.hasNotFolded()) {
			index--;
			wonForSure = players.get(index);
		}
		playersThatWon.add(wonForSure);
		// check for other players that haven't folded with similar strength hands
		for (Player player : players) {
			if (player.hasNotFolded() && !playersThatWon.contains(player) && wonForSure.compareTo(player) == 0) {
				playersThatWon.add(player);
			}
		}
		return playersThatWon;
	}

	/**
	 * Calculates total value for the base pot
	 */
	public void calculateTotalPot() {
		for (Player player : this.currentlyPlaying) {
			this.addBet(player.getBet());
		}
	}

	/**
	 * Adds a bet to the total betting pot
	 * 
	 * @param bet : a bet from a player
	 */
	public void addBet(int bet) {
		this.totalBets += bet;
	}

	/**
	 * Gives their money to the players that won. If there's a draw between n
	 * players, splits the pot n ways.
	 * 
	 * @param playersThatWon
	 */
	public void endTurn(List<Player> playersThatWon) {
		int gainSplit = playersThatWon.size();
		this.calculateTotalPot();
		for (Player player : playersThatWon) {
			player.won(this.totalBets / gainSplit);
			System.out.println(player.getName() + " won " + this.totalBets / gainSplit + " with the hand "
					+ player.getWinningCombination());
		}
		for (Player player : this.currentlyPlaying) {

			if (!playersThatWon.contains(player)) {
				System.out.println(player.getName() + " lost " + player.getBet() + " with the hand "
						+ player.getWinningCombination());
				player.lost();
			}
			// reset player fold state for next turn
			player.setHasNotFolded(true);
		}
		kickBrokePlayers();
		deck.resetDeck();
		dealer.clear();
		this.switchBlinds();
		this.numberOfTurns++;
		this.totalBets = 0;
		this.highestBet = 0;
		if (this.numberOfTurns % this.turnsForBlindIncrease == 0) {
			increaseBlinds();
		}
		clearPots();

	}

	/**
	 * Resets player fold & raise state
	 */
	public void resetPlayersRaise() {
		for (Player player : this.currentlyPlaying) {
			player.setCurrentlyRaising(false);
		}
	}

	public void resetPlayers() {
		for (Player player : this.currentlyPlaying) {
			player.setCurrentlyRaising(false);
			player.setHasNotFolded(true);
			player.setBet(0);
		}
	}

	/**
	 * Gets payment for blinds from the associated players
	 */
	public void askBlindPayment() {
		this.bigBlind.getPlayer().bet(this.bigBlind.getValue());
		this.smallBlind.getPlayer().bet(this.smallBlind.getValue());
		this.findHighestBet();
	}

	/**
	 * Finds the highest bet in the current turn
	 */
	public void findHighestBet() {
		for (Player player : this.currentlyPlaying) {
			if (player.getBet() > this.highestBet) {
				this.highestBet = player.getBet();
			}
		}
	}

	/**
	 * Used to increase blinds
	 */
	public void increaseBlinds() {
		this.bigBlind.increase(defaultBlind);
		this.smallBlind.increase(defaultBlind / 2);
	}

	/**
	 * Used to check if it makes sense to create a new AllIn Pot or not. Creating a
	 * new AllInPot makes sense if a player is all-in with less chips than other
	 * players, or if at least 2 more players have the possibility to bet after the
	 * player we're making a pot for is all-in.
	 */
	public boolean checkIfAnyoneCanStillBet() {
		int numberOfPlayersStillBetting = this.playerList.size();
		boolean flag = false;
		int bet = this.playerList.get(0).getBet();
		for (Player player : this.playerList) {
			if (player.isAllIn() || !player.hasNotFolded()) {
				numberOfPlayersStillBetting--;
			}
			if (player.getBet() != bet) {
				flag = true;
			}
		}
		return numberOfPlayersStillBetting >= 2 || flag;
	}
	// pot implementation
	// -------------------------------------------------------------------

	// we will create a new pot when a player all-ins

	/**
	 * Creates a new pot, used for pot initialization of the first pot
	 */
	public void createPot() {
		this.pots.add(new Pot());
	}

	/**
	 * Adds a player to a given pot
	 * 
	 * @param player to add
	 * @param pot    to add to
	 */
	public void addToPot(Player player, Pot pot) {
		pot.addPlayer(player);
	}

	/**
	 * Updates a pot's value, to be used when no one is all in (all in players will
	 * all have their separate pots)
	 */
	public void updatePotValue(Pot pot) {
		// set value to 0 then add every player bet
		pot.setValue(0);
		for (Player player : this.currentlyPlaying) {
			pot.addBet(player.getBet());
		}

	}

	/**
	 * Add every player to the pot, even folded ones
	 * 
	 * @param player that needs a new pot because he is all in now
	 */
	public void makePotForAllInPlayer(Player player) {
		this.pots.add(new AllInPot(player.getBet(), player));
		// work on the pot we just created
		AllInPot pot = (AllInPot) this.pots.get(this.pots.size() - 1);
		int playerBet = pot.getAllInBet();
		for (Player playa : this.playerList) {
			// if a player is all in with less chips, we add his bet value to the pot
			// else we add the value of the all in player
			pot.addBet(Math.min(playa.getBet(), playerBet));
			pot.addPlayer(playa);
		}
	}

	/**
	 * Updates an all-in pot with the value that the associated <Player> would win
	 * if he were to win the pot.
	 * 
	 * @param pot : the <AllInPot> associated with the <Player>
	 */
	public void updateAllInPot(AllInPot pot) {
		pot.setValue(0);
		for (Player player : this.playerList) {
			pot.addBet(Math.min(pot.getAllInBet(), player.getBet()));
		}
	}

	public void clearPots() {
		this.pots.clear();
	}

	public void updateAllPotsValues() {
		for (Pot pot : this.pots) {
			if (pot instanceof AllInPot) {
				this.updateAllInPot((AllInPot) pot);
			} else {
				this.updatePotValue(pot);
			}
		}
	}

	public int askForBetsWithPots(int playersInRound) {
		boolean everyoneCalled = false;
		// Implementation of support for constant raising : while
		// not everyone has called/folded (i.e. there's still a player raising)
		// We ask every other player for a call/fold/raise
		while (!everyoneCalled) {
			for (Player player : this.currentlyPlaying) {
				// if there's more than one player to ask, player hasn't folded, isn't all in
				// and isn't the one currently raising,
				// ask him for bet
				if (playersInRound > 1 && player.hasNotFolded() && !player.isAllIn() && !player.isCurrentlyRaising()) {
					System.out.println("Current highest bet is " + this.highestBet);
					System.out.println(player.getName() + ", you are currently betting " + player.getBet());
					player.printHand();
					int answer;
					do {
						System.out.println("Press 1 to call, 2 to fold, 3 to raise");
						answer = scanner.nextInt();
					} while (answer != 1 && answer != 2 && answer != 3);

					switch (answer) {
					case 1:
						player.call(this.highestBet - player.getBet());
						player.setCurrentlyRaising(false);
						break;
					case 2:
						player.fold();
						player.setCurrentlyRaising(false);
						playersInRound--;
						break;
					// if a player raises, we set him to currently raising, and all the other
					// players to not currently raising
					case 3:
						System.out.println("How much do you want to raise by? (negative will call!)");
						int x = scanner.nextInt();
						if (x > 0) {
							for (Player aPlayer : this.currentlyPlaying) {
								// we shouldn't have any weird behaviour
								// if we use != for comparison, at least for now
								aPlayer.setCurrentlyRaising(false);
							}
							player.setCurrentlyRaising((true));
							player.bet(this.highestBet - player.getBet() + x);
						} else {
							player.call(this.highestBet - player.getBet());
							player.setCurrentlyRaising(false);
						}
						break;
					}
				}
				this.findHighestBet();
			}
			// If any player raised during the for loop, there will be at least
			// a false in the list, so the function will loop again
			// If no players raised, playersCalled will be empty, so everyoneCalled
			// will be true.
			everyoneCalled = true;
			for (Player player : this.currentlyPlaying) {
				if (player.getBet() != this.highestBet) {
					everyoneCalled = false;
					break;
				}
			}
			makeAllInPotIfNecessary(playersInRound);

		}
		// reset player raise state for next dealer card
		this.resetPlayersRaise();
		return playersInRound;
	}

	public int makeAllInPotIfNecessary(int playersInRound) {
		for (Player player : this.currentlyPlaying) {

			if (player.hasNotFolded() && player.isAllIn()) {
				playersInRound--;
				if (this.checkIfAnyoneCanStillBet())
					this.makePotForAllInPlayer(player);

			}
		}
		return playersInRound;
	}

	/**
	 * A player is still competing for the base pot when he managed to pay through
	 * every raise without being all in OR if he's all in but with the most chips
	 */
	public void addCompetingPlayersToBasePot() {
		for (Player player : this.currentlyPlaying) {
			if (player.hasNotFolded() && player.getBet() >= this.pots.get(0).getThresholdBet()) {
				this.pots.get(0).addPlayer(player);
			}
		}
	}

	public void setThresholdForBasePot() {
		this.pots.get(0).setThresholdBet(this.highestBet);
	}

	public void resetTable() {
		kickBrokePlayers();
		deck.resetDeck();
		dealer.clear();
		this.switchBlinds();
		this.numberOfTurns++;
		this.totalBets = 0;
		this.highestBet = 0;
		if (this.numberOfTurns % this.turnsForBlindIncrease == 0) {
			increaseBlinds();
		}
		clearPots();
		resetPlayers();
	}

	public void payoutPot(Pot pot) {
		int value = pot.getValue();
		if (value <= 0) {
			return;
		}
		List<Player> winners = checkWhoWins(pot.getPlayers());
		System.out.println( "Winner list: " + winners);
		int gainSplit = winners.size();
		for (Player player : winners) {
			player.won(value / gainSplit);
			System.out.println(
					player.getName() + " won " + value / gainSplit + " with hand " + player.getWinningCombination());
		}

		for (Pot aPot : this.pots) {
			aPot.setValue(aPot.getValue() - value);
		}
	}

	public void printAllHands() {
		for (Player player : this.playerList) {
			if (player.hasNotFolded()) {
				System.out.println(player.getName() + " has the hand: " + player.getWinningCombination());
				player.printHand();
			}
		}
	}

	public void turnCards() {
		this.giveCards();
		this.initializeBlinds();
		this.askBlindPayment();
		this.findHighestBet();
		int playersInRound = currentlyPlaying.size();
		playersInRound = this.askForBetsWithPots(playersInRound);
		dealer.flop();
		dealer.printHand();
		playersInRound = this.askForBetsWithPots(playersInRound);
		dealer.turn();
		dealer.printHand();
		playersInRound = this.askForBetsWithPots(playersInRound);
		dealer.river();
		dealer.printHand();
		this.askForBetsWithPots(playersInRound);
	}

	public void turnPots() {
		createPot();
		makeAllInPotIfNecessary(0);
		this.updateAllPotsValues();
		// find highest bet (useful for unit testing)
		this.findHighestBet();
		// threshold for base pot is the highest bet
		this.setThresholdForBasePot();
		// only players that have paid the threshold bet will be added to base pot
		this.addCompetingPlayersToBasePot();
		// sort the pots by increasing order
		this.pots.sort(null);
		/*
		 * for (int i = 0;i<this.pots.size();i++) { System.out.println("i=" +i + " "
		 * +this.pots.get(i)); }
		 */
		for (int i = 0; i < this.pots.size(); i++) {
			payoutPot(this.pots.get(i));
		}
	}

	public void startTurnWithPots() {
		turnCards();
		turnPots();
		this.printAllHands();
		this.getDealer().printHand();
		this.resetTable();
	}

	public DealerHand getDealer() {
		return this.dealer;
	}

	public List<Player> getPlayers() {
		return this.currentlyPlaying;
	}

}
