import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class LLDirectory implements Directory {
    private LinkedList<File> list;
    private int allocationType;

    public LLDirectory(int allocationType) {
        list = new LinkedList<File>();
        this.allocationType = allocationType;
    }

    // Loading Directory data from file;
    public LLDirectory(int allocationType, boolean loadFromFile) {
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
                    f = new LinkedListFile(name, size, startingPosition);
                }
                list.add(f);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public File findFile(String name) {
        for(int i = 0; i < list.size(); i++) {
            File f = list.get(i);
            if(f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public boolean createFile(String name) {

        File f = findFile(name);
        if(f != null) {
            System.out.println("File with that name already exists.");
            return false;
        }

        f = null;
        if(allocationType == File.CONTIGUOUS) {
            System.out.print("Enter size of the file (in Bytes): ");
            Scanner s = new Scanner(System.in);
            int size = s.nextInt();
            s.nextLine();
            f = new ContiguousFile(name, size);            
        } else if(allocationType == File.LINKED) {
            f = new LinkedListFile(name);
        }
        if(f.getStartPosition() == -1){
            System.out.println("Requested space is not available. Enter a smaller size\n");
            return false;
        }
        list.add(f);
        return true;
    }

    public boolean removeFile(String name) {
        File f = findFile(name);
        if(f != null) {
            list.remove(f);
            f.free();
            return true;
        }
        System.out.println("File with the given name does not exist.");
        return false;
    }

    public boolean renameFile(String oldName, String newName) {
        File f = findFile(newName);
        if(f != null) {
            System.out.println("File with that name already exists");
            return false;
        }

        f = findFile(oldName);
        if(f == null) {
            System.out.println("File to be renamed does not exist.");
            return false;
        }

        f.setName(newName);
        return true;
    }

    public File[] listFiles() {
        return Arrays.copyOf(list.toArray(), list.size(), File[].class);
    }


    public void exit() {
        try{
            PrintWriter writer = new PrintWriter("meta.txt", "UTF-8");
            writer.println("LLDirectory " + allocationType);
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
