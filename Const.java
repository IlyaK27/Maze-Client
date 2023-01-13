import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;
import java.awt.FontFormatException;
import java.io.File;
import java.awt.Rectangle;

public class Const{
    // Screen width and height
    public static final int WIDTH = 1200;
    public static final int HALF_WIDTH = WIDTH/2;
    public static final int HEIGHT = 1000;
    public static final Color INFO_RECT_COLOR = new Color(53, 28, 117);
    public static final Rectangle INFO_RECT = new Rectangle(0, 0, 2000, 100);

    // Fonts
    //private static final String TARRGET_FONT_FILE = "extra_files/fonts/TarrgetHalfToneItalic-ozyV.ttf";
    private static final String RAUBFONT_FONT_FILE = "extra_files/fonts/RaubFont.ttf";

    // Button values 
    public static final int BUTTON_HORIZONTAL_SPACE = 10;
    public static final int BUTTON_VERTICAL_SPACE = 5;
    public static final int ABILITY_BUTTON_BORDER = 5;
    public static final int RADIUS = 40;
    public static final int LOBBY_INFO_X = 365;
    public static final int LOBBY_JOIN_X = 790;
    public static final int LOBBY_COUNT_Y_DIFFERENCE = 50; // How much lower the height of the player count is compared to the lobby name
    public static final int LOBBY_JOIN_Y_DIFFERENCE = 25; // How much lower the height of the player count is compared to the lobby name
    public static final int SPACE_BETWEEN_LOBBIES = 125;
    public static final int SELECTED_ABILITY_CENTER_X = 100;
    public static final int ABILITY_BANK_X = 263;
    public static final int ABILITY_BANK_Y = 312;
    public static final int ULTIMATE_BANK_Y = 653;
    public static final int ABILITY_X_DIFFERENCE = 105;
    public static final int ABILITY_Y_DIFFERENCE = 105;
    public static final int GO_BACK_X = 100;
    public static final int CONTINUE_X = 1100;
    public static final int GO_BACK_Y = 900;
    // Lobby Banner
    public static final int LOBBY_BANNER_WIDTH = 500;
    public static final int LOBBY_BANNER_HEIGHT = 100;
    public static final int LOBBY_BANNER_X = HALF_WIDTH - LOBBY_BANNER_WIDTH/2;
    public static final int LOBBY_BANNER_START_Y = 305;

    // Player Banner
    public static final int PLAYER_BANNER_WIDTH = 200;
    public static final int PLAYER_BANNER_START_X = 80 + PLAYER_BANNER_WIDTH / 2; // CenterX of the start playerBanner
    public static final int PLAYER_BANNER_Y = 250;
    public static final int PLAYER_BANNER_IMAGE_Y = 330;
    public static final int PLAYER_BANNER_ABILITY1_Y = 435;
    public static final int PLAYER_BANNER_ABILITY2_Y = 535;
    public static final int PLAYER_BANNER_ULTIMATE_Y = 635;
    public static final int READY_TEXT_Y = 740;
    public static final int PLAYER_BANNER_HEIGHT = 550;
    public static final int PLAYER_BANNER_X_DIFFERENCE = PLAYER_BANNER_WIDTH/2 + PLAYER_BANNER_START_X;

    // Ability Select
    public static final Rectangle SELECTED_ABILITES_BOX =  new Rectangle(0, 270, 200, 850);
    public static final Rectangle ABILITY_BANK_BOX =  new Rectangle(200, 270, 650, 360);
    public static final Rectangle ULTIMATE_BANK_BOX =  new Rectangle(200, 640, 650, 210);
    
    // Game screen
    public static final int ABILITY_1_X = Const.HALF_WIDTH - 140;
    public static final int ABILITIES_X_DIFFERENCE = 100;
    public static final int ABILITIES_Y = Const.HEIGHT - 140;
    public static final Rectangle PLAYER_INFO_RECT =  new Rectangle(Const.HALF_WIDTH - 150, ABILITIES_Y - 20, 300, Const.HEIGHT - ABILITIES_Y + 20);

    // Fonts
    public static final Font MENU_BUTTON_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 95);
    public static final Font TEXT_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 60);
    public static final Font SMALL_BUTTON_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 20);
    public static final Font LOBBY_BANNER_BUTTON_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 35);
    public static final Color LARGE_BUTTON_FONT_COLOR = new Color(153, 217, 234);

    public static final Color LARGE_BUTTON_IN_COLOR = new Color(122, 112, 143);
    public static final Color LARGE_BUTTON_BORDER_COLOR = new Color(92, 114, 163);
    public static final Color LARGE_BUTTON_HOVER_COLOR = new Color(113, 105, 150);
    public static final Color SMALL_BUTTON_IN_COLOR = new Color(153, 153, 153); // Grey
    public static final Color SMALL_BUTTON_HOVER_COLOR = new Color(102, 102, 102); // Dark Grey

    public static final int LOBBY_SIZE = 4;
    public static final int PLAYER_NAME_MAX_LENGTH = 8;
    public static final int MAX_ABILITES_PER_ROW = 6;

    // Commands (See shared doc for more info)
    public static final String PING = "PING"; // Making sure client is still connected
    // Client to Server commands
    public static final String LOBBIES_LIST = "LOBBIES";
    public static final String NEW_LOBBY = "NEW";
    public static final String NAME = "NAME"; 
    public static final String JOIN_LOBBY = "JOIN";

    // Client to Lobby commands
    public static final String SELECTED = "SELECTED"; // Player has selected abilities
    public static final String RESELECT = "RESELECT"; // Player has went back to choose different abilities
    public static final String MY_ABILITIES = "MY_ABILITIES"; // Players abilities after this command
    public static final String READY = "READY"; // Player has selected abilities and is ready to play
    public static final String UNREADY = "UNREADY"; // Player is not ready. Automatically happens when player goes to reselect abilities
    public static final String MOVE = "MOVE"; // Client gives lobby what direction they just went
    public static final String ATTACK = "ATTACK"; // Client used normal attack 
    public static final String ABILITY1 = "ABILITY1"; // Client used first ability  
    public static final String ABILITY2 = "ABILITY2"; // Client used second ability
    public static final String ULTIMATE = "ULTIMATE"; // Client used ultimate ability
    public static final String LEAVE = "LEAVE"; // Client left lobby

    // Server to Client commands
    public static final String CLEAR_LOBBIES = "CLEAR"; // Client clears its list of lobbies
    public static final String LOBBY = "LOBBY"; // Server sends name and current player count of specified lobby 
    public static final String LOBBY_SELECT = "SELECT"; // Tells the client to switch to lobby select screen
    public static final String JOINED = "JOINED"; // Server tells client they have joined the lobby they tried to join
    public static final String NO_LOBBY = "NO_LOBBY"; // Server tells the client it couldn't find a lobby with the name the client gave

    // Lobby to Client commands
    public static final String NEW_PLAYER = "NEWP"; // New player has joined lobby 
    public static final String GAME_START = "GAME_START"; // Tells players game has started and to switch to game screen
    public static final String ABILITIES = "ABILITIES"; // Player has selected abilities
    public static final String REMOVEP = "REMOVEP"; // Player has left lobby, remove them from the player list
    public static final String NEWE = "NEWE"; // New enemy has spawned 
    public static final String PLAYER = "PLAYER"; // Updates the correspondings players information for the client
    public static final String ENEMY = "ENEMY"; // Updates the correspondings enemies information for the client
    public static final String KILLEDE = "KILLEDE"; // Tells the client this enemy has died and to remove it from the enemy list 
    public static final String DIE = "DIE"; // Client has died
    public static final String WIN = "WIN"; // Player(s) have won this round send them to the mid round screen
    public static final String LOSE = "LOSE"; // Player(s) have lost the game send them to the game over screen
    public static final String ABILITY1_READY = "ABILITY1"; // Tells client their first ability is off cooldown
    public static final String ABILITY2_READY = "ABILITY2"; // Tells client their second ability is off cooldown
    public static final String ULTIMATE_READY = "ULTIMATE"; // Tells client their ultimate ability is off cooldown
    public static final String BOUNDS = "BOUNDS"; // Tells client the size of the play area
    public static final String START = "START"; // Tells client there is a start tile here
    public static final String END = "END"; // Tells client there is a end tile here
    public static final String WALl = "WALL"; //Tells client there is a wall tile here

    // Images
    public static final Image MENU_BACKGROUND = loadImage("extra_files/images/MenuBackground.png");
    public static final Image WALL_BACKGROUND = loadImage("extra_files/images/WallBackground.png");
    public static final Image ABILITY_SELECT_BACKGROUND = loadImage("extra_files/images/AbilitySelectBackground.png");
    public static final Image BLANK_BACKGROUND = loadImage("extra_files/images/BlankBackground.png");

    // Ability images
    public static final Image BLANK_ABILITY_IMAGE = loadImage("extra_files/images/abilities/ability_icons/BlankAbility.png");
    private static final Image TIME_STOP_IMAGE = loadImage("extra_files/images/abilities/ability_icons/TimeStop.png");
    //public static final Image TIME_STOP_IMAGE = loadImage("extra_files/images/abilities/ability_icons/SelfHeal.png");
    private static final Image SELF_HEAL_IMAGE = loadImage("extra_files/images/abilities/ability_icons/SelfHeal.png");
    private static final Image INVISIBILTY_IMAGE = loadImage("extra_files/images/abilities/ability_icons/Invisibility.png");

    // Ability descriptions
    private static final Image TIME_STOP_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/TimeStopDescription.png");
    //private static final Image SELF_HEAL_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/SelfHealDescription.png");
    //private static final Image INVISIBILTY_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/InvisbilityDescription.png");

    // Player images
    public static final HashMap<String, Image> PLAYER_ICONS = new HashMap<String, Image>(){ // Name, Image
        {
            put("BLUE", loadImage("extra_files/images/player_icons/BlueIcon.png"));
            put("GREEN", loadImage("extra_files/images/player_icons/GreenIcon.png"));
            put("YELLOW", loadImage("extra_files/images/player_icons/YellowIcon.png"));
            put("ORANGE", loadImage("extra_files/images/player_icons/OrangeIcon.png"));
        }
    };

    // Ability hashmaps
    public static final HashMap<String, Image> ABILITY_IMAGES = new HashMap<String, Image>(){ // Name, Image
        {
            put("SELF_HEAL", SELF_HEAL_IMAGE);
            put("INVISBILITY", INVISIBILTY_IMAGE);
        }
    };

    public static final HashMap<String, Image> ABILITY_DESCRIPTIONS = new HashMap<String, Image>(){ // Name, Description
        {
            put("SELF_HEAL", TIME_STOP_DESCRIPTION);
            put("INVISBILITY", TIME_STOP_DESCRIPTION);
        }
    };

    public static final HashMap<String, Image> ULTIMATE_IMAGES = new HashMap<String, Image>(){ // Name, Image
        {
            put("TIME_STOP", TIME_STOP_IMAGE);
        }
    };

    public static final HashMap<String, Image> ULTIMATE_DESCRIPTIONS = new HashMap<String, Image>(){ // Name, Description
        {
            put("TIME_STOP", TIME_STOP_DESCRIPTION);
        }
    };

    private static Image loadImage(String imageName){
        Image image = new Image(imageName);
        return image;
    }
    private static Font loadFont(String fontFileName, int fontType, int fontStyle, int fontSize){
        Font errorFont = new Font("Bree Serif", fontStyle, fontSize);
        Font font;
        try{
            Font actualFont = Font.createFont(fontType, new File(fontFileName));
            font = actualFont.deriveFont((float)fontSize);
        } catch (IOException ex) {
            System.out.println("Font Files could not be read");
            font = errorFont;
        } catch (FontFormatException ex){
            System.out.println("Font Files are invalid");  
            font = errorFont;
        }
        return font;
    }
    private Const() {}
}