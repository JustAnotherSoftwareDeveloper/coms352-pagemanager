import java.time.LocalDateTime;
/**
 * 
 * @author michael
 * This class will stimulate an Item in memory. 
 */
public class MemoryItem {
	//last time this memory item was used
	private LocalDateTime lastUsed;
	//respective values. offset isn't really needed except for printlines
	private final Integer processNum;
	private final Integer pageNum;
	private Integer offset;
	
	public MemoryItem(Integer processNum, Integer pageNum, Integer offset) {
		lastUsed=LocalDateTime.now();
		this.processNum=processNum;
		this.pageNum=pageNum;
		this.offset=offset;
	}
	
	public synchronized void updateLRUTime() {
		lastUsed=LocalDateTime.now();
		
	}
	public Integer getProcessNum() {
		return processNum;
	}
	
	public Integer getPageNum() {
		return this.pageNum;
	}
	public Integer gettAddr() {
		return MemoryManger.pageSize*this.pageNum+offset;
	}
	public Integer getOffset() {
		return this.offset;
	}
	
	public LocalDateTime getLastUsed() {
		return this.lastUsed;
	}
	/**
	 * Equals Method. Used for the containsAll() in FileReader
	 */
	@Override
	public boolean equals(Object o) {
		MemoryItem m= (MemoryItem) o;
		return (m.getProcessNum()==this.getProcessNum() && m.gettAddr() == this.gettAddr());
	}
	
	
	
}
