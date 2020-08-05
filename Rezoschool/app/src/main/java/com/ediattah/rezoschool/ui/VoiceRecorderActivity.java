package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.R;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class VoiceRecorderActivity extends AppCompatActivity {

    public static final int SAMPLE_RATE = 16000;
    private final String startRecordingLabel = "Start recording";
    private final String stopRecordingLabel = "Stop recording";
    private AudioRecord mRecorder;
    private File mRecording;
    private short[] mBuffer;
    private boolean mIsRecording = false;
    private ProgressBar mProgressBar;
    private android.os.Handler Handler = new Handler();
    private Chronometer chronometer;
    private Button btn_take;
    Uri audioUri;
    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recorder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Record Voice");
        setPermission();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        final Button button = (Button) findViewById(R.id.button);
        button.setText(startRecordingLabel);
        btn_take = findViewById(R.id.btn_take);
        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioUri != null) {
                    Intent intent = new Intent();
                    intent.putExtra("DATA", audioUri);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        btn_take.setEnabled(false);
        chronometer = (Chronometer) findViewById(R.id.chronometer);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                btn_take.setEnabled(false);
                chronometer.setBase(SystemClock.elapsedRealtime());
                if (!mIsRecording) {
                    audioUri = null;
                    button.setText(stopRecordingLabel);
                    mIsRecording = true;
                    mRecorder.startRecording();

                    mRecording = getFile("raw");
                    startBufferedWrite(mRecording);
                    chronometer.start();
                    switch(v.getId()) {
                        case R.id.button:
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                            break;
                    }
                    //SET TIME LIMIT
                    Handler = new Handler();
                    r = new Runnable() {
                        public void run() {
                            button.setText(startRecordingLabel);
//                            button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            mIsRecording = false;
                            mRecorder.stop();
                            chronometer.stop();
                            File waveFile = getFile("wav");
                            switch (v.getId()) {
                                case R.id.button:
                                    chronometer.stop();
                                    break;
                            }


                            try {
                                rawToWave(mRecording, waveFile);
                            } catch (IOException e) {
                                Toast.makeText(VoiceRecorderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(VoiceRecorderActivity.this, "Recorded to " + waveFile.getName(),
                                    Toast.LENGTH_SHORT).show();

                            audioUri = Uri.fromFile(waveFile);
                            btn_take.setEnabled(true);
                        }
                    };

                    Handler.postDelayed(r, 15000); // 30 SECONDS
                    //SET TIME LIMIT
                } else {
                    if (Handler !=null) {
                        Handler.removeCallbacks(r);
                    }
                    button.setText(startRecordingLabel);
//                    button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mIsRecording = false;
                    mRecorder.stop();
                    chronometer.stop();
                    File waveFile = getFile("wav");
                    switch(v.getId()) {
                        case R.id.button:
                            chronometer.stop();
                            break;
                    }


                    try {
                        rawToWave(mRecording, waveFile);
                    } catch (IOException e) {
                        Toast.makeText(VoiceRecorderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(VoiceRecorderActivity.this, "Recorded to " + waveFile.getName(),
                            Toast.LENGTH_SHORT).show();
                    audioUri = Uri.fromFile(waveFile);
                    btn_take.setEnabled(true);
                }


            }
        });
    }

    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    output = new DataOutputStream(new BufferedOutputStream(fileOutputStream));
                    while (mIsRecording) {
                        double sum = 0;
                        int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);
                            sum += mBuffer[i] * mBuffer[i];
                        }
                        if (readSize > 0) {
                            final double amplitude = sum / readSize;
                            mProgressBar.setProgress((int) Math.sqrt(amplitude));
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(VoiceRecorderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    mProgressBar.setProgress(0);
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            Toast.makeText(VoiceRecorderActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Toast.makeText(VoiceRecorderActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, SAMPLE_RATE); // Vp7 rate
            writeInt(output, SAMPLE_RATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per Vp7
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }
            output.write(bytes.array());
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private File getFile(final String suffix) {
        Time time = new Time();
        time.setToNow();
        return new File(App.MY_AUDIO_PATH + File.separator + time.format("%Y%m%d%H%M%S") + "." + suffix);
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }
    private void initRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    }
    public void setPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ) {
            ArrayList<String> arrPermissionRequests = new ArrayList<>();
            arrPermissionRequests.add(Manifest.permission.RECORD_AUDIO);
            ActivityCompat.requestPermissions(this, arrPermissionRequests.toArray(new String[arrPermissionRequests.size()]), 201);
            return;
        } else {
            initRecorder();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 201: {
                if (grantResults[0] == 0) {
                    initRecorder();
                } else {
                    Toast.makeText(VoiceRecorderActivity.this, "Permission denied", Toast.LENGTH_SHORT);
                    finish();
                }
                break;
            }
            default:
                Toast.makeText(VoiceRecorderActivity.this, "Permission denied", Toast.LENGTH_SHORT);
                finish();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        mRecorder.stop();
        chronometer.stop();
        if (Handler!=null) {
            Handler.removeCallbacks(r);
            Handler = null;
        }
        onBackPressed();
        return true;
    }
}