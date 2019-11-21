//Dipesh Manandhar 4/18/18

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GameObject extends Collidable
{
   private BufferedImage image;
   private int imageX,imageY;
   private double x,y;
   
   public GameObject(int imageIndex,int x0,int y0)
   {
      x=x0;
      y=y0;
      image=Renderer.getImage(imageIndex);
      setRigidBody(new RigidBody(image,x0,y0));
   }
   
   public void update()
   {
   }
   
   public void draw(Graphics g)
   {
      setImagePosition((int)x,(int)y);
      g.drawImage(image,imageX,imageY,null);
   }
   protected void setImagePosition(int xPos,int yPos)
   {
      imageX=xPos;
      imageY=yPos;
   }
   
   @Override
   public void collisionResponse(Collidable other)
   {
      
   }
   
   protected void moveBy(double dx,double dy)
   {
      x+=dx;
      y+=dy;
      setPosition((int)x,(int)y);
   }
   
   public int getX()
   {
      return (int)x;
   }
   public int getY()
   {
      return (int)y;
   }
   
   /*
   public int getCenterX()
   {
      return x+image.getWidth()/2;
   }
   public int getCenterY()
   {
      return y+image.getHeight()/2;
   }
   */
}