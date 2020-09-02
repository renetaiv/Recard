package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Deck extends BaseEntity {

    private String name;

    private User owner;

    private List<Card> cards;

    private int capacity;

    public Deck(String name, User owner, int capacity) {
        super();
        this.name = name;
        this.owner = owner;
        this.cards = new ArrayList<>();
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return this.cards.size() == this.capacity;
    }

    public void increaseCapacity() {
        this.capacity++;
    }

    public void addCardToDeck(Card card) {
        cards.add(card);
    }

    public void removeCardFromDeck(Card card) {
        cards.remove(card);
    }

    public Optional<Card> getCardById(int id) {
        for (Card card : cards) {
            if (id == card.getId()) {
                return Optional.of(card);
            }
        }

        return Optional.empty();
    }
}
