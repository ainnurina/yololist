package com.example.ToList.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ItemId {
    @Exclude
    public String ItemId;

    public <I extends ItemId> I withId(@NonNull final String id)    {
        this.ItemId = id;
        return (I) this;
    }
}
