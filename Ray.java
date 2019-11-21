//Dipesh Manandhar

import java.awt.Graphics;
import java.awt.Color;

public class Ray implements Comparable
{
   private int startX,startY,endX,endY;
   private double rotation;   //in radians, counterclockwise from positive x-axis (right), like unit circle
   private double distance;
   private Polygon polygon;
   
   public Ray(int x1,int y1,int x2,int y2,Polygon poly)
   {
      startX=x1;
      startY=y1;
      endX=x2;
      endY=y2;
      
      int xDiff=x2-x1;
      int yDiff=y2-y1;
      
      rotation=Utilities.getRotation(xDiff,yDiff);
      
      distance=Utilities.distance(x1,y1,x2,y2);
      
      polygon=poly;
   }
   
   public void draw(Graphics g)
   {
      g.drawLine(startX,startY,endX,endY);
      //g.drawLine(startX-LightingPanel.screenX(),startY-LightingPanel.screenY(),endX-LightingPanel.screenX(),endY-LightingPanel.screenY());
   }
   
   @Override
   public int compareTo(Object other)
   {
      Ray temp=(Ray)other;
      if(rotation<temp.rotation)
         return -1;
      else if(rotation>temp.rotation)
         return 1;
      else// if(rotation==temp.rotation)
      {
         if(distance<temp.distance)
            return -1;
         else if(distance>temp.distance)
            return 1;
         else// if(distance==temp.distance)
            return 0;
      }
   }
   public double getdX()
   {
      return Math.cos(rotation);
   }
   public double getdY()
   {
      return -Math.sin(rotation);   //negative b/c trig requires y to increase as travel up, but Graphics uses y increases as travel down
   }
   public int getXDiff()
   {
      return endX-startX;
   }
   public int getYDiff()
   {
      return endY-startY;
   }
   public double getRotation()
   {
      return rotation;
   }
   public int getX0()
   {
      return startX;
   }
   public int getY0()
   {
      return startY;
   }
   public int getX1()
   {
      return endX;
   }
   public int getY1()
   {
      return endY;
   }
   public double getLength()
   {
      return distance;
   }
   public Polygon getPolygon()
   {
      return polygon;
   }
   
   @Override
   public String toString()
   {
      return "("+distance+","+rotation+")";
   }
}