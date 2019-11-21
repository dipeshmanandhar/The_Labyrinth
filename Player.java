//Dipesh Manandhar 4/18/18

import java.awt.Graphics;
   

public class Player extends GameCharacter
{
   private int mouseX,mouseY;
   private boolean isSprinting=false;
   
   private double stamina=100;   //as a percentage (max is 100, min is 0)
   private static final long REGEN_TIME=Math.abs(10000*100/LightingPanel.SPEED);   //it takes 10 seconds to fully regenerate stamina
   private static final long EXPEND_TIME=Math.abs(5000*100/LightingPanel.SPEED);   //it takes 5 seconds to fully use up stamina
   private static final double STAMINA_REGEN_RATE=100*1000/60.0/REGEN_TIME; //per frame
   private static final double STAMINA_EXPEND_RATE=-(100*1000/60.0/EXPEND_TIME); //per frame
   private boolean isTired=false;
   
   public Player(int imageIndex,int x0,int y0,double speed)
   {
      super(imageIndex,x0,y0,speed);
   }
   
   private void increaseStaminaBy(double rate)
   {
      stamina+=rate;
      if(stamina>100)
         stamina=100;
      else if(stamina<0)
         stamina=0;
   }
   
   
   @Override
   public void update()
   {
      //lookAt(mouseX,mouseY);
      if(isSprinting && isMoving())
         increaseStaminaBy(STAMINA_EXPEND_RATE);
      else
         increaseStaminaBy(STAMINA_REGEN_RATE);
      
      if(stamina==0)
      {
         isSprinting=false;
         isTired=true;
      }
      else if(stamina>50)
         isTired=false;
      
      super.update();
   }
   
   @Override
   public void draw(Graphics g)
   {
      super.draw(g);
      Renderer.drawStaminaBar(g,stamina);
   }
   
   
   @Override
   protected void setImagePosition(int xPos,int yPos)
   {
      super.setImagePosition(LightingPanel.screenWidth()/2-getWidth()/2,LightingPanel.screenHeight()/2-getHeight()/2);
   }
   @Override
   public void drawShadows()
   {
      Renderer.visibleArea(getCenterX(),getCenterY(),VIEW_DISTANCE,rotation,VIEW_ANGLE);
   }
   //pre: 0<=dir<4
   public void move(int dir)
   {
      if(dir==0)
         setYSpeed(-1);
      else if(dir==1)
         setXSpeed(1);
      else if(dir==2)
         setYSpeed(1);
      else// if(dir==3)
         setXSpeed(-1);
   }
   //pre: 4<=dir<6
   public void stop(int dir)
   {
      if(dir==4)
         setYSpeed(0);
      else// if(dir==5)
         setXSpeed(0);
   }
   @Override
   public void lookAt(int xPos,int yPos)
   {
      rotation=Utilities.getRotation(xPos-LightingPanel.screenWidth()/2,yPos-LightingPanel.screenHeight()/2);
      mouseX=xPos;
      mouseY=yPos;
   }
   @Override
   protected void moveBy(double dx,double dy)
   {
      if(isSprinting)
      {
         dx*=2;
         dy*=2;
      }
      if(stamina<50)
      {
         dx/=2;
         dy/=2;
      }
      super.moveBy(dx,dy);
   }
   public void sprint()
   {
      if(!isTired)
         isSprinting=true;
   }
   public void stopSprint()
   {
      isSprinting=false;
   }
   public void rotateBy(int xDiff)
   {
      setRotation(getRotation()-xDiff/100.0*LightingPanel.SPEED/100);//TODO- make adjustable sensativity
   }
   
}