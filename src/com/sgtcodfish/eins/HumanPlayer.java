package com.sgtcodfish.eins;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.sgtcodfish.eins.Card.CardColour;

/**
 * Defines a human player. Usually handled by {@link Table the Table class}.
 * @author Ashley Davis (SgtCoDFish).
 */
public class HumanPlayer extends CardEntity {
	protected String name;
	
	protected boolean didAccuse = false; // true if the player accused others of saying EINS this turn. Protects against humans drawing the whole deck.
	protected boolean didSayEINS = false; // true if the player shouted EINS this turn. Protects against humans drawing the whole deck.
	
	/**
	 * Creates a player with the name nname
	 * @param nname The name to use for the player.
	 */
	public HumanPlayer(String nname) {
		super(nname);
	}
	
	/**
	 * Executes a turn by asking the player for appropriate input to choose a move to make.
	 */
	@Override
	public void doTurn() {
		Vector<Card> legal = null;
		int clooper = 1;
		int lsize = -1;
		int userInput = -1;
		boolean reprint = true; // true if the player's hand might've been changed since they last saw it and they'll need to have it printed again.
		didSayEINS = false;
		didAccuse = false;
		
		table.getIOHandler().println("~~~~~");
		
		do {
			if(reprint) {
				// work out what options the player has in terms of moves to make.
				// first, work out how many different cards the player can play, if any.
				if(legal != null) legal.clear();
				legal = new Vector<Card>();
				for(Card c : cards) {
					if(table.isLegal(c)) {
						// if the above are all true, this card isn't legal to play.
						legal.add(c);
					}
				}
				
				// we now have a list of legal moves, and we need to output them if we have at least one, or failing that give the player the option to draw a card.
				printHand();
				table.getIOHandler().print("\n");
				
				//System.out.println("Current card in play: " + table.getPile().getTopCard());
				lsize = legal.size(); // for speed reasons
				
				reprint = false;
			}
			
			clooper = 1;
			if(lsize >= 1) {
				table.getIOHandler().println("Your options: ");
				for(Card c : legal) {
					table.getIOHandler().println("Enter " + clooper + " to: Play your " + c + ".");
					clooper++;
				}
			} else {
				table.getIOHandler().println("You have no legal cards!\nEnter 1 to: Draw a card.");
			}
			
			table.getIOHandler().println("Enter " + (lsize + 2) + " to: Shout EINS!");
			table.getIOHandler().println("Enter " + (lsize + 3) + " to: Accuse players of forgetting to shout EINS!");
			table.getIOHandler().println("Enter " + (lsize + 4) + " to: See a list of current players and how many cards they have!");
			table.getIOHandler().println("Enter " + (lsize + 5) + " to: Quit the game.");
			
			userInput = -1;
			
			table.getIOHandler().println("Enter a number corresponding to the above menu of choices. For help, enter 0.");
			// readInt() will cause a flush here
			
			try {
				userInput = table.getIOHandler().readInt();
			} catch(InputMismatchException ime) {
				table.getIOHandler().println("\nOnly type in a number, nothing else!\n");
				userInput = -1;
			} catch(NoSuchElementException nsee) {
				table.getIOHandler().printErrorln("NSEE: " + nsee);
				table.getIOHandler().printErrorln("userInput = " + userInput);
				userInput = -1;
			}
		
			if(userInput == 1 && lsize == 0) {
				break;
			}
			
			if(userInput < 0 || userInput > (lsize + 5)) {
				table.getIOHandler().println("Enter a number between 0 and " + (lsize + 5) + ", not including " + (lsize + 1) + "!");
			} else if(userInput == 0) {
				table.getIOHandler().println("Choose one of the options shown above. For example, enter " + (lsize + 2) + " to shout EINS!");
			} else if(userInput == lsize + 2) {
				if(!didSayEINS) { // make sure the user didn't already shout EINS this turn.
					// The user chose to shout eins, which means we handle that and let them enter another menu option.
					int bcc = countCards();
					table.getIOHandler().doDelay(1000);
					sayEins();
					if(countCards() > bcc) {
						reprint = true; // picked up some cards so need to print our new hand
						table.getIOHandler().doDelay(2500);
					}
					
					didSayEINS = true;
				} else {
					table.getIOHandler().println("You already shouted EINS this turn!");
				}
				
				userInput = -1;
			} else if(userInput == lsize + 3) {
				if(!didAccuse) {
					// The user chose to accuse the last player of forgetting to shout eins, so we handle that and let them choose a new menu option.
					int bcc = countCards();
					table.getIOHandler().doDelay(1000);
					accuseEins();
					
					if(countCards() > bcc) {
						reprint = true;
						table.getIOHandler().doDelay(2500);
					}
					
					didAccuse = true;
				} else {
					table.getIOHandler().println("You already accused others of not saying EINS this turn!");
				}
			
				userInput = -1;
			} else if(userInput == lsize + 4) {
				// The user wants to see a list of current players.
				table.getIOHandler().print("\n");
				table.printPlayers();
				table.getIOHandler().print("\n");
				userInput = -1;
			} else if(userInput == lsize + 5) {
				// Need to quit!
				table.requestEnd();
//				scanner.close();
				return;
			} else if(userInput == (lsize + 1)) {
				userInput = -1;
			} else {
				break;
			}
		} while(true);
		
		if(lsize >= 1) { // if there are legal moves, to get to this point the player must have chosen to make one of them, so make it
			// the user chose to play a card
			playCard(legal.elementAt(userInput-1));
		} else { // if there aren't legal moves, all there is left is to draw.
			Card drawn = takeCard(table.getDeck());
			table.getIOHandler().println(getName() + " drew " + drawn + "!");
			if(table.isLegal(drawn)) {
				// need to show another menu to allow for human players to say EINS
				int secondInput = -1;
				//scanner = new Scanner(System.in);
				
				do {
					table.getIOHandler().println("\nChoose what to do:");
					table.getIOHandler().println("Enter 1 to: Play the " + drawn + " you just picked up!");
					table.getIOHandler().println("Enter 2 to: Shout EINS!");
					// readInt will cause a flush here.
					
					try {
						secondInput = table.getIOHandler().readInt();
					} catch(InputMismatchException ime) {
						table.getIOHandler().println("Only type in a number, nothing else!");
						secondInput = -1;
					} catch(NoSuchElementException nsee) {
						table.getIOHandler().printErrorln("NSEE: " + nsee);
						table.getIOHandler().printErrorln("userInput = " + secondInput);
						secondInput = -1;
					}
					
					if(secondInput == 1) {
						break;
					} else if(secondInput == 2) {
						sayEins();
					}
					
				} while(true);
				
				//System.out.println(getName() + " plays the drawn " + drawn + "!");
				playCard(drawn);
			} else {
				playCard(null);
			}
		}
		
		table.getIOHandler().println("~~~~~");
	}
	
	/**
	 * This implementation asks the user for a command-line input, and takes the first character of whatever they choose to input and checks if it matches a colour.
	 * If so, it returns that colour. If not, it asks again.
	 * For example, input "foo bar" will ask again, while "bar foo" will return CardColour.BLUE.
	 */
	@Override
	public CardColour askForColour() {
		String ncol = new String();
		String first = new String();
		
		do {
			table.getIOHandler().println("Input the colour you want to change to. Either (R)ed, (G)reen, (B)lue, or (Y)ellow. Only the first character of your input will be considered.");
			// readString will cause a flush here
			
			try {
				ncol = table.getIOHandler().readString();
			} catch(InputMismatchException ime) {
				ncol = "";
			}
			
			if(ncol.length() != 0) {
				first = Character.toString(ncol.charAt(0));
			
				if(first.compareToIgnoreCase("R") == 0 || first.compareToIgnoreCase("G") == 0 || first.compareToIgnoreCase("B") == 0 || first.compareToIgnoreCase("Y") == 0) break;
			}
		} while(true);
		
		CardColour choice = CardColour.BLACK; // if we actually return BLACK something has gone horifically wrong.
		
		if(first.compareToIgnoreCase("R") == 0) {
			choice = CardColour.RED;
		} else if(first.compareToIgnoreCase("B") == 0) {
			choice = CardColour.BLUE;
		} else if(first.compareToIgnoreCase("G") == 0) {
			choice = CardColour.GREEN;
		} else if(first.compareToIgnoreCase("Y") == 0) {
			choice = CardColour.YELLOW;
		}
		
		return choice;
	}
	
	/**
	 * Returns a string identifying this entity as a human
	 */
	@Override
	public String getSubclassIdentifier() {
		return "[HUMAN]";
	}
}
