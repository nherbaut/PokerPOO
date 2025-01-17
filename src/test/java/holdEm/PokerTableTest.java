package holdEm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import classes.Card;
import classes.CardColor;
import classes.CardValue;
import classes.Player;
import classes.PlayerHand;
import classes.PokerTable;

class PokerTableTest {

	@Test
	/**
	 * Tests that the base pot from <PokerTable> works correctly when no players
	 * are all in, with 2 players.
	 */
	void noAllInTest() {
		
		//Arrange
		
		Player player1 = new Player("Flavio",50);
		Player player2 = new Player ("Pablo", 50);
		PokerTable table = new PokerTable();
		ArrayList<Card> p1Hand = new ArrayList<>();
		p1Hand.add(new Card(CardValue.TEN,CardColor.CLOVER));
		p1Hand.add(new Card(CardValue.TEN,CardColor.DIAMOND));
		table.addPlayer(player1);
		table.addPlayer(player2);
		player1.setHand(new PlayerHand(p1Hand));
		ArrayList<Card> p2Hand = new ArrayList<>();
		p2Hand.add(new Card(CardValue.NINE,CardColor.CLOVER));
		p2Hand.add(new Card(CardValue.NINE,CardColor.HEART));
		player2.setHand(new PlayerHand(p2Hand));
		ArrayList <Card> dealerHand= new ArrayList<>();
		dealerHand.add(new Card(CardValue.TWO,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.THREE,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.FIVE,CardColor.DIAMOND));
		dealerHand.add(new Card(CardValue.SIX,CardColor.HEART));
		dealerHand.add(new Card(CardValue.SEVEN,CardColor.SPADE));
		table.getDealer().setHand(dealerHand);
		table.getPlayers().get(0).bet(40);
		table.getPlayers().get(1).bet(40);
		table.turnPots();
		
		assertEquals(player1.getChipStack(),90);
		assertEquals(player2.getChipStack(), 10);
	}
	
	@Test
	/**
	 * Tests that <PokerTable> works correctly when both players are all-in with
	 * the same bet
	 */
	void bothAllInTest() {
		
		//arrange
		Player player1 = new Player("Flavio",50);
		Player player2 = new Player ("Pablo", 50);
		PokerTable table = new PokerTable();
		ArrayList<Card> p1Hand = new ArrayList<>();
		p1Hand.add(new Card(CardValue.TEN,CardColor.CLOVER));
		p1Hand.add(new Card(CardValue.TEN,CardColor.DIAMOND));
		table.addPlayer(player1);
		table.addPlayer(player2);
		player1.setHand(new PlayerHand(p1Hand));
		ArrayList<Card> p2Hand = new ArrayList<>();
		p2Hand.add(new Card(CardValue.NINE,CardColor.CLOVER));
		p2Hand.add(new Card(CardValue.NINE,CardColor.HEART));
		player2.setHand(new PlayerHand(p2Hand));
		ArrayList <Card> dealerHand= new ArrayList<>();
		dealerHand.add(new Card(CardValue.TWO,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.THREE,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.FIVE,CardColor.DIAMOND));
		dealerHand.add(new Card(CardValue.SIX,CardColor.HEART));
		dealerHand.add(new Card(CardValue.SEVEN,CardColor.SPADE));
		table.getDealer().setHand(dealerHand);
		
		//Both players bet their whole chipstack
		table.getPlayers().get(0).bet(50);
		table.getPlayers().get(1).bet(50);
		table.turnPots();
		
		//assert
		assertEquals(player1.getChipStack(),100);
		assertEquals(player2.getChipStack(), 0);
	}
	@Test
	/**
	 * Tests if <PokerTable> works correctly when only one player is all-in,
	 * while the other has more chips. In that case, the winning player
	 * has more chips.
	 */
	void oneAllInTest() {
		//arrange
				Player player1 = new Player("Flavio",100);
				Player player2 = new Player ("Pablo", 50);
				PokerTable table = new PokerTable();
				ArrayList<Card> p1Hand = new ArrayList<>();
				p1Hand.add(new Card(CardValue.TEN,CardColor.CLOVER));
				p1Hand.add(new Card(CardValue.TEN,CardColor.DIAMOND));
				table.addPlayer(player1);
				table.addPlayer(player2);
				player1.setHand(new PlayerHand(p1Hand));
				ArrayList<Card> p2Hand = new ArrayList<>();
				p2Hand.add(new Card(CardValue.NINE,CardColor.CLOVER));
				p2Hand.add(new Card(CardValue.NINE,CardColor.HEART));
				player2.setHand(new PlayerHand(p2Hand));
				ArrayList <Card> dealerHand= new ArrayList<>();
				dealerHand.add(new Card(CardValue.TWO,CardColor.CLOVER));
				dealerHand.add(new Card(CardValue.THREE,CardColor.CLOVER));
				dealerHand.add(new Card(CardValue.FIVE,CardColor.DIAMOND));
				dealerHand.add(new Card(CardValue.SIX,CardColor.HEART));
				dealerHand.add(new Card(CardValue.SEVEN,CardColor.SPADE));
				table.getDealer().setHand(dealerHand);
				
				//Both players all-in but p1 has more chips.
				table.getPlayers().get(0).bet(100);
				table.getPlayers().get(1).bet(50);
				table.turnPots();
				
				//assert
				assertEquals(player1.getChipStack(),150);
				assertEquals(player2.getChipStack(), 0);
	}
	@Test
	/**
	 * Tests if the <AllInPot> implementation of <PokerTable> is working fine.
	 * p1 all-ins with less chips than p2 and wins.
	 */
	void oneAllInSidePotTest() {
		//arrange
				Player player1 = new Player("Flavio",50);
				Player player2 = new Player ("Pablo", 100);
				PokerTable table = new PokerTable();
				ArrayList<Card> p1Hand = new ArrayList<>();
				p1Hand.add(new Card(CardValue.TEN,CardColor.CLOVER));
				p1Hand.add(new Card(CardValue.TEN,CardColor.DIAMOND));
				table.addPlayer(player1);
				table.addPlayer(player2);
				player1.setHand(new PlayerHand(p1Hand));
				ArrayList<Card> p2Hand = new ArrayList<>();
				p2Hand.add(new Card(CardValue.NINE,CardColor.CLOVER));
				p2Hand.add(new Card(CardValue.NINE,CardColor.HEART));
				player2.setHand(new PlayerHand(p2Hand));
				ArrayList <Card> dealerHand= new ArrayList<>();
				dealerHand.add(new Card(CardValue.TWO,CardColor.CLOVER));
				dealerHand.add(new Card(CardValue.THREE,CardColor.CLOVER));
				dealerHand.add(new Card(CardValue.FIVE,CardColor.DIAMOND));
				dealerHand.add(new Card(CardValue.SIX,CardColor.HEART));
				dealerHand.add(new Card(CardValue.SEVEN,CardColor.SPADE));
				table.getDealer().setHand(dealerHand);
				
				//Both players all-in but p1 has more chips.
				table.getPlayers().get(0).bet(100);
				table.getPlayers().get(1).bet(50);
				table.turnPots();
				
				//assert
				assertEquals(player1.getChipStack(),100);
				assertEquals(player2.getChipStack(), 50);
	}
	
	//TODO : Test with 3+ players. Test with multiple all-in players, that each
	//have different chipstacks.
	@Test
	/**
	 * Tests <PokerTable> pot system when 3 players are all-in with the same chips
	 */
	void threePlayersAllInSameBet() {
		Player player1 = new Player("Flavio",100);
		Player player2 = new Player ("Pablo", 100);
		Player player3 = new Player("Mingo", 100);
		PokerTable table = new PokerTable();
		ArrayList<Card> p1Hand = new ArrayList<>();
		p1Hand.add(new Card(CardValue.TEN,CardColor.CLOVER));
		p1Hand.add(new Card(CardValue.TEN,CardColor.DIAMOND));
		player1.setHand(new PlayerHand(p1Hand));
		ArrayList<Card> p2Hand = new ArrayList<>();
		p2Hand.add(new Card(CardValue.NINE,CardColor.CLOVER));
		p2Hand.add(new Card(CardValue.NINE,CardColor.HEART));
		player2.setHand(new PlayerHand(p2Hand));
		ArrayList<Card> p3Hand = new ArrayList<>();
		p3Hand.add(new Card (CardValue.JACK,CardColor.CLOVER));
		p3Hand.add(new Card (CardValue.JACK,CardColor.SPADE));
		player3.setHand(new PlayerHand(p3Hand));
		table.addPlayer(player1);
		table.addPlayer(player2);
		table.addPlayer(player3);
		ArrayList <Card> dealerHand= new ArrayList<>();
		dealerHand.add(new Card(CardValue.TWO,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.THREE,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.FIVE,CardColor.DIAMOND));
		dealerHand.add(new Card(CardValue.SIX,CardColor.HEART));
		dealerHand.add(new Card(CardValue.SEVEN,CardColor.SPADE));
		table.getDealer().setHand(dealerHand);
		
		//Both players all-in but p1 has more chips.
		table.getPlayers().get(0).bet(100);
		table.getPlayers().get(1).bet(100);
		table.getPlayers().get(2).bet(100);
		table.turnPots();
		
		//assert
		assertEquals(player1.getChipStack(),0);
		assertEquals(player2.getChipStack(),0);
		assertEquals(player3.getChipStack(), 300);
	}
	@Test
	/**
	 * Flavio has less chips than Pablo and Mingo. Mingo wins, he should get 50+60+60
	 * Pablo and Flavio should be broke.
	 */
	void ThreePlayersDifferentChipsAllIn() {
		Player player1 = new Player("Flavio",50);
		Player player2 = new Player ("Pablo", 60);
		Player player3 = new Player("Mingo", 60);
		PokerTable table = new PokerTable();
		ArrayList<Card> p1Hand = new ArrayList<>();
		p1Hand.add(new Card(CardValue.TEN,CardColor.CLOVER));
		p1Hand.add(new Card(CardValue.TEN,CardColor.DIAMOND));
		player1.setHand(new PlayerHand(p1Hand));
		ArrayList<Card> p2Hand = new ArrayList<>();
		p2Hand.add(new Card(CardValue.NINE,CardColor.CLOVER));
		p2Hand.add(new Card(CardValue.NINE,CardColor.HEART));
		player2.setHand(new PlayerHand(p2Hand));
		ArrayList<Card> p3Hand = new ArrayList<>();
		p3Hand.add(new Card (CardValue.JACK,CardColor.CLOVER));
		p3Hand.add(new Card (CardValue.JACK,CardColor.SPADE));
		player3.setHand(new PlayerHand(p3Hand));
		table.addPlayer(player1);
		table.addPlayer(player2);
		table.addPlayer(player3);
		ArrayList <Card> dealerHand= new ArrayList<>();
		dealerHand.add(new Card(CardValue.TWO,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.THREE,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.FIVE,CardColor.DIAMOND));
		dealerHand.add(new Card(CardValue.SIX,CardColor.HEART));
		dealerHand.add(new Card(CardValue.SEVEN,CardColor.SPADE));
		table.getDealer().setHand(dealerHand);
		
		//Both players all-in but p1 has more chips.
		table.getPlayers().get(0).bet(100);
		table.getPlayers().get(1).bet(100);
		table.getPlayers().get(2).bet(100);
		table.turnPots();
		
		//assert
		assertEquals(player1.getChipStack(),0);
		assertEquals(player2.getChipStack(),0);
		assertEquals(player3.getChipStack(), 170);
	}
	@Test
	/**
	 * Flavio and Pablo lose, so Mingo should get 50+60+60 and Pablo should get 40 back.
	 */
	void ThreePlayersDifferentChipsTwoAreAllIn() {
		Player player1 = new Player("Flavio",50);
		Player player2 = new Player ("Pablo", 100);
		Player player3 = new Player("Mingo", 60);
		PokerTable table = new PokerTable();
		ArrayList<Card> p1Hand = new ArrayList<>();
		p1Hand.add(new Card(CardValue.TEN,CardColor.CLOVER));
		p1Hand.add(new Card(CardValue.TEN,CardColor.DIAMOND));
		player1.setHand(new PlayerHand(p1Hand));
		ArrayList<Card> p2Hand = new ArrayList<>();
		p2Hand.add(new Card(CardValue.NINE,CardColor.CLOVER));
		p2Hand.add(new Card(CardValue.NINE,CardColor.HEART));
		player2.setHand(new PlayerHand(p2Hand));
		ArrayList<Card> p3Hand = new ArrayList<>();
		p3Hand.add(new Card (CardValue.JACK,CardColor.CLOVER));
		p3Hand.add(new Card (CardValue.JACK,CardColor.SPADE));
		player3.setHand(new PlayerHand(p3Hand));
		table.addPlayer(player1);
		table.addPlayer(player2);
		table.addPlayer(player3);
		ArrayList <Card> dealerHand= new ArrayList<>();
		dealerHand.add(new Card(CardValue.TWO,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.THREE,CardColor.CLOVER));
		dealerHand.add(new Card(CardValue.FIVE,CardColor.DIAMOND));
		dealerHand.add(new Card(CardValue.SIX,CardColor.HEART));
		dealerHand.add(new Card(CardValue.SEVEN,CardColor.SPADE));
		table.getDealer().setHand(dealerHand);
		
		//Both players all-in but p1 has more chips.
		table.getPlayers().get(0).bet(100);
		table.getPlayers().get(1).bet(100);
		table.getPlayers().get(2).bet(100);
		table.turnPots();
		
		//assert
		assertEquals(player1.getChipStack(),0);
		assertEquals(player2.getChipStack(),40);
		assertEquals(player3.getChipStack(), 170);
	}
	
}
