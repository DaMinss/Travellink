package com.example.travellink.Expense;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.travellink.R;
import com.squareup.picasso.Picasso;

public class ViewImageFragment extends DialogFragment {

   ImageView bill_image, back;
    Uri image_uri;

    public ViewImageFragment(Uri image) {
        // Required empty public constructor
        image_uri = image;
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_view_image, container, false);
        back = root.findViewById(R.id.backBTN);
        bill_image = root.findViewById(R.id.img);
        bill_image.setImageURI(image_uri);
//        Picasso.get().load(image_uri).into(bill_image);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return root;
    }
}