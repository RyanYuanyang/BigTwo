/**
 * The BigTwoCard class is a subclass of the Card class and is used to model a card used in a 
 * Big Two card game.It overrides the initialize() method it inherits from the 
 * Deck class to create a deck of Big Two cards
 * 		
 * @author Tu Ryan Yuanyang
 *
 */
public class BigTwoCard extends Card{
	/**
	 * A constructor for building a card with the specified suit and rank.
	 * 
	 * @param rank an integer specifying the rank of the card
	 * @param suit an integer specifying the suit of the card
	 */
	public BigTwoCard(int suit, int rank){
		super(suit,rank);
	}
	/**
	 * The BigTwoDeck class is a subclass of the Deck class and is used to model a deck of cards 
	 * used in a Big Two card game
	 * 
	 * @return a negative integer, zero, or a positive integer when this card is less than, equal to, or greater than the specified card
	 */
	public int compareTo(Card card) {
		int newRank1 = this.rank;
		int newRank2 = card.rank;
		if (this.rank < 2){
			newRank1 = 13 + this.rank;
		}
		if (card.rank < 2){
			newRank2 = 13 + card.rank;
		}
		if (newRank1 > newRank2) {
			return 1;
		} else if (newRank1 < newRank2) {
			return -1;
		} else if (this.getSuit() > card.getSuit()) {
			return 1;
		} else if (this.getSuit() < card.getSuit()) {
			return -1;
		} else {
			return 0;
		}
	}
}
