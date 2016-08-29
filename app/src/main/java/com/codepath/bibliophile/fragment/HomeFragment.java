package com.codepath.bibliophile.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.bibliophile.R;
import com.codepath.bibliophile.model.BookModel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    private ParseQuery<BookModel> finalQuery;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.home_fragment, parent, false);

        rvItem = (RecyclerView) v.findViewById(R.id.rvHomePage);
        FloatingActionButton myFab = (FloatingActionButton) v.findViewById(R.id.fabAdd);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new PostFragment()).commit();
            }
        });

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvItem.setLayoutManager(gridLayoutManager);

        rvItem.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String qValue = "";
        if ((getArguments() != null) && getArguments().containsKey("query")) {
            qValue = getArguments().getString("query");
            getBooksUsingQuery(qValue);
        } else {
            getBooks();
        }
    }

    @Override
    public void onResume() {
        finalQuery.findInBackground(new FindCallback<BookModel>() {
            public void done(List<BookModel> itemList, ParseException e) {
                updateList(itemList, e);
            }
        });
        super.onResume();
    }

    private void getBooks() {
        finalQuery = ParseQuery.getQuery(BookModel.class);
        finalQuery.whereNotEqualTo("owner", ParseUser.getCurrentUser());

        // Final query is executed during onResume
    }

    private void getBooksUsingQuery(String q) {
        // Check if the query is contained within the title or author fields
        ParseQuery<BookModel> queryTitle = ParseQuery.getQuery(BookModel.class);
        queryTitle.whereContains("title", q);
        ParseQuery<BookModel> queryAuthor = ParseQuery.getQuery(BookModel.class);
        queryAuthor.whereContains("author", q);

        List<ParseQuery<BookModel>> clauses = new ArrayList<>();
        clauses.add(queryTitle);
        clauses.add(queryAuthor);

        // If query can be parsed into a long, it might be an ISBN
        try {
            long isbn = Long.parseLong(q);
            ParseQuery<BookModel> queryISBN = ParseQuery.getQuery(BookModel.class);
            queryISBN.whereEqualTo("ISBN", isbn);
            clauses.add(queryISBN);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Create the final query and exclude books posted by the user
        finalQuery = ParseQuery.or(clauses);
        finalQuery.whereNotEqualTo("owner", ParseUser.getCurrentUser());

        // Final query is executed during onResume
    }

    private void updateList(List<BookModel> itemList, ParseException e) {
        if (e == null) {
            books.clear();
            addAll(itemList);
            adapter.notifyDataSetChanged();
        } else {
            e.printStackTrace();
        }
    }
}
