package com.sgtcodfish.eins;


/**
 * Defines a card implemented in the game.
 * @author Ashley
 *
 */
public class Card implements Cloneable {
	public final static int VAL_BOUND_LOWER = 0; /** The lower bound of value. */
	public final static int VAL_BOUND_UPPER = 9; /** The upper bound of value. */
	
	enum CardType {
		NUMBER,
		SKIP,
		PICKTWO,
		REVERSE,
		CHANGECOL,
		CHANGECOLFOUR;
		
		public String toString() {
			String result = new String();
			
			switch(this) {
			case NUMBER:
				result = "Number Card";
				break;
			
			case SKIP:
				result = "Skip Card";
				break;
				
			case PICKTWO:
				result = "+2 Card";
				break;
				
			case REVERSE:
				result = "Reverse Card";
				break;
				
			case CHANGECOL:
				result = "Wild Card";
				break;
				
			case CHANGECOLFOUR:
				result = "+4 Wild Card";
				break;
			}
			
			return result;
		}
	}
	
	enum CardColour {
		RED,
		BLUE,
		GREEN,
		YELLOW,
		BLACK;
		
		public String toShortString() {
			String result = new String();
			
			if(this == BLACK) {
				result = "Blk";
			} else {
				result += this.toString().charAt(0);
			}
			
			return result;
		}
		
		/**
		 * @return An array, {RED, BLUE, GREEN, YELLOW} that can be used to iterate over colours or for use in a map.
		 */
		public static CardColour[] getMainColoursAsArray() {
			CardColour colours[] = new CardColour[4];
			
			colours[0] = CardColour.RED;
			colours[1] = CardColour.BLUE;
			colours[2] = CardColour.GREEN;
			colours[3] = CardColour.YELLOW;
			
			return colours;
		}
	}
	
	protected CardType type; /** Which of the cards this card is. */
	protected CardColour colour; /** Which colour we have. */
	
	/**
	 * The scoring value of the card when tallying scores at the end of a round. The default scoring table is presented below:
	 * NUMBER: The face value of the card is the scoring value (from 0-9 inclusive)
	 * SKIP, PICKTWO and REVERSE: 20 points.
	 * CHANGECOL and CHANGECOLFOUR: 50 points.
	 */
	protected int value;
	
	/**
	 * Makes this a copy of other
	 * @param other The card to copy.
	 */
	public Card(Card other) {
		type = other.getType();
		colour = other.getColour();
		value = other.getValue();
	}
	
	/**
	 * Creates a card, assumed to be type NUMBER since this is the only type which needs a value.
	 * @param val The value to assign the card (VAL_BOUND_LOWER-VAL_BOUND_UPPER inclusive). Throws exception if val < VAL_BOUND_LOWER, or val > VAL_BOUND_UPPER.
	 * @param ncol The colour of the card. Throws exception if ncol == CardColour.BLACK (since BLACK cards cannot be NUMBER types).
	 * @trrows IllegalArgumentException
	 */
	public Card(int val, CardColour ncol) {
		type = CardType.NUMBER;
		
		if(ncol == CardColour.BLACK) {
			throw new IllegalArgumentException("Trying to create a NUMBER card in colour BLACK.");
		} else {
			colour = ncol;
		}
		
		if(val > VAL_BOUND_UPPER) {
			throw new IllegalArgumentException("Trying to create NUMBER card with value greater than " + VAL_BOUND_UPPER + ", which is the upper bound.");
		} else if(val < VAL_BOUND_LOWER) {
			throw new IllegalArgumentException("Trying to create NUMBER card with value less than " + VAL_BOUND_LOWER + ", which is the lower bound.");
		} else {
			value = val;
		}
	}
	
	/**
	 * Creates a card of type ntype, which completely describes the card unless ntype == CardType.NUMBER, in which case an IllegalArgumentException is thrown.
	 * @param ntype The type of card this is, as described by Card.CardType. Cannot be CardType.NUMBER, for this type use the other constructor.
	 * @param ncol The colour of the card. Makes no sense for types CardType.CHANGECOL and CardType.CHANGECOLFOUR, which are both CardColour.BLACK types.
	 * @throws IllegalArgumentException
	 */
	public Card(CardType ntype, CardColour ncol) throws IllegalArgumentException {
		if(ntype == CardType.NUMBER) {
			throw new IllegalArgumentException("Card(CardType, CardColour) cannot be used to initialise a NUMBER. Use Card(int, CardColour).");
		}
		
		if((ntype == CardType.CHANGECOL || ntype == CardType.CHANGECOLFOUR) && ncol != CardColour.BLACK) {
			throw new IllegalArgumentException("Trying to create a non-BLACK CHANGECOL or CHANGECOLFOUR card.");
		} else if((ntype != CardType.CHANGECOL && ntype != CardType.CHANGECOLFOUR) && ncol == CardColour.BLACK) {
			throw new IllegalArgumentException("Trying to create a " + ntype.toString() + " in colour BLACK.");
		} else {
			type = ntype;
			colour = ncol;
		}
		
		if(type == CardType.CHANGECOL || type == CardType.CHANGECOLFOUR) {
			value = 50;
		} else {
			value = 20;
		}
	}
	
	/**
	 * @return The colour of this card.
	 */
	public CardColour getColour() {
		return colour;
	}
	
	/**
	 * @return The type of this card.
	 */
	public CardType getType() {
		return type;
	}
	
	/**
	 * @return The value of this card.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns a String representation of the card that identifies all its game-related properties in a human-readable format.
	 * The format is "(COLOUR), (CARDTYPE)[(VALUE)]"
	 * So, for example, a red number 7 will be "(R)(Num)[7]"
	 * A black CHANGECOLFOUR will be "(Blk)(+4 Wild Card)[50]"
	 * And a blue SKIP will be "(B)(Skip)[20]"
	 * 
	 * Note that the colour is "Blk" for black and the first letter of the name of the colour otherwise.
	 */
	public String toString() {
		String result = new String();
		
		result += "(" + colour.toShortString() + ")";
		result += "(" + type.toString() + ")";
		result += "[" + value + "]";
		
		
		return result;
	}
	
	/**
	 * Swaps this card with other
	 * @param other The card with which this card will be swapped.
	 */
	public void swap(Card other) {
		Card temp = new Card(this);
		type = other.type;
		colour = other.colour;
		value = other.value;
		
		other.type = temp.type;
		other.colour = temp.colour;
		other.value = temp.value;
		
		temp = null;
	}
}
