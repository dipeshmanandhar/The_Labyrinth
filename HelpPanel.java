import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HelpPanel extends Screen implements ActionListener
{
   private JPanel[] pages=new JPanel[6];
   private int pageIndex=0;
   private JButton left,right;
   
   public HelpPanel()
   {
      setLayout(new BorderLayout());
      
      JButton back=new JButton("Back to Menu");
      back.addActionListener(this);
      back.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
      add(back,BorderLayout.NORTH);
      
      left=new JButton("Previous");
      left.addActionListener(this);
      left.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
      add(left,BorderLayout.WEST);
      
      right=new JButton("Next");
      right.addActionListener(this);
      right.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
      add(right,BorderLayout.EAST);
      
      
      String[] texts=new String[pages.length];
                                //.   
      texts[0]="The aim of the game\n"+
               "is to exit the maze\n"+
               "by finding the key\n"+
               "hidden in the maze.\n"+
               "The exit will be a\n"+
               "differently colored\n"+
               "wall than the rest\n"+
               "of the walls in the\n"+
               "maze.";
                                //. 
      texts[1]="To move around the\n"+
               "world, use the\n"+
               "left, right, up and\n"+
               "down arrow keys or\n"+
               "W, A, S and D.\n"+
               "Use the mouse to\n"+
               "look around.";
                                //.
      texts[2]="To sprint, press\n"+
               "and hold the SHIFT\n"+
               "button. This will\n"+
               "allow you to run\n"+
               "faster than normal,\n"+
               "but at the cost of\n"+
               "stamina, indicated\n"+
               "by the yellow bar\n"+
               "at the bottom of\n"+
               "the screen.";
                                //.
      texts[3]="Be careful, if you\n"+
               "use up too much\n"+
               "stamina, you will\n"+
               "be tired and\n"+
               "incapable of moving\n"+
               "at normal speeds\n"+
               "anymore. This is\n"+
               "indicated by a red\n"+
               "stamina bar. If you\n"+
               "use up all of your\n"+
               "stamina, you will\n"+
               "be exhausted and no\n"+
               "longer capable of\n"+
               "sprinting until\n"+
               "your stamina bar is\n"+
               "yellow again.";
                                //.
      texts[4]="To exit the maze,\n"+
               "you will need to\n"+
               "find the key hidden\n"+
               "in the maze. Once\n"+
               "you obtain it, it\n"+
               "will be displayed\n"+
               "on the bottom of\n"+
               "the screen.";
                                //.
      texts[5]="Beware of the\n"+
               "minotaur protecting\n"+
               "the maze. If his\n"+
               "axe hits you while\n"+
               "he swings, you will\n"+
               "die and need to try\n"+
               "again. Avoid his\n"+
               "red gaze and you\n"+
               "might be able to\n"+
               "pass by him\n"+
               "unnoticed.";
                                //.
      
      for(int i=0;i<pages.length;i++)
      {
         pages[i]=new JPanel();
         pages[i].setLayout(new GridLayout(1,2));
         
         JTextArea info=new JTextArea("Page "+(i+1)+" of "+pages.length+"\n\n"+texts[i]);
         info.setLineWrap(true);
         info.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
         info.setBackground(getBackground());
         info.setForeground(Color.WHITE);
      
         pages[i].add(info);
         
         JLabel image=new JLabel(new ImageIcon("pics/Info "+(i+1)+".png"));
         image.setOpaque(true);
         image.setBackground(Color.BLACK);
         
         pages[i].add(image);
      }
      
      
      add(pages[pageIndex]);
      left.setEnabled(false);
   }
   
   @Override
   public void actionPerformed(ActionEvent e)
   {
      String id=e.getActionCommand();
      if(id.equals("Back to Menu"))
         setNext(new MainMenuPanel());
      else if(id.equals("Next"))
      {
         if(pageIndex<pages.length-1)
         {
            remove(pages[pageIndex]);
            invalidate();
            pageIndex++;
            add(pages[pageIndex]);
            revalidate();
            
            left.setEnabled(true);
            if(pageIndex==pages.length-1)
               right.setEnabled(false);
         }
      }
      else if(id.equals("Previous"))
      {
         if(pageIndex>0)
         {
            remove(pages[pageIndex]);
            invalidate();
            pageIndex--;
            add(pages[pageIndex]);
            revalidate();
            
            right.setEnabled(true);
            if(pageIndex==0)
               left.setEnabled(false);
         }
      }
   }
}