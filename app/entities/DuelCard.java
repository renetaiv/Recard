package entities;

public class DuelCard extends BaseEntity {

    private Card card;

    private int currentDefence;

    private boolean isUsed;

    public DuelCard(Card card) {
        super();
        this.card = card;
        this.currentDefence = card.getDefense();
    }

    public int getCurrentDefence() {
        return currentDefence;
    }

    public void setCurrentDefence(int currentDefence) {
        this.currentDefence = currentDefence;
        this.isUsed = true;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getCurrentAttack() {
        return this.card.getAttack();
    }

    public Card getCard() {
        return card;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
