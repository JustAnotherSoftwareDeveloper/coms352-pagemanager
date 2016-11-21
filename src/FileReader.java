import java.util.Scanner;

public class FileReader implements Runnable {
	private Integer filenum;
	public FileReader(int i) {
		this.filenum=i;
	}
	@Override
	public void run() {
		String filename="trace_"+filenum+".txt";
		Scanner reader;
		reader=new Scanner(filename);
		while (reader.hasNextLine()) {
			String line=reader.nextLine();
			Integer memNumber=Integer.parseInt(line);
			//Create MemoryItem Object
			Integer pageNum=memNumber/MemoryManger.pageSize;
			Integer offset=memNumber % MemoryManger.pageSize;
			MemoryItem request=new MemoryItem(filenum,pageNum,offset);
			MemoryManger.addToQueue(request);
		}
		reader.close();
		System.out.println("Process "+filenum+" finished");
		MemoryManger.messageFinished();
		
	}

}
