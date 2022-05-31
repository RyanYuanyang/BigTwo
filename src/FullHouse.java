/**
 * The FullHouse class is a subclass of hand used to model a FullHouse hand. 
 * It overrides the isValid and the getType methods.
 * 
 * @author Tu Ryan Yuanyang
 *
 */
public class FullHouse extends Hand{
	/**
	 * An inherited constructor which calls the constructor of its super class 
	 * 
	 * @param player The player that this hand belongs to
	 * @param cards The card list representing this hand
	 */
	public FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	/**
	 * a method for checking if this is a valid hand
	 * 
	 * @return whether this is a valid hand
	 */
	public boolean isValid() {
		if(this.size() != 5) {
			return false;
		}
		this.sort();
		int rank1 = this.getCard(0).getRank();
		int rank2 = this.getCard(4).getRank();
		int rank1Count = 0;
		int rank2Count = 0;

		for (int i = 0; i < this.size(); i++) {
			if (this.getCard(i).getRank() == rank1) {
				rank1Count++;
			}
			else if (this.getCard(i).getRank() == rank2) {
				rank2Count++;
			}
			else {
				return false;
			}
		}
		if (rank1Count == 3 && rank2Count == 2) {
			return true;
		}
		if (rank1Count == 2 && rank2Count == 3) {
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
		return "Full House";
	}

}
