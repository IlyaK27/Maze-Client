/**
 * Final Game ImageButton Class
 * @Author Ilya Kononov
 * @Date = 
 * This class is a button that has text written ontop of it 
 * This type of button is used to swtich screens without needing a server input
 */

 import java.awt.Graphics;
 import java.awt.Color;
 import javax.swing.JFrame;
 
 import java.awt.event.MouseEvent;
 import java.awt.event.MouseListener;
 import java.awt.event.MouseMotionListener;
 
 public class AbilityButton extends Button {
    private Image image;
    private int extraDimensions;
    private boolean enabled;
    private String name;
    private Image description;
    public AbilityButton(JFrame window, Image image, Color borderColor, String name, Image description, int centerX, int y, boolean enabled) {
        super(window, null, borderColor, null, centerX, y, image.getWidth(), image.getHeight(), 0);
        this.image = image;
        this.extraDimensions = 0;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
    }
     
    // Getters and Setters
    public boolean getEnabled(){
        return this.enabled;
    }
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
        if (!(enabled)){
            resetStayHeld();
        }
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public Image getDescription(){
        return this.description;
    }
    public void setDescription(Image description){
        this.description = description;
    }
    public Image getImage(){
        return this.image;
    }
    public void setImage(Image image){
        if(image.getWidth() == this.image.getWidth() && image.getHeight() == this.image.getHeight()){ // To avoid changing dimensions
            this.image = image;
        }
    }

    /**
     * This method draws the button onto a component/panel/window.
     * @param graphics The graphics of the component to be drawn onto.
     */
    public void draw(Graphics graphics) {
        // Draw the button body.
        if(mouseInside || stayHeld){
            this.extraDimensions = Const.ABILITY_BUTTON_BORDER;
        }else if (this.extraDimensions != 0){
            this.extraDimensions = 0;
        }
         
        // Draw the button border.
        graphics.setColor(this.borderColor);
        graphics.fillRoundRect(this.getXVal() - extraDimensions, this.getYVal() - extraDimensions, this.getWidthVal() + extraDimensions * 2, this.getHeightVal() + extraDimensions * 2, 
                                0, 0);
         
        // Draw the button text.
        this.image.draw(graphics, this.getXVal(), this.getYVal());
    }
     
    // Mouse clicking actions
    public class BasicMouseListener implements MouseListener{
        public void mouseClicked(MouseEvent event) {
            // If mouse clicks button switch to correct screen
            if(enabled){
                if (mouseInside) {
                    stayHeld = !stayHeld;
                }
            }
        }
        public void mousePressed(MouseEvent e) {  
        }
        public void mouseReleased(MouseEvent e) { 
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
    }

    // Interface is used to change colour of button and to find out weather mouse coordinates are inside the button
    public class InsideButtonMotionListener implements MouseMotionListener {
        public void mouseMoved(MouseEvent event) {
            boolean mouseRegisteredInside = (event.getX() >= getXVal()) && (event.getX() <= (getXVal() + getWidthVal())) && (event.getY() >= getYVal()) && (event.getY() <= (getYVal() + getHeightVal()));
            if (mouseRegisteredInside && !mouseInside && enabled) {
                // Redraw the button if mouse just moved onto button.
                mouseInside = true;
                window.repaint();
            } else if (!mouseRegisteredInside && mouseInside) {
                // Redraw the button if mouse just moved off button.
                mouseInside = false;
                window.repaint();
            }
        }
        public void mouseDragged(MouseEvent e) {}
    }
}