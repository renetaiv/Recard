import entities.Card;
import entities.Deck;
import services.*;
import services.impl.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        StringOperationsService stringOperationsService = new StringOperationsServiceImpl();
        CardService cardService = new CardServiceImpl();
        UserService userService = new UserServiceImpl(cardService, stringOperationsService);
        AuctionService auctionService = new AuctionServiceImpl(cardService, userService, stringOperationsService);
        DuelService duelService = new DuelServiceImpl(userService, stringOperationsService);

        Scanner sc = new Scanner(System.in);

        int cardId;
        String deckName;

        while (true) {
            String[] tokens = sc.nextLine().split(" ");
            String cmd = tokens[0];
            switch (cmd) {
                case "register":
                    String username = tokens[1];
                    String password = tokens[2];
                    String confirm = tokens[3];

                    if (!password.equals(confirm)) {
                        System.out.println("Password mismatch");
                        break;
                    } else if (userService.userExistsByUsername(username)) {
                        System.out.println("User already exist");
                        break;
                    }

                    userService.registerUser(username, password);
                    break;
                case "login":
                    String loginUsername = tokens[1];
                    String loginPass = tokens[2];

                    userService.loginUser(loginUsername, loginPass);
                    duelService.startDuelIfLoggedUserIsEnrolledAndIsEligibleForDuel();
                    break;
                case "add-card-to-deck":
                    if (userService.getLoggedUser() == null) {
                        System.out.println("Not logged in");
                        break;
                    }

                    cardId = Integer.parseInt(tokens[1]);
                    deckName = Arrays.stream(tokens)
                            .skip(2)
                            .collect(Collectors.joining(" "));

                    Optional<Card> cardOptional = userService.getCardFromUserFreeCardsById(cardId);
                    if (cardOptional.isEmpty()) {
                        System.out.println("Player does not have card with given id in free cards");
                        break;
                    }

                    Optional<Deck> deckOptional = userService.getDeckFromUserByName(deckName);
                    if (deckOptional.isEmpty()) {
                        System.out.println("User does not have deck with given name!");
                        break;
                    }

                    Deck deck = deckOptional.get();
                    if (deck.isFull()) {
                        System.out.println("Player's deck is full!");
                        break;
                    }

                    Card card = cardOptional.get();
                    deck.addCardToDeck(card);
                    userService.removeCardFromUserFreeCards(card);
                    System.out.printf("The card %s has been transferred to %s.%n",
                            card.getName(),
                            deck.getName());
                    break;
                case "list-auction":
                    auctionService.listAuction();
                    break;
                case "auction-buy":
                    cardId = Integer.parseInt(tokens[1]);
                    int credits = Integer.parseInt(tokens[2]);

                    auctionService.auctionBuy(cardId, credits);
                    break;
                case "auction-bid":
                    cardId = Integer.parseInt(tokens[1]);
                    int gold = Integer.parseInt(tokens[2]);

                    auctionService.auctionBid(cardId, gold);
                    break;
                case "set-auction-card":
                    cardId = Integer.parseInt(tokens[1]);
                    int minBid = Integer.parseInt(tokens[2]);

                    auctionService.setAuctionCard(cardId, minBid);
                    break;
                case "auctioned-cards-history":
                    auctionService.printAuctionedCardsHistory();
                    break;
                case "last-turns-history":
                    auctionService.printLastTurnsHistory();
                    break;
                case "my-decks":
                    if (userService.getLoggedUser() == null) {
                        System.out.println("Not logged in");
                        break;
                    }

                    userService.printMyDecks();
                    break;
                case "remove-card-from-deck":
                    cardId = Integer.parseInt(tokens[1]);
                    deckName = Arrays.stream(tokens)
                            .skip(2)
                            .collect(Collectors.joining(" "));

                    userService.removeCardFromUserDeck(cardId, deckName);
                    break;
                case "enroll-to-duel":
                    deckName = Arrays.stream(tokens)
                            .skip(2)
                            .collect(Collectors.joining(" "));

                    duelService.enrollForDuel(deckName);
                    break;
            }
        }
    }
}
