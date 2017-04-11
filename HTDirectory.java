import java.util.HashMap;
import java.util.Scanner;
import java.util.Collection;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class HTDirectory implements Directory {
    private HashMap<String, File> table;
    private int allocationType;

    public HTDirectory(int allocationType) {
        table = new HashMap<String, File>();
        this.allocationType = allocationType;
    }

    // Loading Directory data from file
    public HTDirectory(int allocationType, boolean loadFromFile) {
        this(allocationType);
        try {
            FileReader fr = new FileReader("meta.txt");
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String line = null;
            while((line = br.readLine()) != null) {
                String[] strings = line.split(" ");
                String name = strings[0];
                int size = Integer.parseInt(strings[1]);
                long startingPosition = Long.parseLong(strings[2]);
                File f = null;
                if(allocationType == File.CONTIGUOUS) {
                    f = new ContiguousFile(name, size, startingPosition);
                } else if(allocationType == File.LINKED) {

                } else {

                }
                table.put(name, f);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public File findFile(String name) {
        File f = table.get(name);
        if(f == null) {
            System.out.println("File does not exist.\n");
            return null;
        }
        return f;
    }

    public boolean createFile(String name) {
        if(table.containsKey(name)) {
            System.out.println("File with that name already exists.");
            return false;
        }

        File f = null;
        if(allocationType == File.CONTIGUOUS) {
            System.out.print("Enter size of the file: ");
            Scanner s = new Scanner(System.in);
            int size = s.nextInt();
            s.nextLine();
            f = new ContiguousFile(name, size);
        } else if(allocationType == File.LINKED) {
            // TODO: Yet to be implemented
        } else {

        }

        table.put(name, f);
        return true;
    }

    public boolean removeFile(String name) {
        File f = table.remove(name);
        if(f != null) {
            f.free();
            return true;
        }
        System.out.println("File with the given name does not exist.");
        return false;
    }

    public boolean renameFile(String oldName, String newName) {
        if(table.containsKey(newName)) {
            System.out.println("File with that name already exists.");
            return false;
        }
        if(!table.containsKey(oldName)) {
            System.out.println("File to be renamed does not exist.");
            return false;
        }

        File f = table.remove(oldName);
        f.setName(newName);
        table.put(newName, f);
        return true;
    }

    public File[] listFiles() {
        Collection<File> files = table.values();
        File[] filesArr = files.toArray(new File[files.size()]);
        return filesArr;
    }

    public void exit() {
        try{
            PrintWriter writer = new PrintWriter("meta.txt", "UTF-8");
            writer.println("HTDirectory " + allocationType);
            File[] files = listFiles();
            for(int i = 0; i < files.length; i++) {
                String line = files[i].getName() + " "
                    + files[i].getSize() + " "
                    + files[i].getStartPosition();
                writer.println(line);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
