package com.codepath.bibliophile.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.bibliophile.R;
import com.codepath.bibliophile.activity.DetailsActivity;
import com.codepath.bibliophile.adapter.HomeRecyclerViewAdapter;
import com.codepath.bibliophile.model.BookModel;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public HomeRecyclerViewAdapter adapter;
    public ArrayList<BookModel> books;
    public RecyclerView rvItem;

    private RecyclerTouchListener onTouchListener;


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

        onTouchListener = new RecyclerTouchListener(getActivity(), rvItem);
        onTouchListener.setSwipeOptionViews(R.id.seller_contact, R.id.map_view)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {

                        final BookModel book = books.get(position);



                        Log.d("SUPRIYA", "ROW Clickesh");
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("title", book.getTitle());
                        intent.putExtra("author", book.getAuthor());
                        intent.putExtra("description", book.getDescription());
                        intent.putExtra("price", book.getPrice().toString());
                        intent.putExtra("cover", book.getBookCover());
                        intent.putExtra("isbn", String.valueOf(book.getISBN()));
                        intent.putExtra("condition", book.getCondition());
                        intent.putExtra("bookOwner", book.getBookOwner());
                        intent.putExtra("ownerEmail", book.getContactEmail());
                        getContext().startActivity(intent);

                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                        // Do something
                    }
                })
                .setSwipeable(R.id.card_view, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        if (viewID == R.id.seller_contact) {
                            // Do something
                        } else if (viewID == R.id.map_view) {
                            // Do something
                        }
                    }
                });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        books = new ArrayList<>();
        //construct the adapter from data source
        adapter = new HomeRecyclerViewAdapter(getActivity(), books);

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
        rvItem.addOnItemTouchListener(onTouchListener);
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

    public void addAll(List<BookModel> books) {
        this.books.addAll(books);
        adapter.notifyDataSetChanged();
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

    @Override
    public void onPause() {
        rvItem.removeOnItemTouchListener(onTouchListener);
        super.onPause();
    }


}
