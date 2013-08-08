package me.yeojoy.eng.speaker;

/**
 *  출퇴근 때 교재에 나오는 영어듣기를 하기 위해 만듬
 *  
 *  Reference site : http://jaechulhan.blogspot.kr/2010/07/ttstext-to-speech.html
 */

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final String TAG = "TextToSpeechDemo";
    
    private float mNormalPitch = 1.0f;
    private float mNormalSpeedRate = 1.0f;

    private TextToSpeech mTts;

    private TextView mTvContents;
    
    private Button mPlayButton;
    private Button mStopButton;
    
    private String[] mSentences_1, mSentences_2, mSentences_3;
    private ArrayList<String> mAlSentenses;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize text-to-speech. This is an asynchronous operation.
        // The OnInitListener (second argument) is called after initialization
        // completes.
        mTts = new TextToSpeech(this, myOnInitListener);
        mTts.setPitch(mNormalPitch);
        mTts.setSpeechRate(mNormalSpeedRate);

        mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            
            @Override
            public void onStart(String utteranceId) {
                Log.i("yeojoy", "onStart " + utteranceId);
            }
            
            @Override
            public void onError(String utteranceId) {
                Log.i("yeojoy", "onError " + utteranceId);
                
            }
            
            @Override
            public void onDone(String utteranceId) {
                Log.i("yeojoy", "onDone " + utteranceId);
                
            }
        });
        
        initButtonViews();
        
        mTvContents = (TextView) findViewById(R.id.tv_contents);
        mSentences_1 = getResources().getStringArray(R.array.arr_on_ing);
        mSentences_2 = getResources().getStringArray(R.array.arr_on_touch);
        mSentences_3 = getResources().getStringArray(R.array.arr_up_whole);
        
        mAlSentenses = new ArrayList<String>();
        mAlSentenses.addAll(Arrays.asList(mSentences_1));
        mAlSentenses.addAll(Arrays.asList(mSentences_2));
        mAlSentenses.addAll(Arrays.asList(mSentences_3));
        setSentences(-1, false);
    }

    private void initButtonViews() {
        // The button is disabled in the layout.
        // It will be enabled upon initialization of the TTS engine.
        mPlayButton = (Button)findViewById(R.id.btn_play);
        mPlayButton.setEnabled(false);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speakAll();
            }
        });
        
        mStopButton = (Button) findViewById(R.id.btn_stop);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTts.isSpeaking())
                    mTts.stop();
                
                setSentences(-1, false);
            }
        });
        
        findViewById(R.id.btn_pitch_up).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { mTts.setPitch(mNormalPitch + 0.1f); }
        });
        
        findViewById(R.id.btn_pitch_down).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { mTts.setPitch(mNormalPitch - 0.1f); }
        });
        
        findViewById(R.id.btn_speed_up).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { mTts.setSpeechRate(mNormalSpeedRate + 0.1f); }
        });
        
        findViewById(R.id.btn_speed_down).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { mTts.setSpeechRate(mNormalSpeedRate - 0.1f); }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mTts != null)
            mTts.stop();
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Don't forget to shutdown!
        if (mTts != null)
            mTts.shutdown();

    }

    // Implements TextToSpeech.OnInitListener.
    private OnInitListener myOnInitListener = new OnInitListener() {
        
        @Override
        public void onInit(int status) {
            // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
            if (status == TextToSpeech.SUCCESS) {
                // Set preferred language to US english.
                // Note that a language may not be available, and the result will
                // indicate this.
                int result = mTts.setLanguage(Locale.US);
                // Try this someday for some interesting results.
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Lanuage data is missing or the language is not supported.
                    Log.e(TAG, "Language is not available.");
                } else {
                    // Check the documentation for other possible result codes.
                    // For example, the language may be available for the locale,
                    // but not for the specified country and variant.
                    
                    // The TTS engine has been successfully initialized.
                    // Allow the user to press the button for the app to speak
                    // again.
                    mPlayButton.setEnabled(true);
                }
            } else {
                // Initialization failed.
                Log.e(TAG, "Could not initialize TextToSpeech.");
            }
        }
    };

    private void speakAll() {
        // Drop all pending entries in the playback queue.
        HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_ALARM));
        
        for (int i = 0, j = mAlSentenses.size(); i < j; i++) {
            setSentences(i, true);
            mTts.speak(mAlSentenses.get(i), TextToSpeech.QUEUE_ADD, null);
            mTts.playSilence(1500, TextToSpeech.QUEUE_ADD, null);
        }
    }
    
    private void setSentences(int index, boolean isPlaying) {
        mTvContents.setText("");
        if (index == -1) {
            for (String str : mAlSentenses) {
                mTvContents.append(str + "\n");
            }
            return;
        }
        
        if (isPlaying) {
            Log.d("XXXXX", "Index = " + index);
            // TODO 실제로 재생 중인 상태
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            SpannableString ss;
            for (int i = 0, j = mAlSentenses.size(); i < j; i++) {
                if (i == index) {
                    ss = new SpannableString(mAlSentenses.get(i));
                    ss.setSpan(new ForegroundColorSpan(Color.RED), 0, ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    ssb.append(ss).append("\n");
                } else 
                    ssb.append(mAlSentenses.get(i)).append("\n");
            }
            mTvContents.setText(ssb);
        }
    }

}
