import java.awt.Color;
import java.awt.Font;

public class Const{
    // Screen width and height
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 1000;


    //public static final Color FONT_COLOR = Color.BLACK; 
    //public static final Font BALL_FONT = new Font("Arial", Font.PLAIN, 20);
    //public static final Font TITLE_FONT = new Font("Arial", Font.PLAIN, 40);

    // Commands
    public static final String MOVE = "MOVE";
    public static final String NEW = "NEW";
    public static final String BALL = "BALL";
    public static final String PELLET = "PELLET";
    public static final String TURN = "TURN";
    public static final String DIE = "DIE";
    public static final String JOIN = "JOIN";
    public static final String PING = "PING";
    public static final String REMOVE = "REMOVE";

    // Images
    public static final Image MENU_BACKGROUND = loadImage("extra_files/images/MenuBackground.png");

    private static Image loadImage(String imageName){
        Image image = new Image(imageName);
        return image;
    }

    private Const() {}
}
