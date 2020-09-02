package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BidCard extends BaseEntity {

    private List<Bid> bids;

    private int minBid;

    private int credits;

    private List<Purchase> purchases;

    private User owner;

    private Card tradingCard;

    private User highestBidder;

    private int highestBid;

    private LocalDateTime date;

    public BidCard(User owner, Card tradingCard, int minBid, LocalDateTime date) {
        super();
        this.bids = new ArrayList<>();
        this.purchases = new ArrayList<>();
        this.owner = owner;
        this.tradingCard = tradingCard;
        this.minBid = minBid;
        this.highestBid = Integer.MIN_VALUE;
        this.date = date;
    }

    public BidCard() {
        super();
        this.bids = new ArrayList<>();
        this.purchases = new ArrayList<>();
    }

    public int getMinBid() {
        return minBid;
    }

    public void setMinBid(int minBid) {
        this.minBid = minBid;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setTradingCard(Card tradingCard) {
        this.tradingCard = tradingCard;
    }

    public User getOwner() {
        return owner;
    }

    public Card getTradingCard() {
        return tradingCard;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void addPurchase(Purchase purchase) {
        this.purchases.add(purchase);
    }

    public void addBid(Bid bid) {
        this.bids.add(bid);
        this.findHighestBidder(bid);
    }

    public List<Bid> getBids() {
        return Collections.unmodifiableList(bids);
    }

    public List<Purchase> getPurchases() {
        return Collections.unmodifiableList(purchases);
    }

    public User getHighestBidder() {
        return highestBidder;
    }

    public int getHighestBid() {
        return highestBid;
    }

    private void findHighestBidder(Bid bid) {
        if (bid.getGold() > highestBid) {
            this.highestBid = bid.getGold();
            this.highestBidder = bid.getBidder();
        }
    }

    public void setHighestBidder(User highestBidder) {
        this.highestBidder = highestBidder;
    }

    public void setHighestBid(int highestBid) {
        this.highestBid = highestBid;
    }

}
