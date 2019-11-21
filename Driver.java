//Dipesh Manandhar 3/31/18

//http://blog.ivank.net/fastest-gaussian-blur.html
//https://en.wikipedia.org/wiki/Gaussian_blur
//https://fgiesen.wordpress.com/2012/07/30/fast-blurs-1/
//https://software.intel.com/en-us/blogs/2014/07/15/an-investigation-of-fast-real-time-gpu-based-image-blur-algorithms

//For 2D Soft Body Physics:
//https://www.gamasutra.com/blogs/JuanBelonPerez/20130826/198897/How_to_create_2D_Physics_Games_with_Box2D_Library.php
//https://codepen.io/thomcc/pen/gzbjF

//Water:
//https://gamedevelopment.tutsplus.com/tutorials/make-a-splash-with-dynamic-2d-water-effects--gamedev-236

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.event.WindowEvent;

public class Driver extends JFrame
{
   private Screen panel=new MainMenuPanel();
   public Driver()
   {
      super("The Labyrinth");
      
      
      setContentPane(panel);
      pack();
      setSize(1000,1000);
      setLocationRelativeTo(null);
      setResizable(false);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      
      
      /*
      setContentPane(panel);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setExtendedState(MAXIMIZED_BOTH);
      */
      
      setVisible(true);
      
      runGameLoop();
   }
   private void runGameLoop()
   {
      JFrame temp=this;
      Thread game=
         new Thread()
         {
            @Override
            public void run()
            {
               gameLoop();
               dispose();
               dispatchEvent(new WindowEvent(temp,WindowEvent.WINDOW_CLOSING));
            }
         };
      game.start();
   }
   private void gameLoop()
   {
      final long NANOSECONDS_IN_A_SECOND=1000000000;
      final int TARGET_UPS=60;
      final long TARGET_UPDATE_TIME=NANOSECONDS_IN_A_SECOND/TARGET_UPS;
      final int TARGET_FPS=60;
      final long TARGET_FRAME_TIME=NANOSECONDS_IN_A_SECOND/TARGET_FPS;
      long prevFrameTime=System.nanoTime();
      long accumulator=0;
      long prevUpdateTime=prevFrameTime;
      
      long prevFPSTime=prevFrameTime,prevUPSTime=prevFrameTime;
      int frames=0,updates=0;
      
      while(panel.isRunning())
      {
         
         if(panel.getNext()!=null)
         {
            boolean wasLightingPanel=panel instanceof LightingPanel;
            
            getContentPane().remove(panel);
            getContentPane().invalidate();
            panel=panel.getNext();
            setContentPane(panel);
            getContentPane().revalidate();
            panel.requestFocus();
            
            if(panel instanceof LightingPanel)
               setExtendedState(MAXIMIZED_BOTH);
            else if(wasLightingPanel)
            {
               setVisible(false);
               pack();
               setSize(1000,1000);
               setLocationRelativeTo(null);
               //setResizable(false);
               setVisible(true);
            }
         }
         
         
         double interpolation=0;
         long now=System.nanoTime();
         accumulator+=now-prevFrameTime;
         while(accumulator>TARGET_UPDATE_TIME)
         {
            prevUpdateTime=System.nanoTime();
            if(!panel.isPaused())
               panel.update();
            updates++;
            
            long timeDiff=prevUpdateTime-prevUPSTime;
            if(timeDiff>=NANOSECONDS_IN_A_SECOND)
            {
               //panel.setUps((int)(updates*NANOSECONDS_IN_A_SECOND/timeDiff));
               panel.setUps(updates);
               updates=0;
               prevUPSTime=prevUpdateTime;
            }
            
            accumulator-=TARGET_UPDATE_TIME;
         }
         now=System.nanoTime();
         interpolation=(double)(now-prevFrameTime)/TARGET_FRAME_TIME;
         //System.out.println(interpolation);
         if(panel.isPaused())
            interpolation=0;
         
         
         panel.render(interpolation);
         now=System.nanoTime();
         prevFrameTime=now;
         frames++;
         
         long timeDiff=prevFrameTime-prevFPSTime;
         if(timeDiff>=NANOSECONDS_IN_A_SECOND)
         {
            //panel.setFps((int)(frames*NANOSECONDS_IN_A_SECOND/timeDiff));
            panel.setFps(frames);
            frames=0;
            prevFPSTime=prevFrameTime;
         }
         
         
         while(now-prevFrameTime<TARGET_FRAME_TIME && now-prevFrameTime<TARGET_UPDATE_TIME)
         {
            Thread.yield();
            now=System.nanoTime();
         }
         /*
         if(tempPanel!=null)
         {
            if(panel.isPaused())
            {
               panel.pause();
               TanksSound.play();
            }
         }
         */
      }
   }
   public static void main(String[] arg)
   {
      new Driver();
   }
}