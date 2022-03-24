package com.example.zoomclone.welcome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zoomclone.R;


public class WelcomFragmentViewpager extends Fragment {
    public WelcomFragmentViewpager() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String title, String desc, int img) {
        WelcomFragmentViewpager welcomFragmentViewpager=new WelcomFragmentViewpager();

        Bundle bundle=new Bundle();
        bundle.putString("title",title);
        bundle.putString("desc",desc);
        bundle.putInt("image",img);
        welcomFragmentViewpager.setArguments(bundle);
        return welcomFragmentViewpager;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_welcom_viewpager, container, false);

        TextView textViewTitle=view.findViewById(R.id.signUptitle);
        TextView textViewdesc=view.findViewById(R.id.signUpDesc);
        ImageView imageView=view.findViewById(R.id.imageView);

        textViewTitle.setText(getArguments().getString("title"));
        textViewdesc.setText(getArguments().getString("desc"));
        imageView.setImageResource(getArguments().getInt("image"));

        return view;
    }
}