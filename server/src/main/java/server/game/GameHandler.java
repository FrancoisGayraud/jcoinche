package server.game;
import common.Card;
import common.CardPair;
import common.Hand;
import common.Player;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import common.fileWriter;
import common.*;

public class GameHandler {
    private List<Player> players = new ArrayList<>();
    private List<Player> waitingRoom = new ArrayList<>();
    private int nbPlayer = 0;
    private int playerId = 1;
    private boolean runningAnnounce = true;
    private boolean oneAnnounce = false;
    private boolean runningGame = false;
    private int indexPlayerTurn = 0;
    private static GameHandler instance = new GameHandler();
    private int passTurn = 0;
    private int nbWaiting = 0;
    private int bet = 0;
    private boolean rebelote= false;
    private List<CardPair> roundCards = new ArrayList<>();
    private int firstPlayerIndex;
    private String trumpValue;
    public int turn = 0;

    public static GameHandler getInstance() {
        return instance;
    }

    private void initDeck() {
        Deck.getInstance().initDeck();
    }

    public int getNbPlayer() {
        return (this.nbPlayer);
    }

    public String getTrumpValue() {
        return (this.trumpValue);
    }

    private void setTrumpValue() {
        String color = "";
        int max = 0;
        int teamNb = 1;

        for (Player player : players) {
            if (player.getAnnounceValue() > max) {
                max = player.getAnnounceValue();
                color = player.getAnnounceColor();
                teamNb = player.getTeamNb();
            }
        }
        this.trumpValue = color;
        this.writeAllClient("The trump color is : " + this.trumpValue + " set by team " + teamNb + " with a call of " + max + "\n");
    }

    public boolean notYourTurn(Channel chan) {
        for (int i = 0; i < this.nbPlayer; i++) {
            if (players.get(i).getChannel().equals(chan) && !players.get(i).getIsTurn()) {
                protocolWriter.getInstance().writeProtocol(chan, new protocol("It's not your turn\n"));
                return false;
            }
        }
        return true;
    }

    void setNextTurn(boolean pass, int newIndex) {
        if (!this.runningGame) {
            if (!pass) {
                this.oneAnnounce = true;
                this.writeAllClient("Player " + players.get(this.indexPlayerTurn).getId() + " from team " + players.get(this.indexPlayerTurn).getTeamNb()
                        + " make a call of " + players.get(this.indexPlayerTurn).getAnnounceValue() + " in " +
                        players.get(this.indexPlayerTurn).getAnnounceColor() + " color\r\n");
                this.passTurn = 0;
                this.bet = players.get(this.indexPlayerTurn).getAnnounceValue();
            } else {
                this.writeAllClient("Player " + players.get(this.indexPlayerTurn).getId() + " from team " + players.get(indexPlayerTurn).getTeamNb()
                        + " just pass\r\n");
                this.passTurn += 1;
            }
        }
        if (newIndex == -1) {
            this.players.get(this.indexPlayerTurn).setIsTurn(false);
            this.indexPlayerTurn += 1;
            if (this.indexPlayerTurn >= 4)
                this.indexPlayerTurn = 0;
            this.players.get(this.indexPlayerTurn).setIsTurn(true);
        }
        else {
            this.players.get(this.indexPlayerTurn).setIsTurn(false);
            this.indexPlayerTurn = newIndex;
            this.players.get(this.indexPlayerTurn).setIsTurn(true);
        }
        this.sendIsTurn();
    }

    public boolean checkIfBetGreater(int val) {
        if (val <= this.bet)
            return (false);
        else
            return (true);
    }

    public void setResponse(String rep, Channel chan) {
        if (this.notYourTurn(chan))
        {
            if (this.runningAnnounce) {
                Announce announce = new Announce();
                announce.announceTurn(rep, chan);
            }
            else if (this.runningGame) {
                Round round = new Round(rep, chan);
                round.gameTurn();
            }
        }
        if (this.passTurn == 3 && this.oneAnnounce && !this.runningGame) {
            writeAllClient("--ROUND BEGIN--\r\n");
            this.setTrumpValue();
            this.runningAnnounce = false;
            this.runningGame = true;
            this.firstPlayerIndex = this.indexPlayerTurn;
            this.sendIsTurn();
        }
        else if (this.passTurn == 4 && !this.oneAnnounce)
            this.startingGame();
        this.printClient();
        if (this.turn == 6) {
            this.writeAllClient("10 de der! This turn have a 10 points bonus\n");
        }
        if (this.turn == 8) {
            this.writeAllClient("--GAME OVER--\n");
            finalWrite();
        }
    }

    private void finalWrite() {
        int score = 0;
        int winningTeam = 0;
        for (Player player : this.players) {
            if (player.getGameScore() > score) {
                score = player.getGameScore();
                winningTeam = player.getTeamNb();
            }
        }
        for (Player player : this.players) {
            if (player.getTeamNb() != winningTeam) {
                if (player.getCoinche())
                    score = 370;
            }
        }
        String resultString = "Team : " + winningTeam + " win the match with a score of " + score + "\n";
        fileWriter resultWriter = new fileWriter("./result.log");
        resultWriter.writeFileAndCreateIfNeeded(resultString);
        this.writeAllClient(resultString);
    }

    public void distributeHand() {
        for (int i = 0; i < this.nbPlayer; i++) {
            Hand hand = new Hand();
            hand.setHand(Deck.getInstance().distributeOneHandFirst());
            players.get(i).setHand(hand.getHand());
        }
    }

    public void pushClient(Channel ch, String name) {
        if (this.nbPlayer < 4) {
            players.add(new Player(ch, this.playerId, name));
            this.playerId += 1;
            this.nbPlayer += 1;
            System.out.print("Number of players : " + this.nbPlayer + "\r\n");
            System.out.print("New player add to the game\r\n");
            this.writeAllClient("A new player has joined the game\r\n");
            protocolWriter.getInstance().writeProtocol(ch, new protocol("Welcome To JCoinche\nWaiting for other players...\n"));
            if (this.nbPlayer == 4) {
                this.startingGame();
            }
        }
        else {
            waitingRoom.add(new Player(ch, this.playerId, name));
            this.playerId += 1;
            this.nbWaiting += 1;
            protocolWriter.getInstance().writeProtocol(ch, new protocol("There is already 4 players, waiting for someone to disconnect\n"));
        }

    }

    public void deleteClient(Channel ch) {
        for (int i = 0; i < this.nbPlayer; i++) {
            if (players.get(i).getChannel().equals(ch)) {
                System.out.print("Player " + players.get(i).getId() + " has left the room\r\n");
                this.writeAllClient("Player " + players.get(i).getId() + " has left the room\r\n");
                players.remove(i);
                this.nbPlayer -= 1;
            }
        }
        for (int i = 0; i < this.nbWaiting; i++) { //remove waiting room client when deco
            if (waitingRoom.get(i).getChannel().equals(ch)) {
                System.out.print("Player " + waitingRoom.get(i).getId() + " has left the waiting room\r\n");
                this.writeAllClient("Player " + waitingRoom.get(i).getId() + " has left the waiting room\r\n");
                waitingRoom.remove(i);
                this.nbWaiting -= 1;
            }
        }
        if (this.nbPlayer < 4 && !waitingRoom.isEmpty()) {
            this.players.add(new Player(waitingRoom.get(0).getChannel(), waitingRoom.get(0).getId(), waitingRoom.get(0).getName()));
            waitingRoom.remove(0);
            this.nbWaiting -= 1;
            this.writeAllClient("A new player has joined the game\r\n");
            this.nbPlayer += 1;
            this.startingGame();
        }

    }

    public void printClient() {
        for (Player player : players)
            System.out.print("PLAYER : " + player.getId() + " " +
                    player.getName() + " " + player.getChannel() + " "
                    + player.getTeamNb() + " " + player.getIsTurn() + "\r\n");
    }

    public List<Player> getPlayers() {
        return (this.players);
    }

    public void writeAllClient(String msg) {
        for (Player player : players) {
            protocolWriter.getInstance().writeProtocol(player.getChannel(), new protocol(msg));
        }
    }

    public void initGame() {
        int teamNb = 1;
        for (int i = 0; i < this.nbPlayer; i++) {
            if (i % 2 == 0)
                teamNb++;
            else
                teamNb--;
            players.get(i).setTeamNb(teamNb);
        }
        players.get(0).setIsTurn(true);
    }

    public void sendIsTurn() {
        for (int i = 0; i < this.nbPlayer; i++) {
            if (players.get(i).getIsTurn()) {
                protocolWriter.getInstance().writeProtocol(players.get(i).getChannel(), new protocol("It's your turn\n"));
                if (this.runningAnnounce)
                    protocolWriter.getInstance().writeProtocol(players.get(i).getChannel(), new protocol("You have to bet, make your call : [color] [value], [pass] or [coinche]\n"));
                else if (this.runningGame)
                    protocolWriter.getInstance().writeProtocol(players.get(i).getChannel(), new protocol("You have to play a card [value] [color]\n"));

            }
            else
                protocolWriter.getInstance().writeProtocol(players.get(i).getChannel(), new protocol("Waiting for your turn...\n"));

        }
    }

    public void sendHand() {
        for (int i = 0; i < this.nbPlayer; i++) {
            protocolWriter.getInstance().writeProtocol(players.get(i).getChannel(), new protocol("Your hand is : " + players.get(i).getTeamNb() + "\n"));
        }
    }

    public void sendPlayersHand() {
        for (int i = 0; i < this.nbPlayer; i++) {
            players.get(i).printHand();
        }
    }

    public void startingGame() {
        this.initDeck();
        this.distributeHand();
        this.initGame();
        this.resetGame();
        this.sendPlayersHand();
        this.writeAllClient("Starting game\r\n");
        for (int i = 0; i < this.nbPlayer; i++) {
            protocolWriter.getInstance().writeProtocol(players.get(i).getChannel(), new protocol("Your team is : " + players.get(i).getTeamNb() + "\n"));
        }
        this.sendIsTurn();
    }

    public boolean checkIfWaiting(Channel chan) {
        for (Player player : waitingRoom) {
            if (player.getChannel().equals(chan))
                return (true);
        }
        return (false);
    }

    void printHandPlayer(Channel chan) {
        for (Player player : players) {
            if (player.getChannel().equals(chan))
                player.printHand();
        }
    }

    public void resetGame() {
        for (Player player : players) {
            player.setAnnounceValue(0);
            player.setAnnounceColor(" ");
        }
        this.indexPlayerTurn = 0;
        this.passTurn = 0;
        this.rebelote = false;
        this.runningAnnounce = true;
        this.runningGame = false;
        this.oneAnnounce = false;
        this.bet = 0;
        this.resetRoundCards();
    }

    public boolean getRebelote() {
        return (this.rebelote);
    }

    public void setRebelote(boolean rebelote) {
        this.rebelote = rebelote;
    }

    public List<CardPair> getRoundCards() {
        return roundCards;
    }

    public int getIndexPlayerTurn() {
        return indexPlayerTurn;
    }

    public void setIndexPlayerTurn(int indexPlayerTurn) {
        this.indexPlayerTurn = indexPlayerTurn;
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }

    public void setRunningAnnounce(boolean bool) {
        this.runningAnnounce = bool;
    }

    public void resetRoundCards() {
        this.roundCards.clear();
    }
}
