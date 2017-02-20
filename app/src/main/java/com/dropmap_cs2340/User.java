package com.dropmap_cs2340;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johnbritti on 2/9/17.
 * Holds user data
 */

public class User implements Parcelable {

    private String id;
    private String email;
    private String username;
    private String password;
    private AuthLevel authLevel;
    public User() {

    }
    public User(String _id, String _email, String _username, String _password, AuthLevel _authLevel) {
        id = _id;
        email = _email;
        username = _username;
        password = _password;
        authLevel = _authLevel;
    }

    private User(Parcel in) {
        id = in.readString();
        email = in.readString();
        username = in.readString();
        password = in.readString();
        authLevel = (AuthLevel) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeSerializable(authLevel);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String password) {
        this.password = password;
    }

    public String getPassword() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthLevel() {
        if (authLevel == null) {
            return null;
        } else {
            return authLevel.name();
        }
    }

    public void setAuthLevel(String _authLevel) {
        // Get enum from string
        if (_authLevel == null) {
            authLevel = null;
        } else {
            authLevel = AuthLevel.valueOf(_authLevel);
        }
    }
}
