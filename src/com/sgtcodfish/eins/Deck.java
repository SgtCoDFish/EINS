package com.sgtcodfish.eins;

import java.util.Random;
import java.util.Stack;

import com.sgtcodfish.eins.Card.CardColour;
import com.sgtcodfish.eins.Card.CardType;

public class Deck {
	protected Stack<Card> cards; /** The stack of cards that makes up this deck. */
	protected Table table; /** The table this deck is associated with */
	
	/**
	 * Creates a default deck of cards, stored in Cards, and shuffles them. The default deck consists of:
	 * 2 of each regular colour (RED, BLUE, GREEN, YELLOW) of:
	 * - NUMBER from 0-9 inclusive.
	 * - SKIP
	 * - PICKTWO
	 * - REVERSE
	 * as well as 4 of each of the two BLACK cards, CHANGECOL and CHANGECOLFOUR.
	 */
	public Deck(Table ntable) {
		// Create startRepeats copies of each normal (i.e. non-BLACK) card, the add the black ones later since there are twice as many of each BLACK type card.
		final int startRepeats = 2;
		cards = new Stack<Card>();
		table = ntable;
		
		try {
			for(int repeatCount = 0; repeatCount < startRepeats; repeatCount++) {
				// Create the RED number cards.
				for(int rloop = Card.VAL_BOUND_LOWER; rloop <= Card.VAL_BOUND_UPPER; rloop++) {
					cards.add(new Card(rloop, CardColour.RED));
				}
				
				// Create the BLUE number cards.
				for(int bloop = Card.VAL_BOUND_LOWER; bloop <= Card.VAL_BOUND_UPPER; bloop++) {
					cards.add(new Card(bloop, CardColour.BLUE));
				}
				
				// Create the GREEN number cards.
				for(int gloop = Card.VAL_BOUND_LOWER; gloop <= Card.VAL_BOUND_UPPER; gloop++) {
					cards.add(new Card(gloop, CardColour.GREEN));
				}
				
				// Create the YELLOW number cards.
				for(int yloop = Card.VAL_BOUND_LOWER; yloop <= Card.VAL_BOUND_UPPER; yloop++) {
					cards.add(new Card(yloop, CardColour.YELLOW));
				}
				
				// Create each of the PICKTWO cards.
				cards.add(new Card(CardType.PICKTWO, CardColour.RED));
				cards.add(new Card(CardType.PICKTWO, CardColour.BLUE));
				cards.add(new Card(CardType.PICKTWO, CardColour.GREEN));
				cards.add(new Card(CardType.PICKTWO, CardColour.YELLOW));
				
				// Create each of the REVERSE cards.
				cards.add(new Card(CardType.REVERSE, CardColour.RED));
				cards.add(new Card(CardType.REVERSE, CardColour.BLUE));
				cards.add(new Card(CardType.REVERSE, CardColour.GREEN));
				cards.add(new Card(CardType.REVERSE, CardColour.YELLOW));
				
				// Create each of the SKIP cards.
				cards.add(new Card(CardType.SKIP, CardColour.RED));
				cards.add(new Card(CardType.SKIP, CardColour.BLUE));
				cards.add(new Card(CardType.SKIP, CardColour.GREEN));
				cards.add(new Card(CardType.SKIP, CardColour.YELLOW));
			}
			
			// Now add in the BLACK cards.
			for(int breps = 0; breps < (startRepeats * 2); breps++) {
				cards.add(new Card(CardType.CHANGECOL, CardColour.BLACK));
				cards.add(new Card(CardType.CHANGECOLFOUR, CardColour.BLACK));
			}
		} catch (IllegalArgumentException ie) {
			System.out.println("Illegal Argument in Deck(): " + ie);
		}
	}
	
	/**
	 * Removes the top card of the deck and returns it. The class that invokes this method should then track the card to make sure it doesn't disappear.
	 * Note that if you call this method and the deck is empty, you'll encounter an error; you should be checking the deck size yourself, then use Pile.reseed(Deck) if you run out.
	 */
	public Card takeCard() {
		Card retval = cards.pop();
		
		if(cards.size() == 0) {
			table.reseedDeck();
		}
		
		return retval;
	}
	
	/**
	 * Shuffles the deck, leaving each card in a random position
	 */
	public void shuffle() {
//		System.out.println("In Shuffle. Deck prior to shuffling:");
//		printDeck();
		
		Random random = new Random();
		int rint = -1;
//		int loopint = 0;
		for(int i = (cards.size()-1); i >= 0; i--) {
//			loopint++;
			rint = random.nextInt(i+1);
			//System.out.println(loopint + ": Shuffling " + cards.elementAt(i) + " with " + cards.elementAt(rint) + "!");
			
			cards.elementAt(i).swap(cards.elementAt(rint));
		}
		
//		System.out.println("\nCards after shuffling:");
//		printDeck();
	}
	
	/**
	 * @return The number of cards in the deck.
	 */
	public int countCards() {
		return cards.size();
	}
	
	/**
	 * Prints this deck in its entirety.
	 */
	public void printDeck() {
		int cloop = 0;
		for(Card c : cards) {
			cloop++;
			System.out.format("#%03d:", cloop);
			System.out.println(c);
		}
	}
	
	/**
	 * Called by Pile.reseed(Deck) to recreate the deck from the Pile after the deck has been exhausted. This should ONLY be called by Pile.reseed(Deck)
	 * @param crd The stack of cards to seed the deck with.
	 * @throws IllegalArgumentException
	 */
	public void reseed(Stack<Card> crd) throws IllegalArgumentException {
		if(cards.size() != 0) {
			throw new IllegalArgumentException("Trying to reseed a non-empty deck!");
		} else if(crd == null) {
			throw new IllegalArgumentException("Trying to reseed a deck with a null stack of cards!");
		} else {
			System.out.println("The deck was exhausted, so the pile is turned over.\n");
		}
		
		for(int i = 0; i < crd.size(); i++) {
			cards.add(crd.pop());
		}
	}
}
