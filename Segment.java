//Dipesh Manandhar 4/2/18

public class Segment implements Comparable
{
   private int startX,startY,endX,endY;
   private double distanceToLight,nextDistToLight,rotation;
   private double lastIntersectionX,lastIntersectionY;
   private int normalX,normalY;
   
   public Segment(int x1,int y1,int x2,int y2)
   {
      startX=x1;
      startY=y1;
      endX=x2;
      endY=y2;
      rotation=Utilities.getRotation(getdX(),getdY());
      normalX=-getdY();
      normalY=getdX();
   }
   
   /*
   public void setDistanceToLight(double dist)
   {
      nextDistToLight=dist;
   }
   public void updateDistanceToLight()
   {
      distanceToLight=nextDistToLight;
   }
   public void updateIntersection(double interpolation)
   {
      lastIntersectionX=getdX()*interpolation+startX;
      lastIntersectionY=getdY()*interpolation+startY;
   }
   */
   
   @Override
   public boolean equals(Object other)
   {
      Segment temp=(Segment)other;
      return startX==temp.startX && startY==temp.startY && endX==temp.endX && endY==temp.endY ||
             startX==temp.endX && startY==temp.endY && endX==temp.startX && endY==temp.startY;
   }
   
   
   public int getdX()
   {
      return endX-startX;
   }
   public int getdY()
   {
      return endY-startY;
   }
   /*
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
   public double getRotation()
   {
      return rotation;
   }
   public int getIntersectionX()
   {
      return (int)lastIntersectionX;
   }
   public int getIntersectionY()
   {
      return (int)lastIntersectionY;
   }
   */
   public int getNormalX()
   {
      return normalX;
   }
   public int getNormalY()
   {
      return normalY;
   }
   
   /*
   private boolean closeEnough(double a,double b)
   {
      return Math.abs(a-b)<=1;
   }
   
   //post: returns whether or not the last intersection is an endpoint of the segment
   public boolean isAnEndPoint()
   {
      return closeEnough(lastIntersectionX,startX) && closeEnough(lastIntersectionY,startY) || closeEnough(lastIntersectionX,endX) && closeEnough(lastIntersectionY,endY);
   }
   public boolean intersectsAtEndPoint(Ray ray)
   {
      int rayX1=ray.getX1();
      int rayY1=ray.getY1();
      return closeEnough(rayX1,startX) && closeEnough(rayY1,startY) || closeEnough(rayX1,endX) && closeEnough(rayY1,endY);
   }
   
   //post: returns whether or not the last intersection is the counterclockwise endpoint of the segment relative to (x0,y0)
   public boolean isCounterClockwiseEndPoint(int x0,int y0)
   {
      if(isAnEndPoint())
      {
         double toGivenPoint=Utilities.getRotation(lastIntersectionX-x0,lastIntersectionY-y0);
         double toOtherPoint;
         if(lastIntersectionX==startX && lastIntersectionY==startY)
            toOtherPoint=Utilities.getRotation(endX-x0,endY-y0);
         else// if(x==endX && y==startY)
            toOtherPoint=Utilities.getRotation(startX-x0,startY-y0);
         
         if(toOtherPoint-toGivenPoint>Math.PI)
            toOtherPoint-=Math.PI*2;
         if(toGivenPoint-toOtherPoint>Math.PI)
            toGivenPoint-=Math.PI*2;
         return toGivenPoint>toOtherPoint;
      }
      return false;
   }
   */
   
   //based on distance to light
   @Override
   public int compareTo(Object other)
   {
      Segment temp=(Segment)other;
      
      if(distanceToLight<temp.distanceToLight)
         return -1;
      else if(distanceToLight>temp.distanceToLight)
         return 1;
      else// if(distanceToLight==temp.distanceToLight)
      {
         if(rotation<temp.rotation)
            return -1;
         else if(rotation>temp.rotation)
            return 1;
         else
            return 0;
      }
      //   return 0;
   }
}