package hawaiiappbuilders.omniversapp.meeting.models;

import java.io.Serializable;

public class User implements Serializable {
    public String firstName, lastName, token;
    public String email;
    public int mlid;

    public User(String firstName, String lastName, String token, String email, int mlid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.token = token;
        this.email = email;
        this.mlid = mlid;
    }

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMlid() {
        return mlid;
    }

    public void setMlid(int mlid) {
        this.mlid = mlid;
    }
}
