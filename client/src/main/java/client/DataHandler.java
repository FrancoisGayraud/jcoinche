package client;

import io.netty.channel.Channel;
import common.protocol;

import javax.swing.*;

public class DataHandler {
    private static DataHandler instance = new DataHandler();
    private Channel chan;
    private boolean chanSet = false;
    private boolean userQuit = false;
    private boolean serverDisconnect = false;
    private JTextArea display = null;

    static DataHandler getInstance() {
        return instance;
    }

    void setChannel(Channel chan) {
        this.chan = chan;
        this.chanSet = true;
    }

    Channel getChan() {
        return chan;
    }

    boolean isChanSet() {
        return chanSet;
    }

    boolean isUserQuit() {
        return userQuit;
    }

    boolean getServerDisconnect() {
        return (this.serverDisconnect);
    }

    void setServerDisconnect(boolean bool) {
        this.serverDisconnect = bool;
    }

    void setUserQuit(boolean userQuit) {
        this.userQuit = userQuit;
        if (userQuit) {
            System.out.print("Shutting down...\n");
        }
    }

    void setDisplay(JTextArea display) {
        this.display = display;
    }

    void writeDisplay(protocol proto) {
        if (display != null)
            display.append(proto.data);
    }

}
