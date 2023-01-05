import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
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


    //public static final Color FONT_COLOR = Color.BLACK; 
    //public static final Font BALL_FONT = new Font("Arial", Font.PLAIN, 20);
    //public static final Font TITLE_FONT = new Font("Arial", Font.PLAIN, 40);

    // Fonts
    //private static final String TARRGET_FONT_FILE = "extra_files/fonts/TarrgetHalfToneItalic-ozyV.ttf";
    private static final String RAUBFONT_FONT_FILE = "extra_files/fonts/RaubFont.ttf";

    // Button values 
    public static final int BUTTON_HORIZONTAL_SPACE = 10;
    public static final int BUTTON_VERTICAL_SPACE = 5;
    public static final int RADIUS = 40;
    public static final int LOBBY_NAME_X = 400;
    public static final int LOBBY_COUNT_X = 600;
    public static final int LOBBY_BANNER_START_Y = 300;
    public static final int GO_BACK_X = 100;
    public static final int CONTINUE_X = 1100;
    public static final int GO_BACK_Y = 900;
    
    public static final Font MENU_BUTTON_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 95);
    public static final Font SMALL_BUTTON_FONT = loadFont(RAUBFONT_FONT_FILE, Font.TRUETYPE_FONT, Font.PLAIN, 20);

    public static final Color LARGE_BUTTON_IN_COLOR = new Color(71, 56, 117);
    public static final Color LARGE_BUTTON_BORDER_COLOR = new Color(142, 124, 195);
    public static final Color LARGE_BUTTON_HOVER_COLOR = new Color(32, 18, 77);
    public static final Color SMALL_BUTTON_IN_COLOR = new Color(153, 153, 153); // Grey
    public static final Color SMALL_BUTTON_HOVER_COLOR = new Color(102, 102, 102); // Dark Grey

    public static final int LOBBY_SIZE = 4;

    // Commands (See shared doc for more info)
    public static final String PING = "PING"; // Making sure client is still connected
    // Client to Server commands
    public static final String LOBBIES_LIST = "LOBBIES";
    public static final String NEW_LOBBY = "NEW";
    public static final String JOIN_LOBBY = "JOIN";

    // Client to Lobby commands
    public static final String SELECTED = "SELECTED"; // Player has selected abilities
    public static final String RESELECT = "RESELECT"; // Player has gone to reselect abilities
    public static final String READY = "READY"; // Player has selected abilities and is ready to play
    public static final String UNREADY = "UNREADY"; // Player is not ready. Automatically happens when player goes to reselect abilities
    public static final String MOVE = "MOVE"; // Client gives lobby what direction they just went
    public static final String ATTACK = "ATTACK"; // Client used normal attack 
    public static final String ABILITY1 = "ABILITY1"; // Client used first ability  
    public static final String ABILITY2 = "ABILITY2"; // Client used second ability
    public static final String ULTIMATE = "ULTIMATE"; // Client used ultimate ability
    public static final String LEAVE = "LEAVE"; // Client left lobby

    // Server to Client commands
    public static final String LOBBY = "LOBBY"; // Server sends name and current player count of specified lobby 
    public static final String LOBBY_SELECT = "SELECT"; // Tells the client to switch to lobby select screen
    public static final String JOINED = "JOINED"; // Server tells client they have joined the lobby they tried to join
    public static final String NO_LOBBY = "NO_LOBBY"; // Server tells the client it couldn't find a lobby with the name the client gave

    // Lobby to Client commands
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
