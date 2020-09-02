package services.impl;

import entities.*;
import services.DuelService;
import services.StringOperationsService;
import services.UserService;

import java.util.*;

public class DuelServiceImpl implements DuelService {

    private UserService userService;

    private StringOperationsService stringOperationsService;

    private List<DuelCandidate> duelCandidatesQueue;

    private Duel duel;

    public DuelServiceImpl(UserService userService, StringOperationsService stringOperationsService) {
        this.userService = userService;
        this.stringOperationsService = stringOperationsService;
        this.duelCandidatesQueue = new ArrayList<>();
    }

    @Override
    public void enrollForDuel(String deckName) {
        User loggedUser = this.userService.getLoggedUser();

        Optional<Deck> deckOptional = this.userService.getDeckFromUserByName(deckName);
        if(deckOptional.isEmpty()) {
            System.out.println("The deck name is invalid!");
            return;
        }

        Deck deck = deckOptional.get();
        DuelCandidate newDuelCandidate = new DuelCandidate(loggedUser, deck);
        Optional<DuelCandidate> opponentDuelCandidateOptional =
                this.duelCandidatesQueue
                        .stream()
                        .filter(dc -> dc.isEligibleForDuel(newDuelCandidate))
                        .findFirst();
        if(opponentDuelCandidateOptional.isEmpty()) {
            duelCandidatesQueue.add(newDuelCandidate);
            System.out.println("You are enrolled for a duel. Please wait while we find a player with proper experience...");
            return;
        }

        DuelCandidate opponentDuelCandidate = opponentDuelCandidateOptional.get();
        this.startDuel(newDuelCandidate, opponentDuelCandidate);
    }

    private void startDuel(DuelCandidate newDuelCandidate, DuelCandidate opponentDuelCandidate) {
        this.duel = new Duel(newDuelCandidate, opponentDuelCandidate);
        this.duel.printInitialMessages();
        this.printFieldAndCurrentDuelistHand();
        startReadingUserInput();
    }

    public void printFieldAndCurrentDuelistHand() {
        System.out.println(fieldString());
        System.out.println(handString());
    }

    private String handString() {
        StringBuilder sb = new StringBuilder();

        sb.append("------------------------------MY HAND------------------------------------");
        sb.append(System.lineSeparator());
        sb.append("  №   | Card ID |             Card Name              | Attack | Defence |");
        sb.append(System.lineSeparator());
        sb.append("-------------------------------------------------------------------------");
        sb.append(System.lineSeparator());
        List<DuelCard> hand = this.duel.getCurrentDuelist().getHand();

        for (int i = 0; i < hand.size(); i++) {
            sb.append(this.stringOperationsService.makeColInfoCentred("" + (i + 1), 6));
            sb.append("|");
            sb.append(this.stringOperationsService.makeColInfoCentred("" + hand.get(i).getCard().getId(), 9));
            sb.append("|");
            sb.append(this.stringOperationsService.makeColInfoCentred(hand.get(i).getCard().getName(), 36));
            sb.append("|");
            sb.append(this.stringOperationsService.makeColInfoCentred("" + hand.get(i).getCard().getAttack(), 8));
            sb.append("|");
            sb.append(this.stringOperationsService.makeColInfoCentred("" + hand.get(i).getCurrentDefence(), 9));
            sb.append("|");
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    private String fieldString() {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------------FIELD-----------------------------------------------------");
        sb.append(System.lineSeparator());
        sb.append("---------------------------------------- ");
        String otherDuelistHeading = String.format("%s | Healthpoints: %d ",
                this.duel.getOtherDuelist().getDuelCandidate().getDuelist().getUsername(),
                this.duel.getOtherDuelist().getHealthPoints());
        sb.append(otherDuelistHeading);
        sb.append(stringOperationsService.countDashAfterMainHeading(69, otherDuelistHeading.length()));
        sb.append(System.lineSeparator());
        sb.append("________________________________________________ENEMY'S FIELD_________________________________________________");
        sb.append(System.lineSeparator());

        DuelCard[][] otherDuelistField = duel.getField().getOtherDuelistDuelistField(duel.getCurrentDuelist()).getField();
        DuelCard[][] currentDuelistField = duel.getCurrentDuelistDuelistField().getField();

        for (int i = 0; i < 2; i++) {
            sb.append("|");
            for (int j = 4; j >= 0; j--) {
                appendCardsInfoOnField(sb, otherDuelistField, i, j);
            }
            sb.append(System.lineSeparator());
            sb.append(String.format("|        [%d:4]        |        [%d:3]        |        [%d:2]        |        [%d:1]        |        [%d:0]        |", i, i, i, i, i));
            sb.append(System.lineSeparator());
            if ((i + 1) % 2 != 0) {
                sb.append("|-------------------------------------------------------------------------------------------------------------|");
                sb.append(System.lineSeparator());
            }
        }

        sb.append("|__________________________________________________MY FIELD___________________________________________________|");
        sb.append(System.lineSeparator());

        for (int i = 1; i >= 0; i--) {
            sb.append("|");
            for (int j = 0; j < 5; j++) {
                appendCardsInfoOnField(sb, currentDuelistField, i, j);
            }
            sb.append(System.lineSeparator());
            sb.append(String.format("|        [%d:0]        |        [%d:1]        |        [%d:2]        |        [%d:3]        |        [%d:4]        |", i, i, i, i, i));
            sb.append(System.lineSeparator());
            if ((i) % 2 != 0) {
                sb.append("|-------------------------------------------------------------------------------------------------------------|");
                sb.append(System.lineSeparator());
            }
        }

        String currentDuelistHeading = String.format("%s | Healthpoints: %d ",
                this.duel.getCurrentDuelist().getDuelCandidate().getDuelist().getUsername(),
                this.duel.getOtherDuelist().getHealthPoints());
        sb.append("---------------------------------------- ");
        sb.append(currentDuelistHeading);
        sb.append(stringOperationsService.countDashAfterMainHeading(69, currentDuelistHeading.length()));
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    private void appendCardsInfoOnField(StringBuilder sb, DuelCard[][] currentDuelistField, int i, int j) {
        DuelCard duelCard = currentDuelistField[i][j];
        String cardInfo = duelCard == null
                ? "EMPTY"
                : String.format("A:%d|D:%d",
                duelCard.getCard().getAttack(),
                duelCard.getCurrentDefence());
        sb.append(stringOperationsService.countSpacesBefore(21, cardInfo.length()));
        sb.append(cardInfo);
        sb.append(stringOperationsService.countSpacesAfter(21, cardInfo.length()));
        sb.append("|");
    }

    private void startReadingUserInput() {
        Scanner sc = new Scanner(System.in);

        int cardId;
        while (this.duel.isFinished()) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    duel.endTurn();
                    printFieldAndCurrentDuelistHand();
                    System.out.printf("The time for %s's turn has passed. Now it's %s's turn!%n",
                            duel.getOtherDuelist().getDuelCandidate().getDuelist().getUsername(),
                            duel.getCurrentDuelist().getDuelCandidate().getDuelist().getUsername());
                    timer.cancel();
                }
            }, 30000L);

            String[] tokens = sc.nextLine().split(" ");
            String cmd = tokens[0];
            switch (cmd) {
                case "put-card-on-field":
                    cardId = Integer.parseInt(tokens[1]);
                    int row = Integer.parseInt(tokens[2]);
                    int col = Integer.parseInt(tokens[3]);
                    this.duel.putCardOnField(cardId, row, col);
                    break;
                case "attack-card":
                    int attackingCardRow = Integer.parseInt(tokens[1]);
                    int attackingCardCol = Integer.parseInt(tokens[2]);
                    int attackedCardRow = Integer.parseInt(tokens[3]);
                    int attackedCardCol = Integer.parseInt(tokens[4]);
                    this.duel.attackCard(attackingCardRow, attackingCardCol, attackedCardRow, attackedCardCol);
                    break;
                case "attack-healthpoints":
                    this.duel.attackHealthPoints();
                    break;
                case "end-turn":
                    this.duel.endTurn();
                    System.out.printf("Now it's %s's turn!%n",this.duel.getCurrentDuelist().getDuelCandidate().getDuelist().getUsername());
                    break;
            }
        }
    }

    @Override
    public void startDuelIfLoggedUserIsEnrolledAndIsEligibleForDuel() {
        User loggedUser = userService.getLoggedUser();

        Optional<DuelCandidate> loggedUserDuelCandidateOptional  =  this.duelCandidatesQueue
                .stream()
                .filter(dc -> dc.getDuelist() == loggedUser)
                .findFirst();
        if (loggedUserDuelCandidateOptional.isEmpty()) {
            return;
        }

        DuelCandidate loggedUserDuelCandidate = loggedUserDuelCandidateOptional.get();
        Optional<DuelCandidate> opponentDuelCandidateOptional =  this.duelCandidatesQueue
                .stream()
                .filter(dc -> dc.isEligibleForDuel(loggedUserDuelCandidate))
                .findFirst();
        if (opponentDuelCandidateOptional.isEmpty()) {
            return;
        }
        DuelCandidate candidate = opponentDuelCandidateOptional.get();

        this.startDuel(loggedUserDuelCandidate,candidate);
    }
}
