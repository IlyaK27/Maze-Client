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
    private AbilitySelectPanel abilitySelectScreen;
    private LobbyPanel lobbyScreen;
    private JPanel gameScreen;
    private JPanel howToPlayScreen;
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
         this.abilitySelectScreen = new AbilitySelectPanel(Const.ABILITY_SELECT_BACKGROUND);
         this.lobbyScreen = new LobbyPanel(Const.WALL_BACKGROUND);
         this.gameScreen = new GamePanel(Const.BLANK_BACKGROUND);
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
                if(update != "" && update != null){
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
                        String colorHexCode = updateInfo[2]; 
                        Color playerColor = Color.decode(colorHexCode);
                        lobbyScreen.newPlayerBanner(newPlayerName, playerColor);
                        if(newPlayerName.equals(lobbySelectScreen.name())){
                            Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, ABILITY_SELECT_PANEL);
                            swapper.swap();
                        }
                    }
                    else if(updateInfo[0].equals(Const.LEAVE)){ // This command is given after the player wants to leave a lobby
                        Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, MENU_PANEL); // After player leaves lobby they can swap to main menu right away since the lobbyName will always be correct
                        swapper.swap();
                        abilitySelectScreen.resetButtons();
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
                        if(updateInfo[5] != null && updateInfo[5].equals("ME")){ // This is done so that the player cant swap screens until they have chosen all of their abilities
                            lobbyScreen.updateLobbyTitle();
                            Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, LOBBY_PANEL);
                            swapper.swap();
                        }
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
        private ServerButton continueButton;
        private ServerButton backButton;
        private ArrayList<PlayerBanner> playerBanners;
        //private TextButton creditsButton;

        public LobbyPanel(Image backgroundSprite) {
            super(backgroundSprite);
            this.lobbyTitle = new Text("", Const.MENU_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.HALF_WIDTH, 125);
            // Initialize the buttons.
            Text continueText = new Text("Continue", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y);
            this.continueButton = new ServerButton(window, output, continueText, Const.SELECTED ,Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.CONTINUE_X, Const.GO_BACK_Y, Const.RADIUS);
            
            Text backText = new Text("Go back", Const.SMALL_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y);
            this.backButton = new ServerButton(window, output, backText, Const.LEAVE, Const.SMALL_BUTTON_IN_COLOR, Const.LARGE_BUTTON_BORDER_COLOR, 
                                         Const.SMALL_BUTTON_HOVER_COLOR, Const.GO_BACK_X, Const.GO_BACK_Y, Const.RADIUS);
            
            playerBanners = new ArrayList<PlayerBanner>();

            // Add the listeners for the screens.
            this.addMouseListener(continueButton.new BasicMouseListener());
            this.addMouseMotionListener(continueButton.new InsideButtonMotionListener());
            this.addMouseListener(backButton.new BasicMouseListener());
            this.addMouseMotionListener(backButton.new InsideButtonMotionListener());

            this.setFocusable(true);
            this.addComponentListener(this.FOCUS_WHEN_SHOWN);
        }
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            lobbyTitle.draw(graphics);
            this.continueButton.draw(graphics);
            this.backButton.draw(graphics);
            for(int i = 0; i < playerBanners.size(); i++){
                playerBanners.get(i).draw(graphics, Const.PLAYER_BANNER_START_X + (Const.PLAYER_BANNER_X_DIFFERENCE * i));
            }
        }
        public void newPlayerBanner(String playerName, Color color) {
            PlayerBanner playerBanner = new PlayerBanner(playerName, color);
            playerBanners.add(playerBanner);
            window.repaint();
        }
        public void updatePlayerBanner(String playerName, String ability1, String ability2, String ultimate) {
            for (PlayerBanner playerBanner: playerBanners){
                if(playerBanner.name().equals(playerName)){
                    playerBanner.updateAbilities(ability1, ability2, ultimate);
                    break;
                }
            }
            System.out.println("Abilites  = " + ability1 + " " + ability2 + " " + ultimate);
            window.repaint();
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
            PlayerBanner(String name, Color color){
                this.name = new Text(name, Const.LOBBY_BANNER_BUTTON_FONT, Const.LARGE_BUTTON_FONT_COLOR, 0, Const.PLAYER_BANNER_Y + 20);
                this.ability1 = Const.BLANK_ABILITY_IMAGE;  
                this.ability2 = Const.BLANK_ABILITY_IMAGE;  
                this.ultimate = Const.BLANK_ABILITY_IMAGE;  
            }
            public void draw(Graphics graphics, int centerX){
                graphics.setColor(Const.LARGE_BUTTON_IN_COLOR);
                graphics.fillRoundRect(centerX - Const.PLAYER_BANNER_WIDTH/2, Const.PLAYER_BANNER_Y, Const.PLAYER_BANNER_WIDTH, Const.PLAYER_BANNER_HEIGHT, Const.RADIUS, Const.RADIUS);
                graphics.setColor(Const.LARGE_BUTTON_BORDER_COLOR);
                graphics.fillRoundRect(centerX - Const.PLAYER_BANNER_WIDTH/2, Const.PLAYER_BANNER_Y, Const.PLAYER_BANNER_WIDTH, Const.PLAYER_BANNER_HEIGHT, Const.RADIUS, Const.RADIUS);
                name.setCenterX(centerX);
                name.draw(graphics);
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
        }
    }

    public class GamePanel extends ScreenPanel {
        private ServerButton playButton;
        private TextButton howToPlayButton;
        //private TextButton creditsButton;

        public GamePanel(Image backgroundSprite) {
            super(backgroundSprite);
            Font buttonFont = Const.MENU_BUTTON_FONT;
            Color fontColor = Const.LARGE_BUTTON_FONT_COLOR;
            // Initialize the buttons.
            int playY = 300;
            Text playButtonText = new Text("Playing", buttonFont, fontColor, Const.HALF_WIDTH, playY);
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
}