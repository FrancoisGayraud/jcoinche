package common;

import io.netty.channel.Channel;

import java.util.List;

public class Player {

    private String name;
    private int id;
    private Channel chan;
    private boolean isTurn;
    private int teamNb;
    private Hand hand;
    private int announceValue;
    private String announceColor;
    private int gameScore;
    private boolean coinche;

    public Player(Channel chan, int id, String name) {
        this.chan = chan;
        this.id = id;
        this.name = name;
        this.isTurn = false;
        this.coinche = false;
        this.hand = new Hand();
        this.announceValue = 0;
        this.gameScore = 0;
    }

    public boolean getCoinche() {
        return (this.coinche);
    }

    public void setCoinche(boolean coinche) {
        this.coinche = coinche;
    }

    public String getName()
    {
        return (this.name);
    }

    public int getId()
    {
        return (this.id);
    }

    public Channel getChannel()
    {
        return (this.chan);
    }

    public boolean getIsTurn() {
        return (this.isTurn);
    }

    public int getAnnounceValue() {
        return (this.announceValue);
    }

    public String getAnnounceColor() {
        return (this.announceColor);
    }

    public int getTeamNb() {
        return (this.teamNb);
    }

    public void setTeamNb(int nb) {
        this.teamNb = nb;
    }

    public void setIsTurn(boolean turn) {
        this.isTurn = turn;
    }

    public List<Card> getHand() {
        return (this.hand.getHand());
    }

    public Hand getObjectHand() { return this.hand; }

    public void pushCard(Card card) {
        this.hand.pushCard(card);
    }

    public void setHand(List<Card> h) {
        this.hand.setHand(h);
    }

    public void setAnnounceValue(int val) {
        this.announceValue = val;
    }

    public void setAnnounceColor(String col) {
        this.announceColor = col;
    }

    public void printHand() {
        protocolWriter.getInstance().writeProtocol(chan, new protocol("Your hand is : \n"));
        for (Card card : hand.getHand()) {
            protocolWriter.getInstance().writeProtocol(chan, new protocol(card.getValue() + " "  + card.getColor() + "\n"));
        }
    }

    public int getGameScore() {
        return gameScore;
    }

    public void setGameScore(int gameScore) {
        this.gameScore = gameScore;
    }
}