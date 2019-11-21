//Dipesh Manandhar 4/24/18

public abstract class Collidable
{
   private RigidBody rigidBody;
   
   public void setRigidBody(RigidBody rb)
   {
      rigidBody=rb;
   }
   public void setPosition(int x,int y)
   {
      rigidBody.setPosition(x,y);
   }
   /*
   public RigidBody getRigidBody()
   {
      return rigidBody;
   }
   */
   public boolean collidesWith(Collidable other)
   {
      return rigidBody.collidesWith(other.rigidBody);
   }
   public boolean isInScreen()
   {
      return Math.abs(getCenterX()-LightingPanel.screenCX())<getWidth()/2+LightingPanel.screenWidth()/2 && Math.abs(getCenterY()-LightingPanel.screenCY())<getHeight()/2+LightingPanel.screenHeight()/2;
   }
   public int getWidth()
   {
      return rigidBody.getWidth();
   }
   public int getHeight()
   {
      return rigidBody.getHeight();
   }
   
   public int getCenterX()
   {
      return rigidBody.getCenterX();
   }
   public int getCenterY()
   {
      return rigidBody.getCenterY();
   }
   
   public abstract void collisionResponse(Collidable other);
}