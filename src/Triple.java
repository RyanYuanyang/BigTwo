/**
 * The Triple class is a subclass of hand used to model a Triple hand. 
 * It overrides the isValid and the getType methods.
 * 
 * @author Tu Ryan Yuanyang
 *
 */
public class Triple extends Hand{
	/**
	 * An inherited constructor which calls the constructor of its super class 
	 * 
	 * @param player The player that this hand belongs to
	 * @param cards The card list representing this hand
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	/**
	 * a method for checking if this is a valid hand
	 * 
	 * @return whether this is a valid hand
	 */
	public boolean isValid() {
		if (this.size() == 3 && this.getCard(0).getRank() == this.getCard(1).getRank() && this.getCard(1).getRank() == this.getCard(2).getRank()) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return a string specifying the type of this hand
	 */
	public String getType() {
		return "Triple";
	}

}
