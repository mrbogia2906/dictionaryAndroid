package com.example.dictv6;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements DictionaryFragment.OnWordBookmarkListener, BookmarksFragment.OnBookmarkSelectedListener {

    private DictionaryFragment dictionaryFragment;
    private OnlineDictionaryFragment onlineDictionaryFragment;
    private BookmarksFragment bookmarksFragment;
    private BottomNavigationView bottomNav;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        dictionaryFragment = new DictionaryFragment();
        onlineDictionaryFragment = new OnlineDictionaryFragment();
        bookmarksFragment = new BookmarksFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // Add all the fragments to the container
        ft.add(R.id.fragment_container, dictionaryFragment, "dictionary");
        ft.add(R.id.fragment_container, onlineDictionaryFragment, "onlineDictionary");
        ft.add(R.id.fragment_container, bookmarksFragment, "bookmarks");

        // Hide all the fragments first
        ft.hide(dictionaryFragment);
        ft.hide(onlineDictionaryFragment);
        ft.hide(bookmarksFragment);

        // Then show the DictionaryFragment by default
        ft.show(dictionaryFragment);
        activeFragment = dictionaryFragment;

        ft.commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_dictionary:
                            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(dictionaryFragment).commit();
                            activeFragment = dictionaryFragment;
                            break;
                        case R.id.navigation_online_dictionary:
                            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(onlineDictionaryFragment).commit();
                            activeFragment = onlineDictionaryFragment;
                            break;
                        case R.id.navigation_bookmarks:
                            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(bookmarksFragment).commit();
                            activeFragment = bookmarksFragment;
                            break;
                    }
                    return true;
                }
            };

    // This method is used when a word is selected in the dictionary.
    // It replaces the current fragment with a DictionaryFragment,
    // passing the selected word and origin table as arguments to the new fragment
    public void onWordBookmarked() {
        bookmarksFragment.updateBookmarkList();
    }

    public void setWordToDictionaryFragment(String word, String originTable) {
        dictionaryFragment.updateWord(word, originTable);
    }

    // This method is used when a bookmark is selected.
    // It replaces the current fragment with a DictionaryFragment,
    // passing the selected word and origin table as arguments to the new fragment.
    public void onBookmarkSelected(String word, String originTable) {
        setWordToDictionaryFragment(word, originTable);
        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(dictionaryFragment).commit();
        activeFragment = dictionaryFragment;
        bottomNav.setSelectedItemId(R.id.navigation_dictionary);
    }
}