//Dipesh Manandhar 4/1/18

import java.awt.Graphics;
import java.awt.Color;

public class Polygon extends Collidable
{
   private int numPoints;
   private int[] xs,ys;
   private Segment[] edges;
   private static Color color=new Color((int)(Math.random()*(1<<6))<<2,(int)(Math.random()*(1<<6))<<2,(int)(Math.random()*(1<<6))<<2),
                        inverse=new Color(255-color.getRed(),255-color.getGreen(),255-color.getBlue());
   private boolean[] sharedSegments;
   private boolean isDoor=false;
   
   
   //pre: x.length==y.length, points are given in counterclockwise order
   public Polygon(int[] x,int[] y)
   {
      numPoints=x.length;
      xs=x;
      ys=y;
      edges=new Segment[numPoints];
      sharedSegments=new boolean[numPoints];
      for(int i=0;i<numPoints;i++)
         edges[i]=new Segment(xs[i],ys[i],xs[(i+1)%numPoints],ys[(i+1)%numPoints]);
      
      setRigidBody(new RigidBody(this));
   }
   
   public void findSharedEdges(Polygon other)
   {
      for(int i=0;i<edges.length;i++)
      {
         Segment mySeg=edges[i];
         for(int j=0;j<other.edges.length;j++)
         {
            Segment otherSeg=other.edges[j];
            if(mySeg.equals(otherSeg))
            {
               sharedSegments[i]=true;
               other.sharedSegments[j]=true;
            }
         }
      }
   }
   
   public boolean isSharedEdge(int index)
   {
      return sharedSegments[index];
   }
   
   public void draw(Graphics g)
   {
      if(isDoor)
         g.setColor(inverse);
      else
         g.setColor(color);
      
      g.fillPolygon(xs,ys,numPoints);
      
      g.setColor(Color.WHITE);
      g.drawPolygon(xs,ys,numPoints);
   }
   
   @Override
   public void collisionResponse(Collidable other)
   {
      
   }
   
   public int getNumPoints()
   {
      return numPoints;
   }
   public int[] getXS()
   {
      return xs;
   }
   public int[] getYS()
   {
      return ys;
   }
   public Segment[] getSegments()
   {
      return edges;
   }
   public void setIsDoor(boolean iD)
   {
      isDoor=iD;
   }
   public boolean isDoor()
   {
      return isDoor;
   }
}