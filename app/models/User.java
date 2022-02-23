package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User{

    @Id
    @Constraints.Required
    public Integer id;
    @Constraints.Required
    public String name;
    @Constraints.Required
    public String surname;

    public User() {
    }

    public User(int id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    private static Set<User> users;

    static {
        users = new HashSet<>();
        users.add(new User(1,"Santosh", "Mahato"));
        users.add(new User(2,"ABC", "XYZ"));
        users.add(new User(3,"MVC", "aws"));
    }

    public static Set<User> allUsers(){
        return users;
    }

    public static User findById(Integer id){
        for(User user:users){
            if(id.equals(user.id))
                return user;
        }
        return null;
    }

    public static void add(User user){
        users.add(user);
    }

    public static boolean remove(User user){
        return users.remove(user);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}

