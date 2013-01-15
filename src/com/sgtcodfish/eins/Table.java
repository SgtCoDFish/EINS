package com.sgtcodfish.eins;

import java.util.Vector;

import com.sgtcodfish.eins.AIPlayer.AIDifficulty;
import com.sgtcodfish.eins.Card.CardColour;
import com.sgtcodfish.eins.Card.CardType;

/**
 * Defines a Table of players, that is a group of players of the game. Used to iterate through the players and control whose turn it is.
 * @author Ashley Davis (SgtCoDFish)
 */
public class Table {
	public static final int HAND_SIZE_DEFAULT = 7;
	public static final AIDifficulty AI_DIFFICULTY_DEFAULT = AIDifficulty.REGULAR;
	
	protected Vector<CardEntity> players;
	protected Deck deck;
	protected Pile pile;
	
	protected CardColour currentColour;
	protected int currentPlayer; // 0 = the first player, 1 = the player to the left of 0, and so on.
	protected int lastPlayer; // refers to the last player who played; used for EINS accusations
	
	protected boolean reversed; // true if we're going counterclockwise, false if normal.
	protected boolean endRequested; // true if a player wants the game to end.
	
	protected EINSIOHandler output;
	
	protected boolean nosleep; // used if the user chose to have just AI battling to speed up.
	
	/**
	 * Creates a new table of players with 1 human player using the specified name and numplayers computer players.
	 * @param numplayers The number of AI players at the table, must be a positive integer.
	 */
	public Table(EINSIOHandler nout, String playerName, int numplayers, AIDifficulty diff) throws IllegalArgumentException {
		output = nout;
		deck = new Deck(this);
		deck.shuffle();
		pile = new Pile(deck, this);
		players = new Vector<CardEntity>();
		currentPlayer = 1; // we start to the left of the first player.
		lastPlayer = 0;
		currentColour = pile.getTopCard().getColour();
		reversed = false;
		endRequested = false;
		if(diff == null) { diff = AI_DIFFICULTY_DEFAULT; }
		
		if(numplayers <= 1) { // can't have 1 or less players 
			throw new IllegalArgumentException("Trying to start a game with too few (" + numplayers + ") players!");
		} else if((numplayers * HAND_SIZE_DEFAULT) >= (int)(0.8d * deck.countCards())) { // ensure we have 20% of the size of the deck left to play with after we deal out hands.
			throw new IllegalArgumentException("Trying to start a game with too many (" + numplayers + ") players, leaving too few cards in the deck!");
		}
		
		if(playerName != null) {
			players.add(new HumanPlayer(playerName));
			nosleep = false;
		} else {
			// if playerName == null, we do a special AI only game.
			AIDifficulty otherDiff = null;
			if(diff == AIDifficulty.BEST) {
				otherDiff = AIDifficulty.REGULAR;
			} else {
				otherDiff = AIDifficulty.BEST;
			}
			players.add(new AIPlayer("Special Computer", otherDiff));
			nosleep = true;
		}
		
		for(int i = 0; i < numplayers; i++) {
			players.add(new AIPlayer("Computer " + (i+1), diff));
		}
		
		for(CardEntity ce : players) {
			ce.setTable(this);
		}
		
		dealCards();
//		System.out.println("Cards after dealing:");
//		deck.printDeck();
		
		if(pile.getTopCard().getType() != CardType.NUMBER) {
			// if the first card isn't a number card, we have some fun.
			switch(pile.getTopCard().getType()) {
			case SKIP:
				// if the first card is a STOP the player who would start misses their turn.
				getIOHandler().println(players.elementAt(currentPlayer).getName() + " misses their turn since the first card was a " + pile.getTopCard().getType() + "!");
				advanceCurrentPlayer();
				break;
				
			case PICKTWO:
				// make the starting player draw two cards and miss their go
				for(int i = 0; i < 2; i++) {
					getCurrentPlayer().takeCard(deck);
				}
				
				getIOHandler().println(players.elementAt(currentPlayer).getName() +
						" misses their turn and draws two cards since the first card was a " + pile.getTopCard().getType() + "!");
				advanceCurrentPlayer();
				break;
				
			case REVERSE:
				// if the first card is a REVERSE, the order of play is reversed and we start on the "dealer" (i.e. player 0)
				reversed = !reversed;
				getIOHandler().println(players.elementAt(currentPlayer).getName() + " misses their turn since the first card was a " + pile.getTopCard().getType() + "!");
				getIOHandler().println("The direction of play has also been reversed!");
				advanceCurrentPlayer();
				break;
				
			case CHANGECOL:
				// starting with CHANGECOL means the first player gets to choose the starting colour.
				getIOHandler().println("The first card is " + pile.getTopCard().getType() + " so " + players.elementAt(currentPlayer) + " chooses the starting colour!");
				currentColour = getCurrentPlayer().askForColour();
				getIOHandler().println(players.elementAt(currentPlayer).getName() + " chose " + currentColour + " as the starting colour!");
				break;
				
			case CHANGECOLFOUR:
				// starting with CHANGECOLFOUR means the first player picks up 4 cards and misses their turn, and the next player chooses the colour to start with.
				for(int i = 0; i < 3; i++) {
					getCurrentPlayer().takeCard(deck);
				}
				getIOHandler().println("The first card is " + pile.getTopCard().getType() +
						" so " + players.elementAt(currentPlayer).getName() + " misses their turn and draws four cards!");
				
				advanceCurrentPlayer();
				
				getIOHandler().println(getCurrentPlayer().getName() + " gets to choose the starting colour!");
				currentColour = getCurrentPlayer().askForColour();
				getIOHandler().println(players.elementAt(currentPlayer).getName() + " chose " + currentColour + " as the starting colour!");
				break;
			case NUMBER:
				break;
			default:
				break;
			}
		}
		
		getIOHandler().print("\n");
		getIOHandler().flush();
	}
	
	/**
	 * The main loop; ended by a player choosing to call requestEnd() or by the game ending (i.e. a player reaching 0 cards)
	 */
	public boolean mainLoop(ScoreList slist) {
		try {
			//System.out.println("Top card: " + pile.getTopCard() + (pile.getTopCard().getColour() == CardColour.BLACK ? "[Clr: " + getCurrentColour() + "]!" : "!"));
			while(!endRequested) {
				getIOHandler().println("-----");
				
				getIOHandler().println("Top card: " + pile.getTopCard() + (pile.getTopCard().getColour() == CardColour.BLACK ? "[Clr: " + getCurrentColour() + "]!" : "!"));
				lastPlayer = currentPlayer;
				getCurrentPlayer().doTurn();
				
				getIOHandler().println("-----");
				
				getIOHandler().print("\n");
				
				boolean won = false;
				
				for(CardEntity ce : players) {
					if(ce.countCards() == 0) {
						// we have a winner!
						getIOHandler().println(ce.getName() + " has won this game!");
						won = true;
					}
				}
				
				getIOHandler().flush();
				
				if(won) break;
				
				if(!nosleep) Thread.sleep(1000); // makes the pace a little more reasonable
			}
			
//			System.out.println("Score tallies for this game:");
//			for(CardEntity ce : players) {
//				System.out.println(ce.getName() + ": Score = " + ce.tallyValues());
//			}
			
			slist.addScores(players);
			return endRequested;
		} catch(IllegalArgumentException iae) {
			getIOHandler().printErrorln("Programming error! " + iae);
		} catch(IllegalStateException ise) {
			getIOHandler().printErrorln("Something weird happened! " + ise);
		} catch(Exception e) {
			getIOHandler().printErrorln("Error!\nException: " + e);
		}
		
		return true;
	}
	
	/**
	 * Used to deal out a hand of HAND_SIZE_DEFAULT cards to each player.
	 */
	public void dealCards() {
		for(int handSizeLoop = 0; handSizeLoop < HAND_SIZE_DEFAULT; handSizeLoop++) {
			for(int playerLoop = 0; playerLoop < players.size(); playerLoop++) {
				players.elementAt(playerLoop).takeCard(deck);
			}
		}
	}
	
	/**
	 * CardEntity subclasses call this function to play a card c. The CardEntity.playCard function performs correctly and removes the card from the player's hand.
	 * @param ent The entity that wants to play a card.
	 * @param ncard The card they wish to play.
	 * @throws IllegalArgumentException
	 */
	public void playCard(CardEntity ent, Card ncard) throws IllegalArgumentException {
		Card peeked = pile.getTopCard();
		CardType pType = peeked.getType();
		
		if(ent == null) {
			throw new IllegalArgumentException("Called playCard with null entity.");
		}
		
		if(ncard == null) {
			// the player is reporting that no card was playable; proceed.
			advanceCurrentPlayer();
			return;
		}
		
		// Check if it's a legal move.
		if(ncard.getColour() == CardColour.BLACK) {
			// for both cards, the player that played them gets to choose the colour.
			getIOHandler().println(players.elementAt(currentPlayer).getName() + " played a " + ncard.getType() + "!");
			currentColour = ent.askForColour();
			getIOHandler().println(players.elementAt(currentPlayer).getName() + " chose " + currentColour + " as the new colour!");
			advanceCurrentPlayer();
			
			if(ncard.getType() == CardType.CHANGECOLFOUR) { // now make the next player draw 4 cards and miss their go if that's the card that was played
				for(int i = 0; i < 3; i++) {
					getCurrentPlayer().takeCard(deck);
				}
				
				getIOHandler().println(players.elementAt(currentPlayer).getName() + " misses their turn and draws four cards!");
				advanceCurrentPlayer();
			}
			
			pile.addCard(ncard);
		} else if(isLegal(ncard)) { // the colours/types match so put the card on the pile
			pile.addCard(ncard);
			
			if(ncard.getType() == pType && ncard.getValue() == peeked.getValue()) { // while legal, we need to chance the currentColour if we're changing because of a type match
				currentColour = ncard.getColour();
				getIOHandler().println(players.elementAt(currentPlayer).getName() + " played " + ncard + ", changing the colour to " + ncard.getColour() + "!");
			} else {
				getIOHandler().println(players.elementAt(currentPlayer).getName() + " played " + ncard + "!");
			}
			
			// if ncard isn't a number card, we have additional actions to take
			if(ncard.getType() != CardType.NUMBER) {
				switch (ncard.getType()) {
				case SKIP:
					advanceCurrentPlayer();
					getIOHandler().println(players.elementAt(currentPlayer).getName() + " misses their turn!");
					break;
					
				case REVERSE:
					reversed = !reversed;
					getIOHandler().println("The order of play is reversed! Direction of play is now " + (reversed ? "counter-clockwise" : "clockwise") + "!");
					break;
					
				case PICKTWO:
					advanceCurrentPlayer();
					for(int i = 0; i < 2; i++) {
						getCurrentPlayer().takeCard(deck);
					}
					
					getIOHandler().println(players.elementAt(currentPlayer).getName() + " misses their turn and draws two cards!");
				case CHANGECOL:
					break;
				case CHANGECOLFOUR:
					break;
				case NUMBER:
					break;
				default:
					break;
				}
			}
			
			// all done, so move to the next player.
			advanceCurrentPlayer();
		} else {
			// If we're here the move must be illegal, and report as such
			throw new IllegalArgumentException("Trying to play a card " + ncard + " but the move was illegal.");
		}
	}
	
	/**
	 * Checks to see if c is a legal card to play right now (compared to the top card of the {@link Pile}.
	 * @param c The card whose legality we want to check.
	 * @return true if the card is legal, false otherwise.
	 */
	public boolean isLegal(Card c) {
		Card topCard = pile.getTopCard();
		//System.out.println("Checking legality of " + c + ", top card: " + topCard + "[" + getCurrentColour() + "].");
		
		if(c.getColour() == CardColour.BLACK) { // always legal to play a black card if it's your turn
			//System.out.println(c + " is a black card!");
			return true;
		}
		
		if(c.getColour() == getCurrentColour()) { // always legal to play a same-colour card of any type.
			//System.out.println(c + " has the same colour as " + topCard + "!");
			return true;
		}
		
		if(c.getType() == topCard.getType()) { // not always legal to play cards of the same type; if type == NUMBER, we have to check the values are equal.
			if(topCard.getType() == CardType.NUMBER) { // if we have a number, check the values
				if(topCard.getValue() == c.getValue()) { // values are the same, so return true
					//System.out.println(c + " has the same type as " + topCard + "!");
					return true;
				}
			} else { // else the types are REVERSE, PICKTWO or SKIP and can be played.
				//System.out.println(c + " has the same type as " + topCard + "!");
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Accuses all players but the accusor of having forgotten to say EINS. Any players who have 1 card and for whom hasSaidEins returns false draw two cards.
	 * If there is not at least one player who forgot to say EINS, the accusor draws two cards.
	 */
	public void accuseEins(CardEntity accusor) {
		getIOHandler().println(accusor.getName() + " accuses all players of forgetting to say EINS!");
		
		if(checkEins()) {
			for(CardEntity ce : players) {
				if(ce != accusor) { // let's not make the accusor accuse themselves.
					if(ce.countCards() == 1 && !ce.hasSaidEins()) {
						// this entity forgot to say eins and has one card so is punished by drawing two cards
						getIOHandler().println(ce.getName() + " has only one card and has forgotten to say EINS! They draw two cards as punishment.");
						ce.takeCard(deck);
						ce.takeCard(deck);
					}
				}
			}
		} else {
			getIOHandler().println(accusor.getName() + "\'s accusation was incorrect and so draws two cards as punishment.");
			accusor.takeCard(deck);
			accusor.takeCard(deck);
		}
	}
	
	/**
	 * Used by AI to work out whether or not to accuse people of forgetting to say EINS.
	 * @return true if at least one person has forgotten, false otherwise.
	 */
	public boolean checkEins() {
		boolean retVal = false;
		
		for(CardEntity ce : players) {
			if(ce.countCards() == 1 && !ce.hasSaidEins()) {
				retVal = true;
				break;
			}
		}
		
		return retVal;
	}
	
	/**
	 * Moves the current player on to the next player whose turn it is.
	 */
	public void advanceCurrentPlayer() {
		if(!reversed) {
			currentPlayer++;
			
			if(currentPlayer > players.size()-1) {
				// we've reached the "last player" so loop around
				currentPlayer = 0;
			}
		} else {
			// reversed, so do the opposite
			currentPlayer--;
			
			if(currentPlayer == -1) {
				currentPlayer = (players.size() - 1);
			}
		}
	}
	
	/**
	 * @return The EINSOutputHandler implementation this Table was initialised with.
	 */
	public EINSIOHandler getIOHandler() {
		return output;
	}
	
	/**
	 * @return The next player who will play after this one, assuming no SKIP, DRAWTWO, REVERSE or CHANGECOLFOUR cards are played.
	 */
	public CardEntity getNextPlayer() {
		return players.elementAt((currentPlayer + 1 == players.size() ? 0 : currentPlayer + 1));
	}
	
	/**
	 * Gets the player whose turn it is.
	 * @return The player whose turn it is.
	 */
	public CardEntity getCurrentPlayer() {
		return players.elementAt(currentPlayer);
	}
	
	/**
	 * @return The last player who had a turn.
	 */
	public CardEntity getLastPlayer() {
		return players.elementAt(lastPlayer);
	}
	
	/**
	 * @return The current colour in play.
	 */
	public CardColour getCurrentColour() {
		return currentColour;
	}
	
	/**
	 * @return The current type of card on the table.
	 */
	public CardType getCurrentType() {
		return pile.getTopCard().getType();
	}
	
	/**
	 * @return The current deck.
	 */
	public Deck getDeck() {
		return deck;
	}
	
	/**
	 * @return The current pile.
	 */
	public Pile getPile() {
		return pile;
	}
	
	/**
	 * Indicates that a HumanPlayer has requested that the game end.
	 */
	public void requestEnd() {
		endRequested = true;
	}
	
	/**
	 * Prints a list of all current players, and their hand sizes.
	 */
	public void printPlayers() {
		for(CardEntity ce : players) {
			int thisCardCount = ce.countCards();
			getIOHandler().println(ce.getName() + " has " + thisCardCount + (thisCardCount == 1 ? " card!" : " cards!"));
		}
	}
	
	/**
	 * Called to reseed the deck when it is exhausted.
	 */
	public void reseedDeck() {
		pile.reseed(deck);
	}
	
	/**
	 * @return false if nosleep is true. Used by AI to work out if they should simulate "thinking time" in their turn.
	 */
	public boolean shouldSleep() {
		return (!nosleep);
	}
}
