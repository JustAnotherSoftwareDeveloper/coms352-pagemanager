import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageFaultHandler implements Runnable {
	public static boolean finished;
	public static Object QueueLock;
	public static LinkedList<MemoryItem> faultQueue;
	private static final Logger log=Logger.getLogger(PageFaultHandler.class.getName());

	@Override
	public void run() {
		while (!finished || !faultQueue.isEmpty()) {
			//See Memory Manager Comment
			log.log(Level.FINEST, "");
			if (!faultQueue.isEmpty()) {
				
				MemoryItem neededAddr;
				synchronized (QueueLock) {
					neededAddr = faultQueue.removeFirst();
				}

				boolean nullFound = false;
				int nullIndex = 0;
				// check Null
				int numFrames = MemoryManger.frameTable.length;
				for (nullIndex = 0; nullIndex < numFrames; nullIndex++) {
					if (MemoryManger.frameTable[nullIndex] == null) {
						nullFound = true;
						break;
					}
				}
				if (nullFound) {
					System.out.println("Process " + neededAddr.getProcessNum()
							+ " finds free frame in memory (frame number = " + nullIndex + " )");
					MemoryManger.frameTable[nullIndex] = neededAddr;
				} else {
					int toSwap = this.findLRUIndex();
					System.out.println("Process " + neededAddr.getProcessNum()
							+ " replaces a frame in memory( frame number = " + toSwap + " )");
					System.out.println("Process " + neededAddr.getProcessNum()
							+ " issues an I/O operation  to swap in demanded page (page number = "
							+ neededAddr.getPageNum() + ")");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					MemoryManger.frameTable[toSwap] = neededAddr;
					System.out.println("Process "+neededAddr.getProcessNum() + " access address " + neededAddr.gettAddr()
							+ "(page number = " + neededAddr.getPageNum() + ", page offset = " + neededAddr.getOffset()
							+ ") in main memory (frame number =  " + toSwap + ").");
				}
			}
		}

	}

	/*
	 * Finds lowest date in table. Corresponds to the index of the LRU memory
	 */
	private int findLRUIndex() {
		LocalDateTime oldestDate = MemoryManger.frameTable[0].getLastUsed();
		int LRUIndex = 0;
		for (int i = 1; i < MemoryManger.frameTable.length; i++) {
			if (MemoryManger.frameTable[i] != null) { // Shouldn't be an issue
				LocalDateTime frameLRU = MemoryManger.frameTable[i].getLastUsed();
				if (oldestDate.isAfter(frameLRU)) {
					oldestDate = frameLRU;
					LRUIndex = i;
				}
			}
		}
		return LRUIndex;
	}

}
