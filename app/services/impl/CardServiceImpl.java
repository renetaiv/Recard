package services.impl;

import entities.Card;
import services.CardService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.util.Random;

public class CardServiceImpl implements CardService {

    private final List<Card> allCards;

    public CardServiceImpl() {
        allCards = new ArrayList<>();
        generateCards();
    }

    private void generateCards() {
        for (var i = 0; i < 100; i++) {
            allCards.add(new Card(
                    UUID.randomUUID().toString(),
                    (int) (Math.random() * 5000),
                    (int) (Math.random() * 5000)
            ));
        }
    }

    @Override
    public List<Card> getRandomCardsByCount(int count) {
        Random rand = new Random();

        List<Card> cardList = new ArrayList<>();

        while (cardList.size() < count) {
            Card randomCard = allCards.get(rand.nextInt(allCards.size()));
            if (!cardList.contains(randomCard)) {
                cardList.add(randomCard);
            }
        }

        return cardList;
    }
}
