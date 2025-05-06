package model;

abstract class Person {
    protected String name;
    protected int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        if (age > 0)
            this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void displayInfo();
}