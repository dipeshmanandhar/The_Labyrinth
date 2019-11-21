import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainMenuPanel extends Screen implements ActionListener
{
   public MainMenuPanel()
   {
      super();
      setLayout(new GridLayout(2,1));
      
      JLabel title=new JLabel("The Labyrinth",JLabel.CENTER);
      title.setBackground(getBackground());
      title.setForeground(Color.WHITE);
      title.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,100));
      
      add(title);
      
      JPanel buttons=new JPanel();
      buttons.setBackground(getBackground());
      buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
      
      JButton play=new JButton("Play");
      play.addActionListener(this);
      play.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
      buttons.add(play);
      
      JButton help=new JButton("Help");
      help.addActionListener(this);
      help.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
      buttons.add(help);
      
      JButton quit=new JButton("Quit");
      quit.addActionListener(this);
      quit.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,30));
      buttons.add(quit);
      
      add(buttons);
   }
   
   @Override
   public void actionPerformed(ActionEvent e)
   {
      String id=e.getActionCommand();
      if(id.equals("Play"))
      {
         int speed;
         while(true)
         {
            try
            {
               String input=JOptionPane.showInputDialog("Enter speed to play at (1~100):");
               if(input==null)
                  return;
               else
                  speed = Integer.parseInt(input);
               break;
            }
            catch(Exception ex)
            {
               JOptionPane.showMessageDialog(null,"Enter an integer.","Input ERROR",JOptionPane.ERROR_MESSAGE);
            }
         }
         
         
         setNext(new LightingPanel(speed));
      }
      else if(id.equals("Help"))
         setNext(new HelpPanel());
      else if(id.equals("Quit"))
         setRunning(false);
   }
}