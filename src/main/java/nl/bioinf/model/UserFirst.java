package nl.bioinf.model;

public class UserFirst {
    private String name;
    private String email;
    private int visits;


    public UserFirst(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getVisits() {
        return visits;
    }

    public void addVisit() {
        this.visits++;
    }
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", visits=" + visits +
                '}';
    }
}
