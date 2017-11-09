package server.game;

import common.Card;
import common.CardPair;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import java.util.*;
import common.protocol;
import common.protocolWriter;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Round {
    private String rep;
    private Channel chan;
    private Card inputCard = null;
    private List<CardPair> roundCards = GameHandler.getInstance().getRoundCards();
    private String roundColor = null;
    private boolean validTurn = false;
    private int winningPlayer = 0;
    private boolean endRound = false;

    Round(String rep, Channel chan) {
        this.rep = rep;
        this.chan = chan;
    }

    private boolean validInput() {
        Deck deck = new Deck();
        String[] inputWords = rep.split(" ");
        if (deck.isValidValue(inputWords[0]) && deck.isValidColor(inputWords[1])) {
            this.inputCard = new Card(inputWords[1], inputWords[0]);
            return true;
        } else
            return false;
    }

    void gameTurn() {
        this.roundColor();
        this.inputActions();
        if (this.roundCards.size() == 4)
            this.handleEndTurn();
        this.endTurn();
    }

    private void roundColor() {
        if (this.roundCards.size() > 0)
            roundColor = this.roundCards.get(0).getCard().getColor();
        else
            roundColor = null;
    }

    private void displayHand() {
        GameHandler.getInstance().printHandPlayer(chan);
        protocolWriter.getInstance().writeProtocol(chan, new protocol("This is your hand. Chose a card.\n"));
    }

    private void endTurn() {
        if (this.validTurn && !this.endRound)
            GameHandler.getInstance().setNextTurn(true, -1);
        else if (this.validTurn && this.endRound)
            GameHandler.getInstance().setNextTurn(true, this.winningPlayer);
    }

    private void belote() {
        Card tmp = new Card(GameHandler.getInstance().getTrumpValue(), "queen");
        System.out.println("belote : " + inputCard.getValue() + " " + inputCard.getColor());
        System.out.println("trump : " + GameHandler.getInstance().getTrumpValue());
        System.out.println("belote 2 : " + GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getObjectHand().contains(tmp));
        if (inputCard.getValue().equals("king") && inputCard.getColor().equals(GameHandler.getInstance().getTrumpValue()) &&
                GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getObjectHand().contains(tmp)) {
            GameHandler.getInstance().setRebelote(true);
            GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).setGameScore(
                    GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getGameScore() + 10);
            GameHandler.getInstance().writeAllClient("~Belote for player " + GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getId() + "~\n");
        }
        if (inputCard.getValue().equals("queen") && inputCard.getColor().equals(GameHandler.getInstance().getTrumpValue()) && GameHandler.getInstance().getRebelote()) {
            GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).setGameScore(
                    GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getGameScore() + 10);
            GameHandler.getInstance().writeAllClient("~Rebelote for player " + GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getId() + "~\n");
        }
    }

    private void inputActions() {
        rep = rep.trim();
        if (rep.equals("hand"))
            displayHand();
        else {
            boolean roundColorValid = false;
            int inputType = this.handleInput();
            boolean playerHasCard = this.checkPlayerCards();
            if (inputType == 0) {
                roundColorValid = this.validCardRoundColor();
            }

            if (inputType == 0 && playerHasCard && roundColorValid) {
                this.validTurn = true;
                this.belote();
                roundCards.add(new CardPair(inputCard, GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getTeamNb(),
                        GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getId()));
                GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getHand().remove(inputCard);
                GameHandler.getInstance().writeAllClient("Player " + GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getId()
                        + " from team " + GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getTeamNb() + " played " + inputCard.cardToString(inputCard) + "\n");
                protocolWriter.getInstance().writeProtocol(chan, new protocol("You played " + inputCard.cardToString(inputCard) + "\n"));
            } else if (inputType == 0 && roundColorValid)
                protocolWriter.getInstance().writeProtocol(chan, new protocol("You don't own that card. Type hand to see your hand\n"));
        }
    }

    private int handleInput() {
        if (rep.contains(" ") && this.validInput()) {
            return 0;
        } else {
            protocolWriter.getInstance().writeProtocol(chan, new protocol("Enter card value : [value] [color]\n"));
            return 2;
        }
    }

    private boolean checkPlayerCards() {
        return GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getHand().contains(inputCard);
    }

    private boolean validCardRoundColor() {
        if (roundColor == null)
            return true;
        else if (roundColor.equals(inputCard.getColor()))
            return true;
        else if (!roundColor.equals(inputCard.getColor()) && GameHandler.getInstance().getPlayers().get(GameHandler.getInstance().getIndexPlayerTurn()).getObjectHand().hasColor(roundColor)) {
            protocolWriter.getInstance().writeProtocol(chan, new protocol("Round color is " + roundColor + ". You need to play some.\n"));
            return false;
        } else
            return true;
    }

    private Integer getIndexCard(String value) {
        Integer index = -1;
        String[] valueSample = Deck.getInstance().getValue();

        for (int i = 0; i < 8; i++) {
            if (valueSample[i].matches(value)) {
                index = i;
                break;
            }
        }
        return (index);
    }

    private void handleEndTurn() {
        HashMap<Integer, Integer> finalValue = new HashMap<>();
        HashMap<Integer, Integer> idPlayer = new HashMap<>();

        for (CardPair cardPair : this.roundCards) {
            if (cardPair.getCard().getColor().equals(this.roundColor)) {
                finalValue.put(this.getIndexCard(cardPair.getCard().getValue()), cardPair.getTeamOwner());
                idPlayer.put(this.getIndexCard(cardPair.getCard().getValue()), cardPair.getId());
            }
            else if (cardPair.getCard().getColor().equals(GameHandler.getInstance().getTrumpValue()) ||
                    GameHandler.getInstance().getTrumpValue().equals("all trump")) {
                finalValue.put(this.getIndexCard(cardPair.getCard().getValue()) + 100, cardPair.getTeamOwner());
                idPlayer.put(this.getIndexCard(cardPair.getCard().getValue()), cardPair.getId());
            }
            System.out.print(cardPair.getCard().cardToString(cardPair.getCard()));
        }
        this.winningPlayer = ScoreCalculator.getInstance().calculateValue(finalValue, roundColor, idPlayer);
        GameHandler.getInstance().writeAllClient("Team " + ScoreCalculator.getInstance().getWinningTeam() + " won the round !" + "\n--NEW TURN--\n");
        GameHandler.getInstance().turn += 1;
        this.roundCards.clear();
        this.endRound = true;
    }
}