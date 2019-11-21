//Dipesh Manandhar 4/2/18

import java.util.Iterator;

public class Tree implements Iterable
{
   private TreeNode myRoot;
   
   public Tree()
   {
      myRoot = null;
   }
   
   //pre: root points to an in-order Binary Search Tree
   //post:adds x to the tree such that the tree is still an in-order Binary Search Tree
   
   public void add(Comparable x)
   {
      myRoot = addHelper(myRoot, x);
   }
   
   private TreeNode addHelper(TreeNode root, Comparable x)
   {
   //************COMPLETE THIS METHOD*****************************
      if(root==null)
      {
         root=new TreeNode(x);
      }
      else
      {
         int comparison=root.getValue().compareTo(x);
         if(comparison>=0)//add to left subtree
            root.setLeft(addHelper(root.getLeft(),x));
         else //if(comparison<0)//add to right subtree
            root.setRight(addHelper(root.getRight(),x));
      }
   //************************************************************           
      return root;
   }
   
   //pre: root points to an in-order Binary Search Tree
   //post:removes x from the tree such that the tree is still an in-order Binary Search Tree
   
   public void remove(Comparable x)
   {
      if(x!=null)
         myRoot = removeHelper(myRoot, x);
      else
         System.out.println("x is null in Tree.remove(x)");
   }
   
   private TreeNode removeHelper(TreeNode root, Comparable x)
   {
   //************COMPLETE THIS METHOD*****************************
      if(root!=null)
      {
         int comparison=root.getValue().compareTo(x);
         if(comparison>0)//search in left subtree
            root.setLeft(removeHelper(root.getLeft(),x));
         else if(comparison<0)//search in right subtree
            root.setRight(removeHelper(root.getRight(),x));
         else //if(comparison==0) //remove this node
         {
            //3 cases: leaf, 1 child, 2 children
            if(isLeaf(root))
            {
               root=null;
            }
            else if(oneKid(root))
            {
               if(root.getLeft()!=null)
                  root=root.getLeft();
               else //if(root.getRight()!=null)
                  root=root.getRight();
            }
            else //if(has 2 children)
            {
               //find rightmost node in left subtree
               Comparable value=null;
               for(TreeNode maxLeft=root.getLeft();maxLeft!=null;maxLeft=maxLeft.getRight())
                  value=maxLeft.getValue();
               
               //remove the rightmost node in left subtree
               root.setLeft(removeHelper(root.getLeft(),value));//////CHECK THIS LINE, value is null sometimes
               
               
               //replace the root with the rightmost node in left subtree
               root.setValue(value);
            }
         }
      }
   //************************************************************           
      return root;
   }
   
   //pre: root points to an in-order Binary Search Tree
   //post:shows the elements of the tree such that they are displayed in prefix order
   
   public void showPreOrder()
   {
      preOrderHelper(myRoot);
      System.out.println();
      
   }
   
   private void preOrderHelper(TreeNode root)
   {
   //************COMPLETE THIS METHOD*****************************
      if(root!=null)
      {
         System.out.print(root.getValue()+" ");
         preOrderHelper(root.getLeft());
         preOrderHelper(root.getRight());
      }
   //************************************************************  
   }
   
   //pre: root points to an in-order Binary Search Tree
   //post:shows the elements of the tree such that they are displayed in infix order
   
   public void showInOrder()
   {
      inOrderHelper(myRoot);
      System.out.println();
   }
   
   private void inOrderHelper(TreeNode root)   
   {
      if(root!=null)
      {
         inOrderHelper(root.getLeft());
         System.out.print(root.getValue() + " ");    
         inOrderHelper(root.getRight());
      }
   }
      
   //pre: root points to an in-order Binary Search Tree
   //post:shows the elements of the tree such that they are displayed in postfix order
   
   public void showPostOrder()
   {
      postOrderHelper(myRoot);
      System.out.println();
      
   }
   
   private void postOrderHelper(TreeNode root)
   {
   //************COMPLETE THIS METHOD*****************************
      if(root!=null)
      {
         postOrderHelper(root.getLeft());
         postOrderHelper(root.getRight());
         System.out.print(root.getValue()+" ");
      }
   //************************************************************  
   }
   
   //pre: root points to an in-order Binary Search Tree
   //post:returns whether or not x is found in the tree
   
   public boolean contains(Comparable x)
   {
      if (searchHelper(myRoot, x)==null)
         return false;
      return true;
   }
   
   public Comparable getSmallest()
   {
      if(myRoot==null)
         return null;
      else
         return smallestHelper(myRoot).getValue();
   }
   private TreeNode smallestHelper(TreeNode root)
   {
      if(root==null || root.getLeft()==null)
         return root;
      else
         return smallestHelper(root.getLeft());
   }
   public Comparable get(Comparable x)
   {
      try
      {
         return searchHelper(myRoot, x).getValue();
      }
      catch(NullPointerException e)
      {
         return null;
      }
   }
   
   private TreeNode searchHelper(TreeNode root, Comparable x)
   {
   //************COMPLETE THIS METHOD*****************************
      if(root!=null)
      {
         int comparison=root.getValue().compareTo(x);
         if(comparison>0)//search left subtree
            return searchHelper(root.getLeft(),x);
         else if(comparison<0)//search right subtree
            return searchHelper(root.getRight(),x);
         else //if(comparison==0)
            return root;
      }
   //************************************************************  
      return null;
   }
   
   //pre: root points to an in-order Binary Search Tree
   //post:returns a reference to the parent of the node that contains x, returns null if no such node exists
   //THIS WILL BE CALLED IN THE METHOD removeRecur
   private TreeNode searchParent(TreeNode root, Comparable x)
   {
   //************COMPLETE THIS METHOD*****************************
      //TO DO
      if(root!=null)
      {
         int comparison=root.getValue().compareTo(x);
         if(comparison>0)//search left subtree
         {
            TreeNode child=searchParent(root.getLeft(),x);
            if(child!=null && child.getValue().compareTo(x)==0)
               return root;
         }
         else if(comparison<0)//search right subtree
         {
            TreeNode child=searchParent(root.getRight(),x);
            if(child!=null && child.getValue().compareTo(x)==0)
               return root;
         }
         else //if(comparison==0)
            return null;
      }
   //************************************************************  
      return null;
   }
   
   //post: determines if root is a leaf or not O(1)
   private boolean isLeaf(TreeNode root)
   {
   //************COMPLETE THIS METHOD*****************************
   
   //************************************************************  
      return root.getLeft()==null && root.getRight()==null;
   }
      
   //post: returns true if only one child O(1)
   private boolean oneKid(TreeNode root)
   {
   //************COMPLETE THIS METHOD*****************************
   
   //************************************************************  
      return root.getLeft()==null ^ root.getRight()==null;
   }
      
   //pre: root points to an in-order Binary Search Tree
   //post:returns the number of nodes in the tree
   
   public int size()
   {
      return sizeHelper(myRoot);
   }
   
   private int sizeHelper(TreeNode root)
   {
   //************COMPLETE THIS METHOD*****************************
      if(root!=null)
         return 1+sizeHelper(root.getLeft())+sizeHelper(root.getRight());
   //************************************************************  
      return 0;
   }
         
   public int height()
   {
      return heightHelper(myRoot)-1;
   }

   //pre: root points to an in-order Binary Search Tree
   //post:returns the height (depth) of the tree
   
   public int heightHelper(TreeNode root)
   {
   //************COMPLETE THIS METHOD*****************************
      if(root!=null)
         return 1+Math.max(heightHelper(root.getLeft()),heightHelper(root.getRight()));
   //************************************************************  
      return 0;
   }
   
   //EXTRA CREDIT
   //pre: root points to an in-order Binary Search Tree
   //post:returns true if p is an ancestor of c, false otherwise
   
   public boolean isAncestor(TreeNode root, Comparable p, Comparable c)
   {
      if(root!=null)
      {
         int comparison=root.getValue().compareTo(p);
         if(comparison==0)
         {
            int comparison2=root.getValue().compareTo(c);
            if(comparison2>=0)
               return searchHelper(root.getLeft(),c)!=null;
            else //if(comparison2>0)
               return searchHelper(root.getRight(),c)!=null;
         }
         else if(comparison<0) //check right for parent
            return isAncestor(root.getRight(),p,c);
         else// if(comparison>0) //check left for parent
            return isAncestor(root.getLeft(),p,c);
      }
      
      return false;
   }
   
   //EXTRA CREDIT
   //pre: root points to an in-order Binary Search Tree
   //post:shows all elements of the tree at a particular depth
   
   public void printLevel(TreeNode root, int level)
   {
      if(root!=null)
      {
         if(level==0)
            System.out.print(root.getValue());
         else
         {
            printLevel(root.getLeft(),level-1);
            printLevel(root.getRight(),level-1);
         }
      }
   }
 
  //Nothing to see here...move along.
   private String temp;
   private void inOrderHelper2(TreeNode root)   
   {
      if(root!=null)
      {
         inOrderHelper2(root.getLeft());
         temp += (root.getValue() + ", "); 
         inOrderHelper2(root.getRight());
      }
   }

   public String toString()
   {
      temp="";
      inOrderHelper2(myRoot);
      if(temp.length() > 1)
         temp = temp.substring(0, temp.length()-2);
      return temp;
   }
   public boolean isEmpty()
   {
      return myRoot==null;
   }
   
   @Override
   public Iterator iterator()
   {
      Tree temp=this;
      return
         new Iterator()
         {
            Comparable prevReturned=null;
            myStack<TreeNode> toCheck=new myStack<TreeNode>(myRoot);
            
            @Override
            public boolean hasNext()
            {
               return !toCheck.isEmpty();
            }
            @Override
            public Comparable next()
            {
               if(!hasNext())
                  return null;
               //System.out.println(toCheck);
               TreeNode curr=toCheck.pop();
               TreeNode left=curr.getLeft(),right=curr.getRight();
               if(right!=null)
                  toCheck.push(right);
               if(left!=null)
                  toCheck.push(left);
               prevReturned=curr.getValue();
               return prevReturned;
            }
            @Override
            public void remove()
            {
               temp.remove(prevReturned);
            }
         };
         
   }
}