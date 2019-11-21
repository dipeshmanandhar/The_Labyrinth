//Dipesh Manandhar 12/1/17

import java.util.ArrayList;

public class myStack<anyType> implements Stackable<anyType>
{
   private ArrayList<anyType> list;
   public myStack()
   {
      list=new ArrayList<anyType>();
   }
   public myStack(anyType start)
   {
      list=new ArrayList<anyType>();
      if(start!=null)
         push(start);
   }
   public boolean isEmpty()
   {
      return list.isEmpty();
   }
   public void push(anyType x)
   {
      list.add(x);
   }
   public anyType pop()
   {
      return list.remove(list.size()-1);
   }
   public anyType peek()
   {
      return list.get(list.size()-1);
   }
   public int size()
   {
      return list.size();
   }
   public String toString()
   {
      return list.toString();
   }
}