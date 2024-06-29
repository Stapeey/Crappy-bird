import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{
    int screenHeight = 1080/2;
    int screenWidth = 1920/2;
    int FPS = 60;
    boolean dead = false;
    int Points = 0;

    int gravity = 4;
    double t = 0;
    double relTime = 0;
    boolean passed = true;
    
    int playerSize = screenWidth/18;
    int playerX = screenWidth/2;
    int playerY = screenHeight/2;
    int fallSpeed = 3;

    int pillarGap = playerSize+playerSize*5/4*2;

    int jump = pillarGap-playerSize*2;

    boolean canPress = true;
    int canPressCount = 0;
    boolean a = false; // Don't touch it!

    int pillarSpeed = 3;
    int pillarDistance = screenWidth/7;
    int pillarWidth = 95;
    int pillarHelp = 0;
    ArrayList<Integer> size = new ArrayList<>();
    ArrayList<Integer> coords = new ArrayList<>();
    ArrayList<Integer> pillarTop = new ArrayList<>();
    ArrayList<Integer> pillarBottom = new ArrayList<>();
    BufferedImage madar;
    BufferedImage pillar;
    BufferedImage background;

    AudioHandler audioHandler = new AudioHandler();
    
    
    



    Thread gameThread;
    KeyHandler keyH = new KeyHandler();

    public boolean collision(int py, int ps, int oy, int os){
        if(py<=oy+os&&py+ps>=oy){
            return true;
        }
        else if(playerY>screenHeight){
            return true;
        }
        else{
            return false;
        }
        }

    public void updateFallSpeed(){
        if (keyH.upPressed){
            t = 0.4;
            relTime = System.currentTimeMillis();
        }
        else{
            t = (System.currentTimeMillis()-relTime)/1000;
        }
        fallSpeed = (int) (gravity*t);
    }

    public void delayInput(){
        if (!canPress&&canPressCount<30){
            canPressCount++;
        }
        else{
            canPress = true;
            canPressCount=0;
        }
    }
    
    public GamePanel(){

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread(){

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run(){

        audioHandler.loadSound("birb.wav");
        coords.add(screenWidth);
        int rand = (int) (Math.random()*193);
        size.add(rand);
        pillarTop.add(screenHeight/10+rand);
        pillarBottom.add(screenHeight-(screenHeight/10+rand+pillarGap));
        relTime = System.currentTimeMillis();
        try {
            background = ImageIO.read(getClass().getResourceAsStream("bg.png"));
            madar = ImageIO.read(getClass().getResourceAsStream("birb2.png"));
            pillar = ImageIO.read(getClass().getResourceAsStream("wall.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while(gameThread != null){
            
            update();
            repaint();
            delayInput();

            try {

                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1000000;

                if(remainingTime < 0){
                    remainingTime = 0;
                }
                
                Thread.sleep((long) remainingTime);
                

                if (a){
                    Thread.sleep(1000);
                    nextDrawTime += 1000000000;
                    t = 0;
                    relTime = System.currentTimeMillis();
                    a = false;
                    Points = 0;
                }   //NEM TUDOM H MIÉRT NEM MŰKSZIK KISEBB DELAYYEL DE MAR NINCS KEDVEM SZENVEDNI EZZEL
                if (dead){
                    coords.clear();
                    size.clear();
                    pillarBottom.clear();
                    pillarTop.clear();

                    coords.add(screenWidth);
                    rand = (int) (Math.random()*193);
                    size.add(rand);
                    pillarTop.add(screenHeight/10+rand);
                    pillarBottom.add(screenHeight-(screenHeight/10+rand+pillarGap));
                    pillarHelp = 0;
                    playerX = screenWidth/2;
                    playerY = screenHeight/2;
                    Thread.sleep(1000);
                    nextDrawTime += 1000000000;
                    a=true;
                    dead = false;
                }
                nextDrawTime += drawInterval;


                    
            } catch (Exception e) {
                
            }
            
        }

    }
    public void update(){
    if(!dead){
        for (int i = 0; i<coords.size(); i++){
            coords.set(i, coords.get(i)-pillarSpeed);
            if (coords.get(i)<=playerX+playerSize && coords.get(i)+pillarWidth>=playerX){
                if (playerX>=coords.get(i)+pillarWidth/2&&passed){
                    Points++;
                    passed = false;
                }
                if(coords.get(i)+pillarWidth-pillarSpeed<=playerX){
                    passed = true;
                }
                dead = collision(playerY, playerSize, 0, pillarTop.get(i))||collision(playerY, playerSize, screenHeight/10+size.get(i)+pillarGap, pillarBottom.get(i));
                
            }
            
            
            if (coords.get(pillarHelp)<screenWidth/5*3){
                if (coords.size()==4){
                    if(pillarHelp == 3){
                        pillarHelp = -1;
                    }
                    pillarHelp++;
                    coords.set(pillarHelp,screenWidth);
                    size.set(pillarHelp,(int) (Math.random()*193));
                    
                }
                else{
                    coords.add(screenWidth);
                    size.add((int) (Math.random()*193));
                    pillarTop.add(screenHeight/10+size.get(i));
                    pillarBottom.add(screenHeight-(screenHeight/10+size.get(i)+pillarGap));
                    pillarHelp++;
                    }
                }
            pillarTop.set(i,screenHeight/10+size.get(i));
            pillarBottom.set(i, screenHeight-(screenHeight/10+size.get(i)+pillarGap));
            }
        updateFallSpeed();
        playerY += fallSpeed;
        if (keyH.upPressed && canPress){
            playerY-=jump;
            audioHandler.playSound();
            audioHandler.stopSound();
            canPress = false;
        }
        
    }
    
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", 0, 40));

        if (dead){
            /*g2.setColor(Color.black);
            g2.fillRect(0,0, screenWidth,screenHeight);*/
            g2.drawImage(background,0,0,screenWidth,screenHeight,null);
            g2.setColor(Color.white);
            g2.drawString(String.format("Meghaltál. Ennyi pontod volt: %d", Points),screenWidth/5,screenHeight/6);
        }
        else{

            g2.drawImage(background,0,0,screenWidth,screenHeight,null);

            //g2.fillRect(playerX, playerY, playerSize, playerSize);
            g2.drawImage(madar,playerX,playerY,playerSize,playerSize,null);        

            for (int i = 0;i<coords.size();i++){
                //g2.fillRect(coords.get(i), 0, pillarWidth, screenHeight/10+size.get(i));
                g2.drawImage(pillar,coords.get(i), 0, pillarWidth, screenHeight/10+size.get(i),null);
            }
            for (int i = 0;i<coords.size();i++){
                //g2.fillRect(coords.get(i), screenHeight/10+size.get(i)+pillarGap, pillarWidth, screenHeight-(screenHeight/10+size.get(i)+pillarGap));
                g2.drawImage(pillar,coords.get(i), screenHeight/10+size.get(i)+pillarGap, pillarWidth, screenHeight-(screenHeight/10+size.get(i)+pillarGap),null);
            }
    
            g2.drawString(String.format("Pontok: %d", Points),screenWidth/20,screenHeight/6);
    
            g2.dispose();

        }
        

    }
}