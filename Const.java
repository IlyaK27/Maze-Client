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
    public static final int ADJUST_WIDTH = 16; // Without this variable the screens width happens to be WIDTH - 16 for some reason
    public static final int HEIGHT = 1000;
    public static final int GAME_TITLE_HEIGHT = 100;
    public static final int HALF_HEIGHT = (HEIGHT - GAME_TITLE_HEIGHT)/2 + GAME_TITLE_HEIGHT; // Minus 100 because we aren't including the Games name at the top of the screen
    public static final int ADJUST_HEIGHT = 39;  // Without this variable the screens height happens to be HEIGHT - 39 for some reason
    public static final Image GAME_TITLE = loadImage("extra_files/images/GameTitle.png");

    // Fonts
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
    public static final int PASSIVE_BANK_Y = 285;
    public static final int ABILITY_BANK_Y = 480;
    public static final int ULTIMATE_BANK_Y = 670;
    public static final int ABILITY_X_DIFFERENCE = 105;
    public static final int ABILITY_Y_DIFFERENCE = 105;
    public static final int GO_BACK_X = 100;
    public static final int CONTINUE_X = 1100;
    public static final int GO_BACK_Y = 925;
    // Lobby Banner
    public static final int LOBBY_BANNER_WIDTH = 500;
    public static final int LOBBY_BANNER_HEIGHT = 100;
    public static final int LOBBY_BANNER_X = HALF_WIDTH - LOBBY_BANNER_WIDTH/2;
    public static final int LOBBY_BANNER_START_Y = 305;

    // Player Banner
    public static final int PLAYER_BANNER_WIDTH = 200;
    public static final int PLAYER_BANNER_START_X = 80 + PLAYER_BANNER_WIDTH / 2; // CenterX of the start playerBanner
    public static final int PLAYER_BANNER_Y = 270;
    public static final int PLAYER_BANNER_IMAGE_Y = 350;
    public static final int PLAYER_BANNER_ABILITY1_Y = 455;
    public static final int PLAYER_BANNER_ABILITY2_Y = 555;
    public static final int PLAYER_BANNER_ULTIMATE_Y = 655;
    public static final int READY_TEXT_Y = 760;
    public static final int PLAYER_BANNER_HEIGHT = 550;
    public static final int PLAYER_BANNER_X_DIFFERENCE = PLAYER_BANNER_WIDTH/2 + PLAYER_BANNER_START_X;

    // Ability Select
    public static final Rectangle SELECTED_ABILITES_BOX =  new Rectangle(0, 270, 200, 850);
    public static final Rectangle PASSIVE_BANK_BOX =  new Rectangle(200, 270, 650, 193);
    public static final Rectangle ABILITY_BANK_BOX =  new Rectangle(200, (int)(PASSIVE_BANK_BOX.getY() + PASSIVE_BANK_BOX.getHeight()), 650, (int)PASSIVE_BANK_BOX.getHeight());
    public static final Rectangle ULTIMATE_BANK_BOX =  new Rectangle(200, (int)(ABILITY_BANK_BOX.getY() + ABILITY_BANK_BOX.getHeight()), 650, (int)PASSIVE_BANK_BOX.getHeight());
    
    // Game screen
    public static final int ABILITY_1_X = Const.HALF_WIDTH - 75;
    public static final int ABILITIES_X_DIFFERENCE = 100;
    public static final int ABILITIES_Y = Const.HEIGHT - 115;
    public static final Rectangle PLAYER_INFO_RECT =  new Rectangle(Const.HALF_WIDTH - 225, ABILITIES_Y - 19, 450, Const.HEIGHT - ABILITIES_Y);

    // Fonts
    public static final Font MENU_BUTTON_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 95);
    public static final Font TEXT_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 60);
    public static final Font SMALL_BUTTON_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 20);
    public static final Font PLAYER_NAME_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 15);
    public static final Font LOBBY_BANNER_BUTTON_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 35);
    public static final Color LARGE_BUTTON_FONT_COLOR = new Color(153, 217, 234);

    public static final Color LARGE_BUTTON_IN_COLOR = new Color(122, 112, 143);
    public static final Color LARGE_BUTTON_BORDER_COLOR = new Color(92, 114, 163);
    public static final Color LARGE_BUTTON_HOVER_COLOR = new Color(113, 105, 150);
    public static final Color SMALL_BUTTON_IN_COLOR = new Color(153, 153, 153); // Grey
    public static final Color SMALL_BUTTON_HOVER_COLOR = new Color(102, 102, 102); // Dark Grey
    public static final Color PLAYER_NAME_COLOR = new Color(0, 10, 21);

    public static final int LOBBY_SIZE = 4;
    public static final int PLAYER_NAME_MAX_LENGTH = 8;
    public static final int MAX_ABILITES_PER_ROW = 6;
    public static final int TILE_DIMENSIONS = 150;
    public static final int PLAYER_DIMENSIONS = 110;
    public static final int ENEMY_DIMENSIONS = 120;

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
    public static final String PASSIVE = "PASSIVE"; // Not actually a command but didn't make sense to put somewhere else
    public static final String ABILITY = "ABILITY"; // Client used ability  
    public static final String ULTIMATE = "ULTIMATE"; // Client used ultimate ability
    public static final String DRAWN = "DRAWN"; // Client has drawn their map so send new map update
    public static final String SPECTATE = "SPECTATE"; // Client tells server the want to spectate the provided player
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
    public static final String UPDATE_MAP = "UPDATE_MAP"; // Updates a certian part of the map for the client
    public static final String DRAW_MAP = "DRAW_MAP"; // Tells the client to draw the map and lets them know the map will be updated shortly
    public static final String ABILITIES = "ABILITIES"; // Player has selected abilities
    public static final String REMOVE_PLAYER = "REMOVEP"; // Player has left lobby, remove them from the player list
    public static final String NEWE = "NEWE"; // New enemy has spawned 
    public static final String PLAYER = "PLAYER"; // Updates the correspondings players information for the client
    public static final String DOWNED = "DOWNED"; // Tells the client the specified player has been put into the downed state
    public static final String REVIVED = "REVIVED"; // Tells the client the specified player has been revived
    public static final String DIED = "DIED"; // Tells the client the specified player has died
    public static final String ENEMY = "ENEMY"; // Updates the correspondings enemies information for the client
    public static final String KILLEDE = "KILLEDE"; // Tells the client this enemy has died and to remove it from the enemy list 
    public static final String DIE = "DIE"; // Client has died
    public static final String WIN = "WIN"; // Player(s) have won this round send them to the mid round screen
    public static final String LOSE = "LOSE"; // Player(s) have lost the game send them to the game over screen
    public static final String ABILITY_READY = "ABILITY_READY"; // Tells client their ability is off cooldown
    public static final String ULTIMATE_READY = "ULTIMATE_READY"; // Tells client their ultimate ability is off cooldown

    // Images
    public static final Image MENU_BACKGROUND = loadImage("extra_files/images/MenuBackground.png");
    public static final Image WALL_BACKGROUND = loadImage("extra_files/images/WallBackground.png");
    public static final Image ABILITY_SELECT_BACKGROUND = loadImage("extra_files/images/AbilitySelectBackground.png");
    public static final Image CONTROLS_BACKGROUND = loadImage("extra_files/images/ControlsBackground.png");
    public static final Image BLANK_BACKGROUND = loadImage("extra_files/images/BlankBackground.png");

    // Ability images
    public static final Image BLANK_ABILITY_IMAGE = loadImage("extra_files/images/abilities/ability_icons/BlankAbility.png");

    private static final Image SHARPENED_IMAGE = loadImage("extra_files/images/abilities/ability_icons/Sharpened.png");
    private static final Image LIFE_STEAL_IMAGE = loadImage("extra_files/images/abilities/ability_icons/LifeSteal.png");
    private static final Image MAX_HEALTH_IMAGE = loadImage("extra_files/images/abilities/ability_icons/MaxHealth.png");
    private static final Image HEAL_IMAGE = loadImage("extra_files/images/abilities/ability_icons/Heal.png");
    private static final Image HEALTH_REGEN_IMAGE = loadImage("extra_files/images/abilities/ability_icons/HealthRegen.png");
    private static final Image CLOAKED_IMAGE = loadImage("extra_files/images/abilities/ability_icons/Cloaked.png");
    private static final Image SWIFT_MOVES_IMAGE = loadImage("extra_files/images/abilities/ability_icons/SwiftMoves.png");
    private static final Image SAVAGE_BLOW_IMAGE = loadImage("extra_files/images/abilities/ability_icons/SavageBlow.png");
    private static final Image INVESTIGATE_IMAGE = loadImage("extra_files/images/abilities/ability_icons/Investigate.png");
    private static final Image TIME_STOP_IMAGE = loadImage("extra_files/images/abilities/ability_icons/TimeStop.png");
    private static final Image FLAMING_RAGE_IMAGE = loadImage("extra_files/images/abilities/ability_icons/FlamingRage.png");
    private static final Image FORTIFY_IMAGE = loadImage("extra_files/images/abilities/ability_icons/Fortify.png");

    // Ability descriptions
    private static final Image SHARPENED_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/SharpenedDescription.png");
    private static final Image LIFE_STEAL_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/LifeStealDescription.png");
    private static final Image MAX_HEALTH_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/MaxHealthDescription.png");
    private static final Image HEAL_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/HealDescription.png");
    private static final Image HEALTH_REGEN_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/HealthRegenDescription.png");
    private static final Image CLOAKED_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/CloakedDescription.png");
    private static final Image SWIFT_MOVES_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/SwiftMovesDescription.png");
    private static final Image SAVAGE_BLOW_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/SavageBlowDescription.png");
    private static final Image INVESTIGATE_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/InvestigateDescription.png");
    private static final Image TIME_STOP_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/TimeStopDescription.png");
    private static final Image FLAMING_RAGE_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/FlamingRageDescription.png");
    private static final Image FORTIFY_DESCRIPTION = loadImage("extra_files/images/abilities/ability_descriptions/FortifyDescription.png");

    public static final Image ENEMY_IMAGE = loadImage("extra_files/images/Enemy.png");
    // Player images, Icons are for lobby and images are for actual in game and have different directions, they are also a bit larger too.
    public static final HashMap<String, Image> PLAYER_ICONS = new HashMap<String, Image>(){ // Name, Image
        {
            put("BLUE", loadImage("extra_files/images/player/BlueIcon.png"));
            put("GREEN", loadImage("extra_files/images/player/GreenIcon.png"));
            put("YELLOW", loadImage("extra_files/images/player/YellowIcon.png"));
            put("ORANGE", loadImage("extra_files/images/player/OrangeIcon.png"));
        }
    };
     // 0 - up, 1 - left, 2 - down, 3 - right
    private static final Image[] BLUE_IMAGES = {loadImage("extra_files/images/player/BluePlayer0.png"), loadImage("extra_files/images/player/BluePlayer1.png"), loadImage("extra_files/images/player/BluePlayer2.png"), loadImage("extra_files/images/player/BluePlayer3.png")};
    private static final Image[] GREEN_IMAGES = {loadImage("extra_files/images/player/GreenPlayer0.png"), loadImage("extra_files/images/player/GreenPlayer1.png"), loadImage("extra_files/images/player/GreenPlayer2.png"), loadImage("extra_files/images/player/GreenPlayer3.png")};
    private static final Image[] YELLOW_IMAGES = {loadImage("extra_files/images/player/YellowPlayer0.png"), loadImage("extra_files/images/player/YellowPlayer1.png"), loadImage("extra_files/images/player/YellowPlayer2.png"), loadImage("extra_files/images/player/YellowPlayer3.png")};
    private static final Image[] ORANGE_IMAGES = {loadImage("extra_files/images/player/OrangePlayer0.png"), loadImage("extra_files/images/player/OrangePlayer1.png"), loadImage("extra_files/images/player/OrangePlayer2.png"), loadImage("extra_files/images/player/OrangePlayer3.png")};
    public static final HashMap<String, Image[]> PLAYER_IMAGES = new HashMap<String, Image[]>(){ // Name, Image
        {
            put("BLUE", BLUE_IMAGES);
            put("GREEN", GREEN_IMAGES);
            put("YELLOW", YELLOW_IMAGES);
            put("ORANGE", ORANGE_IMAGES);
        }
    };
    public static final HashMap<Integer, Image> DOWNED_PLAYER_IMAGES = new HashMap<Integer, Image>(){ // Name, Image
        {
            put(0, loadImage("extra_files/images/player/DownedPlayer0.png"));
            put(1, loadImage("extra_files/images/player/DownedPlayer1.png"));
            put(2, loadImage("extra_files/images/player/DownedPlayer2.png"));
            put(3, loadImage("extra_files/images/player/DownedPlayer3.png"));
        }
    };
    public static final HashMap<Integer, Image> DEAD_PLAYER_IMAGES = new HashMap<Integer, Image>(){ // Name, Image
        {
            put(0, loadImage("extra_files/images/player/DeadPlayer0.png"));
            put(1, loadImage("extra_files/images/player/DeadPlayer1.png"));
            put(2, loadImage("extra_files/images/player/DeadPlayer2.png"));
            put(3, loadImage("extra_files/images/player/DeadPlayer3.png"));
        }
    };
    private static final Integer[] HORIZONTAL_CORRECTION = {- 30, 0};
    private static final Integer[] NO_CORRECTION = {0, 0};
    private static final Integer[] VERTICAL_CORRECTION = {0, -30};
    public static final HashMap<Integer, Integer[]> PLAYER_IMAGE_CORRECTIONS = new HashMap<Integer, Integer[]>(){ // Direction, correction
        { // x - 0, y - 1
            put(0, VERTICAL_CORRECTION); 
            put(1, NO_CORRECTION);
            put(2, NO_CORRECTION);
            put(3, HORIZONTAL_CORRECTION);
        }
    };

    // Tile images 
    public static final HashMap<Character, Image> TILE_IMAGES = new HashMap<Character, Image>(){ // Name, Image
        {
            put('W', loadImage("extra_files/images/tile_images/WallImage.png"));
            put('P', loadImage("extra_files/images/tile_images/PathImage.png"));
            put('O', loadImage("extra_files/images/tile_images/OptimalImage.png"));
            put('S', loadImage("extra_files/images/tile_images/StartImage.png"));
            put('E', loadImage("extra_files/images/tile_images/EndImage.png"));
        }
    };

    // Ability hashmaps
    public static final HashMap<String, Image> PASSIVE_IMAGES = new HashMap<String, Image>(){ // Name, Image
        {
            put("SHARPENED", SHARPENED_IMAGE);
            put("MAX_HEALTH", MAX_HEALTH_IMAGE);
            put("HEALTH_REGEN", HEALTH_REGEN_IMAGE);
            put("CLOAKED", CLOAKED_IMAGE);
            put("LIFE_STEAL", LIFE_STEAL_IMAGE);
        }
    };

    public static final HashMap<String, Image> PASSIVE_DESCRIPTIONS = new HashMap<String, Image>(){ // Name, Description
        {
            put("SHARPENED", SHARPENED_DESCRIPTION);
            put("MAX_HEALTH", MAX_HEALTH_DESCRIPTION);
            put("HEALTH_REGEN", HEALTH_REGEN_DESCRIPTION);
            put("CLOAKED", CLOAKED_DESCRIPTION);
            put("LIFE_STEAL", LIFE_STEAL_DESCRIPTION);
        }
    };
    public static final HashMap<String, Image> ABILITY_IMAGES = new HashMap<String, Image>(){ // Name, Image
        {
            put("HEAL", HEAL_IMAGE);
            put("SAVAGE_BLOW", SAVAGE_BLOW_IMAGE);
            put("INVESTIGATE", INVESTIGATE_IMAGE);
            put("SWIFT_MOVES", SWIFT_MOVES_IMAGE);
        }
    };

    public static final HashMap<String, Image> ABILITY_DESCRIPTIONS = new HashMap<String, Image>(){ // Name, Description
        {
            put("HEAL", HEAL_DESCRIPTION);
            put("SAVAGE_BLOW", SAVAGE_BLOW_DESCRIPTION);
            put("INVESTIGATE", INVESTIGATE_DESCRIPTION);
            put("SWIFT_MOVES", SWIFT_MOVES_DESCRIPTION);
        }
    };

    public static final HashMap<String, Image> ULTIMATE_IMAGES = new HashMap<String, Image>(){ // Name, Image
        {
            put("TIME_STOP", TIME_STOP_IMAGE);
            put("FLAMING_RAGE", FLAMING_RAGE_IMAGE);
            put("FORTIFY", FORTIFY_IMAGE);
        }
    };

    public static final HashMap<String, Image> ULTIMATE_DESCRIPTIONS = new HashMap<String, Image>(){ // Name, Description
        {
            put("TIME_STOP", TIME_STOP_DESCRIPTION);
            put("FLAMING_RAGE", FLAMING_RAGE_DESCRIPTION);
            put("FORTIFY", FORTIFY_DESCRIPTION);
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