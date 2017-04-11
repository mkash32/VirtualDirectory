public interface File {
    // Types of File Allocation
    public int CONTIGUOUS = 1;
    public int LINKED = 2;
    public int INDEXED = 3;

    public String getName();
    public void setName(String name);
    public String readFile(int numOfBytes);
    public boolean writeFile(byte[] bytes);
    public int getSize();
    public long getStartPosition();
    public void free();
}
