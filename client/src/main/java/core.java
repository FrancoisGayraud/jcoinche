import client.KeyboardListener;
import client.client;
import client.guiClient;

import java.util.Arrays;
import java.util.List;

public class core {
    public static void main(String[] args) throws Exception {
        List<String> array = Arrays.asList(args);
        IpAddressValidator validator = new IpAddressValidator();
        if (array.contains("--ip") && args.length > array.indexOf("--ip") + 1 &&
                array.contains("--port") && args.length > array.indexOf("--port") + 1 && args.length >= 4) {
            if (array.get(array.indexOf("--port") + 1).matches("^[0-9]*$") && validator.isValid(array.get(array.indexOf("--ip") +1))) {
                client client = new client(array.get(array.indexOf("--ip") + 1), array.get(array.indexOf("--port") + 1));
                client.start();
                if (array.contains("--gui")) {
                    guiClient gui = new guiClient();
                    gui.startGUI();
                }
                else {
                    KeyboardListener keyboardListener = new KeyboardListener();
                    keyboardListener.start();
                }
            }
            else
                System.out.println("Usage : java -jar [.jar] --ip [ip] --port [port]");
        }
        else
            System.out.println("Usage : java -jar [.jar] --ip [ip] --port [port]");
    }
}