import java.time.LocalDateTime;

public class MemoryItem {
	private LocalDateTime lastUsed;
	private final Integer processNum;
	private final Integer pageNum;
	
	public MemoryItem(Integer processNum, Integer pageNum) {
		lastUsed=LocalDateTime.now();
		this.processNum=processNum;
		this.pageNum=pageNum;
	}
	
	public synchronized void updateLRUTime() {
		lastUsed=LocalDateTime.now();
	}
}
