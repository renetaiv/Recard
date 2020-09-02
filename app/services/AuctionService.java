package services;

public interface AuctionService {

    void listAuction();

    void auctionBuy(int cardId, int credits);

    void auctionBid(int cardId, int gold);

    void setAuctionCard(int cardId, int minBid);

    void printAuctionedCardsHistory();

    void printLastTurnsHistory();
}
