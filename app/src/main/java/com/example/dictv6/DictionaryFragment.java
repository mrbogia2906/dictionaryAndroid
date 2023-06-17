package com.example.dictv6;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class DictionaryFragment extends Fragment {
    private EditText etWord;
    private TextView tvHtml;
    private ImageButton btnBookmark;
    private ImageButton btnTranslate;
    private WordDao wordDao;
    private Spinner spinnerTranslationDirection;
    private ImageButton btnPronounce;
    private TextToSpeech tts;

    public interface OnWordBookmarkListener {
        void onWordBookmarked();
    }

    private OnWordBookmarkListener onWordBookmarkListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dictionary, container, false);

        etWord = view.findViewById(R.id.et_word);
        tvHtml = view.findViewById(R.id.tv_Html);
        btnBookmark = view.findViewById(R.id.btn_bookmark);
        btnTranslate = view.findViewById(R.id.btn_translate);
        spinnerTranslationDirection = view.findViewById(R.id.spinner_translation_direction);
        btnPronounce = view.findViewById(R.id.btn_pronounce);

        wordDao = new WordDao(getContext());

        // This listener triggers when user presses the Done button on the keyboard.
        // It performs a click on the btnTranslate, essentially translating the word.
        etWord.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnTranslate.performClick();
                return true;
            }
            return false;
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.translation_directions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTranslationDirection.setAdapter(adapter);

        if (getArguments() != null) {
            String word = getArguments().getString("word");
            String originTable = getArguments().getString("originTable");
            if (word != null && originTable != null) {
                updateWord(word, originTable);
            }
        }

        tts = new TextToSpeech(getContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US);
            }
        });

        btnTranslate.setOnClickListener(v -> {
            String wordToFind = etWord.getText().toString();
            if (!wordToFind.isEmpty()) {
                String dictionaryName = spinnerTranslationDirection.getSelectedItem().toString();
                Word word = wordDao.getWord(dictionaryName, wordToFind);

                if (word != null) {
                    tvHtml.setText(Html.fromHtml(word.getHtml(), Html.FROM_HTML_MODE_COMPACT));
                    if (wordDao.isBookmarked(word.getId())) {
                        btnBookmark.setImageResource(R.drawable.ic_star_light);
                        btnPronounce.setOnClickListener(v1 -> {
                            String wordToPronounce = etWord.getText().toString();
                            if (!wordToPronounce.isEmpty()) {
                                if (spinnerTranslationDirection.getSelectedItem().equals("Từ điển Anh Việt")) {
                                    tts.setLanguage(Locale.US);
                                } else {
                                    tts.setLanguage(new Locale("vi"));
                                }
                                tts.speak(wordToPronounce, TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                Toast.makeText(getContext(), "Please enter a word", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        btnBookmark.setImageResource(R.drawable.ic_star_dark);
                    }
                } else {
                    tvHtml.setText("Word not found");
                }
            } else {
                Toast.makeText(getContext(), "Please enter a word", Toast.LENGTH_SHORT).show();
            }
        });

        btnBookmark.setOnClickListener(v -> {
            String wordToBookmark = etWord.getText().toString();
            Word word;
            String originTable;
            if (spinnerTranslationDirection.getSelectedItem().equals("Từ điển Anh Việt")) {
                word = wordDao.getWordEngToViet(wordToBookmark);
                originTable = "av";
            } else {
                word = wordDao.getWordVietToEng(wordToBookmark);
                originTable = "va";
            }

            if (word != null) {
                if (wordDao.isBookmarked(word.getId())) {
                    wordDao.removeBookmark(word.getId());
                    btnBookmark.setImageResource(R.drawable.ic_star_dark);
                    if (onWordBookmarkListener != null) { // Call onWordBookmarked when a word is unmarked as well
                        onWordBookmarkListener.onWordBookmarked();
                    }
                } else {
                    wordDao.bookmarkWord(word.getId(), originTable);
                    btnBookmark.setImageResource(R.drawable.ic_star_light);
                    if (onWordBookmarkListener != null) {
                        onWordBookmarkListener.onWordBookmarked();
                    }
                }
            }
        });

        return view;
    }

    public void updateWord(String word, String originTable) {
        etWord.setText(word);

        if (originTable.equals("av")) {
            spinnerTranslationDirection.setSelection(0);  // Set to "English-Vietnamese"
        } else {
            spinnerTranslationDirection.setSelection(1);  // Set to "Vietnamese-English"
        }

        btnTranslate.performClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onWordBookmarkListener = (OnWordBookmarkListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnWordBookmarkListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
