package entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class User extends BaseEntity {

    private String username;

    private String password;

    private int credits;

    private int gold;

    private int experience;

    private List<Card> freeCards;

    private List<Deck> decks;

    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
        this.experience = 0;
        this.credits = 0;
        this.gold = 5000;
        this.freeCards = new ArrayList<>();
        this.decks = new ArrayList<>();
    }

    public void removeCardById(int cardId) {
        Optional<Card> card = this.freeCards.stream().filter(c -> c.getId() == cardId).findFirst();
        if (card.isEmpty()) {
            System.out.println("Card Id is invalid!");
            return;
        }

        this.freeCards.remove(card.get());
    }

    public void addCardInFreeDeck(Card card) {
        this.freeCards.add(card);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public List<Card> getFreeCards() {
        return freeCards;
    }

    public void setFreeCards(List<Card> freeCards) {
        this.freeCards = freeCards;
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
