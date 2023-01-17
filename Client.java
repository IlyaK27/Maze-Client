import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
    private AbilitySelectPanel abilitySelectScreen;
    private LobbyPanel lobbyScreen;
    private GamePanel gameScreen;
    private JPanel pauseScreen;
    private JPanel howToPlayScreen;
    private JPanel gameOverScreen;

    public final static String MENU_PANEL = "main menu screen";
    public final static String LOBBY_SELECT_PANEL = "lobby select screen";
    public final static String ABILITY_SELECT_PANEL = "ability select screen";
    public final static String LOBBY_PANEL = "lobby screen";
    public final static String HOW_TO_PLAY_PANEL = "how to play screen";
    public final static String GAME_PANEL = "game screen";
    public final static String PAUSE_PANEL = "pause screen";
    public final static String GAME_OVER_PANEL = "game over screen";

    private PrintWriter output;    
    private BufferedReader input;
    private static ServerHandler server;

    private Socket clientSocket;
    private final String HOST = "localhost";
    private final int PORT = 5001;
    protected static boolean playing;
    private String lobbyName;

    public static void main(String[] args) throws IOException{
        Client client = new Client();
        client.setup();
        server.start();
        while(true){  
            try {
                Thread.sleep(10);
            } catch (Exception e) {}
            if (playing){
                //window.repaint();
                System.out.println("wee");
            }   
        }
    }
    //-------------------------------------------------
    private void setup() throws IOException{
        window = new JFrame("Dungeon Runner");
        window.setPreferredSize(new Dimension(Const.WIDTH + Const.ADJUST_WIDTH, Const.HEIGHT + Const.ADJUST_HEIGHT));// adding because of window problems   
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
         this.abilitySelectScreen = new AbilitySelectPanel(Const.ABILITY_SELECT_BACKGROUND);
         this.lobbyScreen = new LobbyPanel(Const.WALL_BACKGROUND);
         this.gameScreen = new GamePanel(Const.BLANK_BACKGROUND);
         this.pauseScreen = new PausePanel(Const.WALL_BACKGROUND);
         //this.howToPlayScreen = new HowToPlayScreenPanel(Const.MENU_BACKGROUND);
         //this.lobbyScreen = new LobbyScreenPanel(Const.MENU_BACKGROUND);
         //this.pauseScreen = new PauseScreenPanel(Const.MENU_BACKGROUND);
         //this.gameOverScreen = new GameOverScreenPanel(Const.MENU_BACKGROUND);
 
         // Add the screens to the window manager.
         cards.add(menuScreen, MENU_PANEL);
         cards.add(lobbySelectScreen, LOBBY_SELECT_PANEL);
         cards.add(abilitySelectScreen, ABILITY_SELECT_PANEL);
         cards.add(lobbyScreen, LOBBY_PANEL);
         cards.add(gameScreen, GAME_PANEL);
         cards.add(pauseScreen, PAUSE_PANEL);
         //cards.add(howToPlayScreen, HOW_TO_PLAY_PANEL);
         /*cards.add(createLobbyScreen, CREATE_LOBBY_PANEL);
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
                if(update != "" && update != null){
                    updateInfo = update.split(" ", 12);
                    if(!(updateInfo[0].equals(Const.UPDATE_MAP) || updateInfo[0].equals(Const.DRAW_MAP))){System.out.println(update);}
                    //System.out.println(update);
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
                        String secondaryCommand = updateInfo[1];
                        Client.ServerWriter writer = new Client.ServerWriter(output);
                        if(secondaryCommand.equals(Const.NEW_LOBBY)){
                            writer.print(Const.NAME + " " + this.client.lobbySelectScreen.name());
                        }else if(secondaryCommand.equals(Const.JOIN_LOBBY)){
                            String lobbyName = updateInfo[2];
                            writer.print(Const.NAME + " " + lobbyName + " " + this.client.lobbySelectScreen.name());
                        }
                    }
                    else if(updateInfo[0].equals(Const.JOINED)){
                        lobbyName = updateInfo[1];
                    }
                    // From lobby
                    else if(updateInfo[0].equals(Const.NEW_PLAYER)){ // This command is given when a new player joins the lobby
                        String newPlayerName = updateInfo[1]; 
                        String playerColor = updateInfo[2]; 
                        lobbyScreen.newPlayerBanner(newPlayerName, playerColor);
                        gameScreen.addPlayer(newPlayerName, playerColor, 0, 0);
                        if(newPlayerName.equals(lobbySelectScreen.name())){
                            Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, ABILITY_SELECT_PANEL);
                            swapper.swap();
                        }
                    }
                    else if(updateInfo[0].equals(Const.LEAVE)){ // This command is given after the player wants to leave a lobby
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, MENU_PANEL); // After player leaves lobby they can swap to main menu right away since the lobbyName will always be correct
                        swapper.swap();
                        abilitySelectScreen.resetButtons();
                        lobbyScreen.clearBanners();
                        gameScreen.clearGame();
                    }
                    else if(updateInfo[0].equals(Const.REMOVEP)){ // This command is given when a new player joins the lobby
                        String playerName = updateInfo[1]; 
                        lobbyScreen.removePlayerBanner(playerName);
                        gameScreen.removePlayer(playerName);
                    }
                    else if(updateInfo[0].equals(Const.RESELECT)){ // This command is given when a new player joins the lobby
                        String playerName = updateInfo[1]; 
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, ABILITY_SELECT_PANEL);
                        swapper.swap();
                        lobbyScreen.updatePlayerBanner(playerName, false);
                    }
                    else if(updateInfo[0].equals(Const.MY_ABILITIES)){ // This command is given after the player selected their abilities
                        Client.ServerWriter writer = new Client.ServerWriter(output);
                        writer.print(Const.MY_ABILITIES + " " + abilitySelectScreen.abilities());
                    }
                    else if(updateInfo[0].equals(Const.ABILITIES)){ // This command is given after the player wants to leave a lobby
                        String playerName = updateInfo[1];
                        String ability1 = updateInfo[2];
                        String ability2 = updateInfo[3];
                        String ultimate = updateInfo[4];
                        lobbyScreen.updatePlayerBanner(playerName, ability1, ability2, ultimate);
                        gameScreen.setAbilities(ability1, ability2, ultimate);
                        if(updateInfo.length > 5 && updateInfo[5].equals("ME")){ // This is done so that the player cant swap screens until they have chosen all of their abilities
                            lobbyScreen.updateLobbyTitle();
                            Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, LOBBY_PANEL);
                            swapper.swap();
                        }
                    }
                    else if(updateInfo[0].equals(Const.READY)){ // This command is given after a player is ready to play
                        String playerName = updateInfo[1];
                        lobbyScreen.updatePlayerBanner(playerName, true);
                    }
                    else if(updateInfo[0].equals(Const.UNREADY)){ // This command is given after a player is ready to play
                        String playerName = updateInfo[1];
                        lobbyScreen.updatePlayerBanner(playerName, false);
                    }
                    else if(updateInfo[0].equals(Const.GAME_START)){
                        String myName = updateInfo[1];
                        gameScreen.setCurrentPlayer(myName);
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, GAME_PANEL);
                        swapper.swap();
                        lobbyScreen.resetBanners();
                    }
                    else if(updateInfo[0].equals(Const.PLAYER)){
                        String playerName = updateInfo[1];
                        int playerX = Integer.parseInt(updateInfo[2]);
                        int playerY = Integer.parseInt(updateInfo[3]);
                        int direction = Integer.parseInt(updateInfo[4]);
                        int health = Integer.parseInt(updateInfo[5]);
                        gameScreen.updatePlayer(playerName, playerX, playerY, direction, health);
                    }
                    else if(updateInfo[0].equals(Const.NEWE)){
                        int enemyX = Integer.parseInt(updateInfo[1]);
                        int enemyY = Integer.parseInt(updateInfo[2]);
                        int health = Integer.parseInt(updateInfo[3]);
                        gameScreen.addEnemy(enemyX, enemyY, health);
                    }
                    else if(updateInfo[0].equals(Const.ENEMY)){
                        int enemyID = Integer.parseInt(updateInfo[1]);
                        int enemyX = Integer.parseInt(updateInfo[2]);
                        int enemyY = Integer.parseInt(updateInfo[3]);
                        int angle = Integer.parseInt(updateInfo[4]);
                        int health = Integer.parseInt(updateInfo[5]);
                        gameScreen.updateEnemy(enemyX, enemyY, enemyID, angle, health);
                    }
                    else if(updateInfo[0].equals(Const.KILLEDE)){
                        int enemyID = Integer.parseInt(updateInfo[1]);
                        gameScreen.removeEnemy(enemyID);
                    }
                    else if(updateInfo[0].equals(Const.UPDATE_MAP)){
                        int rowNum = Integer.parseInt(updateInfo[1]);
                        char[] row = new char[updateInfo.length - 2];
                        String msg = "";
                        for(int i = 1; i <= row.length && i < updateInfo.length - 1 && updateInfo[i + 1] != null; i++){ // the row length will always have a maximum of 7 with 1200x1000 screen dimensions (1200 / 150) + 1 =7
                            row[i-1] = updateInfo[i + 1].charAt(0);
                            msg = msg + " " + row[i-1];
                            //System.out.println("i=" + i + " msg-" + msg);
                        }
                        //System.out.println("Row - " + msg);
                        gameScreen.modifyFOV(rowNum, row);
                    }
                    else if(updateInfo[0].equals(Const.DRAW_MAP)){
                        int rowCount = Integer.parseInt(updateInfo[1]);
                        int colCount = Integer.parseInt(updateInfo[2]);
                        int mapX = Integer.parseInt(updateInfo[3]) * Const.TILE_DIMENSIONS;
                        int mapY = Integer.parseInt(updateInfo[4]) * Const.TILE_DIMENSIONS;
                        gameScreen.updateFOV(); 
                        gameScreen.newFOVDimensions(rowCount, colCount, mapX, mapY);
                    }
                    else if(updateInfo[0].equals(Const.WIN)){
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, LOBBY_PANEL);
                        swapper.swap();
                        gameScreen.resetGame();
                    }
                    else if(updateInfo[0].equals(Const.LOSE)){
                        //Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, LOBBY_PANEL);
                        //swapper.swap();
                        gameScreen.resetGame();
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
            // Initialize the buttons.
            Text playButtonText = new Text("Play", Const.MENU_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.HALF_WIDTH, 300);
            this.playButton = new ServerButton(window, output, playButtonText, Const.LOBBIES_LIST, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, 300, Const.RADIUS);
            
            Text howToPlayButtonText = new Text("How To Play", Const.MENU_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.HALF_WIDTH, 470);
            this.howToPlayButton = new TextButton(window, cards, HOW_TO_PLAY_PANEL, howToPlayButtonText, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, 470, Const.RADIUS);
            
            
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
            System.out.println(this.playerName.getText());
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
        private class LobbyBanner{
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
                String joinCommand = Const.JOIN_LOBBY + " " + name;
                this.joinButton = new ServerButton(window, output, joinText, joinCommand, Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
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
        // Button, Ability name
        private AbilityButton ability1Button;
        private AbilityButton ability2Button;
        private AbilityButton ultimateButton;
        private int currentAbility;
        private ArrayList<AbilityButton> abilityBank;
        private ArrayList<AbilityButton> ultimateBank;
        private AbilityButton[] selectedAbilities;

        public AbilitySelectPanel(Image backgroundSprite) {
            super(backgroundSprite);
            // Initialize the buttons.
            Text continueText = new Text("Continue", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y);
            this.continueButton = new ServerButton(window, output, continueText, Const.SELECTED ,Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y, Const.RADIUS);
            
            Text backText = new Text("Go back", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y);
            this.backButton = new ServerButton(window, output, backText, Const.LEAVE, Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y, Const.RADIUS);
            
            this.ability1Button = new AbilityButton(window, Const.BLANK_ABILITY_IMAGE, Const.LARGE_BUTTON_BORDER_COLOR, "", null, Const.SELECTED_ABILITY_CENTER_X, 320, true);
            this.ability2Button = new AbilityButton(window, Const.BLANK_ABILITY_IMAGE, Const.LARGE_BUTTON_BORDER_COLOR, "", null, Const.SELECTED_ABILITY_CENTER_X, 505, true);
            this.ultimateButton = new AbilityButton(window, Const.BLANK_ABILITY_IMAGE, Const.LARGE_BUTTON_BORDER_COLOR, "", null, Const.SELECTED_ABILITY_CENTER_X, 700, true);
            this.currentAbility = -1;
            this.abilityBank = new ArrayList<AbilityButton>();
            selectedAbilities = new AbilityButton[3];
            selectedAbilities[0] = ability1Button; selectedAbilities[1] = ability2Button; selectedAbilities[2] = ultimateButton;
            int row = 0;
            int column = 0;
            for (String abilityName : Const.ABILITY_IMAGES.keySet()) {
                if(column == Const.MAX_ABILITES_PER_ROW){
                    column = 0;
                    row++;
                }
                Image abilityImage = Const.ABILITY_IMAGES.get(abilityName);
                Image abilityDescription = Const.ABILITY_DESCRIPTIONS.get(abilityName);
                AbilityButton button = new AbilityButton(window, abilityImage, Const.LARGE_BUTTON_BORDER_COLOR, abilityName, abilityDescription, 
                    Const.ABILITY_BANK_X + (column * Const.ABILITY_X_DIFFERENCE), Const.ABILITY_BANK_Y + (row * Const.ABILITY_Y_DIFFERENCE), false);
                abilityBank.add(button);
                this.addMouseListener(button.new BasicMouseListener());
                this.addMouseMotionListener(button.new InsideButtonMotionListener());
                column++;
            }
            ultimateBank = new ArrayList<AbilityButton>();
            row = 0;
            column = 0;
            for (String ultimateName : Const.ULTIMATE_IMAGES.keySet()) {
                if(column == Const.MAX_ABILITES_PER_ROW){
                    column = 0;
                    row++;
                }
                Image ultimateImage = Const.ULTIMATE_IMAGES.get(ultimateName);
                Image ultimateDescription = Const.ULTIMATE_DESCRIPTIONS.get(ultimateName);
                AbilityButton button = new AbilityButton(window, ultimateImage, Const.LARGE_BUTTON_BORDER_COLOR, ultimateName, ultimateDescription, 
                    Const.ABILITY_BANK_X + (column * Const.ABILITY_X_DIFFERENCE), Const.ULTIMATE_BANK_Y + (row * Const.ABILITY_Y_DIFFERENCE), false);
                ultimateBank.add(button);
                this.addMouseListener(button.new BasicMouseListener());
                this.addMouseMotionListener(button.new InsideButtonMotionListener());
                column++;
            }
            // Add the listeners for the screens.
            this.addMouseListener(new SelectMouseListener());

            this.addMouseListener(backButton.new BasicMouseListener());
            this.addMouseMotionListener(backButton.new InsideButtonMotionListener());
            this.addMouseListener(continueButton.new BasicMouseListener());
            this.addMouseMotionListener(continueButton.new InsideButtonMotionListener());

            this.addMouseListener(ability1Button.new BasicMouseListener());
            this.addMouseMotionListener(ability1Button.new InsideButtonMotionListener());
            this.addMouseListener(ability2Button.new BasicMouseListener());
            this.addMouseMotionListener(ability2Button.new InsideButtonMotionListener());
            this.addMouseListener(ultimateButton.new BasicMouseListener());
            this.addMouseMotionListener(ultimateButton.new InsideButtonMotionListener());

            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            this.backButton.draw(graphics);
            this.continueButton.draw(graphics);
            this.ability1Button.draw(graphics);
            this.ability2Button.draw(graphics);
            this.ultimateButton.draw(graphics);
            for (AbilityButton button : abilityBank) {
                button.draw(graphics);
            }
            for (AbilityButton button : ultimateBank) {
                button.draw(graphics);
            }
            if(currentAbility != -1 && selectedAbilities[currentAbility].getDescription() != null){selectedAbilities[currentAbility].getDescription().draw(graphics, 851, 273);}
        }
        public final ComponentAdapter FOCUS_WHEN_SHOWN = new ComponentAdapter(){
            public void componentShown(ComponentEvent event){
                requestFocusInWindow();
            }
        };
        public String abilities(){
            System.out.println(this.ability1Button.getName() + " " + this.ability2Button.getName() + " " + this.ultimateButton.getName());
            return this.ability1Button.getName() + " " + this.ability2Button.getName() + " " + this.ultimateButton.getName(); 
        }
        private void switchSelectedAbility(int x, int y){
            boolean switched = false;
            for(int i = 0; i < selectedAbilities.length && !(switched); i++){
                if(selectedAbilities[i].contains(x,y) && currentAbility != i){
                    if(i == 2){ // Selecting nothing or ability to selecting ultimate
                        for (AbilityButton button : abilityBank) {
                            button.setEnabled(false);
                        }
                        for (AbilityButton button : ultimateBank) {
                            button.setEnabled(true);
                        }
                    } 
                    else if(i != 2 && currentAbility == 2){ // From selecting ultimate to selecting normal ability
                        for (AbilityButton button : abilityBank) {
                            button.setEnabled(true);
                        }
                        for (AbilityButton button : ultimateBank) {
                            button.setEnabled(false);
                        }
                    }
                    else{ // From selecting anything to selecting abiity
                        if(currentAbility == -1){ // From selecting nothing to ability
                            for (AbilityButton button : abilityBank) {
                                button.setEnabled(true);
                            }
                        }else{ // From selecting ability to selecting other abiity
                            for (AbilityButton button : abilityBank) {
                                button.resetStayHeld();
                            } 
                        }
                    }
                    if (currentAbility != - 1){selectedAbilities[currentAbility].resetStayHeld();}
                    currentAbility = i;
                    switched = true;
                }else if(selectedAbilities[i].contains(x,y) && currentAbility == i){
                    currentAbility = -1;
                    if (i == 2){
                        for (AbilityButton button : ultimateBank) {
                            button.setEnabled(false);
                        }
                    }
                    else{
                        for (AbilityButton button : abilityBank) {
                            button.setEnabled(false);
                        }
                    }
                }
            }
        }
        public void resetButtons(){ // To make sure if you hop inbetween lobbies mid ability selection old selections don't stay
            for (AbilityButton button : abilityBank) {
                button.setEnabled(false);
            }
            for (AbilityButton button : ultimateBank) {
                button.setEnabled(false);
            }
            for(AbilityButton button : selectedAbilities){
                button.setImage(Const.BLANK_ABILITY_IMAGE);
                button.setName("");
                button.setDescription(null);
            }
            if(currentAbility != -1){
                selectedAbilities[currentAbility].resetStayHeld();
                currentAbility = -1;
            }
        }
        public class SelectMouseListener implements MouseListener {
            public void mouseClicked(MouseEvent event) {
                int mouseX = event.getX();
                int mouseY = event.getY();
                // If mouse was pressed within selected abilities box
                if (Const.SELECTED_ABILITES_BOX.contains(mouseX,mouseY)) {
                    switchSelectedAbility(mouseX, mouseY);
                // If mouse was pressed within ability bank box
                } else if (Const.ABILITY_BANK_BOX.contains(mouseX,mouseY)) {
                    if(currentAbility == 0 || currentAbility == 1){
                        boolean swapped = false;
                        int otherAbility = 1 - currentAbility;
                        boolean abilitiesSwitched = false;
                        for (AbilityButton button : abilityBank) {
                            if(button.contains(mouseX, mouseY) && !(selectedAbilities[currentAbility].getName().equals(button.getName()))){ // Swapping selections
                                swapped = true;
                                if(selectedAbilities[otherAbility].getName().equals(button.getName())){ // To prevent 2 abilities being the same swap the places of the abilities
                                    selectedAbilities[otherAbility].setImage(selectedAbilities[currentAbility].getImage());
                                    selectedAbilities[otherAbility].setName(selectedAbilities[currentAbility].getName());
                                    selectedAbilities[otherAbility].setDescription(selectedAbilities[currentAbility].getDescription());
                                    abilitiesSwitched = true;
                                }
                                selectedAbilities[currentAbility].setImage(button.getImage());
                                selectedAbilities[currentAbility].setName(button.getName());
                                selectedAbilities[currentAbility].setDescription(button.getDescription());
                                break;
                            }
                        }
                        if (abilitiesSwitched){
                            for (AbilityButton button : abilityBank) {
                                if(selectedAbilities[otherAbility].getName().equals(button.getName())){ // Reseting all buttons except for last selected
                                    button.resetStayHeld();
                                    break;
                                }
                            }
                        }
                        else if (swapped){ // Unselecting the other ability
                            for (AbilityButton button : abilityBank) {
                                if(!(selectedAbilities[currentAbility].getName().equals(button.getName()))){ // Reseting all buttons except for last selected
                                    button.resetStayHeld();
                                    break;
                                }
                            }
                        }
                    }
                // If mouse was pressed within ultimate bank box
                } else if (Const.ULTIMATE_BANK_BOX.contains(mouseX,mouseY)) {
                    if(currentAbility == 2){
                        boolean swapped = false;
                        for (AbilityButton button : ultimateBank) {
                            if(button.contains(mouseX, mouseY) && !(ultimateButton.getName().equals(button.getName()))){ // Swapping selections
                                swapped = true;
                                ultimateButton.setImage(button.getImage());
                                ultimateButton.setName(button.getName());
                                ultimateButton.setDescription(button.getDescription());
                                break;
                            }
                        }
                        if (swapped){ // Unselecting the other ability
                            for (AbilityButton button : abilityBank) {
                                if(!(ultimateButton.getName().equals(button.getName()))){ // Swapping selections
                                    button.resetStayHeld();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            public void mousePressed(MouseEvent event) { }
            public void mouseReleased(MouseEvent event) { }
            public void mouseEntered(MouseEvent event) { }
            public void mouseExited(MouseEvent event) { }
        }
    }

    public class LobbyPanel extends ScreenPanel {
        private Text lobbyTitle;
        private ServerButton readyButton;
        private ServerButton backButton;
        private ArrayList<PlayerBanner> playerBanners;
        //private TextButton creditsButton;

        public LobbyPanel(Image backgroundSprite) {
            super(backgroundSprite);
            this.lobbyTitle = new Text("", Const.MENU_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.HALF_WIDTH, 125);
            // Initialize the buttons.
            Text readyText = new Text("Ready", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y);
            this.readyButton = new ServerButton(window, output, readyText, Const.READY ,Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y, Const.RADIUS);
            
            Text backText = new Text("Go back", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y);
            this.backButton = new ServerButton(window, output, backText, Const.RESELECT, Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y, Const.RADIUS);

            playerBanners = new ArrayList<PlayerBanner>();

            // Add the listeners for the screens.
            this.addMouseListener(readyButton.new BasicMouseListener());
            this.addMouseMotionListener(readyButton.new InsideButtonMotionListener());
            this.addMouseListener(backButton.new BasicMouseListener());
            this.addMouseMotionListener(backButton.new InsideButtonMotionListener());

            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            lobbyTitle.draw(graphics);
            this.readyButton.draw(graphics);
            this.backButton.draw(graphics);
            for(int i = 0; i < playerBanners.size(); i++){
                playerBanners.get(i).draw(graphics, Const.PLAYER_BANNER_START_X + (Const.PLAYER_BANNER_X_DIFFERENCE * i));
            }
        }
        public void newPlayerBanner(String playerName, String color) {
            PlayerBanner playerBanner = new PlayerBanner(playerName, color);
            playerBanners.add(playerBanner);
            window.repaint();
        }
        public void updatePlayerBanner(String playerName, String ability1, String ability2, String ultimate) {
            for (PlayerBanner playerBanner: playerBanners){
                if(playerBanner.name().equals(playerName)){
                    System.out.println("updateplayerBanner" + playerBanner.name() + " " + (playerName));
                    playerBanner.updateAbilities(ability1, ability2, ultimate);
                    break;
                }
            }
            System.out.println("Abilites  = " + ability1 + " " + ability2 + " " + ultimate);
            window.repaint();
        }
        public void updatePlayerBanner(String playerName, boolean ready) {
            for (PlayerBanner playerBanner: playerBanners){
                if(playerBanner.name().equals(playerName)){
                    playerBanner.updateReady(ready);
                    break;
                }
            }
            System.out.println("Ready");
            window.repaint();
        }
        public void removePlayerBanner(String playerName) {
            for (PlayerBanner player: playerBanners){
                if(player.name().equals(playerName)){
                    playerBanners.remove(player);
                    break;
                }
            }
            window.repaint();
        }
        public void resetBanners() {
            for (PlayerBanner player: playerBanners){
                player.updateReady(false);
            }
        }
        public void clearBanners() {
            playerBanners.clear();
        }
        public void updateLobbyTitle() {
            this.lobbyTitle.setText(lobbyName + " lobby");
        }
        public final ComponentAdapter FOCUS_WHEN_SHOWN = new ComponentAdapter(){
            public void componentShown(ComponentEvent event){
                requestFocusInWindow();
            }
        };
        private class PlayerBanner{
            private Text name;
            private Image playerImage; 
            private Image ability1;
            private Image ability2;
            private Image ultimate;
            private boolean ready;
            PlayerBanner(String name, String color){
                this.name = new Text(name, Const.LOBBY_BANNER_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, 0, Const.PLAYER_BANNER_Y + 20);
                this.playerImage = Const.PLAYER_ICONS.get(color);
                this.ability1 = Const.BLANK_ABILITY_IMAGE;  
                this.ability2 = Const.BLANK_ABILITY_IMAGE;  
                this.ultimate = Const.BLANK_ABILITY_IMAGE;  
            }
            public void draw(Graphics graphics, int centerX){
                graphics.setColor(Const.LARGE_BUTTON_BORDER_COLOR);
                graphics.fillRoundRect(centerX - Const.PLAYER_BANNER_WIDTH/2, Const.PLAYER_BANNER_Y, Const.PLAYER_BANNER_WIDTH, Const.PLAYER_BANNER_HEIGHT, Const.RADIUS, Const.RADIUS);
                name.setCenterX(centerX);
                name.draw(graphics);
                playerImage.draw(graphics, centerX - playerImage.getWidth()/2, Const.PLAYER_BANNER_IMAGE_Y);
                ability1.draw(graphics, centerX - ability1.getWidth()/2, Const.PLAYER_BANNER_ABILITY1_Y);
                ability2.draw(graphics, centerX - ability2.getWidth()/2, Const.PLAYER_BANNER_ABILITY2_Y);
                ultimate.draw(graphics, centerX - ultimate.getWidth()/2, Const.PLAYER_BANNER_ULTIMATE_Y);
                Text readyText;
                if(ready){
                    readyText = new Text("Ready", Const.SMALL_BUTTON_FONT, Color.GREEN, centerX, Const.READY_TEXT_Y);
                    readyText.draw(graphics);
                }
                else if (!(ready)){
                    readyText = new Text("Not Ready", Const.SMALL_BUTTON_FONT, Color.RED, centerX, Const.READY_TEXT_Y);
                    readyText.draw(graphics);
                }
            }
            public String name(){
                return this.name.getText();
            }
            public void updateAbilities(String ability1, String ability2, String ultimate){
                this.ability1 = Const.ABILITY_IMAGES.get(ability1);
                this.ability2 = Const.ABILITY_IMAGES.get(ability2);
                this.ultimate = Const.ULTIMATE_IMAGES.get(ultimate);
            }
            public void updateReady(boolean ready){
                this.ready = ready;
            }
        }
    }

    public class GamePanel extends ScreenPanel {
        private String ability1;
        private String ability2;
        private String ultimate;
        private String[] abilities = new String[3];
        private HashMap<String, Image> abilityImages;
        private HashMap<String, Boolean> abilitiesReady;
        private ArrayList<Player> players;
        private ArrayList<Enemy> enemies;
        private char[][] currentFov;
        private char[][] newFov;
        private Player currentPlayer; // Player that is currently being spectated
        private int mapX; // X coordinate of top left tile of map that is being drawn on screen
        private int mapY; // Y coordinate of top left tile of map that is being drawn on screen
        private boolean alive;
        public GamePanel(Image backgroundSprite) {
            super(backgroundSprite);
            ability1 = Const.ABILITY1_READY; ability2 = Const.ABILITY2_READY; ultimate = Const.ULTIMATE_READY;
            abilities[0] = ability1; abilities[1] = ability2; abilities[2] = ultimate;
            abilityImages = new HashMap<String, Image>();
            players = new ArrayList<Player>();
            enemies = new ArrayList<Enemy>();
            abilityImages.put(ability1, Const.BLANK_ABILITY_IMAGE); abilityImages.put(ability2, Const.BLANK_ABILITY_IMAGE); abilityImages.put(ultimate, Const.BLANK_ABILITY_IMAGE);
            abilitiesReady = new HashMap<String, Boolean>();
            abilitiesReady.put(ability1, true); abilitiesReady.put(ability2, true); abilitiesReady.put(ultimate, true);
            alive = true;
            // Add the listeners for the screens.

            this.addKeyListener(new GameKeyListener());

            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            // Drawing player info bar on bottom middle of screen
            drawMap(graphics);
            drawPlayers(graphics);
            drawEnemies(graphics);
            if(alive){
                graphics.setColor(Const.LARGE_BUTTON_BORDER_COLOR);
                graphics.fillRoundRect((int)Const.PLAYER_INFO_RECT.getX(), (int)Const.PLAYER_INFO_RECT.getY(), (int)Const.PLAYER_INFO_RECT.getWidth(), (int)Const.PLAYER_INFO_RECT.getHeight(), Const.RADIUS, Const.RADIUS);
                int counter = 0;
                Const.PLAYER_ICONS.get(currentPlayer.color).draw(graphics, Const.HALF_WIDTH - 210, Const.ABILITIES_Y);
                for(String ability: abilities){
                    if(abilitiesReady.get(ability)){
                        abilityImages.get(ability).draw(graphics, Const.ABILITY_1_X + (counter * Const.ABILITIES_X_DIFFERENCE), Const.ABILITIES_Y);
                    }else{
                        Const.BLANK_ABILITY_IMAGE.draw(graphics, Const.ABILITY_1_X + (counter * Const.ABILITIES_X_DIFFERENCE), Const.ABILITIES_Y);
                    }
                    counter++;
                }
            }
            Const.GAME_TITLE.draw(graphics, 0, 0);
        }
        public void setCurrentPlayer(String playerName){
            for(Player player: players){
                if(playerName.equals(player.name())){
                    currentPlayer = player;
                    break;
                }
            }
        }
        public void setAbilities(String ability1Name, String ability2Name, String ultimateName){
            abilityImages.replace(ability1, Const.ABILITY_IMAGES.get(ability1Name));
            abilityImages.replace(ability2, Const.ABILITY_IMAGES.get(ability2Name));
            abilityImages.replace(ultimate, Const.ULTIMATE_IMAGES.get(ultimateName));
        }
        private void drawMap(Graphics graphics){
            if(currentFov != null){
                for(int y = 0; y < currentFov.length; y++){
                    for(int x = 0; x < currentFov[y].length; x++){
                        Image tileImage = Const.TILE_IMAGES.get(currentFov[y][x]);
                        tileImage.draw(graphics, Const.HALF_WIDTH + mapX + (x * Const.TILE_DIMENSIONS) - currentPlayer.getX() - Const.PLAYER_DIMENSIONS/2, 
                            Const.HALF_HEIGHT + mapY + (y * Const.TILE_DIMENSIONS) - currentPlayer.getY() - Const.PLAYER_DIMENSIONS/2);
                    }
                }
            }
            Client.ServerWriter writer = new Client.ServerWriter(output);
            writer.print(Const.DRAWN);
        }
        private void drawPlayers(Graphics graphics){
            for(Player player: players){
                player.draw(graphics, currentPlayer.getX(), currentPlayer.getY());
            }
        }
        private void drawEnemies(Graphics graphics){
            for(Enemy enemy: enemies){
                enemy.draw(graphics, currentPlayer.getX(), currentPlayer.getY());
            }
        }
        public void newFOVDimensions(int rows, int cols, int mapX, int mapY){
            this.newFov = new char[rows][cols];
            this.mapX = mapX;
            this.mapY = mapY;
        }
        public void modifyFOV(int rowNum, char[] tileChars){
            this.newFov[rowNum] = tileChars;

        }
        public void updateFOV(){
            this.currentFov = this.newFov;
            window.repaint();
        }
        public void addEnemy(int x, int y, int health){
            Enemy enemy = new Enemy(x, y, health);
            enemies.add(enemy);
        }
        public void addPlayer(String playerName, String color, int x, int y){
            Player player = new Player(playerName, color, x, y);
            players.add(player);
        }
        public void updatePlayer(String playerName, int x, int y, int direction, int health){
            for(Player player: players){
                if(playerName.equals(player.name())){
                    player.setCoords(x,y);
                    player.setDirection(direction);
                    player.setHealth(health);
                    break;
                }
            }
        }
        public void updateEnemy(int x, int y, int enemyID, int angle, int health){
            Enemy enemy = enemies.get(enemyID);
            enemy.setCoords(x,y);
            enemy.setAngle(angle);
            enemy.setHealth(health);
        }
        public void revivePlayer(String playerName){

        }
        public void downPlayer(String playerName){

        }
        public void killPlayer(){

        }
        public void removePlayer(String playerName){
            for(Player player: players){
                if(playerName.equals(player.name())){
                    players.remove(player);
                    break;
                }
            }
        }
        public void removeEnemy(int enemyID){
            enemies.remove(enemies.get(enemyID));
        }
        public void resetGame(){
            for(Player player: players){
                player.setDirection(0);
                player.setHealth(100);
                player.downed = false;
                player.alive = true;
            }
            this.enemies.clear();
        }
        public void clearGame(){
            this.players.clear();
            this.enemies.clear();
        }
        public final ComponentAdapter FOCUS_WHEN_SHOWN = new ComponentAdapter(){
            public void componentShown(ComponentEvent event){
                requestFocusInWindow();
            }
        };
        private class Player{
            private Text name;
            private int direction; // 0 - up, 1 - left, 2 - down, 3 - right
            private String color;
            private Image[] playerImages;
            private int x;
            private int y;
            private int health;
            private boolean downed;
            private boolean alive;
            Player(String name, String color, int x, int y){
                this.name = new Text(name, Const.PLAYER_NAME_FONT, Const.PLAYER_NAME_COLOR, 0, 0);
                this.direction = 0;
                this.color = color;
                this.playerImages = Const.PLAYER_IMAGES.get(color);
                this.x = x;
                this.y = y;
                this.downed = false;
                this.alive = true;
                this.health = 100;
            }
            public String name(){
                return this.name.getText();
            }
            public String color(){
                return this.color();
            }
            public int getX(){
                return this.x;
            }
            public int getY(){
                return this.y;
            }
            public void setDirection(int direction){
                if(direction >= 0 && direction <= 3){this.direction = direction;} // Using if statement just in case
            }
            public void setHealth(int health){
                this.health = health; 
            }
            public void setCoords(int centerX, int centerY){
                this.x = centerX;
                this.y = centerY;
            }
            public void draw(Graphics graphics, int mainPlayerX, int mainPlayerY){
                playerImages[this.direction].draw(graphics, 
                    Const.HALF_WIDTH + (this.x - mainPlayerX) + Const.PLAYER_IMAGE_CORRECTIONS.get(direction)[0] - Const.PLAYER_DIMENSIONS/2, 
                    Const.HALF_HEIGHT + (this.y - mainPlayerY) + Const.PLAYER_IMAGE_CORRECTIONS.get(direction)[1] - Const.PLAYER_DIMENSIONS/2);
                this.name.draw(graphics, Const.HALF_WIDTH + (this.x - mainPlayerX), Const.HALF_HEIGHT + (this.y - mainPlayerY) - 20);
                graphics.setColor(Color.RED);
                graphics.fillRect(Const.HALF_WIDTH + (this.x - mainPlayerX) - 25, Const.HALF_HEIGHT + 5 + (this.y - mainPlayerY), 50, 10);
                graphics.setColor(Color.GREEN);
                graphics.fillRect(Const.HALF_WIDTH + (this.x - mainPlayerX) - 25, Const.HALF_HEIGHT + 5 + (this.y - mainPlayerY), health / 2, 10);
            }
        }
        private class Enemy{
            private int angle; 
            private Image enemyImage;
            private int x;
            private int y;
            private int health;
            private int maxHealth;
            Enemy(int x, int y, int health){
                this.angle = 0;
                this.enemyImage = Const.ENEMY_IMAGE;
                this.x = x;
                this.y = y;
                this.health = health;
                this.maxHealth = health;
            }   
            public void setCoords(int x, int y){
                this.x = x;
                this.y = y;
            }
            public void setAngle(int angle){
                this.angle = angle; // Using if statement just in case
                rotateImage();
            }
            public void setHealth(int health){
                this.health = health; 
            }
            public void draw(Graphics graphics, int mainPlayerX, int mainPlayerY){
                enemyImage.draw(graphics, Const.HALF_WIDTH + (this.x - mainPlayerX), Const.HALF_HEIGHT + (this.y - mainPlayerY));
                graphics.setColor(Color.RED);
                graphics.fillRect(Const.HALF_WIDTH + (this.x - mainPlayerX) - 25, Const.HALF_HEIGHT + 5 + (this.y - mainPlayerY), 50, 10);
                graphics.setColor(Color.GREEN);
                graphics.fillRect(Const.HALF_WIDTH + (this.x - mainPlayerX) - 25, Const.HALF_HEIGHT + 5 + (this.y - mainPlayerY), (health / maxHealth) * 50, 10);
            }
            private void rotateImage(){
                BufferedImage rotatedImage = new BufferedImage(enemyImage.getWidth(), enemyImage.getHeight(), enemyImage.getImage().getType());
                Graphics2D g2d = rotatedImage.createGraphics();
                g2d.rotate(this.angle, enemyImage.getWidth()/2, enemyImage.getHeight()/2);
                g2d.dispose();
                this.enemyImage.setImage(rotatedImage);
            }
        }
        //act upon key events
        public class GameKeyListener implements KeyListener{   
            public void keyPressed(KeyEvent e){
                int key = e.getKeyCode();
                if (alive){
                    if (key == KeyEvent.VK_W){
                        Client.ServerWriter writer = new Client.ServerWriter(output);
                        writer.print(Const.MOVE + " 0");
                    }else if(key == KeyEvent.VK_D){
                        Client.ServerWriter writer = new Client.ServerWriter(output);
                        writer.print(Const.MOVE + " 1");
                    }else if(key == KeyEvent.VK_S){
                        Client.ServerWriter writer = new Client.ServerWriter(output);
                        writer.print(Const.MOVE + " 2");
                    }
                    else if(key == KeyEvent.VK_A){
                        Client.ServerWriter writer = new Client.ServerWriter(output);
                        writer.print(Const.MOVE + " 3");
                    }else if(key == KeyEvent.VK_ESCAPE){
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, PAUSE_PANEL);
                        swapper.swap();
                    }
                }
            }
            public void keyReleased(KeyEvent e){ 
                int key = e.getKeyCode();
            }   
            public void keyTyped(KeyEvent e){
            }           
        }
    }
    public class PausePanel extends ScreenPanel {
        private Text pausedText;
        private TextButton resumeButton;
        private ServerButton leaveButton;

        public PausePanel(Image backgroundSprite) {
            super(backgroundSprite);
            this.pausedText = new Text("Paused", Const.MENU_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.HALF_WIDTH, 125);
            // Initialize the buttons.
            Text resumeText = new Text("Resume", Const.MENU_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.HALF_WIDTH, 400);
            this.resumeButton = new TextButton(window, cards, GAME_PANEL, resumeText, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, 400, Const.RADIUS);
            Text leaveText = new Text("Leave", Const.MENU_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.HALF_WIDTH, 600);
            this.leaveButton = new ServerButton(window, output, leaveText, Const.LEAVE, Const.LARGE_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.LARGE_BUTTON_HOVER_COLOR, Const.HALF_WIDTH, 600, Const.RADIUS);
            
            // Add the listeners for the screens.
            this.addMouseListener(resumeButton.new BasicMouseListener());
            this.addMouseMotionListener(resumeButton.new InsideButtonMotionListener());
            this.addMouseListener(leaveButton.new BasicMouseListener());
            this.addMouseMotionListener(leaveButton.new InsideButtonMotionListener());
            this.addKeyListener(new PauseKeyListener());

            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            this.pausedText.draw(graphics);
            this.resumeButton.draw(graphics);
            this.leaveButton.draw(graphics);
        }
        public final ComponentAdapter FOCUS_WHEN_SHOWN = new ComponentAdapter(){
            public void componentShown(ComponentEvent event){
                requestFocusInWindow();
            }
        };
        public class PauseKeyListener implements KeyListener{   
            public void keyPressed(KeyEvent e){
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ESCAPE){ // Player can press either the button or escape to switch back to the game
                    Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, GAME_PANEL);
                    swapper.swap();
                }
            }
            public void keyReleased(KeyEvent e){ 
            }   
            public void keyTyped(KeyEvent e){
            }           
        }
    }
}