package code.demo.model;

public class Author {
    private int id;
    private String name;
    private String bio;
    private String nationality;

    public Author() {}

    public Author(int id, String name, String bio, String nationality) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.nationality = nationality;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    @Override public String toString() { return name != null ? name : ("#" + id); }
}
