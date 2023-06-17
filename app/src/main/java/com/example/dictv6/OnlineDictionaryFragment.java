package com.example.dictv6;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class OnlineDictionaryFragment extends Fragment {

    private EditText etWordOnline;
    private TextView tvResultOnline;
    private Spinner spinnerSourceLanguage;
    private Spinner spinnerTargetLanguage;

    String[] sourceLanguage = {"English", "French", "German", "Vietnamese"};
    String[] targetLanguage = {"English", "French", "German", "Vietnamese"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    int languageCode, sourceLanguageCode, targetLanguageCode = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_online_dictionary, container, false);

        etWordOnline = view.findViewById(R.id.et_word_online);
        tvResultOnline = view.findViewById(R.id.tv_result_online);
        spinnerSourceLanguage = view.findViewById(R.id.spinner_source_language);
        spinnerTargetLanguage = view.findViewById(R.id.spinner_target_language);

        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sourceLanguage);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSourceLanguage.setAdapter(sourceAdapter);

        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, targetLanguage);
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTargetLanguage.setAdapter(targetAdapter);


        etWordOnline.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etWordOnline.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "please enter text to translate", Toast.LENGTH_SHORT).show();
                    } else if (sourceLanguageCode == 0) {
                        Toast.makeText(getContext(), "please select source language", Toast.LENGTH_SHORT).show();
                    } else if (targetLanguageCode == 0) {
                        Toast.makeText(getContext(), "please select target language", Toast.LENGTH_SHORT).show();
                    } else {
                        translateText(sourceLanguageCode, targetLanguageCode, etWordOnline.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });


        spinnerSourceLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sourceLanguageCode = getLanguageCode(sourceLanguage[position]);
                if (sourceLanguageCode == 0) {
                    Toast.makeText(getContext(), "Invalid source language selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerTargetLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                targetLanguageCode = getLanguageCode(targetLanguage[position]);
                if (targetLanguageCode == 0) {
                    Toast.makeText(getContext(), "Invalid target language selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Button btnTranslateOnline = view.findViewById(R.id.btn_translate_online);
        btnTranslateOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResultOnline.setVisibility(View.VISIBLE);
                tvResultOnline.setText("");
                if (etWordOnline.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "please enter text to translate", Toast.LENGTH_SHORT).show();
                } else if (sourceLanguageCode == 0) {
                    Toast.makeText(getContext(), "please select source language", Toast.LENGTH_SHORT).show();
                } else if (targetLanguageCode == 0) {
                    Toast.makeText(getContext(), "please select target language", Toast.LENGTH_SHORT).show();
                } else {
                    translateText(sourceLanguageCode, targetLanguageCode, etWordOnline.getText().toString());
                }
            }
        });

        return view;
    }

    private void translateText(int sourceLanguageCode, int targetLanguageCode, String source) {
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(targetLanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                tvResultOnline.setText("Translation...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        tvResultOnline.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to translate", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to download model", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //    String[] sourceLanguage = {"To", "English", "French", "German", "Vietnamese"};
    private int getLanguageCode(String language) {
        int languageCode = 0;
        switch (language) {
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "French":
                languageCode = FirebaseTranslateLanguage.FR;
                break;
            case "German":
                languageCode = FirebaseTranslateLanguage.DE;
                break;
            case "Vietnamese":
                languageCode = FirebaseTranslateLanguage.VI;
                break;
        }
        return languageCode;
    }
}
