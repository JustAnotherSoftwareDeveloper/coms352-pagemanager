import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * 
 * @author michael
 * Class that will manage the memory frames.
 */
public class MemoryManager implements Runnable{

	/*
	 * Using Static Variables. I know this isn't the best practice,
	 * but it makes everything easier and I need all the help I can get.
	 * plus I'd use shared memory if I were doing this in C
	 */
	// the number of threads that have finished. Exit when this equals finalSize
	public static Integer finishedQueue;
	public static Object QueueLock;
	//Frame Table where the index corresponds to frame
	public static MemoryItem[] frameTable;
	//Final Size of the Finished Queue
	public static Integer finalSize;
	//Queue of Items to be referenced 
	public static LinkedList<MemoryItem> memoryQueue;
	public static Set<MemoryItem> finishedSet;
	//Page Size
	public static Integer pageSize;
	//Max Pages Per Process
	public static Integer maxPages;
	
	private static final Logger log=Logger.getLogger(MemoryManager.class.getName());
	@Override
	public void run() {
		/*this is basically gonna run in the background for as long as there
		* are threads still reading from their files
		*/
		while (finishedQueue < finalSize || !memoryQueue.isEmpty() ) {
			//For some reason java will not detect changes on this thread unless I put this here
			log.log(Level.FINEST,"");
			if (!memoryQueue.isEmpty()) {
				//pop Item on Queue
				MemoryItem neededAddr;
				synchronized (QueueLock) {
					neededAddr=memoryQueue.removeFirst();
					
				}
				boolean found=false;
				//Not going for best complexity 
				for(int i=0; i<frameTable.length;i++) {
					if (frameTable[i]!=null) { //nesting if statements for readability
						if (frameTable[i].getProcessNum().equals(neededAddr.getProcessNum()) && frameTable[i].getPageNum().equals(neededAddr.getPageNum()) ){
							frameTable[i]=neededAddr; //Might as well swap them
		
							found=true;
							System.out.println("[Process "+neededAddr.getProcessNum()+
									"] access address "
									+neededAddr.gettAddr()+
									"(page number = "+
										neededAddr.getPageNum()+
										", page offset = "+neededAddr.getOffset()+
										") in main memory (frame number =  "+
										i+
										").");

						}
						
					}
				}
				if (!found) {
					if (neededAddr.getPageNum()>=maxPages || neededAddr.getOffset() >= pageSize) {
						System.out.println("Invalid Address for [Process "+neededAddr.getProcessNum()+"] ("+neededAddr.gettAddr()+") and so user process terminates");
					}
					else {
						System.out.println("[Process "+neededAddr.getProcessNum()+
								"] access address "
								+neededAddr.gettAddr()+
								"(page number = "+
									neededAddr.getPageNum()+
									", page offset = "+neededAddr.getOffset()+
									") not in main memory");
						synchronized (PageFaultHandler.QueueLock) {
							PageFaultHandler.faultQueue.add(neededAddr);
						}
					}
					/*
					 * Sleeping Thread. Otherwise the page fault handler doesn't have time to really work and 
					 * page faults are registered multiple times when they really should register once. 
					 */
					
					try {
						Thread.sleep(1100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					neededAddr.updateLRUTime();
				}
				/*
				 * Adds Memory Item to the finished set. This is used
				 * to check if a process is finished or not
				 */
				MemoryManager.finishedSet.add(neededAddr);
				
			}
		
		}
		PageFaultHandler.finished=true;
		
	}
	/*
	 * Adds an Item to the Queue of items 
	 */
	public static void addToQueue(MemoryItem m) {
		synchronized (QueueLock) {
		 	memoryQueue.add(m);
		}
	}
	
	public static synchronized void messageFinished() {
		finishedQueue++;
	}

}
