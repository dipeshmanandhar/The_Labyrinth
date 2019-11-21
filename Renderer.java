//Dipesh Manandhar 4/18/18

import javax.imageio.ImageIO;

import java.io.File;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Color;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import java.util.LinkedList;

public class Renderer
{
   private static BufferedImage[] images=new BufferedImage[4];
   protected static final int PLAYER=0,
                              ENEMY=1,
                              KEY=2,
                              AXE=3;
   
   private static final int PLAYER_SIZE=LightingPanel.getWallSize();
   private static final int DOWN_SCALE=1,BLUR_RADIUS=PLAYER_SIZE*2/DOWN_SCALE,NUM_BLURS=0;
   private static final int[] TOTAL_TO_RGB=new int[256*(BLUR_RADIUS*2+1)],GRAYSCALE_TO_TRANSPARENT=new int[256];
   //private static final int[][][] TOTAL_TO_RGB=new int[256*(BLUR_RADIUS*2+1)][256*(BLUR_RADIUS*2+1)][256*(BLUR_RADIUS*2+1)]; //[r][g][b] (totals)
   private static BufferedImage lights,shadows;
   
   public static void initialize()
   {
      loadImages();
      setUpTotalToRgbConverter();
      setUpGreyscaleToTransparentConverter();
   }
   
   
   //Images methods
   private static void loadImages()
   {
      for(int i=0;i<images.length-1;i++)
      {
         images[i]=new BufferedImage(PLAYER_SIZE,PLAYER_SIZE,BufferedImage.TYPE_INT_ARGB);
         Graphics g=images[i].getGraphics();
         
         g.setColor(new Color((int)(Math.random()*(1<<6))<<2,(int)(Math.random()*(1<<6))<<2,(int)(Math.random()*(1<<6))<<2));
         g.fillRect(0,0,PLAYER_SIZE,PLAYER_SIZE);
         g.setColor(Color.WHITE);
         g.drawRect(0,0,PLAYER_SIZE-1,PLAYER_SIZE-1);
         
         g.dispose();
      }
      images[KEY]=bufferImage("pics/Shine.png");
      images[AXE]=bufferImage("pics/Axe.png");
   }
   
   private static BufferedImage bufferImage(String filename)
   {
      try
      {
         return ImageIO.read(new File(filename));
      }
      catch(Exception e)
      {
         e.printStackTrace();
         return null;
      }
   }
   
   public static BufferedImage getImage(int index)
   {
      return images[index];
   }
   private static BufferedImage scaleImage(BufferedImage old,int newW,int newH)
   {
      BufferedImage scaled=new BufferedImage(newW,newH,BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = scaled.createGraphics();
      
      g2.drawImage(old,0,0,newW,newH,null);
      g2.dispose();
      
      return scaled;
   }
   
   public static void drawStaminaBar(Graphics g,double percent)
   {
      int barHeight=PLAYER_SIZE*2;
      int barWidth=barHeight*10;
      int scaledWidth=(int)(barWidth*percent/100);
      
      g.setColor(Color.BLACK);
      g.fillRect((LightingPanel.screenWidth()-barWidth)/2,LightingPanel.screenHeight()-barHeight*2,barWidth,barHeight);
      
      if(percent<50)
         g.setColor(Color.RED);
      else
         g.setColor(Color.YELLOW);
      g.fillRect((LightingPanel.screenWidth()-barWidth)/2,LightingPanel.screenHeight()-barHeight*2,scaledWidth,barHeight);
      
      g.setColor(Color.WHITE);
      g.drawRect((LightingPanel.screenWidth()-barWidth)/2,LightingPanel.screenHeight()-barHeight*2,barWidth-1,barHeight-1);
   }
   
   //visibility methods
   //post: buffers player's light
   public static void visibleArea(int lightX,int lightY,int radius,double rotation,double viewAngle)
   {
      shadows=new BufferedImage(LightingPanel.screenWidth(),LightingPanel.screenHeight(),BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2=shadows.createGraphics();
      
      g2.setColor(Color.BLACK);
      g2.fillRect(0,0,shadows.getWidth(),shadows.getHeight());
      
      //Composite prevComposite=g2.getComposite();
      
      //lightX-=LightingPanel.screenX();
      //lightY-=LightingPanel.screenY();
      
      int pRadius=PLAYER_SIZE/2;
      //int xDiff=(int)(Math.cos(rotation)*pRadius);
      //int yDiff=(int)(Math.sin(rotation)*pRadius);
      int xDiff=0;
      int yDiff=pRadius;
      int offsettedX=LightingPanel.screenWidth()/2+xDiff;
      int offsettedY=LightingPanel.screenHeight()/2-yDiff;
      
      
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,1.0f));
      
      g2.setColor(Color.WHITE);
      //g2.fillOval(offsettedX-radius,offsettedY-radius,radius*2,radius*2);
      g2.fillArc(offsettedX-radius,offsettedY-radius,radius*2,radius*2,(int)Math.toDegrees(Math.PI/2-viewAngle/2),(int)Math.toDegrees(viewAngle));
      
      //g2.setComposite(prevComposite);
      
      g2.dispose();
      
      BufferedImage visibilityPolygon=Utilities.castShadowsBack(lightX+xDiff,lightY-yDiff,rotation);
      
      g2=visibilityPolygon.createGraphics();
      g2.drawImage(shadows,0,0,null);
      g2.dispose();
      shadows=visibilityPolygon;
      
      //g2.drawImage(visibilityPolygon,0,0,null);
      
      //g2.dispose();
      
      //shadows=boxBlur(shadows);
      
      
      
      
      
      lights=new BufferedImage(LightingPanel.screenWidth(),LightingPanel.screenHeight(),BufferedImage.TYPE_INT_ARGB);
      g2=lights.createGraphics();
      
      
      radius+=BLUR_RADIUS+10*DOWN_SCALE*NUM_BLURS;
      //Color lightColor=new Color(240,255,0,50);
      //Color lightColor=new Color(255,0,0,50);
      Color lightColor=new Color(0,255,0,100);
      
      
      g2.setPaint(new RadialGradientPaint(offsettedX,offsettedY,radius,new float[]{0f,1f},new Color[]{lightColor,new Color(0,0,0,255)}));
      g2.fillOval(offsettedX-radius,offsettedY-radius,radius*2,radius*2);
      //g2.fillArc(offsettedX-radius,offsettedY-radius,radius*2,radius*2,(int)Math.toDegrees(rotation-viewAngle/2)-(BLUR_RADIUS+DOWN_SCALE*10),(int)Math.toDegrees(viewAngle)+(BLUR_RADIUS+DOWN_SCALE*10)*2);
      
      //g2.drawImage(shadows,0,0,null);
      g2.dispose();
      
      
      //return lights;
   } 
   
   //pre: visibleArea() has been called this frame, drawLighting() has not
   //post: buffers enemy light, returns enemy's visibility in a B&W (monochrome/doutone) image - white pixels are visible, black are not
   public static BufferedImage lightArea(int lightX,int lightY,int radius,double rotation,double viewAngle,double pRotation)
   {
      BufferedImage light=new BufferedImage(LightingPanel.screenWidth(),LightingPanel.screenHeight(),BufferedImage.TYPE_INT_ARGB);
      
      int pRadius=PLAYER_SIZE/2;
      int xDiff=(int)(Math.cos(rotation)*pRadius);
      int yDiff=(int)(Math.sin(rotation)*pRadius);
      int offsettedX=lightX-LightingPanel.screenX()+xDiff;
      int offsettedY=lightY-LightingPanel.screenY()-yDiff;
      
      BufferedImage shadow=new BufferedImage(LightingPanel.screenWidth(),LightingPanel.screenHeight(),BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2=shadow.createGraphics();
      
      //black background
      g2.setColor(Color.BLACK);
      g2.fillRect(0,0,shadow.getWidth(),shadow.getHeight());
      
      g2.rotate((pRotation-Math.PI/2),LightingPanel.screenWidth()/2,LightingPanel.screenHeight()/2);
      
      //ready to clear
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,1.0f));
      
      //clear only visible arc
      g2.setColor(Color.WHITE);
      g2.fillArc(offsettedX-radius,offsettedY-radius,radius*2,radius*2,(int)Math.toDegrees(rotation-viewAngle/2),(int)Math.toDegrees(viewAngle));
      
      g2.dispose();
      
      BufferedImage visibilityPolygon=Utilities.castShadowsAll(lightX+xDiff,lightY-yDiff,pRotation);//don't worry, pRotation is correct
      //Color c=new Color(visibilityPolygon.getRGB(LightingPanel.screenCX(),LightingPanel.screenCY()));
      //System.out.println(c.getAlpha()+", "+c.getRed()+", "+c.getGreen()+", "+c.getBlue()+", ");
      
      g2=visibilityPolygon.createGraphics();
      g2.drawImage(shadow,0,0,null);
      g2.dispose();
      shadow=visibilityPolygon;
      
      /*
      g2.setColor(Color.WHITE);
      g2.fillArc(offsettedX-radius,offsettedY-radius,radius*2,radius*2,(int)Math.toDegrees(rotation-viewAngle/2),(int)Math.toDegrees(viewAngle));
      
      g2.dispose();
      */
      g2=light.createGraphics();
      Color lightColor=new Color(255,0,0,0);
      
      g2.rotate((pRotation-Math.PI/2),LightingPanel.screenWidth()/2,LightingPanel.screenHeight()/2);
      
      g2.setPaint(new RadialGradientPaint(offsettedX,offsettedY,radius,new float[]{0f,1f},new Color[]{new Color(255,0,0,200),lightColor}));
      g2.fillOval(offsettedX-radius,offsettedY-radius,radius*2,radius*2);
      
      
      //g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,1.0f));
      //g2.drawImage(shadow,0,0,null);
      
      g2.dispose();
      
      clearCombine(light,shadow);
      
      g2=lights.createGraphics();
      g2.drawImage(light,0,0,null);
      g2.dispose();
      
      return shadow;
   }
   
   //draws all buffered lights to the screen
   public static void drawLighting(Graphics g)
   {
      g.drawImage(lights,0,0,null);
      shadows=boxBlur(shadows);
      g.drawImage(shadows,0,0,null);
      //shadows=null;
      //lights=null;
   }
   
   //Image Blurring methods
   private static BufferedImage boxBlur(BufferedImage image)
   {
      BufferedImage scaled=scaleImage(image,image.getWidth()/DOWN_SCALE,image.getHeight()/DOWN_SCALE);
      for(int i=0;i<NUM_BLURS;i++)
      {
         horizontalBlur(scaled);
         verticalBlur(scaled);
      }
      
      addTransparency(scaled);
      return scaleImage(scaled,image.getWidth(),image.getHeight());
   }
   private static void setUpTotalToRgbConverter()
   {
      for(int i=0;i<TOTAL_TO_RGB.length;i++)
      {
         int b=i/(BLUR_RADIUS*2+1);
         //              (a << 24) | (r << 16 ) | (g<<8) | b;
         TOTAL_TO_RGB[i]=(255 << 24) | (b << 16 ) | (b<<8) | b;
      }
   }
   private static void setUpGreyscaleToTransparentConverter()
   {
      for(int i=0;i<GRAYSCALE_TO_TRANSPARENT.length;i++)
         GRAYSCALE_TO_TRANSPARENT[i]=((255-i) << 24);
   }
   private static void addTransparency(BufferedImage image)
   {
      int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
      //int[] temp=new int[pixels.length];
      for(int i=0;i<pixels.length;i++)
      {
         //temp[i]=GRAYSCALE_TO_TRANSPARENT[pixels[i] & 0xFF];
         pixels[i]=GRAYSCALE_TO_TRANSPARENT[pixels[i] & 0xFF];
      }
      //image.setRGB(0,0,image.getWidth(),image.getHeight(),temp,0,image.getWidth());
   }
   //pre: /*assumes image is in grayscale,*/ no alpha,
   //     horizontal and vertical blurring are done seperately
   private static int avgColor(int b)
   {
      return TOTAL_TO_RGB[b];
   }
   private static void horizontalBlur(BufferedImage image)
   {
      int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
      int[] blurred = new int[pixels.length];
      
      ThreadPool pool=new ThreadPool();
      for(int y=0;y<image.getHeight();y++)
      {
         final int temp=y*image.getWidth();
         pool.addTask(
            new Runnable()
            {
               @Override
               public void run()
               {
                  LinkedList<Integer> box=new LinkedList<Integer>();
                  int totalBlue=0;
                  int i=temp;
                  
                  for(int j=0;j<BLUR_RADIUS;j++)
                     box.addLast(255<<24);
                  box.addLast(pixels[i]);
                  totalBlue+=pixels[i] & 0xFF;
                  for(int j=1;j<=BLUR_RADIUS;j++)
                     if(j<image.getWidth())
                     {
                        box.addLast(pixels[i+j]);
                        totalBlue+=pixels[i+j] & 0xFF;
                     }
                     else
                        box.addLast(255<<24);
                  blurred[i]=avgColor(totalBlue);
                  
                  for(int x=1;x<image.getWidth();x++)
                  {
                     i++;
                     totalBlue-=box.removeFirst() & 0xFF;
                     if(x+BLUR_RADIUS<image.getWidth())
                     {
                        box.addLast(pixels[i+BLUR_RADIUS]);
                        totalBlue+=pixels[i+BLUR_RADIUS] & 0xFF;
                     }
                     else
                        box.addLast(255<<24);
                     blurred[i]=avgColor(totalBlue);
                  }
                  
               }
            });
      }
      pool.close();
      
      /*
      LinkedList<Integer> box=new LinkedList<Integer>();
      int totalBlue=0;
      for(int i=0;i<pixels.length;i++)
      {
         int x=i%image.getWidth();
         //int y=i/image.getWidth();
         if(x==0)//new row
         {
            box.clear();
            totalBlue=0;
            for(int j=0;j<BLUR_RADIUS;j++)
               box.addLast(255<<24);
            box.addLast(pixels[i]);
            totalBlue+=pixels[i] & 0xFF;
            for(int j=1;j<=BLUR_RADIUS;j++)
               if(x+j<image.getWidth())
               {
                  box.addLast(pixels[i+j]);
                  totalBlue+=pixels[i+j] & 0xFF;
               }
               else
                  box.addLast(255<<24);
         }
         else
         {
            totalBlue-=box.removeFirst() & 0xFF;
            if(x+BLUR_RADIUS<image.getWidth())
            {
               box.addLast(pixels[i+BLUR_RADIUS]);
               totalBlue+=pixels[i+BLUR_RADIUS] & 0xFF;
            }
            else
               box.addLast(255<<24);
         }
         blurred[i]=avgColor(totalBlue);
      }
      */
      
      image.setRGB(0,0,image.getWidth(),image.getHeight(),blurred,0,image.getWidth());
      //image.setRGB(0,0,image.getWidth(),image.getHeight(),pixels,0,image.getWidth());
   }
   private static void verticalBlur(BufferedImage image)
   {
      int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
      int[] blurred = new int[pixels.length];
      
      
      ThreadPool pool=new ThreadPool();
      for(int x=0;x<image.getWidth();x++)
      {
         final int temp=x;
         pool.addTask(
            new Runnable()
            {
               @Override
               public void run()
               {
                  LinkedList<Integer> box=new LinkedList<Integer>();
                  int totalBlue=0;
                  int i=temp;
                  
                  for(int j=0;j<BLUR_RADIUS;j++)
                     box.addLast(255<<24);
                  box.addLast(pixels[i]);
                  totalBlue+=pixels[i] & 0xFF;
                  for(int j=1;j<=BLUR_RADIUS;j++)
                     if(j<image.getHeight())
                     {
                        box.addLast(pixels[i+j*image.getWidth()]);
                        totalBlue+=pixels[i+j*image.getWidth()] & 0xFF;
                     }
                     else
                        box.addLast(255<<24);
                  blurred[i]=avgColor(totalBlue);
                  
                  for(int y=1;y<image.getHeight();y++)
                  {
                     i+=image.getWidth();
                     totalBlue-=box.removeFirst() & 0xFF;
                     if(y+BLUR_RADIUS<image.getHeight())
                     {
                        box.addLast(pixels[i+BLUR_RADIUS*image.getWidth()]);
                        totalBlue+=pixels[i+BLUR_RADIUS*image.getWidth()] & 0xFF;
                     }
                     else
                        box.addLast(255<<24);
                     blurred[i]=avgColor(totalBlue);
                  }
                  
               }
            });
      }
      pool.close();
      
      
      /*
      LinkedList<Integer> box=new LinkedList<Integer>();
      int totalBlue=0;
      for(int i=0;i<pixels.length;)
      {
         //int x=i%image.getWidth();
         int y=i/image.getWidth();
         if(y==0)//new col
         {
            box.clear();
            totalBlue=0;
            for(int j=0;j<BLUR_RADIUS;j++)
               box.addLast(255<<24);
            box.addLast(pixels[i]);
            totalBlue+=pixels[i] & 0xFF;
            for(int j=1;j<=BLUR_RADIUS;j++)
               if(y+j<image.getHeight())
               {
                  box.addLast(pixels[i+j*image.getWidth()]);
                  totalBlue+=pixels[i+j*image.getWidth()] & 0xFF;
               }
               else
                  box.addLast(255<<24);
         }
         else
         {
            totalBlue-=box.removeFirst() & 0xFF;
            if(y+BLUR_RADIUS<image.getHeight())
            {
               box.addLast(pixels[i+BLUR_RADIUS*image.getWidth()]);
               totalBlue+=pixels[i+BLUR_RADIUS*image.getWidth()] & 0xFF;
            }
            else
               box.addLast(255<<24);
         }
         blurred[i]=avgColor(totalBlue);
         
         if(y==image.getHeight()-1)
         {
            int x=i%image.getWidth();
            if(x==image.getWidth()-1)
               break;
            else
               i=x+1;
         }
         else
            i=i+image.getWidth();
      }
      */
      
      image.setRGB(0,0,image.getWidth(),image.getHeight(),blurred,0,image.getWidth());
      //image.setRGB(0,0,image.getWidth(),image.getHeight(),pixels,0,image.getWidth());
   }
   //post: light is cleared wherever shadow is black, shadow is unchanged
   private static void clearCombine(BufferedImage light,BufferedImage shadow)
   {
      int[] lightPixels = ((DataBufferInt) light.getRaster().getDataBuffer()).getData();
      int[] shadowPixels = ((DataBufferInt) shadow.getRaster().getDataBuffer()).getData();
      
      for(int i=0;i<lightPixels.length;i++)
         if((shadowPixels[i] & 0xFF)==0) //black b/c zero blue component
            lightPixels[i]=0;//clear
   }
}