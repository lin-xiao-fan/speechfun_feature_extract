package com.example.pdapp2022919.ShortRecorder;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ShortRecorderViewModel extends ViewModel {

    private final MutableLiveData<Integer> _wordNumber = new MutableLiveData<>(1);
    public final LiveData<Integer> wordNumber = _wordNumber;
    private final MutableLiveData<String> _word = new MutableLiveData<>("");
    public final LiveData<String> word = _word;

    private final ArrayList<String> wordList = new ArrayList<>();
    private TextToSpeech t1;
    private final MutableLiveData<Boolean> _isPlaySound = new MutableLiveData<>(false);
    public final LiveData<Boolean> isPlaySound = _isPlaySound;

    public void init(Context ctx, String[] words) {
        t1 = new TextToSpeech(ctx, status -> {
            if (status != TextToSpeech.ERROR) {
                t1.setLanguage(Locale.TAIWAN);
            }
        });
        t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                System.out.println("start");
                _isPlaySound.postValue(true);
            }

            @Override
            public void onDone(String utteranceId) {
                System.out.println("done");
                _isPlaySound.postValue(false);
            }

            @Override
            public void onError(String utteranceId) {
                System.out.println("error");
            }
        });
        // shuffle
        Collections.addAll(wordList, words);
        Collections.shuffle(wordList);
        _word.postValue(wordList.get(0));
    }

    public void playVoice() {
        // play sound
        String speak = word.getValue();
        t1.speak(speak,TextToSpeech.QUEUE_FLUSH,null, "0");
    }

    public void next() {
        _word.postValue(wordList.get(_wordNumber.getValue()));
        _wordNumber.postValue(_wordNumber.getValue() + 1);
    }

}
