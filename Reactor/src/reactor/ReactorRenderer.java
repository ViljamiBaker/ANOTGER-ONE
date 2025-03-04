package reactor;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.awt.event.KeyEvent;

public class ReactorRenderer extends JFrame{
    Unit[][] reactor;
    Building building;
    public String[] infoToDraw = new String[0];
    Graphics g;
    keyListener kl;
    int xoffset = 0;
    int yoffset = 0;
    double zoom = 1;
    public ReactorRenderer(Building building){
        this.building = building;
        this.reactor = building.reactor;
        this.setTitle("painting");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        g = this.getGraphics();
        kl = new keyListener(this);
    }
    Color[] colors = {Color.DARK_GRAY, Color.GREEN, Color.ORANGE, Color.CYAN, Color.LIGHT_GRAY, Color.RED};

    void drawLines(Graphics bg, String[] lines, int x, int y){
        bg.setColor(Color.BLACK);
        for(int i = 0; i<lines.length;i++){
            bg.drawString(lines[i],x,y+i*10);
        }
    }

    @Override
    public void paint(Graphics g){
        if(kl.keyDown(KeyEvent.VK_W)){
            yoffset++;
        }
        if(kl.keyDown(KeyEvent.VK_S)){
            yoffset--;
        }
        if(kl.keyDown(KeyEvent.VK_A)){
            xoffset--;
        }
        if(kl.keyDown(KeyEvent.VK_D)){
            xoffset++;
        }
        if(kl.keyDown(KeyEvent.VK_E)){
            zoom*=1.01;
        }
        if(kl.keyDown(KeyEvent.VK_Q)){
            zoom*=0.99;
        }
        BufferedImage bi = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.setColor(Color.WHITE);
        bg.fillRect(0,0,800,800);
        bg.setColor(Color.BLACK);
        for(int y = 0; y < reactor.length; y++){
            for(int x = 0; x < reactor[y].length; x++){
                bg.setColor(reactor[y][x].color);
                bg.fillRect((int)((double)(x-xoffset*reactor.length/100)*(720.0/reactor.length)/zoom)+440,(int)((double)(y+yoffset*reactor.length/100)*(720.0/reactor.length)/zoom)+330,Math.max((int)(720.0/reactor.length/zoom),1)+1,Math.max((int)(720.0/reactor.length/zoom),1)+1);
            }
        }
        drawLines(bg, infoToDraw, (int)((double)(reactor.length+5-xoffset*reactor.length/100)*(720.0/reactor.length)/zoom)+440,(int)((double)(5+yoffset*reactor.length/100)*(720.0/reactor.length)/zoom)+330);
        g.drawImage(bi,0,0,null);
    }
}