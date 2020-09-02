package services;

import entities.Card;

import java.util.List;

public interface CardService {

    List<Card> getRandomCardsByCount(int count);
}
