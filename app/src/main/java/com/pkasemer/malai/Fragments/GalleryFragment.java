package com.pkasemer.malai.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.pkasemer.malai.Adapters.ImageAdapter;
import com.pkasemer.malai.R;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;


public class GalleryFragment extends Fragment {





    public GalleryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageFiles = fetchImageFilesFromFolder();

    }

    private GridView gridView;
    private ImageAdapter imageAdapter;
    private ArrayList<File> imageFiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        gridView = view.findViewById(R.id.grid_view);
        imageAdapter = new ImageAdapter(getActivity(), imageFiles);
        gridView.setAdapter(imageAdapter);

        // Fetch image files from the folder

        return view;
    }

    private ArrayList<File> fetchImageFilesFromFolder() {
        ArrayList<File> imageFiles = new ArrayList<>();

        File mediaStorageDir = new File(getActivity().getFilesDir(), "Mal_Images");
        if (mediaStorageDir.exists()) {
            File[] files = mediaStorageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Add image files to the list
                    if (file.isFile() && isImageFile(file)) {
                        imageFiles.add(file);
                    }
                }
            }
        }

        return imageFiles;
    }

    private boolean isImageFile(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType != null && mimeType.startsWith("image");
    }
}