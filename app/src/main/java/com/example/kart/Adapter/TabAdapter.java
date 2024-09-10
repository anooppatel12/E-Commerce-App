package com.example.kart.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.kart.Fragments.OneFragment;
import com.example.kart.Fragments.TwoFragment;

public class TabAdapter extends FragmentStateAdapter {
    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 0)
        {
            return new OneFragment();
        }
        return new TwoFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
