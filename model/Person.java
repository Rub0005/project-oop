package model;

/**
 * Abstract base class for persons in the hospital system.
 */
public abstract class Person {
    private String name;
    private int age;

    /**
     * Constructs a Person with the specified name and age.
     *
     * @param name the person's name
     * @param age  the person's age
     */
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Displays information about the person.
     */
    public abstract void displayInfo();
}