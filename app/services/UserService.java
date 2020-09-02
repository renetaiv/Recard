package services;

import entities.BidCard;
import entities.Card;
import entities.Deck;
import entities.User;

import java.util.Optional;

public interface UserService {

    void registerUser(String username, String password);

    void loginUser(String username, String password);

    boolean userExistsByUsername(String username);

    Optional<Card> getCardFromUserFreeCardsById(int id);

    Optional<Deck> getDeckFromUserByName(String name);

    void removeCardFromUserFreeCards(Card card);

    void removeCardFromUserDeck(int cardId, String deckName);

    User getLoggedUser();

    void printMyDecks();
}
