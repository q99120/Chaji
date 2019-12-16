package com.mei.chaji.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mei.chaji.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OpsFragment extends Fragment {
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ops, container, false);
//        btn_buy_tea = view.findViewById(R.id.btn_buy_tea);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public static OpsFragment newInstance() {
        return new OpsFragment();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
