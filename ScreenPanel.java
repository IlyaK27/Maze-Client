/**
 * Final Game Screenpanel Class
 * @Author Ilya Kononov
 * @Date = January 22 2023
 * This class represents the framework that the screen panels within RunNGun.java will follow
 * Additonally all screen panels are located within the RunNGun Class because they can't a shouldn't exist without RunNGun existing.
 */

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
 
abstract public class ScreenPanel extends JPanel {
    private Image background;
 
    public ScreenPanel(){
        setFocusable(true);
        this.background = null;
        this.addComponentListener(this.FOCUS_WHEN_SHOWN);
    }
    public ScreenPanel(Image backgroundImage){
        setFocusable(true);
        this.background = backgroundImage;
        this.addComponentListener(this.FOCUS_WHEN_SHOWN);
    }
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);  
        if (this.background != null) {
            this.background.draw(graphics, 0, 0);
        }
    }
    public final ComponentAdapter FOCUS_WHEN_SHOWN = new ComponentAdapter(){
        public void componentShown(ComponentEvent event){
            requestFocusInWindow();
        }
    };
}