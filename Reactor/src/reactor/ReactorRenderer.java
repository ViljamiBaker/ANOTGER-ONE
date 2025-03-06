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
    double xoffset = 0;
    double yoffset = 0;
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
                yoffset--;
            }
            if(kl.keyDown(KeyEvent.VK_S)){
                yoffset++;
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
        Unit u = building.getUnitAt(EXIT_ON_CLOSE, ABORT);
        infoToDraw = new String[u.temp.length + 3];

        for (int i = 0; i < infoToDraw.length; i++) {
            switch (i) {
                case 0:
                    infoToDraw[i] = "A";
                    break;
                case 1:
                    infoToDraw[i] = "A";
                    break;
                case 2:
                    infoToDraw[i] = "A";
                    break;
                default:
                    infoToDraw[i] = String.valueOf(u.temp[i-3]);
                    break;
            }
        }
        BufferedImage bi = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.setColor(Color.WHITE);
        bg.fillRect(0,0,800,800);
        bg.setColor(Color.BLACK);
        double rectsize = (720.0/building.reactor.length)/zoom;
        for(int x = 0; x < building.reactor.length; x++){
            for(int y = 0; y < building.reactor[x].length; y++){
                Color c = building.reactor[x][y].color;
                bg.setColor(new Color((int)Math.min(c.getRed() + building.temperature[x][y],255.0), c.getGreen(), c.getBlue()));
                bg.fillRect((int)((x-xoffset*building.reactor.length/100.0)*rectsize)+440,(int)((y-yoffset*building.reactor.length/100.0)*rectsize)+330,Math.max((int)(rectsize),1)+1,Math.max((int)(rectsize),1)+1);
            }
        }
        bg.setColor(Color.BLUE);
        for (int i = 0; i < building.neuts.size(); i++) {
            Neut n = building.neuts.get(i);
            bg.drawOval((int)((n.x-xoffset*building.reactor.length/100.0)*rectsize)+440,(int)((n.y-yoffset*building.reactor.length/100.0)*rectsize)+330,(int)(1/zoom*10),(int)(1/zoom*10));
        }
        drawLines(bg, infoToDraw, (int)((building.reactor.length+5.0-xoffset*building.reactor.length/100.0)*rectsize)+440,(int)((5.0-yoffset*building.reactor.length/100.0)*rectsize)+330);
        g.drawImage(bi,0,0,null);
    }
}