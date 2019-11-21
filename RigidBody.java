//Dipesh Manandhar

import java.awt.image.BufferedImage;

public class RigidBody
{
   private int x,y,width,height;
   
   public RigidBody(Polygon poly)
   {
      setBoundingBox(poly);
   }
   public RigidBody(BufferedImage bi,int xPos,int yPos)
   {
      setBoundingBox(bi,xPos,yPos);
   }
   public void setBoundingBox(Polygon poly)
   {
      int minX=Integer.MAX_VALUE,minY=minX;
      int maxX=Integer.MIN_VALUE,maxY=maxX;
      
      int[] xs=poly.getXS();
      int[] ys=poly.getYS();
      
      for(int i=0;i<poly.getNumPoints();i++)
      {
         if(xs[i]<minX)
            minX=xs[i];
         if(xs[i]>maxX)
            maxX=xs[i];
         if(ys[i]<minY)
            minY=ys[i];
         if(ys[i]>maxY)
            maxY=ys[i];
      }
      x=minX;
      y=minY;
      width=maxX-minX;
      height=maxY-minY;
   }
   
   public void setBoundingBox(BufferedImage bi,int xPos,int yPos)
   {
      x=xPos;
      y=yPos;
      width=bi.getWidth();
      height=bi.getHeight();
   }
   public void setPosition(int xPos,int yPos)
   {
      x=xPos;
      y=yPos;
   }
   
   public int getCenterX()
   {
      return x+width/2;
   }
   public int getCenterY()
   {
      return y+height/2;
   }
   public int getWidth()
   {
      return width;
   }
   public int getHeight()
   {
      return height;
   }
   
   
   public boolean collidesWith(RigidBody other)
   {
      return Math.abs(getCenterX()-other.getCenterX())<width/2+other.width/2 && Math.abs(getCenterY()-other.getCenterY())<height/2+other.height/2;
   }
}