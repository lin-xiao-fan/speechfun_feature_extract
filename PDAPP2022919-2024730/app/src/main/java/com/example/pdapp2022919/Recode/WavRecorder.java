package com.example.pdapp2022919.Recode;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.example.pdapp2022919.SystemManager.FileManager2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

public class WavRecorder {

    private static final int BPP = 16;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNELS = 2;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE;
    private static final long BYTE_RATE = BPP * SAMPLE_RATE * CHANNELS / 8;
    private static final String TEMP_FILE_PATH;
    private static final byte[] HEADER = new byte[44];

    private static boolean isRecording = false;
    private static boolean isMark = false;
    private static short maxAmplitude = -1;
    private static int markCount = 0;
    private static long markDataSum = 0;
    private static AudioRecord recorder;

    static {
        BUFFER_SIZE = AudioRecord.getMinBufferSize(8000, CHANNEL, AUDIO_ENCODING);
        TEMP_FILE_PATH = FileManager2.getTempRaw();
        // initial header
        HEADER[0] = 'R'; // RIFF/WAVE header
        HEADER[1] = 'I';
        HEADER[2] = 'F';
        HEADER[3] = 'F';
        HEADER[8] = 'W';
        HEADER[9] = 'A';
        HEADER[10] = 'V';
        HEADER[11] = 'E';
        HEADER[12] = 'f'; // 'fmt ' chunk
        HEADER[13] = 'm';
        HEADER[14] = 't';
        HEADER[15] = ' ';
        HEADER[16] = 16; // 4 bytes: size of 'fmt ' chunk
        HEADER[17] = 0;
        HEADER[18] = 0;
        HEADER[19] = 0;
        HEADER[20] = 1; // format = 1
        HEADER[22] = (byte) CHANNELS;
        HEADER[21] = 0;
        HEADER[23] = 0;
        HEADER[24] = (byte) ((long) SAMPLE_RATE & 0xff);
        HEADER[25] = (byte) (((long) SAMPLE_RATE >> 8) & 0xff);
        HEADER[26] = (byte) (((long) SAMPLE_RATE >> 16) & 0xff);
        HEADER[27] = (byte) (((long) SAMPLE_RATE >> 24) & 0xff);
        HEADER[28] = (byte) (BYTE_RATE & 0xff);
        HEADER[29] = (byte) ((BYTE_RATE >> 8) & 0xff);
        HEADER[30] = (byte) ((BYTE_RATE >> 16) & 0xff);
        HEADER[31] = (byte) ((BYTE_RATE >> 24) & 0xff);
        HEADER[32] = (byte) (2 * 16 / 8); // block align
        HEADER[33] = 0;
        HEADER[34] = BPP; // bits per sample
        HEADER[35] = 0;
        HEADER[36] = 'd';
        HEADER[37] = 'a';
        HEADER[38] = 't';
        HEADER[39] = 'a';
    }

    @SuppressLint("MissingPermission")
    public static void startRecording(Runnable onRecord) {
        if (isRecording) return;
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL, AUDIO_ENCODING, BUFFER_SIZE);
        new File(TEMP_FILE_PATH).delete();
        if (recorder.getState() != 1) return;
        isRecording = true;
        recorder.startRecording();
        new Thread(() -> recordRawData(onRecord)).start();
    }

    public static void stopRecording(String saveFilePath, Runnable onSaveSuccess) {
        if (!isRecording || recorder == null) return;
        if (recorder.getState() != 1) return;
        isRecording = false;
        recorder.stop();
        recorder.release();
        if (saveFilePath == null) return;
        new Thread(() -> outputWavFile(saveFilePath, onSaveSuccess)).start();
    }

    public static void stopRecording(String saveFilePath) {
        stopRecording(saveFilePath, null);
    }

    @SuppressLint("MissingPermission")
    public static void resumeRecording() {
        if (isRecording) return;
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL, AUDIO_ENCODING, BUFFER_SIZE);
        if (recorder.getState() != 1) return;
        isRecording = true;
        recorder.startRecording();
        new Thread(() -> recordRawData(null)).start();
    }

    public static short getMaxAmplitude() {
        short result = maxAmplitude;
        maxAmplitude = -1;
        return result;
    }

    public static double getDB(double amp) {
        if (amp <= 0) return 0;
        return 20 * Math.log10(amp);
    }

    public static void enableMark(boolean enable) {
        isMark = enable;
        if (enable) {
            markCount = 0;
            markDataSum = 0;
        }
    }

    public static double getMarkAverage() {
        if (markCount == 0) return 0;
        return (double) markDataSum / markCount;
    }

    public static boolean isRecording() {
        return isRecording;
    }

    private static void recordRawData(Runnable onRecord) {
        try (
                RandomAccessFile writer = new RandomAccessFile(TEMP_FILE_PATH, "rw");
                FileChannel channel = writer.getChannel()
        ) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            while (isRecording) {
                if (recorder.read(buffer, BUFFER_SIZE) != AudioRecord.ERROR_INVALID_OPERATION) {
                    channel.write(buffer);
                    dataProcessing(buffer.compact().order(ByteOrder.LITTLE_ENDIAN).asShortBuffer());
                    if (onRecord != null) onRecord.run();
                }
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dataProcessing(ShortBuffer buffer) {
        short[] data = new short[buffer.remaining()];
        buffer.get(data);
        for (short s : data) {
            // max
            if (s < 0) s = (short) -s;
            if (maxAmplitude == -1 || maxAmplitude < s) maxAmplitude = s;
            // average
            if (!isMark) return;
            markDataSum += s;
            markCount++;
        }
    }

    private static void outputWavFile(String saveFilePath, Runnable onSaveSuccess) {
        try (
                RandomAccessFile reader = new RandomAccessFile(TEMP_FILE_PATH, "r");
                FileChannel rChannel = reader.getChannel();
                RandomAccessFile writer = new RandomAccessFile(saveFilePath, "rw");
                FileChannel wChannel = writer.getChannel()
        ) {
            setHeader(rChannel.size());
            wChannel.write(ByteBuffer.wrap(HEADER));
            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            while (rChannel.read(buffer) > 0) {
                buffer.flip();
                wChannel.write(buffer);
                buffer.clear();
            }
            if (onSaveSuccess != null) onSaveSuccess.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setHeader(long audioLength) {
        long dataLength = audioLength + 36;
        HEADER[4] = (byte) (dataLength & 0xff);
        HEADER[5] = (byte) ((dataLength >> 8) & 0xff);
        HEADER[6] = (byte) ((dataLength >> 16) & 0xff);
        HEADER[7] = (byte) ((dataLength >> 24) & 0xff);

        HEADER[40] = (byte) (audioLength & 0xff);
        HEADER[41] = (byte) ((audioLength >> 8) & 0xff);
        HEADER[42] = (byte) ((audioLength >> 16) & 0xff);
        HEADER[43] = (byte) ((audioLength >> 24) & 0xff);
    }

}
