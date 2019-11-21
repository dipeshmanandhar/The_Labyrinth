//Dipesh Manandhar 4/2/18

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class Utilities
{
   //post: 0<=theta<2*PI
   public static double getRotation(double xDiff,double yDiff)
   {
      double rotation=Math.atan(-yDiff/(double)xDiff);    //negative b/c trig requires y to increase as travel up, but Graphics uses y increases as travel down
      if(xDiff==0)
      {
         if(-yDiff>0)
            rotation=Math.PI/2;
         else if(-yDiff<0)
            rotation=-Math.PI/2;
         else
            rotation=0;   //x1,y1 == x2,y2
      }
      else if(xDiff<0)
         rotation+=Math.PI;
      
      if(rotation<0)
         rotation+=Math.PI*2;
      return rotation;
   }
   public static double distance(double x1,double y1,double x2,double y2)
   {
      return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
   }   
   public static double distance(Collidable c1,Collidable c2)
   {
      return distance(c1.getCenterX(),c1.getCenterY(),c2.getCenterX(),c2.getCenterY());
   } 
   public static int dotProduct(int ux,int uy,int vx, int vy)
   {
      return ux*vx+uy*vy;
   }
   
   //pre: no element in polygons is null, (lightX,lightY) is not too close to an edge of a polygon in polygons,
   //     no two polygons in polygons are intersecting
   //post: returns an image of the shadows on the screen, only of backfacing walls
   public static BufferedImage castShadowsBack(int lightX,int lightY,double rotation)
   {
      BufferedImage shadows=new BufferedImage(LightingPanel.screenWidth(),LightingPanel.screenHeight(),BufferedImage.TYPE_INT_ARGB);
      castShadowLayerBack(shadows,lightX,lightY,Color.BLACK,rotation);
      return shadows;
   /*
      int lights=4;
      int maxRGB=(lights-1)*30>255 ? 255:(lights-1)*30;
      Color curr=new Color(maxRGB,maxRGB,maxRGB);
      for(int i=0;i<360;i+=360/lights)
      {
         castShadowLayer(shadows,lightX-(int)(Math.cos(Math.toRadians(i))*8),lightY+(int)(Math.sin(Math.toRadians(i))*8),curr);
         
         maxRGB-=30;
         if(maxRGB<0)
            maxRGB=0;
         curr=new Color(maxRGB,maxRGB,maxRGB);
      }
      */
   }
   private static void castShadowLayerBack(BufferedImage shadows,int lightX,int lightY,Color c,double rotation)
   {
      Graphics2D g2=shadows.createGraphics();
      g2.setColor(new Color(255,255,255,255));
      g2.fillRect(0,0,shadows.getWidth(),shadows.getHeight());
      
      g2.rotate((rotation-Math.PI/2),LightingPanel.screenWidth()/2,LightingPanel.screenHeight()/2);
      
      g2.setColor(c);
      
      for(Polygon polygon:LightingPanel.getPolygons())
      {
         if(!polygon.isInScreen())
            continue;
         Ray[] rays=new Ray[polygon.getNumPoints()];
         int[] xs=polygon.getXS();
         int[] ys=polygon.getYS();
         Segment[] segments=polygon.getSegments();
         
         //add all rays from light to vertices in a list
         for(int i=0;i<rays.length;i++)
            rays[i]=new Ray(lightX,lightY,xs[i],ys[i],polygon);
         
         //draw apropriate shadows on g
         for(int i=0;i<rays.length;i++)
         {
            int dotProduct=Utilities.dotProduct(rays[i].getXDiff(),rays[i].getYDiff(),segments[i].getNormalX(),segments[i].getNormalY());
            //skip drawing shadows of forward facing edges
            if(dotProduct<0)
               continue;
            
            //skip shared edges with other polygons
            if(polygon.isSharedEdge(i))
               continue;
            
            /*
            g.setColor(Color.RED);
            rays[i].draw(g);
            g.setColor(c);
            */
            
            //shadow of each edge is a quadrilateral (close edge is polygon's back-facing edge, 
            //                                        far ege is dilation about light source of close edge,
            //                                        side edges are extensions of rays from light to close edge's vertices) 
            int[] fillXs=new int[4];
            int[] fillYs=new int[4];
            
            //get close edge
            for(int j=0;j<2;j++)
            {
               fillXs[j]=xs[(i+j)%rays.length]-LightingPanel.screenX();
               fillYs[j]=ys[(i+j)%rays.length]-LightingPanel.screenY();
            }
            //get far edge
            Ray ray=rays[(i+1)%rays.length];
            fillXs[2]=(int)(LightingPanel.VERY_FAR*ray.getXDiff()/ray.getLength())+fillXs[1];
            fillYs[2]=(int)(LightingPanel.VERY_FAR*ray.getYDiff()/ray.getLength())+fillYs[1];
            
            ray=rays[i];
            fillXs[3]=(int)(LightingPanel.VERY_FAR*ray.getXDiff()/ray.getLength())+fillXs[0];
            fillYs[3]=(int)(LightingPanel.VERY_FAR*ray.getYDiff()/ray.getLength())+fillYs[0];
            
            /*
            fillXs[2]=(int)(LightingPanel.VERY_FAR*Math.cos(rays[(i+1)%rays.length].getRotation()))+fillXs[1]-LightingPanel.screenX();
            fillYs[2]=-(int)(LightingPanel.VERY_FAR*Math.sin(rays[(i+1)%rays.length].getRotation()))+fillYs[1]-LightingPanel.screenY();
            fillXs[3]=(int)(LightingPanel.VERY_FAR*Math.cos(rays[i].getRotation()))+fillXs[0]-LightingPanel.screenX();
            fillYs[3]=-(int)(LightingPanel.VERY_FAR*Math.sin(rays[i].getRotation()))+fillYs[0]-LightingPanel.screenY();
            */
            
            //draw the shadow
            g2.fillPolygon(fillXs,fillYs,4);
         }
      }
      g2.dispose();
   }
   //pre: no element in polygons is null, (lightX,lightY) is not too close to an edge of a polygon in polygons,
   //     no two polygons in polygons are intersecting
   //post: returns an image of the shadows on the screen, of all walls
   public static BufferedImage castShadowsAll(int lightX,int lightY,double rotation)
   {
      BufferedImage shadows=new BufferedImage(LightingPanel.screenWidth(),LightingPanel.screenHeight(),BufferedImage.TYPE_INT_ARGB);
      castShadowLayerAll(shadows,lightX,lightY,Color.BLACK,rotation);
      return shadows;
   /*
      int lights=4;
      int maxRGB=(lights-1)*30>255 ? 255:(lights-1)*30;
      Color curr=new Color(maxRGB,maxRGB,maxRGB);
      for(int i=0;i<360;i+=360/lights)
      {
         castShadowLayer(shadows,lightX-(int)(Math.cos(Math.toRadians(i))*8),lightY+(int)(Math.sin(Math.toRadians(i))*8),curr);
         
         maxRGB-=30;
         if(maxRGB<0)
            maxRGB=0;
         curr=new Color(maxRGB,maxRGB,maxRGB);
      }
      */
   }
   private static void castShadowLayerAll(BufferedImage shadows,int lightX,int lightY,Color c,double rotation)
   {
      Graphics2D g2=shadows.createGraphics();
      g2.setColor(new Color(255,255,255,255));
      g2.fillRect(0,0,shadows.getWidth(),shadows.getHeight());
      
      g2.rotate((rotation-Math.PI/2),LightingPanel.screenWidth()/2,LightingPanel.screenHeight()/2);
      
      g2.setColor(c);
      
      for(Polygon polygon:LightingPanel.getPolygons())
      {
         if(!polygon.isInScreen())
            continue;
         Ray[] rays=new Ray[polygon.getNumPoints()];
         int[] xs=polygon.getXS();
         int[] ys=polygon.getYS();
         Segment[] segments=polygon.getSegments();
         
         //add all rays from light to vertices in a list
         for(int i=0;i<rays.length;i++)
            rays[i]=new Ray(lightX,lightY,xs[i],ys[i],polygon);
         
         //draw apropriate shadows on g
         for(int i=0;i<rays.length;i++)
         {
            int dotProduct=Utilities.dotProduct(rays[i].getXDiff(),rays[i].getYDiff(),segments[i].getNormalX(),segments[i].getNormalY());
            //skip drawing shadows of back facing edges
            if(dotProduct>0)
               continue;
            
            //skip shared edges with other polygons
            if(polygon.isSharedEdge(i))
               continue;
            
            /*
            g.setColor(Color.RED);
            rays[i].draw(g);
            g.setColor(c);
            */
            
            //shadow of each edge is a quadrilateral (close edge is polygon's back-facing edge, 
            //                                        far ege is dilation about light source of close edge,
            //                                        side edges are extensions of rays from light to close edge's vertices) 
            int[] fillXs=new int[4];
            int[] fillYs=new int[4];
            
            //get close edge
            for(int j=0;j<2;j++)
            {
               fillXs[j]=xs[(i+j)%rays.length]-LightingPanel.screenX();
               fillYs[j]=ys[(i+j)%rays.length]-LightingPanel.screenY();
            }
            //get far edge
            Ray ray=rays[(i+1)%rays.length];
            fillXs[2]=(int)(LightingPanel.VERY_FAR*ray.getXDiff()/ray.getLength())+fillXs[1];
            fillYs[2]=(int)(LightingPanel.VERY_FAR*ray.getYDiff()/ray.getLength())+fillYs[1];
            
            ray=rays[i];
            fillXs[3]=(int)(LightingPanel.VERY_FAR*ray.getXDiff()/ray.getLength())+fillXs[0];
            fillYs[3]=(int)(LightingPanel.VERY_FAR*ray.getYDiff()/ray.getLength())+fillYs[0];
            
            /*
            fillXs[2]=(int)(LightingPanel.VERY_FAR*Math.cos(rays[(i+1)%rays.length].getRotation()))+fillXs[1]-LightingPanel.screenX();
            fillYs[2]=-(int)(LightingPanel.VERY_FAR*Math.sin(rays[(i+1)%rays.length].getRotation()))+fillYs[1]-LightingPanel.screenY();
            fillXs[3]=(int)(LightingPanel.VERY_FAR*Math.cos(rays[i].getRotation()))+fillXs[0]-LightingPanel.screenX();
            fillYs[3]=-(int)(LightingPanel.VERY_FAR*Math.sin(rays[i].getRotation()))+fillYs[0]-LightingPanel.screenY();
            */
            
            //draw the shadow
            g2.fillPolygon(fillXs,fillYs,4);
         }
      }
      g2.dispose();
   }
   
}