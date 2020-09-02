package entities;

public class Bid extends BaseEntity {

    private User bidder;

    private int gold;

    public Bid(User bidder, int gold) {
        super();
        this.bidder = bidder;
        this.gold = gold;
    }

    public User getBidder() {
        return bidder;
    }

    public void setBidder(User bidder) {
        this.bidder = bidder;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
