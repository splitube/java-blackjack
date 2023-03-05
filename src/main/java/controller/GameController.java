package controller;

import domain.BlackJackGame;
import domain.TurnAction;
import domain.box.BoxResult;
import domain.user.Player;
import dto.ParticipantDTO;
import java.util.List;
import view.InputView;
import view.OutputView;

public class GameController {

    private final BlackJackGame blackJackGame;

    public GameController(BlackJackGame game) {
        this.blackJackGame = game;
    }

    public void ready() {
        ParticipantDTO participantDTO = new ParticipantDTO();
        blackJackGame.initializeHand();
        blackJackGame.makeParticipants(participantDTO);
        List<Player> players = participantDTO.getPlayers();
        Player dealer = participantDTO.getDealer();
        OutputView.printNameAndHand(dealer.getName(), dealer.faceUpInitialHand());
        players.forEach((player) -> OutputView.printNameAndHand(player.getName(), player.faceUpInitialHand()));
    }

    public void play() {
        Player current = blackJackGame.getCurrentPlayer();
        while (current.isPlayer()) {
            playersTurn(current);
            current = blackJackGame.getCurrentPlayer();
        }
        dealerTurn(current);
    }

    private void playersTurn(Player player) {
        String input = InputView.inputNeedMoreCard(player.getName());
        TurnAction action = TurnAction.getActionByInput(input);
        blackJackGame.playTurn(player, action);
        OutputView.printNameAndHand(player.getName(), player.showHand());
    }

    private void dealerTurn(Player dealer) {
        while (blackJackGame.isDealerUnderThresholds(dealer)) {
            OutputView.printDealerReceivedCard();
            blackJackGame.playTurn(dealer, TurnAction.HIT);
        }
    }

    public void printFinalGameResult() {
        ParticipantDTO participantDTO = new ParticipantDTO();
        blackJackGame.makeParticipants(participantDTO);
        List<Player> players = participantDTO.getPlayers();
        printAllStatus(participantDTO.getDealer(), players);
        List<BoxResult> playerBoxResults = blackJackGame.getPlayerBoxResults(players);
        OutputView.printDealerGameResult(blackJackGame.getDealerBoxResult(playerBoxResults), players.size());
        for (int index = 0; index < players.size(); index++) {
            OutputView.printPlayerBoxResult(players.get(index).getName(), playerBoxResults.get(index));
        }
    }

    private void printAllStatus(Player dealer, List<Player> players) {
        OutputView.printNameHandScore(dealer.getName(), dealer.showHand(), dealer.calculatePoint());
        players.forEach(
            (participant) -> OutputView.printNameHandScore(participant.getName(), participant.showHand(),
                participant.calculatePoint()));
    }
}
