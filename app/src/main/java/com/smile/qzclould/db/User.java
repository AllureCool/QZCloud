package com.smile.qzclould.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "User")
public class User {
    @PrimaryKey
    @NotNull
    public String uuid;

    public String name;

    public String phone;

    @Ignore
    public String icon;
}
