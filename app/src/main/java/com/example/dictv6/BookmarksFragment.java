package com.example.dictv6;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class BookmarksFragment extends Fragment {
    private ListView lvBookmarks;
    private WordDao wordDao;
    private OnBookmarkSelectedListener mListener;

    public interface OnBookmarkSelectedListener {
        void onBookmarkSelected(String word, String originTable);
    }

    public void updateBookmarkList() {
        List<Bookmark> bookmarks = wordDao.getAllBookmarks2();
        List<String> bookmarkWords = new ArrayList<>();
        for (Bookmark bookmark : bookmarks) {
            if (bookmark != null && bookmark.getWord() != null) {
                bookmarkWords.add(bookmark.getWord().getWord());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bookmarkWords);
        lvBookmarks.setAdapter(adapter);

        lvBookmarks.setOnItemClickListener((parent, view1, position, id) -> {
            Bookmark selectedBookmark = bookmarks.get(position);
            String wordToFind = selectedBookmark.getWord().getWord();
            String originTable = selectedBookmark.getOriginTable();

            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.onBookmarkSelected(wordToFind, originTable);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bookmarks, container, false);

        lvBookmarks = view.findViewById(R.id.lv_bookmarks);
        wordDao = new WordDao(getContext());

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnBookmarkSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnBookmarkSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBookmarkList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}





