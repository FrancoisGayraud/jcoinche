package client;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import io.netty.channel.Channel;
import common.protocolWriter;
import common.protocol;

public class guiClient extends Thread {
    public void startGUI() throws InterruptedException {
        JFrame frame = new JFrame("JCoinche --gui - A belote coinchee multiplayer game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel upPanel = new JPanel();
        JTextField input = new JTextField(30);
        JButton button = new JButton("Send message");

        JTextArea display = new JTextArea ( 16, 58 );
        display.setEditable ( false ); // set textArea non-editable
        JScrollPane scroll = new JScrollPane ( display );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        upPanel.add(input);
        upPanel.add(button);
        upPanel.add (scroll);
        upPanel.setBorder ( new TitledBorder( new EtchedBorder(), "Jcoinche Game" ) );

        frame.add (upPanel);
        frame.pack();
        frame.setLocationRelativeTo (null);
        frame.setVisible (true);
        while (!DataHandler.getInstance().isChanSet()) {
            System.out.print("Connecting to server...\n");
        }
        DataHandler.getInstance().setDisplay(display);
        Channel chan = DataHandler.getInstance().getChan();
        input.addActionListener(e -> {
            String inputText = input.getText();
            protocolWriter.getInstance().writeProtocol(chan, new protocol(inputText + "\n"));
        });
    }
}
