/**
 * Final Game TextButton Class
 * @Author Ilya Kononov
 * @Date = 
 * This class is a button that has text written ontop of it 
 * This type of button is used to swtich screens without needing a server input
 */

 import java.awt.Graphics;
 import java.awt.Color;
 import javax.swing.JPanel;
 import javax.swing.JFrame;
 
 import java.awt.event.MouseEvent;
 import java.awt.event.MouseListener;
 
 public class TextButton extends Button {
     protected JPanel cards;
     protected String panel;
 
     private Text text;
 
     public TextButton(JFrame window, JPanel cards, String panel, Text text, Color inColor, Color outColor, Color hoverColor, int centerX, int y, int radius) {
         super(window, inColor, outColor, hoverColor, centerX, y, text.getWidth() + 
         (2 * Const.BUTTON_HORIZONTAL_SPACE), text.getHeight() + (2 * Const.BUTTON_VERTICAL_SPACE), radius);
         this.cards = cards;
         this.panel = panel;
         this.text = text;
     }
     
     /**
      * This method draws the button onto a component/panel/window.
      * @param graphics The graphics of the component to be drawn onto.
      */
     public void draw(Graphics graphics) {
         // Draw the button body.
        if(mouseInside || stayHeld){
            graphics.setColor(this.hoverColor);
        }else{
            graphics.setColor(this.inColor);
        }
         graphics.fillRoundRect(this.getXVal(), this.getYVal(), this.getWidthVal(), this.getHeightVal(), 
                                this.getRadiusVal(), this.getRadiusVal());
         
         // Draw the button border.
         graphics.setColor(this.borderColor);
         graphics.drawRoundRect(this.getXVal(), this.getYVal(), this.getWidthVal(), this.getHeightVal(), 
                                this.getRadiusVal(), this.getRadiusVal());
         
         // Draw the button text.
         this.text.draw(graphics);
     }
     
     // Mouse clicking actions
     public class BasicMouseListener implements MouseListener{
         public void mouseClicked(MouseEvent event) {
             // If mouse clicks button switch to correct screen
             if (mouseInside) {
                 if (!(cards == null && panel == null)){
                     Client.ScreenSwapper swapper = new Client.ScreenSwapper(cards, panel);
                     swapper.swap();
                 }
                 else if (cards == null && panel == null){
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
 }