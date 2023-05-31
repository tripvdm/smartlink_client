package com.lipakov.smartlink.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.lipakov.smartlink.R;
import com.lipakov.smartlink.databinding.FragmentGalleryBinding;

import me.relex.photodraweeview.PhotoDraweeView;

public class GalleryFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        FragmentGalleryBinding fragmentGalleryBinding = FragmentGalleryBinding.inflate(inflater);
        PhotoDraweeView photoDraweeView = fragmentGalleryBinding.photoOfSmartLink;
        photoDraweeView.setMinimumScale(1.0f);
        photoDraweeView.setMaximumScale(2.0f);
        Bundle bundleGallery = getArguments();
        assert bundleGallery != null;
        String photoPath = bundleGallery.getString("photoPath");
        Uri uri = Uri.parse(photoPath);
        photoDraweeView.setPhotoUri(uri);
        return view;
    }

}