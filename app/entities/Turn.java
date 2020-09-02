package entities;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Turn extends BaseEntity {

    private List<BidCard> bidCards;

    private LocalDateTime date;

    public Turn() {
        super();
        this.bidCards = new ArrayList<>();
        this.date = LocalDateTime.now();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void addBidCard(BidCard bidCard){
        this.bidCards.add(bidCard);
    }

    public List<BidCard> getBidCards() {
        return Collections.unmodifiableList(this.bidCards);
    }

    public List<BidCard> getBidsCardsFromTheGame() {
        return bidCards.stream().filter(x -> x.getOwner() == null).collect(Collectors.toList());
    }

    public List<BidCard> getBidsCardsFromThePlayers() {
        return bidCards.stream().filter(x -> x.getOwner() != null).collect(Collectors.toList());
    }
}
