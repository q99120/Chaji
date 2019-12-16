package com.mei.chaji.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.dlc.dlclogfile.FileComparator;
import com.facebook.stetho.common.Utf8Charset;

import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LogsFileUtil {
    private static LogsFileUtil instance;
    private Context context;
    private String SDPATH;
    private File fileDir;
    private int trackLevel = 4;
    private List<String> list = new ArrayList();
    private List<String> list1 = new ArrayList();
    private boolean isSaving;

    public LogsFileUtil() {
    }

    public static LogsFileUtil getInstance() {
        if (instance == null) {
            instance = new LogsFileUtil();
        }

        return instance;
    }

    public void init(Context context) {
        this.context = context;
        this.SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chaji" + File.separator+"log";
        this.fileDir = new File(this.SDPATH);
        if (!this.fileDir.exists()) {
            this.fileDir.mkdirs();
        }

    }

    public void setTrackLevel(int trackLevel) {
        this.trackLevel = trackLevel;
    }

    public String[] showAllLevelMethod() {
        StackTraceElement[] es = Thread.currentThread().getStackTrace();
        String[] methods = new String[es.length];
        int i = 0;
        StackTraceElement[] var4 = es;
        int var5 = es.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            StackTraceElement e = var4[var6];
            methods[i] = e.getMethodName();
            ++i;
        }

        return methods;
    }

    public void addLog(String title,String log) {
        if (!this.isSaving) {
            list.add(title);
            list1.add(log);
        }

        this.dealList();
    }

    private void dealList() {
        if (!this.isSaving) {
            this.isSaving = true;
            this.saveLog(list.get(0),list1.get(0));
        }

    }

    private void saveLog(String title,String log) {
        File[] files = this.fileDir.listFiles();
        if (files != null && files.length > 90) {
            List<File> fileList = Arrays.asList(files);
            Collections.sort(fileList, new FileComparator(false));
            ((File)fileList.get(0)).delete();
        }

        StackTraceElement[] es = Thread.currentThread().getStackTrace();
        StackTraceElement e = null;
        if (es[this.trackLevel].getMethodName().contains("access")) {
            e = es[this.trackLevel + 1];
        } else {
            e = es[this.trackLevel];
        }

        String verCode = null;
        String verName = null;
        PackageManager manager = this.context.getPackageManager();

        try {
            PackageInfo info = manager.getPackageInfo(this.context.getPackageName(), 0);
            int versionCode = info.versionCode;
            verCode = versionCode + "";
            verName = info.versionName;
        } catch (PackageManager.NameNotFoundException var36) {
            var36.printStackTrace();
        }

        String model = Build.MODEL;
        String carrier = Build.MANUFACTURER;
        String androidVerCode = Build.VERSION.SDK_INT + "";
        String androidVerName = Build.VERSION.RELEASE + "";
        String fileName = e.getFileName();
        int lineNum = e.getLineNumber();
        String methodName = e.getMethodName();
        StringBuilder sb = new StringBuilder();
        sb.append("日志时间:" + this.timestampToDate(System.currentTimeMillis() + "", "yyyy-MM-dd HH:mm:ss") + "\n").append(("日志标题： "+title)+ "\n").append("日志内容:" + log);
        log = sb.toString();
        this.spm("log:" + log);
        sb.delete(0, sb.length());
        String today = this.timestampToDate(System.currentTimeMillis() + "", "yyyy-MM-dd");
        File file = new File(this.SDPATH + "/",   today + ".txt");
        String originContent = this.getLog(today);
        if (originContent != null) {
            log = originContent + "\n- - - - - - - - - -我是分割线--------------------\n" + log;
        }

        FileOutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(log.getBytes());
            bufferedOutputStream.flush();
        } catch (Exception var35) {
            var35.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException var34) {
                    var34.printStackTrace();
                }
            }

            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception var33) {
                    var33.printStackTrace();
                }
            }

        }

        this.list.remove(0);
        list1.remove(0);
        this.isSaving = false;
        this.spm("save success");
        if (this.list.size() > 0  && list1.size()>0) {
            this.dealList();
        }

    }

    public String getLog() {
        String today = this.timestampToDate(System.currentTimeMillis() + "", "yyyy-MM-dd");
        return this.getLog(today);
    }

    public String getLog(String date) {
        String content = null;
        File file = new File(this.SDPATH + "/",  date + ".txt");
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                byte[] bys = new byte[1024];
                boolean var7 = false;

                int len;
                while((len = bufferedInputStream.read(bys)) != -1) {
                    if (content == null) {
                        content = new String(bys, 0, len);
                    } else {
                        content = content + new String(bys, 0, len);
                    }
                }
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        }

        return content;
    }

    public File getFile() {
        String today = this.timestampToDate(System.currentTimeMillis() + "", "yyyy-MM-dd");
        return this.getFile(today);
    }

    public File getFile(String date) {
        File file = new File(this.SDPATH + "/",  date + ".txt");
        return file.exists() ? file : null;
    }

    public String timestampToDate(String time, String dateFormat) {
        int length = time.length();
        if (length == 13) {
            time = time.substring(0, 10);
        }

        long timeI = (long)Integer.parseInt(time);
        long temp = timeI * 1000L;
        Timestamp ts = new Timestamp(temp);
        Object date = new Date();

        try {
            date = ts;
        } catch (Exception var12) {
            var12.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String dateString = formatter.format((Date)date);
        return dateString;
    }


    private void spm(String msg) {
    }
}
