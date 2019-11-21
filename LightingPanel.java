//Dipesh Manandhar 3/31/18

import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;

import java.awt.Robot;
import java.awt.Point;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import java.util.List;
import java.util.ArrayList;

public class LightingPanel extends Screen implements MouseMotionListener
{
   //Static constants
   public static final int VERY_FAR=10000;
   
   private static final String[][] map=new String[10*2+1][10*2+1];
   private static final Polygon[][] objectMap=new Polygon[map.length][map[0].length];
   
   private static final int WALLS_BETWEEN=5,
                            WALL_SIZE=20,
                            mazeCenterX=toPixelSpace(objectMap[0].length/2)+WALL_SIZE*WALLS_BETWEEN/2+WALL_SIZE/2,
                            mazeCenterY=toPixelSpace(objectMap.length/2)+WALL_SIZE*WALLS_BETWEEN/2+WALL_SIZE/2;
                            
   private static final String MOVE_UP="move up",
                               MOVE_DOWN="move down",
                               MOVE_LEFT="move left",
                               MOVE_RIGHT="move right",
                               STOP_VERTICAL="stop vertical",
                               STOP_HORIZONTAL="stop horizontal",
                               SPRINT="run",
                               STOP_SPRINT="stop sprint",
                               SPACE_UP="space up",
                               SPACE_DOWN="space down",
                               RESET_LEVEL="reset level",
                               BACK_TO_MAIN_MENU="main menu",
                               EXIT="exit";
   
   
   //Static variables
   private static int screenWidth,screenHeight,screenX,screenY;
   protected static Player player;
   protected static int SPEED;
   
      
   //Instance variables
   private boolean won=false,lost=false,timeSlowed=false;
   private Robot robot=null;
   
   private static Polygon[] polygons;//=new Polygon[4];
   //private int lightX=-1,lightY;
   
   
   private int playerX0,
               playerY0,
               playerWins=0,
               minoWins=0;
      
   
   private Enemy enemy;
   private GameObject key;
   
   
   //Constructors
   public LightingPanel(double speed)
   {
      //setBackground(Color.WHITE);
      //setBackground(Color.GRAY.darker());
      setBackground(new Color(100,100,100));
      addMouseMotionListener(this);
      setKeyBindings();
      
      Renderer.initialize();
      
      
      initialize(map);
      setUp(map);
      int playerPos=placeObjects(map);
      
      int r=playerPos/map[0].length;
      int c=playerPos%map[0].length;
      
      playerY0=toPixelSpace(r)+WALL_SIZE*WALLS_BETWEEN/2+WALL_SIZE/2;
      playerX0=toPixelSpace(c)+WALL_SIZE*WALLS_BETWEEN/2+WALL_SIZE/2;
      
      SPEED=(int)speed;
      speed*=WALL_SIZE/10.0/100;
      
      player=new Player(Renderer.PLAYER,playerX0,playerY0,speed);
      enemy=new Enemy(Renderer.ENEMY,Renderer.AXE,mazeCenterX,mazeCenterY,speed);
      key=new GameObject(Renderer.KEY,mazeCenterX,mazeCenterY);
      
      player.setRotation(Math.PI/2);
      
      
      setCursor(getToolkit().createCustomCursor(
                new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
                new Point(),
                null));
   }
   
   //Static methods
   public static int screenWidth()
   {
      return screenWidth;
   }
   public static int screenHeight()
   {
      return screenHeight;
   }
   public static int screenX()
   {
      return screenX;
   }
   public static int screenY()
   {
      return screenY;
   }
   //post: returns the x-position of the center of the screen
   public static int screenCX()
   {
      return screenX+screenWidth/2;
   }
   //post: returns the y-position of the center of the screen
   public static int screenCY()
   {
      return screenY+screenHeight/2;
   }
   
   public static Polygon[] getPolygons()
   {
      return polygons;
   }
   public static int getWallSize()
   {
      return WALL_SIZE;
   }
   
   //post: returns the board position of pixels (an index of map[][])
   public static int toBoardSpace(double pixels)
   {
      return (int)Math.floor((pixels)/(WALL_SIZE*(WALLS_BETWEEN+1)))*2+1;
   }
   //post: returns the top left corner of the topleft wall in the row/col cell denoted by n
   public static int toPixelSpace(int n)
   {
      return (n/*-1*/)/2*WALL_SIZE*(WALLS_BETWEEN+1);//+60;
   }
   
   //Instance Methods
   //Helpers
   
   //post: prepares all polygons to make future calculations easier
   private void initialize()
   {
      for(int i=0;i<polygons.length;i++)
      {
         Polygon poly1=polygons[i];
         for(int j=i+1;j<polygons.length;j++)
         {
            Polygon poly2=polygons[j];
            poly1.findSharedEdges(poly2);
         }
      }
   }
   
   private void gimp(String key,String name)
   {
      getInputMap().put(KeyStroke.getKeyStroke(key),name);
      if(!key.contains("shift"))
         getInputMap().put(KeyStroke.getKeyStroke("shift "+key),name);
   }
   private void gamp(String name,AbstractAction action)
   {
      getActionMap().put(name,action);
   }
   private void setKeyBindings()
   {
      gimp("UP",MOVE_UP);
      gimp("W",MOVE_UP);
      gimp("DOWN",MOVE_DOWN);
      gimp("S",MOVE_DOWN);
      gimp("LEFT",MOVE_LEFT);
      gimp("A",MOVE_LEFT);
      gimp("RIGHT",MOVE_RIGHT);
      gimp("D",MOVE_RIGHT);
      gimp("shift pressed SHIFT",SPRINT);
      gimp("SPACE",SPACE_DOWN);
      
      gimp("released UP",STOP_VERTICAL);
      gimp("released W",STOP_VERTICAL);
      gimp("released DOWN",STOP_VERTICAL);
      gimp("released S",STOP_VERTICAL);
      gimp("released LEFT",STOP_HORIZONTAL);
      gimp("released A",STOP_HORIZONTAL);
      gimp("released RIGHT",STOP_HORIZONTAL);
      gimp("released D",STOP_HORIZONTAL);
      gimp("released SHIFT",STOP_SPRINT);
      gimp("released SPACE",SPACE_UP);
      
      gimp("ESCAPE",EXIT);
      gimp("R",RESET_LEVEL);
      gimp("M",BACK_TO_MAIN_MENU);
      
      
         
      gamp(MOVE_UP,new MoveAction(0));
      gamp(MOVE_RIGHT,new MoveAction(1));
      gamp(MOVE_DOWN,new MoveAction(2));
      gamp(MOVE_LEFT,new MoveAction(3));
      gamp(SPRINT,new MoveAction(6));
         
      gamp(STOP_VERTICAL,new MoveAction(4));
      gamp(STOP_HORIZONTAL,new MoveAction(5));
      gamp(STOP_SPRINT,new MoveAction(7));
      
      gamp(EXIT,new MoveAction(-1));
      gamp(RESET_LEVEL,new MoveAction(-2));
      gamp(BACK_TO_MAIN_MENU,new MoveAction(-3));
      
      gamp(SPACE_DOWN,new MoveAction(8));
      gamp(SPACE_UP,new MoveAction(9));
   }
   
   private int positionX(Graphics g,String text,double mult)
   {
      FontMetrics fm = g.getFontMetrics();
      return (int)(getWidth()*mult - fm.stringWidth(text)*0.5);
      //int centerY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
   }
   private void moveMouse(int mouseX,int mouseY)
   {
      if(robot==null)
         try
         {
            robot=new Robot();
         }
         catch(Exception e)
         {
            e.printStackTrace();
         }
      
      robot.mouseMove(mouseX, mouseY);
   }
   
   //pre: gc!=null
   //post: handles all collisions with gc
   private void collisionHelper(GameCharacter gc)
   {
      int gcC=toBoardSpace(gc.getCenterX());
      int gcR=toBoardSpace(gc.getCenterY());
      for(int r=gcR-1;r<=gcR+1 && r<objectMap.length;r++)
      {
         if(r<0)
            continue;
         for(int c=gcC-1;c<=gcC+1 && c<objectMap[0].length;c++)
         {
            if(c<0)
               continue;
            Polygon polygon=objectMap[r][c];
            if(polygon!=null && !(polygon.isDoor() && gc.hasKey()) && gc.collidesWith(polygon))
               gc.collisionResponse(polygon);
         }
      }
   }
   
   private void resetLevel()
   {
      lost=false;
      setPaused(false);
      double speed=SPEED*WALL_SIZE/10.0/100;
      
      player=new Player(Renderer.PLAYER,playerX0,playerY0,speed);
      enemy=new Enemy(Renderer.ENEMY,Renderer.AXE,mazeCenterX,mazeCenterY,speed);
      key=new GameObject(Renderer.KEY,mazeCenterX,mazeCenterY);
      
      player.setRotation(Math.PI/2);
   }
   private void nextLevel()
   {
      won=false;
      initialize(map);
      setUp(map);
      int playerPos=placeObjects(map);
      
      int r=playerPos/map[0].length;
      int c=playerPos%map[0].length;
      
      playerY0=toPixelSpace(r)+WALL_SIZE*WALLS_BETWEEN/2+WALL_SIZE/2;
      playerX0=toPixelSpace(c)+WALL_SIZE*WALLS_BETWEEN/2+WALL_SIZE/2;
      
      resetLevel();
   }
   
   /////////////////////////// START MAZE METHODS //////////////////////////////////////
   private void setUp(String[][] board)
   {
      int r,c;
      List<Integer> wallRows=new ArrayList<Integer>(0);
      List<Integer> wallCols=new ArrayList<Integer>(0);
      if(Math.random()<0.5)
      {
         r=1;
         c=(int)(Math.random()*(board[0].length-1)/2)*2+1;
         
         board[board.length-1][board[0].length-1-c]=" DOOR ";
      }
      else
      {
         r=(int)(Math.random()*(board.length-1)/2)*2+1;
         c=1;
         
         board[board.length-1-r][board[0].length-1]="DOOR";
      }
      board[r][c]=" A ";
      if(r>1 && board[r-1][c].equals("---"))
      {
         wallRows.add(r-1);
         wallCols.add(c);
      }
      if(r<board.length-2 && board[r+1][c].equals("---"))
      {
         wallRows.add(r+1);
         wallCols.add(c);
      }
      if(c>1 && board[r][c-1].equals("|"))
      {
         wallRows.add(r);
         wallCols.add(c-1);
      }
      if(c<board[0].length-2 && board[r][c+1].equals("|"))
      {
         wallRows.add(r);
         wallCols.add(c+1);
      }
      while(wallRows.size()!=0)
      {
         int count=0;
         int index=(int)(Math.random()*wallRows.size());
         r=wallRows.get(index);
         c=wallCols.get(index);
         if(board[r][c].equals("---"))
         {
            if(board[r-1][c].equals("   "))
               count++;
            if(board[r+1][c].equals("   "))
               count++;
         }
         else if(board[r][c].equals("|"))
         {
            if(board[r][c-1].equals("   "))
               count++;
            if(board[r][c+1].equals("   "))
               count++;
         }
         if(count==1)
         {
            if(board[r][c].equals("---"))
            {
               board[r][c]="   ";
               if(board[r-1][c].equals("   "))
                  r--;
               else
                  r++;
            }
            else if(board[r][c].equals("|"))
            {
               board[r][c]=" ";
               if(board[r][c-1].equals("   "))
                  c--;
               else
                  c++;
            }
            if(r>1 && board[r-1][c].equals("---"))
            {
               wallRows.add(r-1);
               wallCols.add(c);
            }
            if(r<board.length-2 && board[r+1][c].equals("---"))
            {
               wallRows.add(r+1);
               wallCols.add(c);
            }
            if(c>1 && board[r][c-1].equals("|"))
            {
               wallRows.add(r);
               wallCols.add(c-1);
            }
            if(c<board[0].length-2 && board[r][c+1].equals("|"))
            {
               wallRows.add(r);
               wallCols.add(c+1);
            }
            board[r][c]=" 1 ";
         }
         wallRows.remove(index);
         wallCols.remove(index);
      }
      for(int row=0;row<board.length;row++)
         for(int col=0;col<board[0].length;col++)
            if(board[row][col].equals(" 1 "))
               board[row][col]="   ";
      
      
      r=getRow(board," A ");
      c=getCol(board," A ");
      board[r][c]=" o ";
      board[board.length-r-1][board[0].length-c-1]="END";
      
   }
   private int getRow(String[][] board,String search)
   {
      for(int r=0;r<board.length;r++)
         for(int c=0;c<board[0].length;c++)
            if(board[r][c].equals(search))
               return r;
      return -1;
   }
   private int getCol(String[][] board,String search)
   {
      for(int r=0;r<board.length;r++)
         for(int c=0;c<board[0].length;c++)
            if(board[r][c].equals(search))
               return c;
      return -1;
   }
   private void swap(String[][] board,int r1,int c1,int r2,int c2)
   {
      String temp=board[r1][c1];
      board[r1][c1]=board[r2][c2];
      board[r2][c2]=temp;
   }
   private void initialize(String[][] board)
   {
      for(int r=0;r<board.length;r++)
      {
         for(int c=0;c<board[0].length;c++)
         {
            if(r%2==0)
            {
               if(c%2==0)
                  board[r][c]="+";
               else
                  board[r][c]="---";
            }
            else
            {
               if(c%2==0)
                  board[r][c]="|";
               else
                  board[r][c]="   ";
            }
         }
      }
   }
   //pre: board.length>=(offset*2)*2+1 & board[0].length>=(offset*2)*2+1
   private int placeObjects(String[][] board)
   {
      int playerPos=-1;
      
      List<Polygon> walls=new ArrayList<Polygon>();
      for(int r=0;r<board.length;r++)
      {
         for(int c=0;c<board[0].length;c++)
         {
            //top left x,y
            int x=c*WALL_SIZE*(WALLS_BETWEEN+1)/2,y=r*WALL_SIZE*(WALLS_BETWEEN+1)/2;
            
            Polygon toAdd=null;
            
            if(board[r][c].equals("+"))
               toAdd=new Polygon(new int[]{x,x,x+WALL_SIZE,x+WALL_SIZE},new int[]{y,y+WALL_SIZE,y+WALL_SIZE,y});
            else if(board[r][c].equals("---"))
            {
               x=(int)(x-WALL_SIZE*(WALLS_BETWEEN+1)/2.0+WALL_SIZE);
               toAdd=new Polygon(new int[]{x,x,x+WALL_SIZE*WALLS_BETWEEN,x+WALL_SIZE*WALLS_BETWEEN},new int[]{y,y+WALL_SIZE,y+WALL_SIZE,y});
            }
            else if(board[r][c].equals("|"))
            {
               y=(int)(y-WALL_SIZE*(WALLS_BETWEEN+1)/2.0+WALL_SIZE);
               toAdd=new Polygon(new int[]{x,x,x+WALL_SIZE,x+WALL_SIZE},new int[]{y,y+WALL_SIZE*WALLS_BETWEEN,y+WALL_SIZE*WALLS_BETWEEN,y});
            }
            else if(board[r][c].equals(" o "))
            {
               //board[board.length-r-1][board[0].length-c-1]="END";
               //p=new Player(c*WALL_SIZE*(WALLS_BETWEEN+1)/2.0,r*WALL_SIZE*(WALLS_BETWEEN+1)/2.0);
               
               //lightX=(int)((c+0.5)*WALL_SIZE*(WALLS_BETWEEN+1)/2);
               //lightY=(int)((r+0.5)*WALL_SIZE*(WALLS_BETWEEN+1)/2);
            }
            else if(board[r][c].equals("END"))
            {
               playerPos=r*board[0].length+c;
            }
            else if(board[r][c].equals(" DOOR "))
            {
               x=(int)(x-WALL_SIZE*(WALLS_BETWEEN+1)/2.0+WALL_SIZE);
               toAdd=new Polygon(new int[]{x,x,x+WALL_SIZE*WALLS_BETWEEN,x+WALL_SIZE*WALLS_BETWEEN},new int[]{y,y+WALL_SIZE,y+WALL_SIZE,y});
               toAdd.setIsDoor(true);
            }
            else if(board[r][c].equals("DOOR"))
            {
               y=(int)(y-WALL_SIZE*(WALLS_BETWEEN+1)/2.0+WALL_SIZE);
               toAdd=new Polygon(new int[]{x,x,x+WALL_SIZE,x+WALL_SIZE},new int[]{y,y+WALL_SIZE*WALLS_BETWEEN,y+WALL_SIZE*WALLS_BETWEEN,y});
               toAdd.setIsDoor(true);
            }
            if(toAdd!=null)
               walls.add(toAdd);
            objectMap[r][c]=toAdd;
            
            /*
            if(board[r][c].equals("+"))
               walls.add(new Polygon(new int[]{x,x,x+WALL_SIZE,x+WALL_SIZE},new int[]{y,y+WALL_SIZE,y+WALL_SIZE,y}));
            else if(board[r][c].equals("---"))
            {
               x=(int)(x-WALL_SIZE*(WALLS_BETWEEN+1)/2.0+WALL_SIZE);
               walls.add(new Polygon(new int[]{x,x,x+WALL_SIZE*WALLS_BETWEEN,x+WALL_SIZE*WALLS_BETWEEN},new int[]{y,y+WALL_SIZE,y+WALL_SIZE,y}));
            }
            else if(board[r][c].equals("|"))
            {
               y=(int)(y-WALL_SIZE*(WALLS_BETWEEN+1)/2.0+WALL_SIZE);
               walls.add(new Polygon(new int[]{x,x,x+WALL_SIZE,x+WALL_SIZE},new int[]{y,y+WALL_SIZE*WALLS_BETWEEN,y+WALL_SIZE*WALLS_BETWEEN,y}));
            }
            else if(board[r][c].equals(" o "))
            {
               //board[board.length-r-1][board[0].length-c-1]="END";
               //p=new Player(c*WALL_SIZE*(WALLS_BETWEEN+1)/2.0,r*WALL_SIZE*(WALLS_BETWEEN+1)/2.0);
               
               //lightX=(int)((c+0.5)*WALL_SIZE*(WALLS_BETWEEN+1)/2);
               //lightY=(int)((r+0.5)*WALL_SIZE*(WALLS_BETWEEN+1)/2);
            }
            */
         }
      }
      polygons=walls.toArray(new Polygon[walls.size()]);
      initialize();
      /*
      for(int i=0;i<INITIAL_ENEMIES;i++)
      {
         Enemy temp;
         do
         {
            temp=new Enemy(((int)(Math.random()*(board[0].length-1)/2)*2+1)*WALL_SIZE*(WALLS_BETWEEN+1)/2.0,((int)(Math.random()*(board.length-1)/2)*2+1)*WALL_SIZE*(WALLS_BETWEEN+1)/2.0);
         }
         while(temp.checkCollision(p));
         enemies.add(temp);
         
         //System.out.println("enemy "+i+" created");
      }
      */
      
      return playerPos;
   }
   /////////////////////////// END MAZE METHODS //////////////////////////////////////
   
   
   
   //Mutators
   public void update()
   {
      //if(lightX<0)
      //   initialize();
      
      //screenWidth=getWidth();
      //screenHeight=getHeight();
      player.update();
      enemy.enemyAI(player);
      enemy.update();
      
      handleCollisions();
      
      int r=toBoardSpace(player.getCenterY());
      int c=toBoardSpace(player.getCenterX());
      if(!won && !lost && (r<0 || r>=map.length || c<0 || c>=map[0].length))
      {
         won=true;
         playerWins++;
      }
   }
   
   public void handleCollisions()
   {
      if(key!=null && player.collidesWith(key))
      {
         player.pickUpKey();
         key=null;
      }
      
      
      /*
      //This snippet is too slow, only need to collision detect for walls nearby to the player
      
      for(Polygon polygon:polygons)
         if(polygon.isDoor() && player.hasKey())
            continue;
         else if(player.collidesWith(polygon))
            player.collisionResponse(polygon);
      */
      
      collisionHelper(player);
      collisionHelper(enemy);
      
      if(player.collidesWith(enemy))
         enemy.collisionResponse(player);
      if(!won && !lost && enemy.isAttacking() && Utilities.distance(player,enemy)<enemy.getWeaponLength())
      {
         lost=true;
         //won=false;
         setPaused(true);
         minoWins++;
      }
   }
   
   public void render(double interpolation)
   {
      repaint();
   }
   
   @Override
   public void drawScene(Graphics g)
   {
      super.drawScene(g);
      
      screenWidth=getWidth();
      screenHeight=getHeight();
      
      
      //int dx=screenWidth/2-(player.getCenterX()/*-playerX0*/),dy=screenHeight/2-(player.getCenterY()/*-playerY0*/);
      screenX=player.getCenterX()-screenWidth/2;
      screenY=player.getCenterY()-screenHeight/2;
      
      Graphics2D g2=(Graphics2D)g;
      
      g2.rotate((player.getRotation()-Math.PI/2),screenWidth/2,screenHeight/2);
      //g.translate(dx,dy);
      g.translate(-screenX,-screenY);
      
      
      //g.setColor(Color.BLUE.darker());
      for(int i=0;i<polygons.length;i++)
         if(polygons[i].isInScreen())
            polygons[i].draw(g);
      //drawVisibilityPolygon(g);
      enemy.draw(g);
      
      if(key!=null)
         key.draw(g);
      //g.drawImage(Renderer.getImage(2),mazeCenterX,mazeCenterY,null);
      
      /*
      g.setColor(Color.BLACK);
      g.drawLine(0,screenHeight/2,screenWidth,screenHeight/2);
      g.drawLine(screenWidth/2,0,screenWidth/2,screenHeight);
      */
      
      //g.translate(-dx,-dy);
      g.translate(screenX,screenY);
      g2.rotate(-(player.getRotation()-Math.PI/2),screenWidth/2,screenHeight/2);
      
      //player.drawShadows(g,-dx,-dy);
      
      //draw all shadows to Renderer
      player.drawShadows();
      enemy.drawShadows();
      
      //draw lights and shadows stored in Renderer
      Renderer.drawLighting(g);
      
      //g2.rotate((player.getRotation()-Math.PI/2),screenWidth/2,screenHeight/2);
      
      //draw the player on top of everything else
      player.draw(g);
      
      //g2.rotate(-(player.getRotation()-Math.PI/2),screenWidth/2,screenHeight/2);
      
      if(player.hasKey())
         g.drawImage(Renderer.getImage(Renderer.KEY),getWidth()/2,getHeight()-200,null);
      
   
      
   }
   
   
   @Override
   public void drawUI(Graphics g)
   {
      super.drawUI(g);
      
      //draw won string if player won
      if(won)
      {
         g.setColor(Color.GREEN.darker());
         g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,75));
         String text="YOU WIN";
         g.drawString(text,positionX(g,text,0.5),80);
         
         g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
         text="(Press spacebar to continue)";
         g.drawString(text,positionX(g,text,0.5),85+40);
      }
      else if(lost)
      {
         g.setColor(Color.BLUE.darker());
         g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,75));
         String text="YOU LOSE";
         g.drawString(text,positionX(g,text,0.5),80);
         
         g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
         text="(Press spacebar or 'R' to try again)";
         g.drawString(text,positionX(g,text,0.5),85+40);
      }
      
      
      g.setColor(Color.WHITE);
      g.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
      
      //mino wins and player wins
      
      //top right
      g.drawString("Player Wins: "+playerWins,getWidth()*3/4,50);
      g.drawString("Minotaur Wins: "+minoWins,getWidth()*3/4,100);
   }
    
    //Mouse Events
   @Override
   public void mouseMoved(MouseEvent e)
   {
      //lightX=e.getX();
      //lightY=e.getY();
      
      int xDiff=e.getX()-screenWidth/2;
      int yDiff=e.getY()-screenHeight/2;
      player.rotateBy(xDiff);
      
      Point point=getLocationOnScreen();
      moveMouse((int)(screenWidth/2+point.getX()),(int)(screenHeight/2+point.getY()));
      
      //player.lookAt(e.getX(),e.getY());
   }
   @Override
   public void mouseDragged(MouseEvent e)
   {
      //lightX=e.getX();
      //lightY=e.getY();
      mouseMoved(e);
   }
   
   
   
   //Keyboard events
   private class MoveAction extends AbstractAction
   {
      private int direction;
      private MoveAction(int dir)
      {
         direction=dir;
      }
      @Override
      public void actionPerformed(ActionEvent e)
      {
         if(direction==-1)
            setRunning(false);
         else if(direction==-2)  //reset level
            resetLevel();
         else if(direction==-3)  //back to main menu
            setNext(new MainMenuPanel());
         else if(direction<4)
         {
            player.move(direction);
            //System.out.println("move pressed");
         }
         else if(direction<6)
            player.stop(direction);
         else if(direction==6)
         {
            player.sprint();
            //System.out.println("Shift pressed");
         }
         else if(direction==7)
         {
            player.stopSprint();
            //System.out.println("Shift released");
         }
         else if(direction==8)   //space bar down
         {
            if(won)
               nextLevel();
            else if(lost)
               resetLevel();
            else
               timeSlowed=true;
         }
         else if(direction==9)   //space bar up
            timeSlowed=false;//TODO implement 
      }
   }
}