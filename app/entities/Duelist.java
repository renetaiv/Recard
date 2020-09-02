package entities;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Duelist extends BaseEntity {

    private DuelCandidate duelCandidate;

    private int healthPoints;

    private List<DuelCard> hand;

    private ArrayDeque<DuelCard> deck;

    private List<DuelCard> grave;


    public Duelist(DuelCandidate duelCandidate, int healthPoints) {
        super();
        this.duelCandidate = duelCandidate;
        this.healthPoints = healthPoints;
        this.hand = new ArrayList<>();
        this.grave = new ArrayList<>();
        this.setShuffledDeck();
        this.takeANumberOfCardsFromDeckToHand(4);
    }

    public DuelCandidate getDuelCandidate() {
        return duelCandidate;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    private void takeANumberOfCardsFromDeckToHand(int n) {
        for (int i = 0; i < n; i++) {
            if (deck.isEmpty()) {
                System.out.println("No cards.");
                return;
            }

            hand.add(deck.pop());
        }
    }

    private void setShuffledDeck() {
        List<DuelCard> newDeck = duelCandidate.getDeck().getCards().stream().map(DuelCard::new).collect(Collectors.toList());
        Collections.shuffle(newDeck);
        this.deck = new ArrayDeque<>(newDeck);
    }

    public Optional<DuelCard> getDuelCardFromHandByCardId(int cardId) {
        return hand.stream().filter(x -> x.getCard().getId() == cardId).findFirst();
    }

    public void putDuelCardInGrave(DuelCard duelCard) {
        grave.add(duelCard);
    }

    public void removeDuelCardFromHand(DuelCard duelCard) {
        hand.remove(duelCard);
    }

    public List<DuelCard> getHand() {
        return hand;
    }
}
