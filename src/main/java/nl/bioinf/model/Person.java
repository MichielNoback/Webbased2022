package nl.bioinf.model;

public record Person(String first, String last, int age) {
    public String fullName() {
        return first + ' ' + last;
    }
}
