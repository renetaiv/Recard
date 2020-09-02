package services.impl;

import entities.Card;
import entities.Deck;
import entities.User;
import services.CardService;
import services.StringOperationsService;
import services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final CardService cardService;

    private final List<User> allUsers;

    private User loggedUser;

    private StringOperationsService stringOperationsService;

    public UserServiceImpl(CardService cardService, StringOperationsService stringOperationsService) {
        this.cardService = cardService;
        this.allUsers = new ArrayList<>();
        this.stringOperationsService = stringOperationsService;
    }


    @Override
    public void registerUser(String username, String password) {
        User user = new User(username, password);
        this.allUsers.add(user);

        user.setFreeCards(cardService.getRandomCardsByCount(5));
        user.setDecks(new ArrayList<>());
        user.getDecks().add(new Deck("Deck 1", user, 20));
        user.getDecks().add(new Deck("Deck 2", user, 30));

        System.out.println("Successfully registered!");
    }

    @Override
    public void loginUser(String username, String password) {
        Optional<User> user = this.allUsers
                .stream()
                .filter(p -> p.getUsername().equals(username) && p.getPassword().equals(password))
                .findFirst();
        if (user.isEmpty()) {
            System.out.println("Not valid user/pass");
            return;
        }

        this.loggedUser = user.get();
        System.out.println("Successfully logged in");
    }

    @Override
    public boolean userExistsByUsername(String username) {
        return allUsers.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public Optional<Card> getCardFromUserFreeCardsById(int id) {
        return this.loggedUser.getFreeCards().stream().filter(e -> e.getId() == id).findFirst();
    }

    @Override
    public Optional<Deck> getDeckFromUserByName(String name) {
        return this.loggedUser.getDecks().stream().filter(e -> e.getName().equals(name)).findFirst();
    }

    @Override
    public void removeCardFromUserFreeCards(Card card) {
        this.loggedUser.getFreeCards().remove(card);
    }

    @Override
    public void removeCardFromUserDeck(int cardId, String deckName) {
        Optional<Deck> optionalDeck = this.loggedUser.getDecks()
                .stream()
                .filter(x -> x.getName().equals(deckName))
                .findFirst();
        if (optionalDeck.isEmpty()) {
            System.out.println("ebi se ");
            return;
        }

        Deck deck = optionalDeck.get();
        deck.getCards().removeIf(x -> x.getId() == cardId);
    }

    @Override
    public User getLoggedUser() {
        return this.loggedUser;
    }

    @Override
    public void printMyDecks() {
        System.out.println(printFreeCards());
        System.out.println(printAllDecks());
    }

    private String printFreeCards() {
        StringBuilder spaces = new StringBuilder();

        spaces.append("----------------------------MY FREE CARDS-------------------------------");
        spaces.append(System.lineSeparator());
        spaces.append("  №  | Card ID |             Card Name              | Attack | Defence |");
        spaces.append(System.lineSeparator());
        spaces.append("------------------------------------------------------------------------");
        spaces.append(System.lineSeparator());

        for (int i = 0; i < loggedUser.getFreeCards().size(); i++) {
            spaces.append(stringOperationsService.countSpacesBefore(5, ("" + i).length()));
            spaces.append(i + 1);
            spaces.append(stringOperationsService.countSpacesAfter(5, ("" + i).length())).append("|");
            spaces.append(
                    stringOperationsService.countSpacesBefore(
                            9,
                            ("" + loggedUser.getFreeCards().get(i).getId()).length()
                    )
            );
            spaces.append(loggedUser.getFreeCards().get(i).getId());
            spaces.append(stringOperationsService.countSpacesAfter(9, ("" + loggedUser.getFreeCards().get(i).getId()).length())).append("|");
            spaces.append(stringOperationsService.countSpacesBefore(36, ("" + loggedUser.getFreeCards().get(i).getName()).length()));
            spaces.append(loggedUser.getFreeCards().get(i).getName());
            spaces.append(stringOperationsService.countSpacesAfter(36, ("" + loggedUser.getFreeCards().get(i).getName()).length())).append("|");
            spaces.append(stringOperationsService.countSpacesBefore(8, ("" + loggedUser.getFreeCards().get(i).getAttack()).length()));
            spaces.append(loggedUser.getFreeCards().get(i).getAttack());
            spaces.append(stringOperationsService.countSpacesAfter(8, ("" + loggedUser.getFreeCards().get(i).getAttack()).length())).append("|");
            spaces.append(stringOperationsService.countSpacesBefore(9, ("" + loggedUser.getFreeCards().get(i).getDefense()).length()));
            spaces.append(loggedUser.getFreeCards().get(i).getDefense());
            spaces.append(stringOperationsService.countSpacesAfter(9, ("" + loggedUser.getFreeCards().get(i).getDefense()).length())).append("|");
            spaces.append(System.lineSeparator());
        }

        return spaces.toString();
    }

    private String printAllDecks() {
        StringBuilder space = new StringBuilder();
        space.append("----------------------------MY DECKS------------------------------------");

        for (Deck deck : loggedUser.getDecks()) {
            space.append(System.lineSeparator());
            space.append("------------------------").append(deck.getName()).append(" | Capacity: ").append(deck.getCapacity()).append("---------------------------");
            space.append(System.lineSeparator());

            if (deck.getCards().isEmpty()) {
                space.append("|                            NO CARDS                                  |");
                space.append(System.lineSeparator());
                space.append("------------------------------------------------------------------------");
                space.append(System.lineSeparator());
            } else {
                space.append("  №  | Card ID |             Card Name              | Attack | Defence |");
                space.append(System.lineSeparator());
                space.append("------------------------------------------------------------------------");
                space.append(System.lineSeparator());

                for (int i = 0; i < ((deck.getCards()).size()); i++) {
                    space.append(stringOperationsService.countSpacesBefore(5, ("" + i).length()));
                    space.append(i + 1);
                    space.append(stringOperationsService.countSpacesAfter(5, ("" + i).length())).append("|");
                    space.append(stringOperationsService.countSpacesBefore(9, ("" + (deck.getCards().get(i).getId())).length()));
                    space.append(deck.getCards().get(i).getId());
                    space.append(stringOperationsService.countSpacesAfter(9, ("" + (deck.getCards().get(i).getId())).length())).append("|");
                    space.append(stringOperationsService.countSpacesBefore(36, ("" + (deck.getCards().get(i).getName())).length()));
                    space.append(deck.getCards().get(i).getName());
                    space.append(stringOperationsService.countSpacesAfter(36, ("" + (deck.getCards().get(i).getName())).length())).append("|");
                    space.append(stringOperationsService.countSpacesBefore(8, ("" + (deck.getCards().get(i).getAttack())).length()));
                    space.append(deck.getCards().get(i).getAttack());
                    space.append(stringOperationsService.countSpacesAfter(8, ("" + (deck.getCards().get(i).getAttack())).length())).append("|");
                    space.append(stringOperationsService.countSpacesBefore(9, ("" + (deck.getCards().get(i).getDefense())).length()));
                    space.append(deck.getCards().get(i).getDefense());
                    space.append(stringOperationsService.countSpacesAfter(9, ("" + (deck.getCards().get(i).getDefense())).length())).append("|");
                    space.append(System.lineSeparator());
                }
            }
        }

        return space.toString();
    }
}
