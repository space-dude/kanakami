package kana;

import java.util.Random;
import java.util.ArrayList;

/**
 * 
 * @author Cory Carpenter
 *
 */

public class Deck extends ArrayList<Card> {
	
	private static final long serialVersionUID = 1L;
	
	private final static int MIN_NUM_OF_CARDS = 1;
	private final static int MAX_NUM_OF_CARDS = 46 + 25;
	private int deckSize = 0;
	
	private String deckName = "";
	
	// Switch to Singly Linked Lists?
	ArrayList<Card> deckOfCards = new ArrayList<Card>();
	
	ArrayList<Card> sortedCards = new ArrayList<Card>();
	
	public Deck() {
		this.deckName = "Untitled";
	}
	
	public Deck(ArrayList<Card> deck) {
		this.deckOfCards = deck;
		this.deckName = "Untitled";
		this.deckSize = deckOfCards.size();
	}
	
	public Deck(ArrayList<Card> deck, String name) {
		this.deckOfCards = deck;
		this.deckName = name;
		this.deckSize = deck.size();
	}
	
	public void addCard(Card card) {
		//If less than the MAXSIZE
		deckOfCards.add(card);
		sortedCards.add(card);
		deckSize++;
	}
	
	public void removeCard(Card card) {
		deckOfCards.remove(card);
		sortedCards.remove(card);
		deckSize--;
	}
	
	public Card drawCard() {
		Card drawnCard = deckOfCards.get(deckOfCards.size()-1);
		return drawnCard;
	}
	
	public void placeInDeck(Card card) {
		deckOfCards.add(card);
		shuffle(deckOfCards);
	}
	
	public void placeInDeck(ArrayList<Card> cards) {
		for(int i = 0; i < cards.size(); i++)
			deckOfCards.add((Card) cards.get(i));
		shuffle(deckOfCards);
	}
	
	public void placeInDeckBottom(Card card) {
		deckOfCards.add(card);
	}
	
	public void placeInDeckTop(Card card) {
		deckOfCards.add(0, card);
	}
	
	public ArrayList<Card> lookAtTopCards(int n) {
		ArrayList<Card> peekingCards = new ArrayList<Card>();
		
		for(int i = 0; i < n; i++)
			if(deckOfCards.size() >= n)
				peekingCards.add(deckOfCards.get(i));
		
		return peekingCards;
	}
	
	public void shuffle() {
		
		  ArrayList<Card> list = this.getDeck();
		
		  Random rnd = new Random();
		  
		  //Creates an array list of randomly generated indexes for current values
		  ArrayList<Integer> indexes = new ArrayList<Integer>();
		  for(Integer i = 0; i < list.size(); i++) {
			  int randomNum = rnd.nextInt(list.size());
			  while(indexes.contains(randomNum)) {
				  randomNum = rnd.nextInt(list.size());
			  }
			  indexes.add(randomNum);
		  }
		  
		  //Creates an array with the values of list in their new indexes
		  Object[] listSwap = new Object[list.size()];
		  for(int i = 0; i < indexes.size(); i++) {
			  listSwap[indexes.get(i)] = list.get(i);
		  }
		  
		  //Changes list to match listSwap
		  list.clear();
		  for(int i = 0; i < listSwap.length; i++) {
			  list.add((Card)listSwap[i]);
		  }
	  }
	
	public static void shuffle(ArrayList<Card> list) {
		  Random rnd = new Random();
		  
		  //Creates an array list of randomly generated indexes for current values
		  ArrayList<Integer> indexes = new ArrayList<Integer>();
		  for(Integer i = 0; i < list.size(); i++) {
			  int randomNum = rnd.nextInt(10);
			  while(indexes.contains(randomNum))
				  randomNum = rnd.nextInt(10);
			  indexes.add(randomNum);
		  }
		  
		  //Creates an array with the values of list in their new indexes
		  Object[] listSwap = new Object[list.size()];
		  for(int i = 0; i < indexes.size(); i++) {
			  listSwap[indexes.get(i)] = list.get(i);
		  }
		  
		  //Changes list to match listSwap
		  list.clear();
		  for(int i = 0; i < listSwap.length; i++) {
			  list.add((Card)listSwap[i]);
		  }
	  }
	
	public void sortDeck() {
		
		for(int i = 0; i < sortedCards.size(); i++) {
			deckOfCards.set(i, sortedCards.get(i));
		}
		
	}
	
	public void update() {
		//Implement when graphics are added
	}
	
	public int getSize() {
		return this.deckSize;
	}
	
	public Card getCard(int index) {
		// TODO: Need to code for proper indexing
		if (deckOfCards.size() >= this.size()) {
			if (index < deckOfCards.size())
				return deckOfCards.get(index);
			else
				return null;
		} else {
			// System.out.println("deckOfCards.size(): " + deckOfCards.size() + ", deck.size(): " + this.size());
			if (index < size())
				return get(index);
			else
				return null;
		}
	}
	
	public String getName() {
		return this.deckName;
	}
	
	public ArrayList<Card> getDeck() {
		return this.deckOfCards;
	}
	
}