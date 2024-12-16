package com.example.uniconnect.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private final MutableLiveData<String> groupName = new MutableLiveData<>();
    private final MutableLiveData<String> ownerName = new MutableLiveData<>();

    public void setGroupName(String groupName) {
        this.groupName.setValue(groupName);
    }

    public LiveData<String> getGroupName() {
        return groupName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName.setValue(ownerName);
    }

    public LiveData<String> getOwnerName() {
        return ownerName;
    }

    public void setIndex(int index) {
        // Set data based on index, if needed
        // Example:
        setGroupName("Group " + index);
        setOwnerName("Owner " + index);
    }
}
