package edu.byu.cs.tweeter.model.net.request;

public class RegisterRequest {

    String firstName;
    String lastName;
    String alias;
    String password;
    String imageBytesBase64;

    public RegisterRequest() {
    }


    public RegisterRequest(String firstName, String lastName, String alias, String password, String imageBytesBase64) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.alias = alias;
        this.password = password;
        this.imageBytesBase64 = imageBytesBase64;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageBytesBase64() {
        return imageBytesBase64;
    }

    public void setImageBytesBase64(String imageBytesBase64) {
        this.imageBytesBase64 = imageBytesBase64;
    }
}
