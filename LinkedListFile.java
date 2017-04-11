import java.util.LinkedList;

public class LinkedListFile implements File{

	private static int MAX_PARTITION_SIZE = 100;
	private int size; // Size of the file in bytes
    private String name; // Name of the file
    private LinkedList<Long> block_pos;    
    private static MemoryManager mm;
    private static final int HEADER_SIZE = 4;

    public LinkedListFile(String name){
    	this.name = name;
    	block_pos = new LinkedList<Long>();
        this.size = MAX_PARTITION_SIZE;
    	if(mm == null) {
            mm = new MemoryManager();
    	}
    	allocate_block(this.size);
    }

    public LinkedListFile(String name, int size,long startingPosition){
        this.size = size;
        this.name = name;
        block_pos = new LinkedList<Long>();
        block_pos.add(startingPosition);
        while(true){
            long pos = block_pos.getLast();
            Disk.seek(pos);
            long read = Disk.readInt();
            if(read == -1)
                break;
            block_pos.add(read);
        }
    }

    private long allocate_block(int size){
        long pos = 0;
        long prev = -1;
        while(size > MAX_PARTITION_SIZE){        
            pos = mm.allocate(MAX_PARTITION_SIZE + HEADER_SIZE);
            if(prev != -1){
                Disk.seek(prev);
                Disk.writeInt((int)pos);
            }
            prev = pos;
            block_pos.add(pos);
            size -= MAX_PARTITION_SIZE;
        }
        pos = mm.allocate(size + HEADER_SIZE);
        block_pos.add(pos);
        Disk.seek(pos);
        Disk.writeInt(-1);
        return pos;
    }

    public String readFile(int numOfBytes){
        
        byte[] b = new byte[numOfBytes];        
        String content = "";
        int read = 0;
        for(int i = 0;i < block_pos.size();i++){
            if(numOfBytes > MAX_PARTITION_SIZE){            
                Disk.read(b, block_pos.get(i) + HEADER_SIZE,MAX_PARTITION_SIZE);
                read += MAX_PARTITION_SIZE;
                numOfBytes -= read;
            }else
                Disk.read(b, block_pos.get(i) + HEADER_SIZE,numOfBytes);
            try{
                content += new String(b,"UTF-8");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return content;
    }

    public boolean writeFile(byte[] bytes){

        for(int i = 0;i < block_pos.size() - 1;i++){
            mm.freeBlock(block_pos.get(i), MAX_PARTITION_SIZE + HEADER_SIZE);
        }
        int free = this.size % MAX_PARTITION_SIZE;
        if(free == 0) {
            free = MAX_PARTITION_SIZE;
        }
        mm.freeBlock(block_pos.getLast(), free + HEADER_SIZE);
        block_pos = new LinkedList<Long>();
        this.size = bytes.length;
        long pos = allocate_block(size);
        Disk.write(bytes,pos + HEADER_SIZE,size);
        return true;
    }

    public void free(){

        for(int i = 0;i < block_pos.size() - 1; i++){
            mm.freeBlock(block_pos.get(i),MAX_PARTITION_SIZE + HEADER_SIZE);
        }
        int free = this.size % MAX_PARTITION_SIZE;
        if(free == 0) {
            free = MAX_PARTITION_SIZE;
        }
        mm.freeBlock(block_pos.getLast(), free + HEADER_SIZE);
    }

    public long getStartPosition(){
        return block_pos.get(0);
    }

	public String getName(){
		return name;
	}

    public void setName(String name){
    	this.name = name;
    }

    public int getSize(){
    	return size;
    }

    public static void setMemoryManager(MemoryManager mmNew) {
        mm = mmNew;
    }
}