package client;

import common.protocol;
import io.netty.channel.Channel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import common.protocolWriter;

public class KeyboardListener extends Thread {
    public void run() {
        while (!DataHandler.getInstance().isUserQuit()) {
            System.out.print("Connecting to server...\n");
            if (DataHandler.getInstance().isChanSet()) {
                Channel chan = DataHandler.getInstance().getChan();
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String s = null;
                do {
                    try {
                        s = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!DataHandler.getInstance().getServerDisconnect())
                        protocolWriter.getInstance().writeProtocol(chan, new protocol(s + "\n"));
                } while ((s != null && !s.equals("quit")) && !DataHandler.getInstance().getServerDisconnect());
                DataHandler.getInstance().setUserQuit(true);
            }
        }
    }
}

