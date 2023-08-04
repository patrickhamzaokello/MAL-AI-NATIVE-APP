package com.pkasemer.malai.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pkasemer.malai.Adapters.GalleryAdapter;
import com.pkasemer.malai.Database.DatabaseHelper;
import com.pkasemer.malai.HelperClasses.Utility;
import com.pkasemer.malai.Models.MalSavedImage;
import com.pkasemer.malai.R;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment {


    public GalleryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        imageFiles = fetchImageFilesFromFolder();
        DatabaseHelper db = new DatabaseHelper(getContext());
        malSavedImageList = db.getSavedImageList(null);
    }

    private GalleryAdapter galleryAdapter;
    private ArrayList<File> imageFiles;
    List<MalSavedImage> malSavedImageList;
    private RecyclerView itemRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        itemRecyclerView = (RecyclerView) view.findViewById(R.id.main_recycler);


        int mNoOfColumns = Utility.calculateNoOfColumns(getContext(), 250);
        GridLayoutManager catgridLayoutManager = new GridLayoutManager(getContext(), mNoOfColumns);
        itemRecyclerView.setLayoutManager(catgridLayoutManager);

        itemRecyclerView.setItemAnimator(new DefaultItemAnimator());
        galleryAdapter = new GalleryAdapter(getActivity(), malSavedImageList);

        itemRecyclerView.setAdapter(galleryAdapter);


        // Fetch image files from the folder

        return view;
    }

    private ArrayList<File> fetchImageFilesFromFolder() {
        ArrayList<File> imageFiles = new ArrayList<>();

        // Get the destination folder path in internal storage
        File folder = new File(getContext().getExternalFilesDir(null), "Mal_Images");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
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

    @Override
    public void onResume() {
        super.onResume();
        galleryAdapter.notifyDataSetChanged();
    }

    private boolean isImageFile(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType != null && mimeType.startsWith("image");
    }
}