package entities;

import java.util.concurrent.atomic.AtomicInteger;

public class Field extends BaseEntity {

    private DuelistField firstDuelistField;

    private DuelistField secondDuelistField;

    public Field(Duelist firstDuelist, Duelist secondDuelist) {
        super();
        this.firstDuelistField = new DuelistField(firstDuelist);
        this.secondDuelistField = new DuelistField(secondDuelist);
    }

    public DuelistField getFirstDuelistField() {
        return firstDuelistField;
    }

    public DuelistField getSecondDuelistField() {
        return secondDuelistField;
    }

    public void putCardOnField(Duelist currentDuelist, DuelCard duelCard, int row, int col) {
        DuelCard[][] currentField = getCurrentDuelistDuelistField(currentDuelist).getField();

        if (row < 0 || row > 1 || col < 0 || col > 4) {
            System.out.print("No such row or column.");
        } else {
            if (currentField[row][col] == null) {
                currentField[row][col] = duelCard;
                currentDuelist.removeDuelCardFromHand(duelCard);
                System.out.printf("Card is set on %s %s", row, col);
                System.out.println();
            } else {
                System.out.println("There already is a card on this place.");
            }
        }
    }

    public void attackCard(Duelist currentDuelist, int attackingDuelCardRow, int attackingDuelCardCol, int attackedDuelCardRow, int attackedDuelCardCol) {
        if (isValidIndexes(attackingDuelCardCol,
                attackingDuelCardRow,
                this.firstDuelistField.getField()
        ) &&
                isValidIndexes(attackedDuelCardRow,
                        attackedDuelCardCol,
                        this.secondDuelistField.getField())
        ) {
            System.out.println("Indexes of card is not valid!");
            return;
        }

        DuelCard[][] firstPlayerField = firstDuelistField.getField();
        DuelCard[][] secondPlayerField = secondDuelistField.getField();

        if (isFirstDuelistField(currentDuelist)) {
            if (secondPlayerField[attackedDuelCardRow][attackedDuelCardCol] == null) {
                System.out.println("No such card in this indexes");
            } else {
                DuelCard firstPlayerCard = firstPlayerField
                        [attackingDuelCardRow]
                        [attackingDuelCardCol];
                DuelCard secondPlayerCard = secondPlayerField
                        [attackedDuelCardRow]
                        [attackedDuelCardCol];

                int firstPlayerCardAttack = firstPlayerCard.getCurrentAttack();
                int secondPlayerCardDefence = secondPlayerCard.getCurrentDefence();

                secondPlayerCard.setCurrentDefence(secondPlayerCardDefence - firstPlayerCardAttack);
                firstPlayerCard.setCurrentDefence(firstPlayerCard.getCurrentDefence() - secondPlayerCard.getCurrentAttack());

                if (firstPlayerCardAttack - secondPlayerCardDefence <= 0) {
                    System.out.printf("The card with name %s is dead.%n",
                            secondPlayerCard.getCard().getName()
                    );
                    secondDuelistField.getDuelist().putDuelCardInGrave(secondPlayerCard);
                    secondPlayerField
                            [attackedDuelCardRow]
                            [attackedDuelCardCol] = null;
                }

                if (secondPlayerCard.getCurrentAttack() - firstPlayerCard.getCurrentDefence() <= 0) {
                    System.out.printf("The card with name %s is dead.%n",
                            secondPlayerCard.getCard().getName()
                    );
                    firstDuelistField.getDuelist().putDuelCardInGrave(secondPlayerCard);
                    firstPlayerField
                            [attackingDuelCardRow]
                            [attackingDuelCardCol] = null;
                }
            }
        } else {
            if (firstPlayerField[attackedDuelCardRow][attackedDuelCardCol] == null) {
                System.out.println("No such card in this indexes.");
            } else {
                DuelCard firstPlayerCard = firstPlayerField
                        [attackingDuelCardRow]
                        [attackingDuelCardCol];
                DuelCard secondPlayerCard = secondPlayerField
                        [attackedDuelCardRow]
                        [attackedDuelCardCol];

                firstPlayerCard.setCurrentDefence(firstPlayerCard.getCurrentDefence() - secondPlayerCard.getCurrentAttack());
                secondPlayerCard.setCurrentDefence(secondPlayerCard.getCurrentDefence() - firstPlayerCard.getCurrentAttack());

                int firstPlayerCardAttack = firstPlayerCard.getCurrentAttack();
                int secondPlayerCardDefence = secondPlayerCard.getCurrentDefence();

                if (firstPlayerCardAttack - secondPlayerCardDefence <= 0) {
                    System.out.printf("The card with name %s is dead.%n",
                            secondPlayerCard.getCard().getName());
                    secondDuelistField.getDuelist().putDuelCardInGrave(secondPlayerCard);
                    secondPlayerField
                            [attackedDuelCardRow]
                            [attackedDuelCardCol] = null;
                }

                if (secondPlayerCard.getCurrentAttack() - firstPlayerCard.getCurrentDefence() <= 0) {
                    System.out.printf("The card with name %s is dead.%n",
                            secondPlayerCard.getCard().getName());
                    firstDuelistField.getDuelist().putDuelCardInGrave(secondPlayerCard);
                    firstPlayerField
                            [attackingDuelCardRow]
                            [attackingDuelCardCol] = null;
                }
            }
        }
    }

    private boolean isFirstDuelistField(Duelist duelist) {
        return this.firstDuelistField.getDuelist() == duelist;
    }

    private boolean isValidIndexes(int row, int col, DuelCard[][] field) {
        if (row < 0 || col < 0) {
            return false;
        }

        return row <= field.length - 1 && col <= field[row].length - 1;
    }

    public DuelistField getCurrentDuelistDuelistField(Duelist currentDuelist) {
        return (currentDuelist == this.firstDuelistField.getDuelist())
                ? this.getFirstDuelistField()
                : this.getSecondDuelistField();
    }

    public DuelistField getOtherDuelistDuelistField(Duelist currentDuelist) {
        return getCurrentDuelistDuelistField(currentDuelist).getDuelist()
                == firstDuelistField.getDuelist()
                ? this.getSecondDuelistField()
                : this.getFirstDuelistField();
    }
}
