package services;

public interface DuelService {

    void enrollForDuel(String deckName);

    void startDuelIfLoggedUserIsEnrolledAndIsEligibleForDuel();
}
