//Dipesh Manandhar 4/18/18

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Enemy extends GameCharacter
{
   private BufferedImage weaponImage,visibilityImage;
   private final long ATTACK_DELAY=2000;  // attack every 2 seconds
   private final long ATTACK_TIME=500;    //each attack only lasts for half a second
   private double weaponRotation=0,weaponSpeed=Math.PI/2/60*1000/ATTACK_TIME;
   private long prevAttackTime=0;
   private boolean isAttacking=false;
   
   private int prevSeenX,prevSeenY;
   private long prevSeenTime=-1; //in milliseconds
   private final long SEARCH_TIME=10000;    //only continues searching for a lost player for 10 seconds
   
   private final long MOVE_TIME=3000;    //each random walk lasts 3 seconds
   private long moveStartTime;   //for random wandering
   
   public Enemy(int imageIndex,int weaponImageIndex,int x0,int y0,double speed)
   {
      super(imageIndex,x0,y0,speed);
      weaponImage=Renderer.getImage(weaponImageIndex);
      //pickUpKey();    //Enemies can always pass through door
   }
   
   private boolean isVisible(int xPos,int yPos)
   {
      if(visibilityImage==null)
         return false;
      else
         return (visibilityImage.getRGB(xPos-LightingPanel.screenX(),yPos-LightingPanel.screenY()) & 0xFF) ==255;
   }
   
   private void wander()
   {
      long now=System.currentTimeMillis();
      if(now-moveStartTime>MOVE_TIME*2 || now-moveStartTime<=MOVE_TIME)
      {
         //start walking again
         if(!isMoving())
         {
            setRotation(Math.random()*Math.PI*2);
            setYSpeed(-0.5);
         }
         if(now-moveStartTime>MOVE_TIME*2)
            moveStartTime=now;
      }
      else// if(now-moveStartTime>MOVE_TIME)
      {
         //stop walking now
         setYSpeed(0);
      }
   }
    
   private void lookAround()
   {
      setRotation(getRotation()+weaponSpeed/5);
   }
    
   @Override
   public void update()
   {
      super.update();
      if(isAttacking)
      {
         long now=System.currentTimeMillis();
         if(now-prevAttackTime>ATTACK_TIME)
         {
            isAttacking=false;
            weaponRotation=0;
         }
         else
            weaponRotation+=weaponSpeed;
      }
   }
   
   //pre xSpeed==0
   public void enemyAI(Player player)
   {
      long now=System.currentTimeMillis();
      if(isVisible(player.getCenterX(),player.getCenterY()))
      {
         lookAt(player.getCenterX(),player.getCenterY());
         setYSpeed(-1);
         if(Utilities.distance(getCenterX(),getCenterY(),player.getCenterX(),player.getCenterY())<weaponImage.getHeight()*5)
            attack();
         prevSeenX=player.getCenterX();
         prevSeenY=player.getCenterY();
         prevSeenTime=now;
      }
      else
      {
         if(prevSeenTime<0 || now-prevSeenTime>SEARCH_TIME)   //never even seen player yet or has been too long since seen player
            wander();
         else
         {
            //move towards last seen area of player
            
            if(Utilities.distance(getCenterX(),getCenterY(),prevSeenX,prevSeenY)<LightingPanel.getWallSize()/Math.sqrt(2)) //close to player's last spot
            {
               lookAround();
               setYSpeed(0);
            }
            else  //move closer
            {
               lookAt(prevSeenX,prevSeenY);
               setYSpeed(-1);
            }
         }
      }
   }
   public void attack()
   {
      long now=System.currentTimeMillis();
      if(!isAttacking && now-prevAttackTime>ATTACK_DELAY)
      {
         isAttacking=true;
         prevAttackTime=now;
      }
   }
   
   public void draw(Graphics g)
   {
      super.draw(g);
      Graphics2D g2=(Graphics2D)g;
      g2.rotate(-(rotation+weaponRotation-Math.PI*3/4),getCenterX(),getCenterY());
      g2.drawImage(weaponImage,getCenterX()-weaponImage.getWidth()/2,getCenterY()-weaponImage.getHeight(),null);
      g2.rotate(rotation+weaponRotation-Math.PI*3/4,getCenterX(),getCenterY());
   }
   @Override
   public void drawShadows()
   {
      visibilityImage=Renderer.lightArea(getCenterX(),getCenterY(),VIEW_DISTANCE,rotation,VIEW_ANGLE,LightingPanel.player.getRotation());
   }
   public int getWeaponLength()
   {
      return weaponImage.getHeight();
   }
   public boolean isAttacking()
   {
      return isAttacking;
   }
}