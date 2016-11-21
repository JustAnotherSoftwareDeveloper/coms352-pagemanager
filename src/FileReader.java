import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;

public class FileReader implements Runnable {
	private static final Logger log=Logger.getLogger(FileReader.class.getName());
	private Integer filenum;
	public FileReader(int i) {
		this.filenum=i;
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
			Integer pageNum=memNumber/MemoryManger.pageSize;
			Integer offset=memNumber % MemoryManger.pageSize;
			MemoryItem request=new MemoryItem(filenum,pageNum,offset);
			MemoryManger.addToQueue(request);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		reader.close();
		MemoryManger.messageFinished();
		System.out.println("Process "+filenum+" finished");
		
	}

}
