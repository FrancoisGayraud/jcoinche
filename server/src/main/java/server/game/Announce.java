package server.game;

import common.protocolWriter;
import io.netty.channel.Channel;
import common.protocol;

public class Announce {
    public void announceTurn(String rep, Channel chan) {
        rep = rep.trim();
        boolean err = false;
        boolean value = true;
        Deck deck = new Deck();
        if (rep.contains(" ")) {
            String[] repCut = rep.split(" ");
            for (int i = 0; i < 4; i++) {
                if (GameHandler.getInstance().getPlayers().get(i).getChannel().equals(chan) && GameHandler.getInstance().getPlayers().get(i).getIsTurn()) {
                    if (deck.isValidColor(repCut[0]) || repCut[0].equals("alltrump") || repCut[0].equals("notrump"))
                        GameHandler.getInstance().getPlayers().get(i).setAnnounceColor(repCut[0]);
                    else
                        err = true;
                    if (repCut[1].trim().matches("^[0-9]*$")) {
                        int tmp = Integer.parseInt(repCut[1].trim());
                        if (tmp < 80 || tmp > 160 || tmp % 10 != 0 || !GameHandler.getInstance().checkIfBetGreater(tmp)) {
                            err = true;
                            value = false;
                        }
                        else
                            GameHandler.getInstance().getPlayers().get(i).setAnnounceValue(tmp);
                    } else
                        err = true;
                    if (err) {
                        if (value)
                            protocolWriter.getInstance().writeProtocol(chan, new protocol("Wrong input, [color] [value], [pass] or [coinche]\n"));
                        else
                            protocolWriter.getInstance().writeProtocol(chan, new protocol("Wrong value, your bet must be between 80 and 160, a multiple of " + "10 and superior than the last bet\n"));
                    }
                    else
                        GameHandler.getInstance().setNextTurn(false, -1);
                }
            }
        } else {
            if (rep.matches("pass"))
                GameHandler.getInstance().setNextTurn(true, -1);
            else if (rep.matches("coinche")) {
                for (int i = 0; i < 4; i++) {
                    if (GameHandler.getInstance().getPlayers().get(i).getChannel().equals(chan) && GameHandler.getInstance().getPlayers().get(i).getIsTurn()) {
                        GameHandler.getInstance().getPlayers().get(i).setCoinche(true);
                        GameHandler.getInstance().writeAllClient("Player " + GameHandler.getInstance().getPlayers().get(i).getId() + " has just COINCHE\n");
                    }
                }
                GameHandler.getInstance().setNextTurn(true, -1);
            }
            else
                protocolWriter.getInstance().writeProtocol(chan, new protocol("Wrong input, [color] [value], [pass] or [coinche]\n"));
        }
    }
}
