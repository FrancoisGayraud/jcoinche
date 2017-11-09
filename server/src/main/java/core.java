import server.Server;
import server.game.Game;

public class core {
    public static void main(String[] av) throws Exception {
        try {
            Server server = new Server(8080);
            server.start();
            Game game = new Game();
            game.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}