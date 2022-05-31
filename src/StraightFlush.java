/**
 * The StraightFlush class is a subclass of hand used to model a Straight Flush hand. 
 * It overrides the isValid and the getType methods.
 * 
 * @author Tu Ryan Yuanyang
 *
 */
public class StraightFlush extends Hand{
	/**
	 * An inherited constructor which calls the constructor of its super class 
	 * 
	 * @param player The player that this hand belongs to
	 * @param cards The card list representing this hand
	 */
	public StraightFlush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	/**
	 * a method for checking if this is a valid hand
	 * 
	 * @return whether this is a valid hand
	 */
	public boolean isValid() {
		if (this.size() == 5) {
			this.sort();
			int first = this.getCard(0).getRank();
			int suit = this.getCard(0).getSuit();
			
			for (int i = 1; i < 5; i++) {
				int current = this.getCard(i).getRank();
				if (current < 2) {
					current += 13;
				}
				if (current != first + i | this.getCard(i).getSuit() != suit) {
					return false;
				}
			}
		}
		else {
			return false;
		}
		return true;

	}
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return a string specifying the type of this hand
	 */
	public String getType() {
		return "StraightFlush";
	}

}
