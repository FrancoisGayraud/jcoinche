package server.game;

public class Game extends Thread {

    private static Game instance;
    private boolean gameReady = false;

    public static Game getInstance() {
        return instance;
    }

    private void gameLoop() {
        System.out.print("gameloop\r\n");
        GameHandler.getInstance().printClient();
        try {
            sleep(1000);
            //GameHandler.getInstance().sendIsTurn();
        } catch (InterruptedException e) {
            System.out.print("error in Game");
        }
    }

    public void run() {
        try {
            while (true) {
                sleep(1000);
                int i = 3;
                if (GameHandler.getInstance().getNbPlayer() == 4 && !this.gameReady) {
                    this.gameReady = true;
                    System.out.print("Game is ready, starting game\r\n");
                    //while (i > 0) {
                      //  GameHandler.getInstance().writeAllClient("Game is ready, starting game in " + i +  "...\r\n");
                        //sleep(1000);
                        //i--;
                    //}
                }
                if (this.gameReady) {
                    this.gameLoop();
                    break;
                }
            }
        } catch (InterruptedException e) {
            System.out.print("error in Game");
        }
    }
}