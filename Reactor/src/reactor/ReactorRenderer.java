package reactor;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.awt.event.KeyEvent;

public class ReactorRenderer extends JFrame{
    Building building;
    public String[] infoToDraw = new String[0];
    Graphics g;
    keyListener kl;
    int xoffset = 0;
    int yoffset = 0;
    double zoom = 1;
    public ReactorRenderer(Building building){
        this.building = building;
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
        try {
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
        } catch (Exception e) {}

        BufferedImage bi = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.setColor(Color.WHITE);
        bg.fillRect(0,0,800,800);
        bg.setColor(Color.BLACK);
        for(int y = 0; y < building.reactor.length; y++){
            for(int x = 0; x < building.reactor[y].length; x++){
                Color c = building.reactor[y][x].color;
                bg.setColor(new Color((int)Math.min(c.getRed() + building.temperature[y][x],255.0), c.getGreen(), c.getBlue()));
                bg.fillRect((int)((double)(x-xoffset*building.reactor.length/100.0)*(720.0/building.reactor.length)/zoom)+440,(int)((double)(y+yoffset*building.reactor.length/100.0)*(720.0/building.reactor.length)/zoom)+330,Math.max((int)(720.0/building.reactor.length/zoom),1)+1,Math.max((int)(720.0/building.reactor.length/zoom),1)+1);
            }
        }
        bg.setColor(Color.BLUE);
        for (int i = 0; i < building.neuts.size(); i++) {
            bg.drawOval((int)((double)(building.neuts.get(i).y-xoffset*(double)building.reactor.length/100.0)*(720.0/building.reactor.length)/zoom)+440,(int)((double)(building.neuts.get(i).x+yoffset*(double)building.reactor.length/100.0)*(720.0/building.reactor.length)/zoom)+330,(int)(1/zoom*10),(int)(1/zoom*10));
        }
        drawLines(bg, infoToDraw, (int)((double)(building.reactor.length+5.0-xoffset*building.reactor.length/100.0)*(720.0/building.reactor.length)/zoom)+440,(int)((double)(5.0+yoffset*building.reactor.length/100.0)*(720.0/building.reactor.length)/zoom)+330);
        g.drawImage(bi,0,0,null);
    }
}