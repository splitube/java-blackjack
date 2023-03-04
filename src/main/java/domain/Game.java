package domain;

import domain.user.Participant;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private static final int DEALER_THRESHOLDS = 16;
    private final Deck deck = new Deck();
    private final Participants participants;

    public Game(Participants participants) {
        this.participants = participants;
    }

    public Game(String participantNames) {
        this.participants = Participants.of(participantNames);
    }

    public void deal(Participant participant) {
        participant.addCard(deck.draw());
    }

    public void ready() {
        deck.shuffle();
        List<Participant> allParticipants = participants.getAllParticipantsDealerInLastIndex();
        allParticipants.forEach((participant -> {
            deal(participant);
            deal(participant);
            participants.update(participant);
        }));
    }

    public void run(Participant currentParticipant, BlackJackAction action) {
        if (action == BlackJackAction.HIT) {
            deal(currentParticipant);
        }
        participants.update(currentParticipant);
    }

    public Participant getCurrentParticipant() {
        return participants.getCurrentParticipant();
    }

    public boolean isDealerNeedCard(Participant dealer) {
        return dealer.calculateScore() <= DEALER_THRESHOLDS;
    }

    public List<GameResult> getFinalGameResults() {
        List<Participant> allParticipants = getAllParticipant();
        List<GameResult> finalGameResult = new ArrayList<>();
        finalGameResult.add(new GameResult(0, 0));
        for (int i = 1; i < allParticipants.size(); i++) {
            GameResult gameResult = participants.compareWithDealer(allParticipants.get(i));
            finalGameResult.add(gameResult);
            GameResult updatedDealerGameResult = finalGameResult.get(0).add(gameResult);
            finalGameResult.set(0, updatedDealerGameResult);
        }
        return finalGameResult;
    }

    public List<Participant> getAllParticipant() {
        List<Participant> allParticipants = participants.getAllParticipantsDealerInLastIndex();
        Participant dealer = allParticipants.remove(allParticipants.size() - 1);
        allParticipants.add(0, dealer);
        return allParticipants;
    }
}
