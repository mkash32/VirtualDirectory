public class ContiguousFile implements File {
    private int size; // Size of the file in bytes
    private long startingPosition;
    private String name;
    private static MemoryManager mm;

    public ContiguousFile(String name, int size) {
        this.name = name;
        this.size = size;
        if(mm == null) {
            mm = new MemoryManager();
        }
        this.startingPosition = mm.allocate(size);
    }

    public ContiguousFile(String name, int size, long startingPosition) {
        this.name = name;
        this.size = size;
        this.startingPosition = startingPosition;
    }

    public String readFile(int numOfBytes) {
        byte[] b = new byte[numOfBytes];
        Disk.read(b, startingPosition, numOfBytes);
        System.out.println("Read length + " + b.length);
        return new String(b);
    }

    public boolean writeFile(byte[] bytes) {
        if(bytes.length > size) {
            System.out.println("Attempted write is bigger than the file size. Aborting.\n");
            return false;
        }
        Disk.write(bytes, startingPosition, bytes.length);
        return true;
    }

    public boolean writeFile(String s) {
        if(s.length() > size) {
            System.out.println("Attempted write is bigger than the file size. Aborting.\n");
            return false;
        }
        System.out.println("Starting position of file " + name + " is " + startingPosition);
        Disk.write(s, startingPosition);
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public long getStartPosition() {
        return startingPosition;
    }

    public void free() {
        mm.freeBlock(startingPosition, size);
    }

    public static void setMemoryManager(MemoryManager mmNew) {
        mm = mmNew;
    }
}
