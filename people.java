import java.util.*;

public class people {
    private String name;
    private String category;
    private ArrayList<String> biographies;

    public people() {
        this.name = null;
        this.category = null;
        this.biographies = null;
    }

    public people(String name, String category, ArrayList<String> biographies) {
        this.name = name;
        this.category = category;
        this.biographies = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }
    public String getCategory() {
        return this.category;
    }
    public ArrayList<String> getBiographies() {
        return this.biographies;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setBiographies(ArrayList<String> biographies) {
        this.biographies = biographies;
    }

    @Override
    public String toString() {
        return this.name + " " + this.category + " " + this.biographies.toString();
    }
}
