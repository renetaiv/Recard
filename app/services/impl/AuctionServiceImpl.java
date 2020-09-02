package services.impl;

import entities.BidCard;
import entities.Card;
import entities.Turn;
import entities.User;
import entities.*;
import services.*;
import java.util.*;
import java.util.stream.Collectors;

public class AuctionServiceImpl implements AuctionService {

    private final CardService cardService;

    private final UserService userService;

    private final List<Turn> previousTurns;

    private Turn awaitingTurn;

    private Turn currentTurn;

    private StringOperationsService stringOperationsService;

    public AuctionServiceImpl(CardService cardService, UserService userService, StringOperationsService stringOperationsService) {
        this.previousTurns = new ArrayList<>();
        this.currentTurn = new Turn();
        this.awaitingTurn = new Turn();
        this.cardService = cardService;
        this.userService = userService;
        startAuction();
        this.stringOperationsService = stringOperationsService;
    }

    @Override
    public void setAuctionCard(int cardId, int minBid) {
        Optional<Card> optionalCard = this.userService.getCardFromUserFreeCardsById(cardId);
        if (optionalCard.isEmpty()) {
            System.out.println("Card with such an id does not exist!");
            return;
        }

        Card userCard = optionalCard.get();

        int lowerBound = (int) (userCard.getPower() * 0.80);
        int upperBound = (int) (userCard.getPower() * 1.3);

        if (minBid < lowerBound || minBid > upperBound) {
            System.out.println("Please enter valid bid! The bid is restricted to 80-130% range around the card power!");
            System.out.printf("Possible ranges is between %d or %d%n", lowerBound, upperBound);
            return;
        }

        BidCard bidCard = new BidCard();
        bidCard.setOwner(this.userService.getLoggedUser());
        bidCard.setTradingCard(userCard);
        bidCard.setMinBid(minBid);
        bidCard.setCredits(calculateCardCredits(minBid));
        bidCard.setDate(this.awaitingTurn.getDate());

        this.userService.removeCardFromUserFreeCards(userCard);
        this.awaitingTurn.addBidCard(bidCard);
        System.out.printf("You have bid successfully on %s.%n", userCard.getName());
    }

    @Override
    public void printAuctionedCardsHistory() {
        System.out.println(auctionedCardsHistory());
    }

    @Override
    public void printLastTurnsHistory() {
        System.out.println(getLastTurnsHistoryString());
    }

    private String getLastTurnsHistoryString() {
        StringBuilder space = new StringBuilder();
        space.append("---------------------------------Last Turns History----------------------------------");
        space.append(System.lineSeparator());
        space.append("  №  | Card ID |             Card Name              |   Won By   |      Won On      |");
        space.append(System.lineSeparator());
        space.append("-------------------------------------------------------------------------------------");
        space.append(System.lineSeparator());
        StringOperationsServiceImpl getTimeOfWinnings = new StringOperationsServiceImpl();

        int counter = 1;
        boolean hasCards = false;
        for (Turn turn : previousTurns) {
            for (int i = 0; i < (turn.getBidCards()).size(); i++) {
                if (turn.getBidCards().get(i).getHighestBidder() == null) {
                    continue;
                }

                hasCards = true;
                space.append(stringOperationsService.countSpacesBefore(5, ("" + counter).length()));
                space.append(counter);
                space.append(stringOperationsService.countSpacesAfter(5, ("" + counter++).length())).append("|");
                space.append(stringOperationsService.countSpacesBefore(9, ("" + (turn.getBidCards().get(i).getId())).length()));
                space.append(turn.getBidCards().get(i).getId());
                space.append(stringOperationsService.countSpacesAfter(9, ("" + (turn.getBidCards().get(i).getId())).length())).append("|");
                space.append(stringOperationsService.countSpacesBefore(36, ("" + (turn.getBidCards().get(i).getTradingCard().getName())).length()));
                space.append(turn.getBidCards().get(i).getTradingCard().getName());
                space.append(stringOperationsService.countSpacesAfter(36, ("" + (turn.getBidCards().get(i).getTradingCard().getName())).length())).append("|");
                space.append(stringOperationsService.countSpacesBefore(12, ("" + (turn.getBidCards().get(i).getHighestBidder().getUsername())).length()));
                space.append(turn.getBidCards().get(i).getHighestBidder().getUsername());
                space.append(stringOperationsService.countSpacesAfter(12, ("" + (turn.getBidCards().get(i).getHighestBidder().getUsername())).length())).append("|");
                space.append(stringOperationsService.countSpacesBefore(18, ("" + (getTimeOfWinnings.getLocalDateTimeFormattedString(turn.getBidCards().get(i).getDate()))).length()));
                space.append(getTimeOfWinnings.getLocalDateTimeFormattedString(turn.getBidCards().get(i).getDate()));
                space.append(stringOperationsService.countSpacesAfter(18, ("" + (getTimeOfWinnings.getLocalDateTimeFormattedString(turn.getBidCards().get(i).getDate()))).length())).append("|");
                space.append(System.lineSeparator());
            }
        }

        if (!hasCards) {
            space.append("|                                  NO CARDS WON                                     |");
            space.append(System.lineSeparator());
        }

        space.append("-------------------------------------------------------------------------------------");
        space.append(System.lineSeparator());

        return space.toString();
    }


    @Override
    public void listAuction() {
        System.out.println(cardsFromTheGame());
        System.out.println(cardsFromThePlayers());
    }

    @Override
    public void auctionBuy(int cardId, int credits) {
        User buyer = this.userService.getLoggedUser();

        Optional<BidCard> bidCardToBuying = findCardById(cardId);
        if (bidCardToBuying.isEmpty()) {
            return;
        }

        BidCard bidCard = bidCardToBuying.get();
        if (isBuy(bidCard, buyer)) {
            System.out.println("You have already bought this card!");
            return;
        }

        long bidSum = calculateSumOfBids(bidCard.getBids(), buyer);
        int sumToBack = (int) (bidSum * 0.80);

        buyer.setGold(buyer.getGold() + sumToBack);

        User owner = bidCard.getOwner();
        if (owner != null) {
            int creditsToOwner = (int) (bidCard.getCredits() * 0.20);
            owner.setCredits(owner.getCredits() + creditsToOwner);
        }

        bidCard.addPurchase(new Purchase(buyer, credits));
        buyer.addCardInFreeDeck(bidCard.getTradingCard());
        System.out.printf("%s has been added to your free cards.%n",
                bidCard.getTradingCard().getName());
    }

    @Override
    public void auctionBid(int cardId, int gold) {
        User user = this.userService.getLoggedUser();

        Optional<BidCard> optionalBidCard = findCardById(cardId);
        if (optionalBidCard.isEmpty()) {
            System.out.println("CardId is invalid or card not exist!");
            return;
        }

        BidCard bidCard = optionalBidCard.get();
        if (isBuy(bidCard, user)) {
            System.out.println("You already buy this card!");
            return;
        }

        if (user.getGold() - gold < 0) {
            System.out.println("You don't have enough gold for this bid.");
            return;
        }

        user.setGold(user.getGold() - gold);
        bidCard.addBid(new Bid(user, gold));
        System.out.printf("You have bid on %s with %d gold.%n",
                bidCard.getTradingCard().getName(),
                gold);
    }


    public void startAuction() {
        Timer timer = new Timer();
        TimerTask updatingTask = new TimerTask() {
            @Override
            public void run() {
                if (userService.getLoggedUser() != null) {
                    giveCardsToWinners(currentTurn);
                }
                previousTurns.add(currentTurn);
                currentTurn = awaitingTurn;
                awaitingTurn = new Turn();
                mapCardsToBidCards(cardService.getRandomCardsByCount(5)).forEach(card -> currentTurn.addBidCard(card));
            }
        };

        mapCardsToBidCards(cardService.getRandomCardsByCount(5)).forEach(card -> currentTurn.addBidCard(card));

        timer.schedule(updatingTask, 300000L, 300000L);
    }

    private void giveCardsToWinners(Turn turn) {
        turn.getBidCards().stream()
                .filter(x -> Objects.nonNull(x.getHighestBidder()))
                .forEach(e -> e.getHighestBidder().addCardInFreeDeck(e.getTradingCard()));
    }

    private Optional<BidCard> findCardById(int cardId) {
        return this.currentTurn.getBidCards().stream()
                .filter(bidCard -> bidCard.getTradingCard().getId() == cardId)
                .findFirst();
    }

    private List<BidCard> mapCardsToBidCards(List<Card> cards) {
        return cards.stream()
                .map(card -> {
                        int minBid = calculateMinBid(card.getPower());
                        int credits = calculateCardCredits(minBid);

                        BidCard c = new BidCard();
                        c.setMinBid(minBid);
                        c.setOwner(null);
                        c.setTradingCard(card);
                        c.setCredits(credits);
                        return c; })
                .collect(Collectors.toList());
    }

    private int calculateMinBid(int cardPower) {
        Random r = new Random();
        double randomValue = 0.80 + (1.30 - 0.80) * r.nextDouble();

        return (int) (cardPower * randomValue);
    }

    private int calculateCardCredits(int gold) {
        int randomTimes = new Random().nextInt((24 - 12 + 1)) + 12;

        return gold / randomTimes;
    }

    private boolean isBuy(BidCard bidCard, User user) {
        return bidCard.getPurchases().stream()
                .anyMatch(purchase -> purchase.getBuyer().getId() == user.getId());
    }

    private long calculateSumOfBids(List<Bid> bids, User user) {
        return bids.stream()
                .filter(bid -> bid.getBidder().getId() == user.getId())
                .map(Bid::getGold)
                .reduce(Integer::sum)
                .orElse(0);
    }

    private String cardsFromTheGame() {
        StringBuilder sb = new StringBuilder();

        int getLengthOfTurn = (currentTurn.getId() + "").length();

        sb.append("----------------------------------------------Auction ")
                .append("Turn ")
                .append(currentTurn.getId())
                .append(stringOperationsService.countDashAfterMainHeading(43, getLengthOfTurn));
        sb.append(System.lineSeparator());
        sb.append("-------------------------------------------CARDS FROM THE GAME----------------------------------------");
        sb.append(System.lineSeparator());
        sb.append("Card ID |             Card Name              | Attack | Defence | Min Bid | Credits |" +
                " Highest Bidder |");
        sb.append(System.lineSeparator());
        sb.append("------------------------------------------------------------------------------------------------------");

        for (BidCard bidCard : currentTurn.getBidsCardsFromTheGame()) {
            sb.append(System.lineSeparator());
            sb.append(stringOperationsService.countSpacesBefore(8, ("" + bidCard.getTradingCard().getId()).length()));
            sb.append(bidCard.getTradingCard().getId());
            sb.append(stringOperationsService.countSpacesAfter(8, ("" + bidCard.getTradingCard().getId()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(36, bidCard.getTradingCard().getName().length()));
            sb.append(bidCard.getTradingCard().getName());
            sb.append(stringOperationsService.countSpacesAfter(36, bidCard.getTradingCard().getName().length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(8, ("" + bidCard.getTradingCard().getAttack()).length()));
            sb.append(bidCard.getTradingCard().getAttack());
            sb.append(stringOperationsService.countSpacesAfter(8, ("" + bidCard.getTradingCard().getAttack()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(9, ("" + bidCard.getTradingCard().getDefense()).length()));
            sb.append(bidCard.getTradingCard().getDefense());
            sb.append(stringOperationsService.countSpacesAfter(9, ("" + bidCard.getTradingCard().getDefense()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(9, ("" + bidCard.getMinBid()).length()));
            sb.append(bidCard.getMinBid());
            sb.append(stringOperationsService.countSpacesAfter(9, ("" + bidCard.getMinBid()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(9, ("" + bidCard.getCredits()).length()));
            sb.append(bidCard.getCredits());
            sb.append(stringOperationsService.countSpacesAfter(9, ("" + bidCard.getCredits()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(16, ("" + (bidCard.getHighestBidder() == null ? "" : bidCard.getHighestBidder())).length()));
            sb.append((bidCard.getHighestBidder() == null ? "" : bidCard.getHighestBidder()));
            sb.append(stringOperationsService.countSpacesAfter(16, ("" + (bidCard.getHighestBidder() == null ? "" : bidCard.getHighestBidder())).length())).append("|");
        }
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    private String cardsFromThePlayers() {
        StringBuilder sb = new StringBuilder();

        sb.append("------------------------------------------CARDS FROM THE PLAYERS------------------------------------------------");
        sb.append(System.lineSeparator());
        sb.append("Card ID |             Card Name              | Attack | Defence |  Owner  | Min Bid | Credits |" +
                " Highest Bidder |");
        sb.append(System.lineSeparator());
        sb.append("----------------------------------------------------------------------------------------------------------------");

        if (currentTurn.getBidsCardsFromThePlayers().isEmpty()) {
            sb.append(System.lineSeparator());
            sb.append("|                                          NO CARDS LISTED BY PLAYERS                                          |");
            sb.append(System.lineSeparator());
            sb.append("----------------------------------------------------------------------------------------------------------------");
            sb.append(System.lineSeparator());
        }

        for (BidCard bidCard : currentTurn.getBidsCardsFromThePlayers()) {
            sb.append(System.lineSeparator());
            sb.append(stringOperationsService.countSpacesBefore(8, ("" + bidCard.getTradingCard().getId()).length()));
            sb.append(bidCard.getTradingCard().getId());
            sb.append(stringOperationsService.countSpacesAfter(8, ("" + bidCard.getTradingCard().getId()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(36, bidCard.getTradingCard().getName().length()));
            sb.append(bidCard.getTradingCard().getName());
            sb.append(stringOperationsService.countSpacesAfter(36, bidCard.getTradingCard().getName().length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(8, ("" + bidCard.getTradingCard().getAttack()).length()));
            sb.append(bidCard.getTradingCard().getAttack());
            sb.append(stringOperationsService.countSpacesAfter(8, ("" + bidCard.getTradingCard().getAttack()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(9, ("" + bidCard.getTradingCard().getDefense()).length()));
            sb.append(bidCard.getTradingCard().getDefense());
            sb.append(stringOperationsService.countSpacesAfter(9, ("" + bidCard.getTradingCard().getDefense()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(9, ("" + bidCard.getOwner().getUsername()).length()));
            sb.append(bidCard.getOwner().getUsername());
            sb.append(stringOperationsService.countSpacesAfter(9, ("" + bidCard.getOwner().getUsername()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(9, ("" + bidCard.getMinBid()).length()));
            sb.append(bidCard.getMinBid());
            sb.append(stringOperationsService.countSpacesAfter(9, ("" + bidCard.getMinBid()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(9, ("" + bidCard.getCredits()).length()));
            sb.append(bidCard.getCredits());
            sb.append(stringOperationsService.countSpacesAfter(9, ("" + bidCard.getCredits()).length())).append("|");
            sb.append(stringOperationsService.countSpacesBefore(16, ("" + bidCard.getHighestBidder()).length()));
            sb.append(bidCard.getHighestBidder());
            sb.append(stringOperationsService.countSpacesAfter(16, ("" + bidCard.getHighestBidder()).length())).append("|");
        }

        return sb.toString();
    }

    private String auctionedCardsHistory() {
        StringBuilder sb = new StringBuilder();

        sb.append("--------------------------------------------Auctioned Cards History------------------------------------------");
        sb.append(System.lineSeparator());
        sb.append("  №  | Card ID |             Card Name              |  Won For |   Won By   |      Won On      |   Actions  |");
        sb.append(System.lineSeparator());
        sb.append("-------------------------------------------------------------------------------------------------------------");
        sb.append(System.lineSeparator());

        int count = 0;
        boolean hasCards = false;
        for (Turn turn : previousTurns) {
            for (int i = 0; i < turn.getBidCards().size(); i++) {
                if (userService.getLoggedUser() == turn.getBidCards().get(i).getOwner()) {
                    if (turn.getBidCards().get(i).getHighestBidder() != null) {
                        hasCards = true;
                        sb.append(stringOperationsService.countSpacesBefore(5, ("" + ++count).length()));
                        sb.append(count);
                        sb.append(stringOperationsService.countSpacesAfter(5, ("" + count).length())).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(9, ("" + turn.getBidCards().get(i).getId()).length()));
                        sb.append(turn.getBidCards().get(i).getId());
                        sb.append(stringOperationsService.countSpacesAfter(9, ("" + turn.getBidCards().get(i).getId()).length())).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(36, ("" + turn.getBidCards().get(i).getTradingCard().getName()).length()));
                        sb.append(turn.getBidCards().get(i).getTradingCard().getName());
                        sb.append(stringOperationsService.countSpacesAfter(36, ("" + turn.getBidCards().get(i).getTradingCard().getName()).length())).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(10, ("" + turn.getBidCards().get(i).getHighestBid()).length()));
                        sb.append(turn.getBidCards().get(i).getHighestBid());
                        sb.append(stringOperationsService.countSpacesAfter(10, ("" + turn.getBidCards().get(i).getHighestBid()).length())).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(12, ("" + turn.getBidCards().get(i).getHighestBidder().getUsername()).length()));
                        sb.append(turn.getBidCards().get(i).getHighestBidder().getUsername());
                        sb.append(stringOperationsService.countSpacesAfter(12, ("" + turn.getBidCards().get(i).getHighestBidder().getUsername()).length())).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(18, ("" + stringOperationsService.getLocalDateTimeFormattedString(turn.getBidCards().get(i).getDate())).length()));
                        sb.append(stringOperationsService.getLocalDateTimeFormattedString(turn.getBidCards().get(i).getDate()));
                        sb.append(stringOperationsService.countSpacesAfter(18, ("" + stringOperationsService.getLocalDateTimeFormattedString(turn.getBidCards().get(i).getDate())).length())).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(12, 0));
                        sb.append(stringOperationsService.countSpacesAfter(12, 0)).append("|");
                        sb.append(System.lineSeparator());
                    } else {
                        sb.append(stringOperationsService.countSpacesBefore(5, ("" + ++count).length()));
                        sb.append(count);
                        sb.append(stringOperationsService.countSpacesAfter(5, ("" + count).length())).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(9, ("" + turn.getBidCards().get(i).getId()).length()));
                        sb.append(turn.getBidCards().get(i).getId());
                        sb.append(stringOperationsService.countSpacesAfter(9, ("" + turn.getBidCards().get(i).getId()).length())).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(36, ("" + turn.getBidCards().get(i).getTradingCard().getName()).length()));
                        sb.append(turn.getBidCards().get(i).getTradingCard().getName());
                        sb.append(stringOperationsService.countSpacesAfter(36, ("" + turn.getBidCards().get(i).getTradingCard().getName()).length())).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(10, 1));
                        sb.append(" ");
                        sb.append(stringOperationsService.countSpacesAfter(10, 1)).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(12, 1));
                        sb.append(" ");
                        sb.append(stringOperationsService.countSpacesAfter(12, 1)).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(18, 1));
                        sb.append(" ");
                        sb.append(stringOperationsService.countSpacesAfter(18, 1)).append("|");
                        sb.append(stringOperationsService.countSpacesBefore(12, 6));
                        sb.append("RETURN");
                        sb.append(stringOperationsService.countSpacesAfter(12, 6)).append("|");
                        sb.append(System.lineSeparator());
                    }
                }
            }
        }
        if (!hasCards) {
            sb.append("|                                              NO HISTORY                                                   |");
            sb.append(System.lineSeparator());
        }

        sb.append("-------------------------------------------------------------------------------------------------------------");

        return sb.toString();
    }
}
