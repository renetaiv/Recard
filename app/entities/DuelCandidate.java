package entities;

public class DuelCandidate extends BaseEntity {

    private User duelist;

    private Deck deck;

    private int minExperienceNeeded;

    private int maxExperienceAllowed;


    public DuelCandidate(User duelist, Deck deck) {
        super();
        this.duelist = duelist;
        this.deck = deck;
        this.minExperienceNeeded = calculateMinExperienceNeeded();
        this.maxExperienceAllowed = calculateMaxExperienceAllowed();
    }

    public User getDuelist() {
        return duelist;
    }

    public void setDuelist(User duelist) {
        this.duelist = duelist;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public int getMinExperienceNeeded() {
        return minExperienceNeeded;
    }

    public int getMaxExperienceAllowed() {
        return maxExperienceAllowed;
    }

    private int calculateMaxExperienceAllowed() {
        return (int) (this.duelist.getExperience() * 1.5);
    }

    private int calculateMinExperienceNeeded() {
        return (int) (this.duelist.getExperience() / 1.5);
    }

    public boolean isEligibleForDuel(DuelCandidate duelCandidate) {

        return this.duelist.getExperience() >= duelCandidate.getMinExperienceNeeded()
                && this.duelist.getExperience() <= duelCandidate.getMaxExperienceAllowed()
                && duelCandidate.getDuelist().getExperience() >= this.getMinExperienceNeeded()
                && duelCandidate.getDuelist().getExperience() <= this.getMaxExperienceAllowed();
    }
}
