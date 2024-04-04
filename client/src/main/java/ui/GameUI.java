package ui;

import chess.ChessGame;
import observer.ServerMessageObserver;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.ServerMessage;

public class GameUI implements ServerMessageObserver {
    private final ChessBoardUI chessBoardUI;
    private ChessGame.TeamColor perspective;

    public GameUI(ChessBoardUI chessBoardUI) {
        this.chessBoardUI = chessBoardUI;
    }

    @Override
    public void sendMessage(ServerMessage message) {

        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                var loadGameMessage = (LoadGame) message;
                var gameData = loadGameMessage.getGameData();
                var boardString = chessBoardUI.buildChessBoardDisplayString(gameData.game().getBoard(), perspective);

            }
            case ERROR -> {
            }
            case NOTIFICATION -> {
            }
        }

    }

    public void startGameUI(ChessGame.TeamColor perspective) {
        this.perspective = perspective;
    }
}
