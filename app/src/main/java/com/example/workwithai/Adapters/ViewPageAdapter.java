package com.example.workwithai.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.workwithai.Fragments.CategoryFragment;
import com.example.workwithai.Fragments.HomeFragment;
import com.example.workwithai.Fragments.QuestionFragment;
import com.example.workwithai.Fragments.QuizFragment;
import com.example.workwithai.Fragments.SettingsFragment;

public class ViewPageAdapter extends FragmentStateAdapter {

    public ViewPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new HomeFragment();
        } else if (position == 1){
            return new CategoryFragment();
        } else if (position == 2){
            return new QuizFragment(); // Index 2 is Quiz (Matches MenuActivity)
        } else if (position == 3) {
            return new SettingsFragment(); // Index 3 is Settings
        } else if (position == 4) {
            return new QuestionFragment(); // Index 4 is the new Question Tab
        }
        return new HomeFragment();
    }

    @Override
    public int getItemCount() {
        return 5; // Updated to return 5 fragments total
    }
}