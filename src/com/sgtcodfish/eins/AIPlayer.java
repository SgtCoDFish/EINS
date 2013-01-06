package com.sgtcodfish.eins;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import com.sgtcodfish.eins.Card.CardColour;

/**
 * AIPlayer defines an AI (i.e. computer-controlled player).
 * @author Ashley Davis (SgtCoDFish)
 */
public class AIPlayer extends CardEntity {
	/**
	 * AIDifficulty determines how well the AI will play the game:
	 * - REGULAR difficulty makes the correct decision most of the time, but will play random legal cards, making it not as "good" as BEST
	 *   
	 * - BEST will always play the highest value cards immediately to get the lowest score possible at the end.
	 *   > BEST will never forget to say EINS, and will always pick up on another player's failure to say it.
	 *   > BEST will always play when possible and will always try to make the best possible move value-wise.
	 *   
	 * See {@link AIPlayer.askForColour()} for a description of what happens when the function is called depending on the chosen difficulty.
	 * 
	 * There used to be STUPID difficulty as well, but this was redundant after testing BEST.
	 * @author Ashley Davis (SgtCoDFish)
	 *
	 */
	enum AIDifficulty {
		REGULAR,
		BEST;
	}
	
	protected AIDifficulty difficulty;
	
	public AIPlayer(String nname, AIDifficulty ndifficulty) {
		super(nname);
		difficulty = ndifficulty;
	}
	
	/**
	 * Executes a turn using the appropriate AI difficulty. Split into three protected functions for ease of maintenance.
	 * @throws IllegalStateException 
	 * @throws IllegalArgumentException 
	 */
	@Override
	public void doTurn() throws IllegalArgumentException, IllegalStateException {
		// sleep for a bit so the user isn't overwhelmed with output
		try {
			final int DELAY = 700;
			
			System.out.print(difficulty + " AI (" + getName() + ") engaged. Calculating move");
			
			Thread.sleep(DELAY);
			System.out.print(".");
			
			Thread.sleep(DELAY);
			System.out.print(".");
			
			Thread.sleep(DELAY);
			System.out.println(".");
		} catch(InterruptedException ie) {}
		
		if(difficulty == AIDifficulty.REGULAR) {
			doTurnRegular();
		} else if(difficulty == AIDifficulty.BEST) {
			doTurnBest();
		}
	}
	
	/**
	 * Execute a turn regularly. See AIDifficulty for a description of what constitues "REGULAR" AI.
	 */
	protected void doTurnRegular() {
		// loop through cards to establish legal moves, and then play a random legal card. If no legal moves exist, draw a card.
		// says EINS 90% of the time when down to one card.
		// makes correct EINS accusations (that is, accuses when it thinks someone has forgotten to say EINS) 90% of the time one is available.
		// makes incorrect EINS accusations (that is, accuses of eins when it knows it is false) 1% of the time.
		
		Vector<Card> legal = new Vector<Card>();
		Random random = new Random();
		
		if(random.nextFloat() < 0.01f) {
			// 1% of the time, accuse people of forgetting to say eins regardless of whether we think they have forgotten or not.
			// note this can (rarely) be a correct accusation
			accuseEins();
		} else if(table.checkEins()) { // someone has forgotten to say EINS, so correctly accuse 90% of the time
			if(random.nextFloat() < 0.9f) {
				accuseEins();
			}
		}
		
		for(Card c : cards) {
			if(table.isLegal(c)) {
				legal.add(c);
			}
		}
		
		if(legal.size() == 0) {
			// no legal cards so draw (and play then if possible)
			System.out.println("REGULAR AI: No legal cards found, drawing...");
			Card latest = table.getDeck().takeCard();
			takeCard(latest);
			if(table.isLegal(latest)) {
				// we can play our new card!
				System.out.println("REGULAR AI: Drawn card is legal, playing " + latest + "!");
				
				if(countCards() == 2) {
					// we're going to have EINS again, so we need to say it 90% of the time
					if(random.nextFloat() < 0.9f) {
						sayEins();
					}
				}
				
				playCard(latest);
			} else {
				// no legal move still, so report this.
				playCard(null);
			}
		} else {
			//there's at least one legal move, so make one!
			//System.out.println("REGULAR AI: " + legal.size() + " legal cards found, playing...");
			
			if(countCards() == 2) {
				// we'll be on EINS, so we need to shout this, but we make a mistake 10% of the time and forget
				if(random.nextFloat() < 0.9f) {
					// we won't "forget" to say it
					sayEins();
				}
			}
			
			playCard(legal.elementAt(random.nextInt(legal.size())));
		}
	}
	
	/**
	 * Execute a turn in the best way possible for the AI. See AIDifficulty for a description of what constitues "BEST" AI.
	 * @throws IllegalStateException 
	 * @throws IllegalArgumentException 
	 */
	protected void doTurnBest() throws IllegalArgumentException, IllegalStateException {
		for(Card c : cards) {
			if(c.getColour() == CardColour.BLACK) {
				// we found a black card so play it straight away and the turn is over. The colour we pick is asked of us later, by the Table class.
				//System.out.println("BEST AI: Found black card, playing.");
				if(countCards() == 2) {
					sayEins();
				}
				
				playCard(c);
				return;
			}
		}
		
		if(table.checkEins()) { // always accuse people of forgetting to say EINS if they actually did forget
			accuseEins();
		}
		
		// we don't have any black cards, so now we want to see what's legal for us to play.
		Vector<Card> legal = new Vector<Card>();
		for(Card c : cards) {
			if(table.isLegal(c)) {
				legal.add(c);
			}
		}
		
		if(legal.size() == 0) {
			// we don't have any legal moves to play, so we need to draw a card.
			System.out.println("BEST AI: No legal cards found, drawing card.");
			Card latest = table.getDeck().takeCard();
			takeCard(latest);
			if(table.isLegal(latest)) {
				System.out.println("BEST AI: Drawn card is legal, playing " + latest + "!");
				
				if(countCards() == 2) { // always say EINS if we need to.
					sayEins();
				}
				
				playCard(latest); // we drew a card we can play, so play it!
			} else {
				// we drew a card and couldn't play; need to make sure the table knows about this
				playCard(null);
			}
		} else {
			// there's at least one legal move, so we need to work out which is the best one (by value)
			// System.out.println("BEST AI: Legal card found, playing!");
			if(legal.size() == 1) {
				if(countCards() == 2) { // if we only have 2 cards left, say EINS
					sayEins();
				}
				
				playCard(legal.elementAt(0));
			} else {
				int highestIndex = 0;
				int highestValue = -1;
				
				for(Card c : legal) {
					if(c.getValue() > highestValue) {
						highestValue = c.getValue();
						highestIndex = legal.indexOf(c);
					}
				}
				
				if(countCards() == 2) { // if we only have 2 cards left, say EINS
					sayEins();
				}
				
				// we have our best move, so play it
				playCard(legal.elementAt(highestIndex));
			}
		}
	}
	
	/**
	 * AI implementation of askForColour(). The returned value depends on the difficulty:
	 * REGULAR works out what the most populous colour is, and if there are two or more colours with the same largest number of cards, chooses one at random.
	 * BEST always chooses the best colour based on the cards it has; if there are two or more colours with the same largest number of cards, it chooses based on value.
	 */
	@Override
	public CardColour askForColour() {
		CardColour result = CardColour.BLACK; // This is a nonsense value since we are asking for a non-black colour. The AI should always change this.
		CardColour[] colours = CardColour.getMainColoursAsArray();
		
		// Work out what the best colour to choose is.
		Vector<CardColour> biggest = new Vector<CardColour>(); // vector in case there are two/three/four colours with the same number of cards.
		int biggestCount = 0;
		
		for(CardColour c : colours) {
			int colCount = countCardsOfColour(c);
			
			if(colCount == biggestCount) { // we have another of the same size as the previous biggest, so add it.
				biggest.add(c);
			} else if(colCount > biggestCount) { // we have a new biggest, so all previous biggest are out
				biggest.clear();
				biggest.add(c);
				biggestCount = colCount;
			}
		}
		
		if(biggest.size() == 1) {
			result = biggest.firstElement();
		} else if(difficulty == AIDifficulty.REGULAR) { // we have more than one "most common colour" so return a random one.
			Random random = new Random();
			result = biggest.elementAt(random.nextInt(biggest.size()));
		} else if(difficulty == AIDifficulty.BEST) {
			Hashtable<CardColour, Integer> ht = new Hashtable<CardColour, Integer>(4);
			
			for(CardColour bigs : biggest) { // work out what the highest value colour is, i.e. if we have 2 red stops and 2 yellow "3" numbers, we choose red.
				int totalValue = 0; // the total value of this colours' cards.
				
				for(Card c : cards) { // iterate through our cards and total the values for each of our colours
					if(c.getColour() == bigs) {
						totalValue += c.getValue();
					}
				}
				
				ht.put(bigs, totalValue);
			}
			
			// we have a hashtable of the total values of the most populous colour cards in our hand, now work out which is biggest.
			Vector<CardColour> biggestValue = new Vector<CardColour>();
			int biggestValueNum = -1;
			for(CardColour cc : ht.keySet()) {
				if(ht.get(cc) == biggestValueNum) { // we have at least two most populous cards with the same value, so we'll have to pick at random if we don't find a bigger valued one 
					biggestValue.add(cc);
				} else if(ht.get(cc) > biggestValueNum) {
					biggestValue.clear();
					biggestValue.add(cc);
					biggestValueNum = ht.get(cc);
				}
			}
			
			if(biggestValue.size() > 1) {
				Random random = new Random();
				int ind = random.nextInt(biggestValue.size());
				result = biggestValue.elementAt(ind);
			} else {
				result = biggestValue.firstElement();
			}
		}
		
		return result;
	}
	
	/**
	 * Returns a string identifying this entity as an AI player.
	 */
	@Override
	public String getSubclassIdentifier() {
		return "[COMPUTER]";
	}
}
