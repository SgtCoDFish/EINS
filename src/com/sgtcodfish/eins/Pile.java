package com.sgtcodfish.eins;

import java.util.Stack;

/**
 * The Pile of cards is specifically the "playing pile" of cards, which players place cards on.
 * Inverse to a deck, which starts with all the cards and only loses them, a pile starts with nothing and gains cards.
 * @author Ashley Davis (SgtCoDFish)
 *
 */
public class Pile {
	protected Stack<Card> cards; /** A stack of the cards comprising this Pile */
	protected Table table; /** The table with which this Pile is associated */
	
	/**
	 * Creates a pile with the first card being the top card of the specified {@link Deck}.
	 * Note that this constructor means you should make the Pile class after you have dealt the cards, to perfectly mimic the actual game, although mathematically this is irrelevant.
	 * @param d The deck from which to "seed" the pile.
	 */
	public Pile(Deck d, Table t) {
		cards = new Stack<Card>();
		cards.add(d.takeCard());
		table = t;
	}
	
	/**
	 * Creates a pile using c as the first card; used mainly to "recreate" the pile after the deck has been exhausted.
	 * @param c The card to start the pile with.
	 */
	public Pile(Card c, Table t) {
		cards = new Stack<Card>();
		cards.add(c);
		table = t;
	}
	
	/**
	 * Adds a card to the pile, taking care to ensure the move is legal.
	 * @param ncard
	 */
	public void addCard(Card ncard) {
		cards.add(ncard);
	}
	
	/**
	 * Shows us the top card of the pile. This is always allowed per the rules, so that players know what moves are legal.
	 * @return The top card of the pile.
	 */
	public Card getTopCard() {
		return cards.peek();
	}
	
	/**
	 * Used to recreate the deck d with the cards in the pile after the deck has been exhausted. Deck.reseed() should never be called by the user; use this method instead.
	 * Keeps the top card of the pile as the first card of the new pile and adds all of the rest of the cards to d.
	 * @param d The deck which we will reseed.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	public void reseed(Deck d) throws IllegalArgumentException,IllegalStateException {
		if(d.countCards() != 0) {
			throw new IllegalArgumentException("Trying to reseed with a non-empty deck!");
		}
		
		Card top = cards.pop(); // keep the top card of the pile for later.
		
		d.reseed(cards);
		
		if(cards.size() != 0) {
			for(Card c : cards) {
				System.out.println(c);
			}
			
			throw new IllegalStateException("deck.reseed(Stack<Card>) didn't empty the pile.");
		}
		
		cards.add(top);
	}
}
