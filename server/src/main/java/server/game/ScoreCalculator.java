package server.game;

import common.Player;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class ScoreCalculator {
    Integer winningTeam;
    Integer winningPlayer;
    private static ScoreCalculator instance = new ScoreCalculator();

    public static ScoreCalculator getInstance() {
        return (instance);
    }

    public int calculateValue(HashMap<Integer, Integer> round, String roundColor, HashMap<Integer, Integer> id) {
        Integer maxValue = 0;
        winningTeam = 0;
        winningPlayer = 0;
        Integer score = 0;

        for (Integer value : round.keySet()) {
            if (value > maxValue) {
                maxValue = value;
                winningPlayer = id.get(value);
                winningTeam = round.get(value);
            }
        }
        for (Integer value : round.keySet()) {
            if (value >= 100)
                score += value - 95;
            else
                score += value;
        }
        for (Player player : GameHandler.getInstance().getPlayers()) {
            if (player.getTeamNb() == winningTeam) {
                player.setGameScore(score + player.getGameScore());
                System.out.println("player : " + player.getId() + "from team : " + player.getTeamNb() + "score is " + player.getGameScore());
            }
        }
        try {
            for (int i = 0; i <= 3; i++) {
                if (GameHandler.getInstance().getPlayers().get(i).getId() == winningPlayer) {
                    winningPlayer = i;
                    break;
                }
            }
        } catch (NullPointerException e) {
            System.out.println("winning player : " + 0 + "\n");
        }
        if (winningPlayer == null)
            winningPlayer = 0;
        System.out.println("winning player : " + winningPlayer + "\n");
        return winningPlayer;
    }

    public Integer getWinningTeam() {
        return (this.winningTeam);
    }

    public Integer getWinningPlayer() {
        return (this.winningPlayer);
    }

}
