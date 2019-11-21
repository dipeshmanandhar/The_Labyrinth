import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;

public class Screen extends JPanel
{
   //Instance Variables
   private int fps,ups;
   private boolean paused=false,running=true;
   private Screen next=null;
   
   
   //Construtors
   public Screen()
   {
      setBackground(new Color(100,100,100));
   }
   
   //Mutators
   
   //only for things that happen every frame
   public void update()
   {
   
   }
   public void render(double interpolation)
   {
      repaint();
   }
   
   protected void drawScene(Graphics g)
   {
      
   }
   
   protected void drawUI(Graphics g)
   {
      g.setColor(Color.WHITE);
      g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,20));
      
      //bottom right
      g.drawString("UPS: "+ups,getWidth()*3/4,getHeight()-50);
      g.drawString("FPS: "+fps,getWidth()*3/4,getHeight()-25);
   }
   
   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      
      if(next==null)
      {
      //draw everything not related to UI below UI
         drawScene(g);
      
      
      //draw info on top of everything
         drawUI(g);
      }
   }
   
   //Setters
   public void setFps(int f)
   {
      fps=f;
   }
   public void setUps(int u)
   {
      ups=u;
   }
   public void setPaused(boolean p)
   {
      paused=p;
   }
   public void setRunning(boolean r)
   {
      running=r;
   }
   public void setNext(Screen n)
   {
      next=n;
   }
   
   //Getters
   public boolean isPaused()
   {
      return paused;
   }
   public boolean isRunning()
   {
      return running;
   }
   public Screen getNext()
   {
      return next;
   }
}