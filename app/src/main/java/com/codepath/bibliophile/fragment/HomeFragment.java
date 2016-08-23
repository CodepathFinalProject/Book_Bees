package com.codepath.bibliophile.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.bibliophile.R;
import com.codepath.bibliophile.model.BookModel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.home_fragment, parent, false);

        rvItem = (RecyclerView)v.findViewById(R.id.rvHomePage);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvItem.setLayoutManager(gridLayoutManager);


        //Define the class we would like to query


        rvItem.setAdapter(adapter);
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.home_fragment, parent, false);
        return  v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateTimeline();

    }

    private void populateTimeline() {
        ParseQuery<BookModel > query = ParseQuery.getQuery(BookModel.class);
// Define our query conditions
        //query.whereEqualTo("bookTitle", ParseUser.getCurrentUser());
// Execute the find asynchronously
        query.findInBackground(new FindCallback<BookModel>() {
            public void done(List<BookModel> itemList, ParseException e) {
                if (e == null) {
                    Log.d("SUPRIYA", itemList.get(0).getBookCover());
                    addAll(itemList);
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }


}
