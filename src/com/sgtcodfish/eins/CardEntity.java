package com.sgtcodfish.eins;

import java.util.Vector;

import com.sgtcodfish.eins.Card.CardColour;

/**
 * A CardEntity in terms of the game is anything which can have a "hand" of cards. You should inherit CardEntity to create a Player class or an AI class.
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
abstract public class CardEntity {
	public static final String DEFAULT_ENTITY_NAME = "Unnamed Player";
	
	protected Vector<Card> cards; /** The cards that this CardEntity holds currently. */
	protected String name; /** The name of the player */
	protected Table table;
	
	protected boolean saidEins; /** True if this entity has said eins, false otherwise */
	
	/**
	 * used to do initialisation in each form of the constructor
	 */
	protected void init() {
		cards = new Vector<Card>();
		name = new String();
		table = null;
		saidEins = false;
	}
	
	/**
	 * Create a CardEntity with a default name, specified by DEFAULT_ENTITY_NAME.
	 */
	public CardEntity() {
		init();
		name = DEFAULT_ENTITY_NAME;
	}
	
	/**
	 * Create a CardEntity with the specified name.
	 * @param nname The name for this entitiy.
	 */
	public CardEntity(String nname) {
		init();
		name = nname;
	}
	
	/**
	 * Abstract method intended to be called when it's a player's turn to act. Will vary depending on the player type;
	 * For example, human players will need to give some kind of input to choose what move to make, while AI players will work out what to do. 
	 */
	public abstract void doTurn();
	
	/**
	 * Takes a card from the specified source, in this case a {@link Card Card class}.
	 * @param ncard The card to add.
	 */
	public Card takeCard(Card ncard) {
		if(countCards() == 1) {
			// they'll need to say EINS again
			saidEins = false;
		}
		
		cards.add(ncard);
		return ncard;
	}
	
	/**
	 * Takes a card from the specified {@link Deck}.
	 * @param d The deck from which the card is taken.
	 */
	public Card takeCard(Deck d) {
		return takeCard(d.takeCard());
	}
	
	/**
	 * Attempt to play a {@link Card} c by placing it on the specified {@link Pile}. Protected since it should only be called from within a doTurn().
	 * @param p The pile to which we should add the card.
	 * @param c The card to play, which the player must have in their hand.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	protected void playCard(Card c) throws IllegalArgumentException, IllegalStateException {
		if(table == null) {
			throw new IllegalStateException("Trying to play a card on a non-existant table!");
		}
		
		if(c == null) {
			// we can't actually play this turn, so we need to report this.
			table.playCard(this, null);
			return;
		}
		
		if(!hasCard(c)) { // trying to play a card we don't have!
			throw new IllegalArgumentException("Player " + name + " tried to play a card they don't have! (Card is " + c + ")");
		}
		
		if(!table.isLegal(c)) {
			throw new IllegalArgumentException("Player " + name + " tried to play an illegal card, " + c + "! (in CardEntity.playCard(Card))");
		}
		
		table.playCard(this, c);
		
		if(!cards.remove(c)) {
			throw new IllegalStateException("Tried to remove a card (" + c + ") from player " + name + "\'s hand which wasn't there. Fatal error.");
		}
	}
	
	/**
	 * @return The values of all cards in the hand, typically used at the end of the game.
	 */
	public int tallyValues() {
		int tally = 0;
		
		for(Card c : cards) {
			tally += c.getValue();
		}
		
		return tally;
	}
	
	/**
	 * Ask the Entity to choose a colour for a CHANGECOL or CHANGECOLFOUR card type.
	 * This has use if, for example, the first card in a {@link Pile} is BLACK, or if a BLACK card is played.
	 */
	public abstract CardColour askForColour();
	
	/**
	 * Returns the number of cards the player has.
	 * @return The size of cards
	 */
	public int countCards() {
		return cards.size();
	}
	
	/**
	 * Checks if the card c is present in cards
	 * @param c The card to check
	 * @return true if this Entity has at least one of c, false otherwise.
	 */
	public boolean hasCard(Card c) {
		boolean result = false;
		
		if(cards.contains(c)) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Counts the number of cards of colour col that the player has and returns this as an integer.
	 * @param col The colour to count.
	 * @return The number of cards of colour col the player has.
	 */
	public int countCardsOfColour(CardColour col) {
		int count = 0;
		
		for(Card c : cards) {
			if(c.getColour() == col) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Gets the name of this player.
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Prints the Entity's hand to System.out
	 */
	public void printHand() {
		//int cardCount = 1;
		
		System.out.println(name + "\'s cards:");
		
		for(Card c : cards) {
			System.out.println(c);
		//	cardCount++;
		}
	}
	
	/**
	 * Sets the table for this player.
	 * @param nt The table which the player is "sitting" at.
	 */
	public void setTable(Table nt) {
		table = nt;
	}
	
	/**
	 * If this CardEntity has 2 cards left, flags the player as having said eins and "innoculates" the player from accusations they forgot to say it.
	 * Otherwise forces the player to draw two cards for saying it incorrectly;
	 * This is to prevent human players saying it every turn as a routine to avoid ever forgetting it.
	 * 
	 * Should be said BEFORE the second-to-last card is played.
	 */
	public void sayEins() {
		if(countCards() == 2) {
			System.out.println(name + " shouts EINS!");
			saidEins = true;
		} else {
			System.out.println(name + " says EINS incorrectly, and draws two cards as punishment.");
			takeCard(table.getDeck());
			takeCard(table.getDeck());
		}
	}
	
	/**
	 * @return true if this player has said EINS (which is only the case if they have 1 card left and called sayEins()), false otherwise.
	 */
	public boolean hasSaidEins() {
		return saidEins;
	}
	
	/**
	 * Checks all players for their card counts. See {@link Table.accuseEins}.
	 */
	public void accuseEins() {
		table.accuseEins(this);
	}
	
	/**
	 * Subclasses should return a string identifying themselves as [HUMAN] or [COMPUTER]
	 */
	abstract public String getSubclassIdentifier();
}
