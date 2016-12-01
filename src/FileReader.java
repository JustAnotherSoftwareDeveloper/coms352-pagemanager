import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReader implements Runnable {
	private static final Logger log=Logger.getLogger(FileReader.class.getName());
	private Integer filenum;
	private Set<MemoryItem> requests;
	public FileReader(int i) {
		this.filenum=i;
		requests=new HashSet<>();
	}
	@Override
	public void run() {
		String filename="trace_"+filenum+".txt";
		Scanner reader=null;
		try {
			reader=new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (reader.hasNextInt()) {
			Integer memNumber=reader.nextInt();
			//Create MemoryItem Object
			Integer pageNum=memNumber/MemoryManager.pageSize;
			Integer offset=memNumber % MemoryManager.pageSize;
			MemoryItem request=new MemoryItem(filenum-1,pageNum,offset);
			MemoryManager.addToQueue(request);
			this.requests.add(request);
			/*
			 * If Request is invalid then no more memory items will be read.
			 */
			if (pageNum>=MemoryManager.maxPages || offset >= MemoryManager.pageSize) {
				reader.close();
				MemoryManager.messageFinished();
				return;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		reader.close();
		while (!MemoryManager.finishedSet.containsAll(requests)) {
			log.log(Level.FINEST, "");
			
		}
		MemoryManager.messageFinished();
		System.out.println("[Process "+(filenum-1)+"] ends");
		
	}

}
