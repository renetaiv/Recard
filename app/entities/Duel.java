package entities;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Duel extends BaseEntity {

    private Duelist firstDuelist;

    private Duelist secondDuelist;

    private Duelist currentDuelist;

    private Field field;

    private boolean isFinished;

    public Duel(DuelCandidate firstDuelCandidate, DuelCandidate secondDuelCandidate) {
        super();
        Duelist firstDuelist = new Duelist(firstDuelCandidate, calculateInitialHealthPoints());
        this.firstDuelist = firstDuelist;
        Duelist secondDuelist = new Duelist(secondDuelCandidate, calculateInitialHealthPoints());
        this.secondDuelist = secondDuelist;
        this.setRandomCurrentDuelist();
        this.field = new Field(firstDuelist, secondDuelist);
        this.isFinished = false;
    }

    private int calculateInitialHealthPoints() {
        if (this.firstDuelist.getDuelCandidate().getDuelist().getExperience() < 1000 ||
            this.secondDuelist.getDuelCandidate().getDuelist().getExperience() < 1000) {
            return 1000;
        }

        return (this.firstDuelist.getDuelCandidate().getDuelist().getExperience()
                        + this.secondDuelist.getDuelCandidate().getDuelist().getExperience()) / 2;
    }

    private void setRandomCurrentDuelist() {
        Random random = new Random();
        if (random.nextBoolean()) {
            this.currentDuelist = firstDuelist;
        } else {
            this.currentDuelist = secondDuelist;
        }
    }

    private void finalizeDuel() {
        Duelist winnerDuelist = getWinnerDuelist();
        Duelist loserDuelist = getLoserDuelist();
        giveWinnerAwards(winnerDuelist, loserDuelist);
        applySanctionsToTheLoserDuelist(winnerDuelist, loserDuelist);
        this.isFinished = true;
    }

    private void applySanctionsToTheLoserDuelist(Duelist winner, Duelist loser) {
        User winnerDuelist = loser.getDuelCandidate().getDuelist();
        User loserDuelist = winner.getDuelCandidate().getDuelist();

        int loserGold = (int) (loserDuelist.getGold() * 0.20);
        loserDuelist.setGold(loserDuelist.getGold() - loserGold);

        double strongerDiff = (loserDuelist.getExperience() * 1.0) / winnerDuelist.getExperience();
        int experienceOfWin = calculateExperienceMultiplier(loserDuelist.getExperience(), strongerDiff);
        int loseXP = (int) (experienceOfWin * 0.05);
        loser.setHealthPoints(loser.getHealthPoints() - loseXP);
    }

    public Duelist getFirstDuelist() {
        return firstDuelist;
    }

    public void setFirstDuelist(Duelist firstDuelist) {
        this.firstDuelist = firstDuelist;
    }

    public Duelist getSecondDuelist() {
        return secondDuelist;
    }

    public void setSecondDuelist(Duelist secondDuelist) {
        this.secondDuelist = secondDuelist;
    }

    public Duelist getCurrentDuelist() {
        return currentDuelist;
    }

    public void setCurrentDuelist(Duelist currentDuelist) {
        this.currentDuelist = currentDuelist;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void printInitialMessages() {
        System.out.println("The duel is about to begin.");
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println("We must choose who will be first...");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println("3...");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println("2...");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println("1...");
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(String.format("%s is first. Let the duel begin!", this.getCurrentDuelist().getDuelCandidate().getDuelist().getUsername()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void putCardOnField(int cardId, int row, int col) {
        Optional<DuelCard> duelCardFromHandByCardId = currentDuelist.getDuelCardFromHandByCardId(cardId);
        if (duelCardFromHandByCardId.isEmpty()) {
            System.out.print("There is no such card in your hand with given card id, please try with another");
            return;
        }

        field.putCardOnField(currentDuelist, duelCardFromHandByCardId.get(), row, col);
    }

    public void attackCard(int attackingDuelCardRow, int attackingDuelCardCol, int attackedDuelCardRow, int attackedDuelCardCol) {
        this.field.attackCard(currentDuelist, attackingDuelCardRow, attackingDuelCardCol, attackedDuelCardRow, attackedDuelCardCol);
    }

    public void attackHealthPoints() {
        DuelCard[][] otherDuelistField = this.field.getOtherDuelistDuelistField(currentDuelist).getField();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                if (otherDuelistField[i][j] != null) {
                    System.out.println("Enemy has cards on his field. Cannot be directly attacked.");
                    return;
                }
            }
        }

        for (DuelCard[] duelCards : getCurrentDuelistDuelistField().getField()) {
            for (DuelCard duelCard : duelCards) {
                if (duelCard.isUsed()) {
                    continue;
                }
                getOtherDuelist().setHealthPoints(Math.max(0, getOtherDuelist().getHealthPoints() - duelCard.getCard().getAttack()));
                System.out.println(String.format
                        ("%s was attacked by %s with %d. %s has %d health points remaining.",
                                getOtherDuelist().getDuelCandidate().getDuelist().getUsername(),
                                duelCard.getCard().getName(),
                                duelCard.getCard().getAttack(),
                                getOtherDuelist().getDuelCandidate().getDuelist().getUsername(),
                                getOtherDuelist().getHealthPoints()
                        )
                );

                if (getOtherDuelist().getHealthPoints() == 0) {
                    System.out.println("The duel has finished. The winner is "
                            + currentDuelist.getDuelCandidate()
                            .getDuelist()
                            .getUsername()
                            + " !!!");
                    finalizeDuel();
                    return;
                }
            }
        }
    }

    public void endTurn() {
        DuelCard[][] field = this.field.getCurrentDuelistDuelistField(currentDuelist).getField();
        for (DuelCard[] duelCards : field) {
            for (DuelCard card : duelCards) {
                if (card != null) {
                    if (card.isUsed()) {
                        card.setUsed(false);
                    }
                }
            }
        }

        this.currentDuelist = getOtherDuelist();
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public Duelist getOtherDuelist() {
        return getCurrentDuelist() == firstDuelist ? this.secondDuelist : this.firstDuelist;
    }

    public DuelistField getCurrentDuelistDuelistField() {
        return (field.getFirstDuelistField().getDuelist() == currentDuelist)
                ? field.getFirstDuelistField()
                : field.getSecondDuelistField();
    }

    private Duelist getWinnerDuelist() {
        return firstDuelist.getHealthPoints() > 0 ? this.firstDuelist : this.secondDuelist;
    }

    private Duelist getLoserDuelist() {
        return firstDuelist.getHealthPoints() <= 0 ? this.firstDuelist : this.secondDuelist;
    }

    private void giveWinnerAwards(Duelist winner, Duelist loser) {
        User winnerDuelist = loser.getDuelCandidate().getDuelist();
        User loserDuelist = winner.getDuelCandidate().getDuelist();

        int loserGold = (int) (loserDuelist.getGold() * 0.20);
        winnerDuelist.setGold(winnerDuelist.getGold() + loserGold);

        double strongerDiff = (loserDuelist.getExperience() * 1.0) / winnerDuelist.getExperience();
        int experienceToAdd = calculateExperienceMultiplier(
                loserDuelist.getExperience(), strongerDiff);
        winnerDuelist.setExperience(winnerDuelist.getExperience() + experienceToAdd);


        int randomNumber = getRandomNumberInRange(100);
        if (randomNumber >= 50 && randomNumber <= 55) {
            int freeCardDeckSize = loserDuelist.getFreeCards().size() - 1;
            int indexOfRandomCard = getRandomNumberInRange(freeCardDeckSize);
            Card card = loserDuelist.getFreeCards().get(indexOfRandomCard);
            loserDuelist.removeCardById(card.getId());
            winnerDuelist.addCardInFreeDeck(card);
        }
    }

    private int calculateExperienceMultiplier(int experience, double strongerDiff) {

        if (experience < 1000) {
            return 10;
        }

        int calculatedExperience = 2000;

        while (experience >= 0) {
            if (experience / 20000 > 0) {
                calculatedExperience += 1000;
            }
            experience = experience - 20000;
        }

        return (int) (strongerDiff * calculatedExperience);
    }

    private int getRandomNumberInRange(int range) {
        return new Random().nextInt(range);
    }

}
