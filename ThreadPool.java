import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ThreadPool implements RejectedExecutionHandler
{
   ThreadPoolExecutor executor;
   public ThreadPool()
   {
      int numCores=Runtime.getRuntime().availableProcessors();
      executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(numCores*2);
      executor.setRejectedExecutionHandler(this);
   }
   public ThreadPool(int numCores)
   {
      //int numCores=Runtime.getRuntime().availableProcessors();
      executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(numCores*2);
      executor.setRejectedExecutionHandler(this);
   }
   public void addTask(Runnable task)
   {
      executor.execute(task);
   }
   public void addTasks(Runnable[] tasks)
   {
      for(Runnable task:tasks)
         executor.execute(task);
   }
   public void close()
   {
      executor.shutdown();
      while(!executor.isTerminated())
      {
         Thread.yield();
      }
   }
   @Override
   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
   {
      System.out.println(r + " is rejected");
   }
}