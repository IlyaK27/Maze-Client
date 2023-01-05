/**
 * Final Game ServerButton Class
 * @Author Ilya Kononov
 * @Date = 
 * This class is a button that has text written ontop of it 
 * This type of button is sends a command to the server which the server will send something back to the client
 * After the server sent back the command after recieving this buttons command the client will switch screens
 */

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JFrame;
 
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintWriter;
 
public class ServerButton extends TextButton {
    private PrintWriter output;  
    private String command;

    public ServerButton(JFrame window, PrintWriter output, Text text, String command, Color inColor, Color outColor, Color hoverColor, int centerX, int y, int radius) {
        super(window, null, "", text, inColor, outColor, hoverColor, centerX, y, radius);
        this.output = output;
        this.command = command;
    }   
    // Mouse clicking actions
    public class BasicMouseListener implements MouseListener{
        public void mouseClicked(MouseEvent event) {
            // If mouse clicks button switch to correct screen
            if (mouseInside) {
                if (!(cards == null && panel == null)){
                    output.println(command);
                    System.out.println("heh");
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