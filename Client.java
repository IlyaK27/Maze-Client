import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Client {
    private static JFrame window;
    private static MyPanel gamePanel;

    private static Ball myBall;
    private String name;
    private Color color;

    private PrintWriter output;    
    private BufferedReader input;
    private static ServerHandler server;

    private Socket clientSocket;
    private final String HOST = "localhost";
    private final int PORT = 5001;
    protected static boolean playing;

    private JLabel titleLabel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JButton playButton;
    
    private int mouseX;
    private int mouseY;

    private HashMap<Integer, Circle> entities = new HashMap<Integer, Circle>();
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
    private class MyPanel extends JPanel {
        public void paintComponent(Graphics g) {
            Set<Integer> entitiesSet = entities.keySet(); // Using a set because you can't itterate through a hashMap
            for (Integer entityID : entitiesSet) {
                if(withinFOV(entities.get(entityID))){
                    entities.get(entityID).draw(g, myBall);
                }
            }
        }
    }
    //-------------------------------------------------
    private void setup() throws IOException{
        window = new JFrame("Agar.io");
        window.setPreferredSize(new Dimension(Const.WIDTH, Const.HEIGHT));// adding because of window problems   
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setSize(Const.WIDTH, Const.HEIGHT);
        window.addMouseMotionListener(new MyMouseMotionListener());

        gamePanel = new MyPanel();

        window.add(gamePanel);

        clientSocket = new Socket(HOST, PORT);  
        output = new PrintWriter(clientSocket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        name = "";
        titleLabel = new JLabel("AGARIO");
        nameLabel = new JLabel("Enter your name: ");
        nameField = new JTextField(name,10);
        playButton = new JButton("PLAY");
        playButton.addActionListener(new ButtonListener());
        server = new ServerHandler(this);
        playing = false;
        addGUI();
        window.setVisible(true);
    }
    public void stop() throws Exception{ 
        input.close();
        output.close();
        clientSocket.close();
    }
    private void addGUI(){
        nameField.setText(name);      
        gamePanel.add(titleLabel);
        gamePanel.add(nameLabel);
        gamePanel.add(nameField);
        gamePanel.add(playButton);
    }
    private void removeGUI(){
        gamePanel.remove(titleLabel);
        gamePanel.remove(nameLabel);
        gamePanel.remove(nameField);
        gamePanel.remove(playButton); 
    }
    private int calculateAngle(){
        int angle = (int)(Math.atan( (double)(mouseY - (double)Const.HEIGHT/2) / (mouseX - (double)Const.WIDTH/2)) * (180 / Math.PI));
        int raa = Math.abs(angle); // related acute angle
        if (mouseX >= (double)Const.WIDTH/2 && mouseY >= (double)Const.HEIGHT/2) return raa;
        else if (mouseX < (double)Const.WIDTH/2 && mouseY >= (double)Const.HEIGHT/2) return 180 - raa;
        else if (mouseX < (double)(Const.WIDTH)/2 && mouseY < (double)Const.HEIGHT/2) return 180 + raa;
        else return 360 - raa;
    }

    private boolean withinFOV(Circle entity){
        boolean inFOV = false;
        if(((myBall.getX() - entity.getX() + entity.getRadius() <= Const.WIDTH/2) ||
           (entity.getX() - entity.getRadius() - myBall.getX() <= Const.WIDTH/2)) &&
           ((myBall.getY() - entity.getY() + entity.getRadius() <= Const.HEIGHT/2) ||
           (entity.getY() - entity.getRadius() - myBall.getY() <= Const.HEIGHT/2))){
            inFOV = true;
        }
        return inFOV;
    }
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
                if(update != "" || update != null){
                    updateInfo = update.split(" ", 8);
                    if(updateInfo[0].equals(Const.MOVE)){
                        myBall.setX(Integer.parseInt(updateInfo[1]));
                        myBall.setY(Integer.parseInt(updateInfo[2]));
                        myBall.setRadius(Integer.parseInt(updateInfo[3]));
                        int angle = calculateAngle();
                        output.println(Const.TURN + " " + angle);
                        output.flush();
                    }
                    else if(updateInfo[0].equals(Const.DIE)){
                        System.out.println("die");
                        playing = false;
                        addGUI();
                    }
                    else if(updateInfo[0].equals(Const.JOIN)){
                        int id = Integer.parseInt(updateInfo[1]);
                        int x = Integer.parseInt(updateInfo[2]);
                        int y = Integer.parseInt(updateInfo[3]);
                        int radius = Integer.parseInt(updateInfo[4]);
                        myBall = new Ball(name, x, y, radius, color);
                        entities.put(id, myBall);
                        playing = true;
                    }
                    else if(updateInfo[0].equals(Const.NEW)){
                        int id = Integer.parseInt(updateInfo[1]);
                        int red = Integer.parseInt(updateInfo[2]);
                        int green = Integer.parseInt(updateInfo[3]);
                        int blue = Integer.parseInt(updateInfo[4]);
                        String name = updateInfo[5];
                        entities.put(id, new Ball(name, 0, new Color(red, green, blue)));
                    }
                    else if(updateInfo[0].equals(Const.PELLET)){
                        int id = Integer.parseInt(updateInfo[1]);
                        int x = Integer.parseInt(updateInfo[2]);
                        int y = Integer.parseInt(updateInfo[3]);
                        int radius = Integer.parseInt(updateInfo[4]);
                        int red = Integer.parseInt(updateInfo[5]);
                        int green = Integer.parseInt(updateInfo[6]);
                        int blue = Integer.parseInt(updateInfo[7]);
                        entities.put(id, new Circle(x, y, radius, new Color(red, green, blue)));
                    }
                    else if(updateInfo[0].equals(Const.BALL)){
                        int id = Integer.parseInt(updateInfo[1]);
                        int x = Integer.parseInt(updateInfo[2]);
                        int y = Integer.parseInt(updateInfo[3]);
                        int radius = Integer.parseInt(updateInfo[4]);
                        entities.get(id).setX(x);
                        entities.get(id).setY(y);
                        entities.get(id).setRadius(radius);
                    }
                    else if(updateInfo[0].equals(Const.REMOVE)){
                        int id = Integer.parseInt(updateInfo[1]);
                        entities.remove(id);
                    }
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
    private class ButtonListener implements ActionListener{       //this is the required class definition
        public void actionPerformed(ActionEvent event){          //this is the only method in this class 
            // The code below will run automatically when the enterButton is activated.
            name = nameField.getText();
            color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
            output.println(Const.JOIN + " " + color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " " + name); 
            output.flush();
            removeGUI();
        }
    }
}