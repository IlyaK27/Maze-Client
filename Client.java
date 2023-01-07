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

    private PrintWriter output;    
    private BufferedReader input;
    private static ServerHandler server;

    private Socket clientSocket;
    private final String HOST = "localhost";
    private final int PORT = 5001;
    protected static boolean playing;
    private String lobbyName;
    
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
                System.out.println("wee");
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
         this.lobbySelectScreen = new LobbySelectPanel(Const.WALL_BACKGROUND);
         this.abilitySelectScreen = new AbilitySelectPanel(Const.BLANK_BACKGROUND);
         this.lobbyScreen = new LobbyPanel(Const.WALL_BACKGROUND);
         //this.howToPlayScreen = new HowToPlayScreenPanel(Const.MENU_BACKGROUND);
         //this.lobbyScreen = new LobbyScreenPanel(Const.MENU_BACKGROUND);
         //this.gameScreen = new GameScreenPanel(null);
         //this.pauseScreen = new PauseScreenPanel(Const.MENU_BACKGROUND);
         //this.gameOverScreen = new GameOverScreenPanel(Const.MENU_BACKGROUND);
 
         // Add the screens to the window manager.
         cards.add(menuScreen, MENU_PANEL);
         cards.add(lobbySelectScreen, LOBBY_SELECT_PANEL);
         cards.add(abilitySelectScreen, ABILITY_SELECT_PANEL);
         cards.add(lobbyScreen, LOBBY_PANEL);
         //cards.add(howToPlayScreen, HOW_TO_PLAY_PANEL);
         /*cards.add(createLobbyScreen, CREATE_LOBBY_PANEL);
         cards.add(gameScreen, GAME_PANEL);
         cards.add(gameOverScreen, GAME_OVER_PANEL);
         cards.add(pauseScreen, PAUSE_PANEL);*/
 
         window.add(cards);
         window.setVisible(true);
         window.setResizable(false);
         window.pack();
        playing = false;
        lobbyName = "";
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
                        String lobbyName = updateInfo[1] + " Lobby";
                        String lobbyCount = "Players = " + updateInfo[2] + "/4";
                        lobbySelectScreen.newLobbyBanner(lobbyName, lobbyCount, Const.LOBBY_BANNER_START_Y + lobbySelectScreen.lobbies().size() * Const.SPACE_BETWEEN_LOBBIES);      
                    }
                    else if(updateInfo[0].equals(Const.LOBBY_SELECT)){
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, LOBBY_SELECT_PANEL);
                        swapper.swap();
                    }
                    else if(updateInfo[0].equals(Const.CLEAR_LOBBIES)){
                        this.client.lobbySelectScreen.lobbies.clear();
                    }

                    else if(updateInfo[0].equals(Const.NAME)){
                        Client.ServerWriter writer = new Client.ServerWriter(output);
                        writer.print(Const.NAME + " " + this.client.lobbySelectScreen.name());
                    }
                    else if(updateInfo[0].equals(Const.JOINED)){
                        lobbyName = updateInfo[1];
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, ABILITY_SELECT_PANEL);
                        swapper.swap();
                    }
                    else if(updateInfo[0].equals(Const.LEAVE)){ // This command is given after the player wants to leave a lobby
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, MENU_PANEL); // After player leaves lobby they can swap to main menu right away since the lobbyName will always be correct
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

    public static class ServerWriter{
        private PrintWriter output;
        /*
         * Constructs a ServerWriter object with the command to write to the server.
         * @param output PrintWriter that is connected to the server
         */
        public ServerWriter(PrintWriter output) {
            this.output = output;
        }
        public void print(String text){
            output.println(text);
            output.flush();
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
            Color fontColor = Const.LARGE_BUTTON_FONT_COLOR;
            // Initialize the buttons.
            int playY = 300;
            Text playButtonText = new Text("Play", buttonFont, fontColor, Const.HALF_WIDTH, playY);
            this.playButton = new ServerButton(window, output, playButtonText, Const.LOBBIES_LIST, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, playY, Const.RADIUS);
            
            int howToPlayY = 470;
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
        private ServerButton newlobbyButton;
        private TextButton backButton;
        private TextButton nameField;
        private Text playerName;
        private ArrayList<LobbyBanner> lobbies;
        //private TextButton creditsButton;

        public LobbySelectPanel(Image backgroundSprite) {
            super(backgroundSprite);
            // Initialize the buttons.
            Text newlobbyText = new Text("New Lobby", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y);
            this.newlobbyButton = new ServerButton(window, output, newlobbyText, Const.NEW_LOBBY ,Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y, Const.RADIUS);
            
            Text backText = new Text("Go back", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y);
            this.backButton = new TextButton(window, cards, MENU_PANEL, backText, Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y, Const.RADIUS);

            int nameY = 170;
            this.playerName = new Text("Player", Const.TEXT_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.HALF_WIDTH, nameY);
            Text nameFieldText = new Text("                     ", Const.TEXT_FONT, Color.WHITE, Const.HALF_WIDTH, nameY);
            this.nameField = new TextButton(window, null, null, nameFieldText, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, nameY, Const.RADIUS);
            lobbies = new ArrayList<LobbyBanner>();
            
            // Add the listeners for the screens.
            this.addMouseListener(newlobbyButton.new BasicMouseListener());
            this.addMouseMotionListener(newlobbyButton.new InsideButtonMotionListener());
            this.addMouseListener(backButton.new BasicMouseListener());
            this.addMouseMotionListener(backButton.new InsideButtonMotionListener());
            this.addMouseListener(nameField.new BasicMouseListener());
            this.addMouseMotionListener(nameField.new InsideButtonMotionListener());
            this.addKeyListener(new NameKeyListener());

            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            this.newlobbyButton.draw(graphics);
            this.backButton.draw(graphics);
            this.nameField.draw(graphics);
            this.playerName.draw(graphics);
            for (LobbyBanner lobbyBanner: lobbies) {
                lobbyBanner.draw(graphics);
            }
        }
        public ArrayList<LobbyBanner> lobbies() {
            return this.lobbies;
        }
        public String name(){
            return this.playerName.getText();
        }
        public void newLobbyBanner(String lobbyName, String playerCount, int y) {
            LobbyBanner lobbyBanner = new LobbyBanner(lobbyName, playerCount, y, this.playerName.getText());
            this.addMouseListener(lobbyBanner.joinButton().new BasicMouseListener());
            this.addMouseMotionListener(lobbyBanner.joinButton().new InsideButtonMotionListener());
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
            private int y;
            LobbyBanner(String name, String playerCount, int y, String playerName){
                this.name = new Text(name, Const.LOBBY_BANNER_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, 0, y);
                this.name.setX(Const.LOBBY_INFO_X); // Setting x's after so that they always line up no matter how long the name is
                this.playerCount = new Text(playerCount, Const.LOBBY_BANNER_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, 0, y + Const.LOBBY_COUNT_Y_DIFFERENCE);
                this.playerCount.setX(Const.LOBBY_INFO_X); // Setting x's after so that they always line up no matter how long the name is
                this.y = y;
                Text joinText = new Text("Join", Const.LOBBY_BANNER_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.LOBBY_JOIN_X, y + Const.LOBBY_JOIN_Y_DIFFERENCE);
                String command = Const.JOIN_LOBBY + " " + name + " " + playerName;
                this.joinButton = new ServerButton(window, output, joinText, command, Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                Const.SMALL_BUTTON_HOVER_COLOR, Const.LOBBY_JOIN_X, y + Const.LOBBY_JOIN_Y_DIFFERENCE, Const.RADIUS - 5);
            }
            public void draw(Graphics graphics){
                graphics.setColor(Const.LARGE_BUTTON_IN_COLOR);
                graphics.fillRoundRect(Const.LOBBY_BANNER_X, y, Const.LOBBY_BANNER_WIDTH, Const.LOBBY_BANNER_HEIGHT, Const.RADIUS, Const.RADIUS);
                graphics.setColor(Const.LARGE_BUTTON_BORDER_COLOR);
                graphics.drawRoundRect(Const.LOBBY_BANNER_X, y, Const.LOBBY_BANNER_WIDTH, Const.LOBBY_BANNER_HEIGHT, Const.RADIUS, Const.RADIUS);
                name.draw(graphics);
                playerCount.draw(graphics);
                joinButton.draw(graphics);
            }
            public ServerButton joinButton(){
                return this.joinButton;
            }
        }
        public class NameKeyListener implements KeyListener {
            public void keyPressed(KeyEvent event) {
                int key = event.getKeyCode();
                String newChar = Character.toString((char)key);
                if (nameField.getStayHeld() == true){
                    String name = (playerName.getText());
                    if (name.length() <= Const.PLAYER_NAME_MAX_LENGTH && key != 8 && key != 32){ //8 is backspace, 32 is space, no spaces in name to not mess up networking
                        playerName.setText(name + newChar);
                    }
                    else if (key == 8 && playerName.getText().length() > 0){
                        playerName.setText(name.substring(0, (name.length() - 1)));
                    }
                    //playerName.setText(playerName);
                }
                window.repaint();
            }
            public void keyReleased(KeyEvent event) { }
            public void keyTyped(KeyEvent event) { }
        }
    }

    public class AbilitySelectPanel extends ScreenPanel {
        private ServerButton continueButton;
        private ServerButton backButton;
        private Text titleText;
        private Text abilitiesText;
        private Text abilityBankText;
        private Text descriptionText; 
        //private TextButton creditsButton;

        public AbilitySelectPanel(Image backgroundSprite) {
            super(backgroundSprite);
            // Initialize the buttons.
            String abilitiesCommand = Const.SELECTED;
            Text continueText = new Text("Continue", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y);
            this.continueButton = new ServerButton(window, output, continueText, abilitiesCommand ,Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y, Const.RADIUS);
            
            Text backText = new Text("Go back", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y);
            String backCommand = Const.LEAVE;
            this.backButton = new ServerButton(window, output, backText, backCommand, Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y, Const.RADIUS);
            
            titleText = new Text("Ability Select", Const.TEXT_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.HALF_WIDTH, 120);
            
            // Add the listeners for the screens.
            this.addMouseListener(backButton.new BasicMouseListener());
            this.addMouseMotionListener(backButton.new InsideButtonMotionListener());
            this.addMouseListener(continueButton.new BasicMouseListener());
            this.addMouseMotionListener(continueButton.new InsideButtonMotionListener());
            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            this.backButton.draw(graphics);
            this.continueButton.draw(graphics);
            graphics.drawLine(200, 225, 200, 850);
            graphics.drawLine(0, 225, Const.WIDTH, 225);
            graphics.drawLine(0, 850, Const.WIDTH, 850);
            graphics.drawLine(850, 225, 850, 850);
            titleText.draw(graphics);
        }
        public final ComponentAdapter FOCUS_WHEN_SHOWN = new ComponentAdapter(){
            public void componentShown(ComponentEvent event){
                requestFocusInWindow();
            }
        };
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
            Text playButtonText = new Text("beee", buttonFont, fontColor, Const.HALF_WIDTH, playY);
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