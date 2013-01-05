package com.sgtcodfish.eins;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Defines a list of scores for a single game of EINS. Intended to be used to support multi-game "tournaments" with persistant scores.
 * 
 * Note that if there are two matching names in the list of players, one will be renamed.
 * @author Ashley Davis (SgtCoDFish)
 */
public class ScoreList {
	protected int playerCount;
	protected int datasetCount;
	protected HashMap<String, Integer> hash;
	
	// probably won't work but left in for curiosity's sake
	// will order from lowest to highest
	class HashComparator implements Comparator<String> {
		Map<String, Integer> base;
		
		public HashComparator(Map<String, Integer> n) {
			this.base = n;
		}
		
		@Override
		public int compare(String a, String b) {
			if(base.get(a) >= base.get(b)) {
				return 1;
			} else {
				return -1;
			}
		}
	}
	
	public ScoreList() {
		playerCount = 0;
		hash = new HashMap<String, Integer>();
	}
	
	/**
	 * Adds a vector of players to the hashtable, using a unique ID for their names and storing their scores.
	 * @param players
	 * @throws IllegalArgumentException
	 */
	public void addScores(Vector<CardEntity> players) throws IllegalArgumentException {
		// need to check to make sure there are no duplicate names in players
		// this could happen if a player enters their name as "Computer 1" for example.
		
		for(CardEntity ce : players) { // iterate through each player, check for name conflicts, and then add to the hashtable.
//			for(CardEntity in : players) {
//				if(ce != in) {
//					if(ce.getName().compareTo(in.getName()) == 0) {
//						// we have a name conflict, change ce's name
//						ce.setName(ce.getName() + " (")
//					}
//				}
//			}
			
			// to avoid name conflicts, which could only be caused by human input, add [HUMAN] or [COMPUTER] depending on which derived class of CardEntity we're using.
			
			String key = ce.getSubclassIdentifier() + ce.getName();
			if(hash.containsKey(key) == true) {
				int nscore = hash.get(key) + ce.tallyValues();
				hash.put(key, nscore);
			} else if(datasetCount == 0) {
				hash.put(key, ce.tallyValues());
			} else {
				throw new IllegalArgumentException("Trying to use a different player list in a constant ScoreList!");
			}
		}
		
		playerCount = hash.size();
		datasetCount++;
	}
	
	/**
	 * Prints all the current scores
	 */
	public void printScores() {
		System.out.println();
		for(int i = 0; i < 10; i++) {
			System.out.print("><");
		}
		
		System.out.println("\nCurrent standings:");
		int val = -1;
		
		for(String key : hash.keySet()) {
			val = hash.get(key);
			System.out.println(key + " has " + val + (val == 1 ? " point!" : " points!"));
		}
		
		for(int i = 0; i < 10; i++) {
			System.out.print("><");
		}
		
		System.out.println();
	}
	
	/**
	 * Prints the scores, ordered from highest value to lowest.
	 */
	public void printScoresFinal() {		
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(new HashComparator(hash));
		sortedMap.putAll(hash);
		
		int lcount = 0;
		for(String s : sortedMap.keySet()) {
			lcount++;
			System.out.println("In position " + lcount + " we have " + s + " with a score of: " + hash.get(s));
		}
	}
	
	/**
	 * Returns the number of times addScores has been called
	 */
	public int getDatasetCount() {
		return datasetCount;
	}
}
