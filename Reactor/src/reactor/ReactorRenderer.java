package reactor;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.KeyEvent;

public class ReactorRenderer extends JFrame{
    Building building;
    public String[] infoToDraw = new String[0];
    public ArrayList<String> strings = new ArrayList<>();
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

    private Color addColor(Color c1, double r, double b, double g){
        double red = c1.getRed() + r;
        double blue = c1.getBlue() + b;
        double green = c1.getGreen() + g;
        double max = Math.max(Math.max(red, green), blue);
        if(max>255){
            red *= 255.0/max;
            blue *= 255.0/max;
            green *= 255.0/max;
        }
        return new Color((int)red,(int)green,(int)blue);
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
        BufferedImage bi = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.setColor(Color.WHITE);
        bg.fillRect(0,0,800,800);
        bg.setColor(Color.BLACK);
        double rectsize = (720.0/building.reactor.length)/zoom;
        Square s = building.getSquareAt((int)(xoffset*building.reactor.length/100.0+0.5), (int)(yoffset*building.reactor.length/100.0));
        strings.add("square:");
        strings.add(String.valueOf(s.x));
        strings.add(String.valueOf(s.y));
        strings.add(String.valueOf(s.temperature));
        strings.add("temp:");
        for (int i = 0; i < s.u.temp.length+s.u.global.length+1; i++) {
            if(i<s.u.temp.length){
                strings.add(String.valueOf(s.u.temp[i]));
            }else if(i==s.u.temp.length){
                strings.add("global:");
            }else{
                strings.add(String.valueOf(s.u.global[i-s.u.temp.length-1]));
            }
        }
        infoToDraw = strings.toArray(new String[0]);
        try {
        for(int x = 0; x < building.reactor.length; x++){
               for(int y = 0; y < building.reactor[x].length; y++){
                    Square s2 = building.getSquareAt(x, y);
                    Color c = addColor(s2.u.color,(int)s2.temperature/10,0,0);
                    if(x==s.x&&y==s.y){
                        c = addColor(c, 100,0,0);
                    }
                    bg.setColor(c);
                    bg.fillRect((int)((x-xoffset*building.reactor.length/100.0)*rectsize)+400,(int)((y-yoffset*building.reactor.length/100.0)*rectsize)+400,Math.max((int)(rectsize),1)+1,Math.max((int)(rectsize),1)+1);
                }
            }
        } catch (Exception e) {}
        bg.setColor(Color.BLUE);
        for (int i = 0; i < building.neuts.size(); i++) {
            Neut n = building.neuts.get(i);
            bg.drawOval((int)((n.x-xoffset*building.reactor.length/100.0)*rectsize)+400-(int)(0.5/zoom*10),(int)((n.y-yoffset*building.reactor.length/100.0)*rectsize)+400-(int)(0.5/zoom*10),(int)(1/zoom*10),(int)(1/zoom*10));
        }
        drawLines(bg,infoToDraw,40,80);
        g.drawImage(bi,0,0,null);
        strings.clear();
    }
}