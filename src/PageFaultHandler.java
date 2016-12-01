import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageFaultHandler implements Runnable {
	public static boolean finished;
	public static Object QueueLock;
	public static LinkedList<MemoryItem> faultQueue;
	private static final Logger log = Logger.getLogger(PageFaultHandler.class.getName());

	@Override
	public void run() {
		while (!finished || !faultQueue.isEmpty()) {
			// See Memory Manager Comment
			log.log(Level.FINEST, "");
			if (!faultQueue.isEmpty()) {

				MemoryItem neededAddr;
				synchronized (QueueLock) {
					neededAddr = faultQueue.removeFirst();
				}

				boolean nullFound = false;
				int nullIndex = 0;
				// check Null
				int numFrames = MemoryManager.frameTable.length;
				for (nullIndex = 0; nullIndex < numFrames; nullIndex++) {
					if (MemoryManager.frameTable[nullIndex] == null) {
						nullFound = true;
						break;
					}
				}
				int toSwap;
				if (nullFound) {
					System.out.println("[Process " + neededAddr.getProcessNum()
							+ "] finds free frame in memory (frame number = " + nullIndex + " )");
					toSwap = nullIndex;
				} else {
					toSwap = this.findLRUIndex();
					System.out.println("[Process " + neededAddr.getProcessNum()
							+ "] replaces a frame in memory( frame number = " + toSwap + " )");
				}

				System.out.println("[Process " + neededAddr.getProcessNum()
						+ "] issues an I/O operation  to swap in demanded page (page number = "
						+ neededAddr.getPageNum() + ")");
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				MemoryManager.frameTable[toSwap] = neededAddr;
				
				System.out.println("[Process " + neededAddr.getProcessNum() + "] demanded page (page number = "
						+ neededAddr.getPageNum() + ") has been swapped into main memory (frame number =" + toSwap
						+ ")");
				System.out.println("[Process " + neededAddr.getProcessNum() + "] access address "
						+ neededAddr.gettAddr() + "(page number = " + neededAddr.getPageNum() + ", page offset = "
						+ neededAddr.getOffset() + ") in main memory (frame number =  " + toSwap + ").");
				neededAddr.updateLRUTime();
				
			}
		}

	}

	/*
	 * Finds lowest date in table. Corresponds to the index of the LRU memory
	 */
	private int findLRUIndex() {
		LocalDateTime oldestDate = MemoryManager.frameTable[0].getLastUsed();
		int LRUIndex = 0;
		for (int i = 1; i < MemoryManager.frameTable.length; i++) {
			LocalDateTime frameLRU = MemoryManager.frameTable[i].getLastUsed();
			if (!oldestDate.isBefore(frameLRU)) {
				oldestDate = frameLRU;
				LRUIndex = i;
			}
		}
		return LRUIndex;
	}

}
