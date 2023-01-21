/**
 * Final Game Image Class
 * @Author Ilya Kononov
 * @Date = January 22 2023
 * This class loads image files and stores them inside
 */

import java.awt.Graphics;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
 
public class Image {
    private BufferedImage picture;
    private int width;
    private int height;
    private String picName;
    public Image(String picName) {
        // Load the image from file.
        this.tryLoadImage(picName);
        this.width = this.picture.getWidth();
        this.height = this.picture.getHeight();
    }
    public void tryLoadImage(String picName) {
        try {
            this.picture = ImageIO.read(new File(picName));
            this.picName = picName;
            this.width = this.picture.getWidth();
            this.height = this.picture.getHeight();
        } catch (IOException ex) {
            System.out.println("File not found! (" + picName + ")");
            picture = null;
        }
    }
    public int getWidth() {
        return this.width;
    }   
    public int getHeight() {
        return this.height;
    }
    public String getPicName(){
        return this.picName;
    }
    public BufferedImage getImage(){
        return this.picture;
    }
    public void setImage(BufferedImage picture){
        this.picture = picture;
    }
    public void draw(Graphics g, int x, int y) {
        g.drawImage(this.picture, x, y, null);
    }
}