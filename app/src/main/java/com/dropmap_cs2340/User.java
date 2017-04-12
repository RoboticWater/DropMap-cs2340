package com.dropmap_cs2340;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Created by johnbritti on 2/9/17.
 * Holds user data
 */

public class User implements Parcelable {

    private String id;
    private String email;
    private String name;
    private String password;
    @Nullable
    private AuthLevel authLevel;
    User() {

    }
    User(String _id, String _email, String _name, String _password,
         @Nullable AuthLevel _authLevel) {
        id        = _id;
        email     = _email;
        name      = _name;
        password  = _password;
        authLevel = _authLevel;
    }

    private User(Parcel in) {
        id        = in.readString();
        email     = in.readString();
        name      = in.readString();
        password  = in.readString();
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
        dest.writeString(name);
        dest.writeString(password);
        dest.writeSerializable(authLevel);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getPassword() {
        if (password == null) {
            return null;
        } else {
            return password;
        }

    }

    void setPassword(String password) {
        if (password == null) {
            this.password = null;
        } else {
            this.password = password;
        }
    }

    String getAuthLevel() {
        if (authLevel == null) {
            return null;
        } else {
            return authLevel.name();
        }
    }

    void setAuthLevel(@Nullable String _authLevel) {
        if (_authLevel == null) {
            authLevel = null;
        } else {
            authLevel = AuthLevel.valueOf(_authLevel);
        }
    }
}
