//Dipesh Manandhar 4/18/18

public class GameCharacter extends GameObject
{
   private final double MAX_SPEED;
   private double xSpeed,ySpeed;
   protected double rotation; //unit circle like
   protected static final double VIEW_ANGLE=Math.PI/2;  //total angle character can see
   protected static final int VIEW_DISTANCE=LightingPanel.getWallSize()*20;
   private boolean hasKey=false;
   
   public GameCharacter(int imageIndex,int x0,int y0,double speed)
   {
      super(imageIndex,x0,y0);
      MAX_SPEED=speed;
      //xSpeed=MAX_SPEED;
   }
   
   protected boolean isMoving()
   {
      return xSpeed!=0 || ySpeed!=0;
   }
   
   @Override
   public void update()
   {
      super.update();
      
      moveBy(xSpeed,ySpeed);
      /*
      double cos=Math.cos(rotation);
      double sin=Math.sin(rotation);
      moveBy(xSpeed*sin-ySpeed*cos,xSpeed*cos+ySpeed*sin);
      */
   }
   public void drawShadows()
   {
      Renderer.visibleArea(getCenterX(),getCenterY(),VIEW_DISTANCE,rotation,VIEW_ANGLE);
   }
   public void lookAt(int xPos,int yPos)
   {
      rotation=Utilities.getRotation(xPos-getCenterX(),yPos-getCenterY());
      //rotation=Utilities.getRotation(xPos-LightingPanel.screenWidth()/2,yPos-LightingPanel.screenHeight()/2);
   }
   
   //pre: this.collidesWith(other)==true
   @Override
   public void collisionResponse(Collidable other)
   {
      int xDiff=other.getCenterX()-getCenterX();
      int yDiff=other.getCenterY()-getCenterY();
      
      int widthSum=getWidth()/2+other.getWidth()/2;
      
      if(/*xSpeed!=0 && */Math.abs(xDiff)<widthSum)
      {
         //xSpeed=0;
         if(xDiff>0)
            xDiff=Math.abs(widthSum-xDiff)+1;
         else
            xDiff=-Math.abs(widthSum+xDiff)-1;
      }
      else
         xDiff=0;
      
      
      int heightSum=getHeight()/2+other.getHeight()/2;
      
      if(/*ySpeed!=0 && */Math.abs(yDiff)<heightSum/* && collidesWith(other)*/)
      {
         //ySpeed=0;
         if(yDiff>0)
            yDiff=Math.abs(heightSum-yDiff)+1;
         else
            yDiff=-Math.abs(heightSum+yDiff)-1;
      }
      else
         yDiff=0;
      
      double cos=Math.cos(rotation);
      double sin=Math.sin(rotation);
      
      double dx=xSpeed*sin-ySpeed*cos;
      double dy=xSpeed*cos+ySpeed*sin;
      
      if(!(dx==0 ^ dy==0))
      {
         xSpeed=0;
         ySpeed=0;
         if(Math.abs(xDiff)<Math.abs(yDiff))
            super.moveBy(-xDiff,0);
         else
            super.moveBy(0,-yDiff);
      }
      else if(dx!=0)
      {
         xSpeed=0;
         //ySpeed=0;
         super.moveBy(-xDiff,0);
      }
      else// if(dy!=0)
      {
         //xSpeed=0;
         ySpeed=0;
         super.moveBy(0,-yDiff);
      }
      
      /*
      
      if(xDiff!=0 && (yDiff==0 || Math.abs(xDiff)<Math.abs(yDiff))) //compare intersection box's width and height
      {
         xSpeed=0;
         moveBy(-xDiff,0);
      }
      else if(ySpeed!=0)
      {
         ySpeed=0;
         moveBy(0,-yDiff);
      }
      else// if(xSpeed==0 && ySpeed==0)
      {
         moveBy(-xDiff,-ySpeed);
      }
      */
   }
    
    
   @Override
   protected void moveBy(double dx,double dy)
   {
      double cos=Math.cos(rotation);
      double sin=Math.sin(rotation);
      
      super.moveBy(dx*sin-dy*cos,dx*cos+dy*sin);
   }
   
    
   protected void setXSpeed(double xS)
   {
      xSpeed=xS*MAX_SPEED;
   }
   protected void setYSpeed(double yS)
   {
      ySpeed=yS*MAX_SPEED;
   }
   public double getRotation()
   {
      return rotation;
   }
   public void setRotation(double rot)
   {
      rotation=rot;
   }
   
   public void pickUpKey()
   {
      hasKey=true;
   }
   public boolean hasKey()
   {
      return hasKey;
   }
}