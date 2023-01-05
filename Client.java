import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Client {
    private static JFrame window;
    private JPanel cards;
    //private static MyPanel gamePanel;

    // The different screens.
    private MenuPanel menuScreen;
    private LobbySelectPanel lobbySelectScreen;
    private JPanel abilitySelectScreen;
    private JPanel lobbyScreen;
    private JPanel howToPlayScreen;
    private JPanel gameScreen;
    private JPanel midRoundScreen;
    private JPanel gameOverScreen;

    public final static String MENU_PANEL = "main menu screen";
    public final static String LOBBY_SELECT_PANEL = "lobby select screen";
    public final static String ABILITY_SELECT_PANEL = "ability select screen";
    public final static String LOBBY_PANEL = "lobby screen";
    public final static String HOW_TO_PLAY_PANEL = "how to play screen";
    public final static String GAME_PANEL = "game screen";
    public final static String MID_ROUND_PANEL = "mid round screen";
    public final static String GAME_OVER_PANEL = "game over screen";

    private String name;

    private PrintWriter output;    
    private BufferedReader input;
    private static ServerHandler server;

    private Socket clientSocket;
    private final String HOST = "localhost";
    private final int PORT = 5001;
    protected static boolean playing;
    
    private int mouseX;
    private int mouseY;

    public static void main(String[] args) throws IOException{
        Client client = new Client();
        client.setup();
        server.start();
        while(true){  
            try {
                Thread.sleep(10);
            } catch (Exception e) {}
            if (playing){
                window.repaint();
            }   
        }
    }
    //-------------------------------------------------
    public class MyMouseMotionListener implements MouseMotionListener{   
        public void mouseMoved(MouseEvent  e){
            mouseX = e.getX();
            mouseY = e.getY();
        }
        public void mouseDragged(MouseEvent  e){}
    }
    //-------------------------------------------------
    private void setup() throws IOException{
        window = new JFrame("Dungeon Runner");
        window.setPreferredSize(new Dimension(Const.WIDTH, Const.HEIGHT));// adding because of window problems   
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setSize(Const.WIDTH, Const.HEIGHT);
        this.cards = new JPanel(new CardLayout());

        clientSocket = new Socket(HOST, PORT);  
        output = new PrintWriter(clientSocket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        server = new ServerHandler(this);

         //Initialize the screens.
         this.menuScreen = new MenuPanel(Const.MENU_BACKGROUND);
         this.lobbySelectScreen = new LobbySelectPanel(Const.MENU_BACKGROUND);
         this.lobbyScreen = new LobbyPanel(Const.MENU_BACKGROUND);
         //this.howToPlayScreen = new HowToPlayScreenPanel(Const.MENU_BACKGROUND);
         //this.lobbyScreen = new LobbyScreenPanel(Const.MENU_BACKGROUND);
         //this.gameScreen = new GameScreenPanel(null);
         //this.pauseScreen = new PauseScreenPanel(Const.MENU_BACKGROUND);
         //this.gameOverScreen = new GameOverScreenPanel(Const.MENU_BACKGROUND);
 
         // Add the screens to the window manager.
         cards.add(menuScreen, MENU_PANEL);
         cards.add(lobbySelectScreen, LOBBY_SELECT_PANEL);
         //cards.add(howToPlayScreen, HOW_TO_PLAY_PANEL);
         /*cards.add(createLobbyScreen, CREATE_LOBBY_PANEL);
         cards.add(lobbyScreen, LOBBY_PANEL);
         cards.add(gameScreen, GAME_PANEL);
         cards.add(gameOverScreen, GAME_OVER_PANEL);
         cards.add(pauseScreen, PAUSE_PANEL);*/
 
         window.add(cards);
         window.setVisible(true);
         window.setResizable(false);
         window.pack();

        window.setVisible(true);
    }
    public void stop() throws Exception{ 
        input.close();
        output.close();
        clientSocket.close();
    }
    private int calculateAngle(){
        int angle = (int)(Math.atan( (double)(mouseY - (double)Const.HEIGHT/2) / (mouseX - (double)Const.WIDTH/2)) * (180 / Math.PI));
        int raa = Math.abs(angle); // related acute angle
        if (mouseX >= (double)Const.WIDTH/2 && mouseY >= (double)Const.HEIGHT/2) return raa;
        else if (mouseX < (double)Const.WIDTH/2 && mouseY >= (double)Const.HEIGHT/2) return 180 - raa;
        else if (mouseX < (double)(Const.WIDTH)/2 && mouseY < (double)Const.HEIGHT/2) return 180 + raa;
        else return 360 - raa;
    }
    

    /*private boolean withinFOV(Circle entity){
        boolean inFOV = false;
        if(((myBall.getX() - entity.getX() + entity.getRadius() <= Const.WIDTH/2) ||
           (entity.getX() - entity.getRadius() - myBall.getX() <= Const.WIDTH/2)) &&
           ((myBall.getY() - entity.getY() + entity.getRadius() <= Const.HEIGHT/2) ||
           (entity.getY() - entity.getRadius() - myBall.getY() <= Const.HEIGHT/2))){
            inFOV = true;
        }
        return inFOV;
    }*/
    class ServerHandler extends Thread{
        private Client client;
        private Pinger pinger;
        ServerHandler(Client client){
            this.client = client;
            this.pinger = new Pinger(this.client.output);
            this.pinger.start();
        }

        @Override 
        public void run(){
            while(true){
                String update = "";
                String[] updateInfo = new String[8];
                try {
                    update = input.readLine();
                } catch (Exception e) {}
                if(update != "" || update != null){
                    System.out.println(update);
                    updateInfo = update.split(" ", 8);
                    // From server
                    if(updateInfo[0].equals(Const.LOBBY)){
                        String lobbyName = updateInfo[1];
                        String lobbyCount = updateInfo[2];
                        lobbySelectScreen.newLobbyBanner(lobbyName, lobbyCount, Const.LOBBY_BANNER_START_Y + lobbySelectScreen.lobbies().size() * 100);
                    }
                    else if(updateInfo[0].equals(Const.LOBBY_SELECT)){
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, LOBBY_SELECT_PANEL);
                        swapper.swap();
                    }
                    /* From lobby
                    else if(updateInfo[0].equals(Const.JOIN)){
                        
                    }
                    else if(updateInfo[0].equals(Const.NEW)){
                        
                    }
                    else if(updateInfo[0].equals(Const.PELLET)){
                        
                    }
                    else if(updateInfo[0].equals(Const.BALL)){
                        
                    }
                    else if(updateInfo[0].equals(Const.REMOVE)){
                        
                    }*/
                }
            }
        }
        private class Pinger extends Thread{
            PrintWriter output;
            Pinger(PrintWriter output){
                this.output = output;
            }
            @Override 
            public void run(){
                while(true){
                    output.println(Const.PING);
                    output.flush();
                    try {
                        Thread.sleep(20000);
                    } catch (Exception e){}
                }
            }
        }
    }

    public static class ScreenSwapper implements ActionListener {
        private String nextPanel;
        private JPanel cards;
        /*
         * Constructs a ScreenSwapper object with the screen to switch to.
         * @param cards The JPanel that stores the different screens.
         * @param nextPanel The screen to switch to.
         */
        public ScreenSwapper(JPanel cards, String nextPanel) {
            this.nextPanel = nextPanel;
            this.cards = cards;
        }
        @Override
        public void actionPerformed(ActionEvent event) {
            this.swap();
        }
        /** 
         * This method swaps the current screen to the set screen.
        */
        public void swap() {
            System.out.println("Switching to " + this.nextPanel);

            // Switch to the specified panel.
            CardLayout layout = (CardLayout) this.cards.getLayout();
            layout.show(this.cards, this.nextPanel);
        }
    }

    public class MenuPanel extends ScreenPanel {
        private ServerButton playButton;
        private TextButton howToPlayButton;
        //private TextButton creditsButton;

        public MenuPanel(Image backgroundSprite) {
            super(backgroundSprite);
            Font buttonFont = Const.MENU_BUTTON_FONT;
            Color fontColor = Color.WHITE;
            // Initialize the buttons.
            int playY = 260;
            Text playButtonText = new Text("Play", buttonFont, fontColor, Const.HALF_WIDTH, playY);
            this.playButton = new ServerButton(window, output, playButtonText, Const.LOBBIES_LIST, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, playY, Const.RADIUS);
            
            int howToPlayY = 430;
            Text howToPlayButtonText = new Text("How To Play", buttonFont, fontColor, Const.HALF_WIDTH, howToPlayY);
            this.howToPlayButton = new TextButton(window, cards, HOW_TO_PLAY_PANEL, howToPlayButtonText, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, howToPlayY, Const.RADIUS);
            
            
            /*this.creditsButton = new TextButton(window, cards, CREDITS_PANEL, "Credits", buttonFont, fontColor, 
                                         Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, 770, Const.RADIUS);*/
            
            // Add the listeners for the screens.
            this.addMouseListener(playButton.new BasicMouseListener());
            this.addMouseMotionListener(playButton.new InsideButtonMotionListener());
            this.addMouseListener(howToPlayButton.new BasicMouseListener());
            this.addMouseMotionListener(howToPlayButton.new InsideButtonMotionListener());
            //this.addMouseListener(creditsButton.new BasicMouseListener());
            //this.addMouseMotionListener(creditsButton.new InsideButtonMotionListener());
            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            this.playButton.draw(graphics);
            this.howToPlayButton.draw(graphics);
            //this.creditsButton.draw(graphics);
        }
        public final ComponentAdapter FOCUS_WHEN_SHOWN = new ComponentAdapter(){
            public void componentShown(ComponentEvent event){
                requestFocusInWindow();
            }
        };
    }

    public class LobbySelectPanel extends ScreenPanel {
        private TextButton newlobbyButton;
        private TextButton backButton;
        private ArrayList<LobbyBanner> lobbies;
        //private TextButton creditsButton;

        public LobbySelectPanel(Image backgroundSprite) {
            super(backgroundSprite);
            Font buttonFont = Const.SMALL_BUTTON_FONT;
            Color fontColor = Color.WHITE;
            // Initialize the buttons.
            Text newlobbyText = new Text("New Lobby", buttonFont, fontColor, Const.CONTINUE_X, Const.GO_BACK_Y);
            this.newlobbyButton = new TextButton(window, cards, LOBBY_SELECT_PANEL, newlobbyText, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y, Const.RADIUS);
            
            Text backText = new Text("Go back", buttonFont, fontColor, Const.GO_BACK_X, Const.GO_BACK_Y);
            this.backButton = new TextButton(window, cards, HOW_TO_PLAY_PANEL, backText, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y, Const.RADIUS);
            
            lobbies = new ArrayList<LobbyBanner>();
            
            // Add the listeners for the screens.
            this.addMouseListener(newlobbyButton.new BasicMouseListener());
            this.addMouseMotionListener(newlobbyButton.new InsideButtonMotionListener());
            this.addMouseListener(backButton.new BasicMouseListener());
            this.addMouseMotionListener(backButton.new InsideButtonMotionListener());

            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            this.newlobbyButton.draw(graphics);
            this.backButton.draw(graphics);
            for (LobbyBanner lobbyBanner: lobbies) {
                lobbyBanner.draw(graphics);
            }
        }
        public ArrayList<LobbyBanner> lobbies() {
            return this.lobbies;
        }
        public void newLobbyBanner(String name, String playerCount, int y) {
            LobbyBanner lobbyBanner = new LobbyBanner(name, playerCount, y);
            lobbies.add(lobbyBanner);
        }
        public final ComponentAdapter FOCUS_WHEN_SHOWN = new ComponentAdapter(){
            public void componentShown(ComponentEvent event){
                requestFocusInWindow();
            }
        };
        public class LobbyBanner{
            private Text name;
            private Text playerCount;
            private ServerButton joinButton;
            LobbyBanner(String name, String playerCount, int y){
                this.name = new Text(name, Const.SMALL_BUTTON_FONT, Const.SMALL_BUTTON_IN_COLOR, Const.LOBBY_NAME_X, y);
                this.playerCount = new Text(playerCount, Const.SMALL_BUTTON_FONT, Const.SMALL_BUTTON_IN_COLOR, Const.LOBBY_COUNT_X, y);
            }

            public void draw(Graphics graphics){
                name.draw(graphics);
                playerCount.draw(graphics);
            }
        }
    }

    public class LobbyPanel extends ScreenPanel {
        private TextButton playButton;
        private TextButton howToPlayButton;
        //private TextButton creditsButton;

        public LobbyPanel(Image backgroundSprite) {
            super(backgroundSprite);
            Font buttonFont = Const.MENU_BUTTON_FONT;
            Color fontColor = Color.WHITE;
            // Initialize the buttons.
            int playY = 260;
            Text playButtonText = new Text("Play", buttonFont, fontColor, Const.HALF_WIDTH, playY);
            this.playButton = new TextButton(window, cards, LOBBY_SELECT_PANEL, playButtonText, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, playY, Const.RADIUS);
            
            int howToPlayY = 430;
            Text howToPlayButtonText = new Text("How To Play", buttonFont, fontColor, Const.HALF_WIDTH, howToPlayY);
            this.howToPlayButton = new TextButton(window, cards, HOW_TO_PLAY_PANEL, howToPlayButtonText, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, howToPlayY, Const.RADIUS);
            
            
            /*this.creditsButton = new TextButton(window, cards, CREDITS_PANEL, "Credits", buttonFont, fontColor, 
                                         Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, 770, Const.RADIUS);*/
            
            // Add the listeners for the screens.
            this.addMouseListener(playButton.new BasicMouseListener());
            this.addMouseMotionListener(playButton.new InsideButtonMotionListener());
            this.addMouseListener(howToPlayButton.new BasicMouseListener());
            this.addMouseMotionListener(howToPlayButton.new InsideButtonMotionListener());
            //this.addMouseListener(creditsButton.new BasicMouseListener());
            //this.addMouseMotionListener(creditsButton.new InsideButtonMotionListener());
            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            this.playButton.draw(graphics);
            this.howToPlayButton.draw(graphics);
            //this.creditsButton.draw(graphics);
        }
        public final ComponentAdapter FOCUS_WHEN_SHOWN = new ComponentAdapter(){
            public void componentShown(ComponentEvent event){
                requestFocusInWindow();
            }
        };
    }
}