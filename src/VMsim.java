import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VMsim {

	public static void main(String[] args) {
		if (args.length!=4) {
			System.out.println("Invalid Number of Arguments");
			return;
		}
		int pageSize,maxPages,numFrames,numFiles;
		try {
			pageSize=Integer.valueOf(args[0]);
			maxPages=Integer.valueOf(args[1]);
			numFrames=Integer.valueOf(args[2]);
			numFiles=Integer.valueOf(args[3]);
		}
		catch (NumberFormatException e) {
			System.out.println("Invalid Input");
			return;
		}
		//Init Variables
		MemoryManager.finalSize=numFiles;
		MemoryManager.finishedQueue=0;
		MemoryManager.memoryQueue=new LinkedList<>();
		MemoryManager.frameTable=new MemoryItem[numFrames];
		MemoryManager.pageSize=pageSize;
		MemoryManager.maxPages=maxPages;
		MemoryManager.QueueLock=new Object();
		MemoryManager.finishedSet=new HashSet<>();
		PageFaultHandler.finished=false;
		PageFaultHandler.faultQueue=new LinkedList<>();
		PageFaultHandler.QueueLock=new Object();
		//Start Threads
		
		new Thread(new PageFaultHandler()).start();
		new Thread(new MemoryManager()).start();
		for(int i=1; i<=numFiles; i++) {
			new Thread(new FileReader(i)).start();
		}
		Thread.yield();
		
	}

}
