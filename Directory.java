// The various Directory Implementations (Hash Table, Linked List) should adhere to this interface.
public interface Directory {
    public File findFile(String name);
    public boolean createFile(String name);
    public boolean removeFile(String name);
    public boolean renameFile(String oldName, String newName);
    public File[] listFiles();
    public void exit();
}
