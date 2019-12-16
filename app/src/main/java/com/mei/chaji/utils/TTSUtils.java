package com.mei.chaji.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.mei.chaji.app.ChajiAPP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TTSUtils implements SpeechSynthesizerListener {
    private static final String TEXT = "欢迎使用百度语音合成，请在代码中修改合成文本";
    private static final String TAG = "TTSUtils";
    private static volatile TTSUtils instance = null;
    private static final String SAMPLE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/baiduTTS/";

    // ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    protected String appId = "17633267";
    protected String appKey = "q6HSQ5OeHdnkuyTlHGQGxGof";
    protected String secretKey = "P6RRd5xXuI4BlXiePVbs6dO1qIemw9jW";
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = TtsMode.MIX;
    // ================选择TtsMode.ONLINE  不需要设置以下参数; 选择TtsMode.MIX 需要设置下面2个离线资源文件的路径
    private static final String TEMP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chajibaidu/chajiTTS" + File.separator; // 重要！请手动将assets目录下的3个dat 文件复制到该目录
    // 请确保该PATH下有这个文件
    private static final String TEXT_FILENAME = "bd_etts_text.dat";
    // 请确保该PATH下有这个文件
    private static final String TEXT_MODENAME = "bd_etts_speech_female.dat";

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    protected SpeechSynthesizer mspeech;

    private TTSUtils() {
    }

    public static TTSUtils getInstance() {
        if (instance == null) {
            synchronized (TTSUtils.class) {
                if (instance == null) {
                    instance = new TTSUtils();
                }
            }
        }
        return instance;
    }

    /**
     *
     */
    public void init() {
//        String tmpDir = FileUtils.createTmpDir(this);
        Context context = ChajiAPP.getContext();
        File file = new File(TEMP_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        File textModel = new File(TEMP_DIR + TEXT_MODENAME);
        if (!textModel.exists()) {
            copyAssetsFile2SDCard(context, TEXT_MODENAME, TEMP_DIR + TEXT_MODENAME);
        }
        File textFile = new File(TEMP_DIR + TEXT_FILENAME);
        if (!textFile.exists()) {
            copyAssetsFile2SDCard(context, TEXT_FILENAME, TEMP_DIR + TEXT_FILENAME);
        }
        mspeech = SpeechSynthesizer.getInstance();
        mspeech.setContext(context);
        mspeech.setSpeechSynthesizerListener(this);
        // 3. 设置appId，appKey.secretKey
        mspeech.setAppId(appId);
        mspeech.setApiKey(appKey, secretKey);


        // 4. 支持离线的话，需要设置离线模型

        // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
        mspeech.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEMP_DIR + TEXT_FILENAME);
        // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
        mspeech.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, TEMP_DIR + TEXT_MODENAME);


        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mspeech.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        mspeech.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        mspeech.setParam(SpeechSynthesizer.PARAM_SPEED, "7");
        // 设置合成的语调，0-9 ，默认 5
        mspeech.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        mspeech.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        mspeech.initTts(ttsMode);
    }

    public void speak(String msg) {
        if (mspeech == null) {
            return;
        }
        int result = mspeech.speak(msg);
        if (result < 0) {
            Log.e(TAG, "error,please look up error code = " + result + " in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
        }
//        Log.e(TAG, "speak: " + msg);
    }

    public void pauseSpeech() {
        if (mspeech != null) {
            mspeech.pause();
        }
    }

    public static void copyAssetsFile2SDCard(Context context, String fileName, String path) {
        try {
            InputStream is = context.getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(new File(path));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "copyAssetsFile2SDCard: " + e.toString());
        }
    }


    @Override
    public void onSynthesizeStart(String s) {
        Log.e(TAG, "onSynthesizeStart: "+s );
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
//        Log.e(TAG, "onSynthesizeDataArrived: "+s );

    }

    @Override
    public void onSynthesizeFinish(String s) {
//        Log.e(TAG, "onSynthesizeFinish: "+s );
    }

    @Override
    public void onSpeechStart(String s) {
//        Log.e(TAG, "onSpeechStart: "+s );
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
//        Log.e(TAG, "onSpeechProgressChanged: "+s  +"进度"+i );
    }

    @Override
    public void onSpeechFinish(String s) {
        Log.e(TAG, "onSpeechFinish: "+s );
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        Log.e(TAG, "onError: "+s );
    }

}
