package com.netease.nmc.nertcsample.beauty.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.view.FilterItem;
import com.netease.nmc.nertcsample.beauty.view.ObjectItem;
import com.netease.nmc.nertcsample.beauty.view.StickerItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
* Created by netease on 16-11-16.
 */

public class FileUtils {
    public static final String MODEL_NAME= "model.dat";
    public static final String LICENSE_NAME= "netease.lic";

    public static ArrayList<String> copyStickerFiles(Context context) {
        String files[] = null;
        ArrayList<String> zipfiles = new ArrayList<String>();

        try {
            files = context.getAssets().list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath();
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".zip") != -1){
                copyFileIfNeed(context, str);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为zip结尾
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    zipfiles.add(filename);
                }
            }
        }

        return zipfiles;
    }

    public static boolean copyFileIfNeed(Context context, String fileName) {
        String path = getFilePath(context, fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                //如果模型文件不存在
                try {
                    if (file.exists())
                        file.delete();

                    file.createNewFile();
                    InputStream in = context.getApplicationContext().getAssets().open(fileName);
                    if(in == null)
                    {
                        Log.e("copyMode", "the src is not existed");
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean copyFileIfNeed(Context context, String fileName, String className) {
        String path = getFilePath(context, className + File.separator + fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                //如果模型文件不存在
                try {
                    if (file.exists())
                        file.delete();

                    file.createNewFile();

                    InputStream in = context.getAssets().open(className + File.separator + fileName);
                    if(in == null)
                    {
                        Log.e("copyMode", "the src is not existed");
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static String getFilePath(Context context, String fileName) {
        String path = null;
        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + fileName;
        }
        return path;
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("FileUtil", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static void copyModelFiles(Context context) {
        copyFileIfNeed(context, MODEL_NAME);
        copyFileIfNeed(context, LICENSE_NAME);
    }


    public static List<ObjectItem> getObjectList(){
        List<ObjectItem> objectList = new ArrayList<>();

//        objectList.add(new ObjectItem("close", R.drawable.close_object));
//        objectList.add(new ObjectItem("null", R.drawable.none));
        objectList.add(new ObjectItem("object_hi", R.drawable.object_hi));
        objectList.add(new ObjectItem("object_happy", R.drawable.object_happy));
        objectList.add(new ObjectItem("object_star", R.drawable.object_star));

        objectList.add(new ObjectItem("object_sticker", R.drawable.object_sticker));
        objectList.add(new ObjectItem("object_love", R.drawable.object_love));
        objectList.add(new ObjectItem("object_sun", R.drawable.object_sun));


        return objectList;
    }

    public static void copyStickerFiles(Context context, String index){
        copyStickerZipFiles(context, index);
        copyStickerIconFiles(context, index);
    }

    public static void copyFilterFiles(Context context, String index){
        copyFilterModelFiles(context, index);
        copyFilterIconFiles(context, index);
    }

    public static ArrayList<StickerItem> getStickerFiles(Context context, String index){
        ArrayList<StickerItem> stickerFiles = new ArrayList<StickerItem>();
        Bitmap iconNone = BitmapFactory.decodeResource(context.getResources(), R.drawable.none);

        List<String> filterModels = copyAllFilesRetDirs(context, index);
        Map<String, Bitmap> filterIcons = copyFilterIconFiles(context, index);
        List<String> filterNames = getFilterNames(context, index);

        for(int i = 0; i < filterModels.size(); i++){
            if(filterIcons.get(filterNames.get(i)) != null) {
                Log.e("FILEUTIL", "file name: " + filterNames.get(i));
                stickerFiles.add(new StickerItem(filterNames.get(i), filterIcons.get(filterNames.get(i)), filterModels.get(i)));
            } else{
                stickerFiles.add(new StickerItem(filterNames.get(i), iconNone, filterModels.get(i)));
            }
        }


        return  stickerFiles;
    }

    public static int copyAllFiles(Context context, String className) {
        String files[] = null;

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(files.length > 0)
        {
            String folderpath = null;
            File dataDir = context.getExternalFilesDir(null);
            if (dataDir != null) {
                folderpath = dataDir.getAbsolutePath() + File.separator + className;

                File folder = new File(folderpath);

                if (!folder.exists()) {
                    folder.mkdir();
                }
            }

            for (String fileName : files)
                copyAllFiles(context, className + File.separator + fileName);
        }
        else
        {
            copyFileIfNeed(context, className);
        }

        return files.length;
    }

    public static List<String> copyAllFilesRetDirs(Context context, String className) {
        String files[] = null;
        List<String> dirs = new ArrayList<String>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(files.length > 0)
        {
            String folderpath = null;
            File dataDir = context.getExternalFilesDir(null);
            String dataDirPath = null;
            if (dataDir != null) {
                dataDirPath = dataDir.getAbsolutePath();
                folderpath = dataDirPath + File.separator + className;

                File folder = new File(folderpath);

                if (!folder.exists()) {
                    folder.mkdir();
                }
            }

            for (String fileName : files) {
                String tmpStr = className + File.separator + fileName;
                int num = copyAllFiles(context, tmpStr);
                if(num > 0) dirs.add(dataDirPath + File.separator + tmpStr);
            }
        }
        else {
            copyFileIfNeed(context, className);
        }

        return dirs;
    }


    public static List<String> copyStickerZipFiles(Context context, String className){
        String files[] = null;
        List<String> modelFiles = new ArrayList<String>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".zip") != -1){
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static List<String> getStickerZipFilesFromSd(Context context, String className){
        ArrayList<String> modelFiles = new ArrayList<String>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".zip")) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static Map<String, Bitmap> copyStickerIconFiles(Context context, String className){
        String files[] = null;
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        try {
            files = context.getAssets().list(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".png") != -1){
                copyFileIfNeed(context, str, className);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为png结尾
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static Map<String, Bitmap> getStickerIconFilesFromSd(Context context, String className){
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为png结尾
                if (filename.trim().toLowerCase().endsWith(".png") && filename.indexOf("mode_") == -1) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static List<String> getStickerNames(Context context, String className){
        ArrayList<String> modelNames = new ArrayList<String>();
        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + className;
            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".zip") && filename.indexOf("filter") == -1) {
                    String name = subFile[i].getName();
                    modelNames.add(getFileNameNoEx(name));
                }
            }
        }

        return modelNames;
    }

    public static ArrayList<FilterItem> getFilterFiles(Context context, String index){
        ArrayList<FilterItem> filterFiles = new ArrayList<FilterItem>();
        Bitmap iconNature = BitmapFactory.decodeResource(context.getResources(), R.drawable.mode_original);

        if(index.equals("filter_portrait")){
            iconNature = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_portrait_nature);
        }

        filterFiles.add(new FilterItem("original", iconNature, null));

        List<String> filterModels = copyAllFilesRetDirs(context, index);
        Map<String, Bitmap> filterIcons = copyFilterIconFiles(context, index);
        List<String> filterNames = getFilterNames(context, index);

        for(int i = 0; i < filterModels.size(); i++){
            if(filterIcons.get(filterNames.get(i)) != null)
                filterFiles.add(new FilterItem(filterNames.get(i).substring(13), filterIcons.get(filterNames.get(i)), filterModels.get(i)));
            else{
                filterFiles.add(new FilterItem(filterNames.get(i).substring(13), iconNature, filterModels.get(i)));
            }
        }

        return  filterFiles;
    }

    public static List<String> copyFilterModelFiles(Context context, String index){
        String files[] = null;
        ArrayList<String> modelFiles = new ArrayList<String>();

        try {
            files = context.getAssets().list(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + index;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".model") != -1){
                copyFileIfNeed(context, str, index);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".model") && filename.indexOf("filter") != -1) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static Map<String, Bitmap> copyFilterIconFiles(Context context, String index){
        String files[] = null;
        TreeMap<String, Bitmap> iconFiles = new TreeMap<String, Bitmap>();

        try {
            files = context.getAssets().list(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + index;

            File folder = new File(folderpath);

            if(!folder.exists()){
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if(str.indexOf(".png") != -1){
                copyFileIfNeed(context, str, index);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        for (int i = 0; i < subFile.length; i++) {
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();

                if (filename.trim().toLowerCase().endsWith(".png")) {
                    String name = subFile[i].getName();
                    iconFiles.put(getFileNameNoEx(name), BitmapFactory.decodeFile(filename));
                }
            }
        }

        return iconFiles;
    }

    public static List<String> getFilterNames(Context context, String index){
        String files[] = null;
        List<String> modelNames = new ArrayList<>();

        try {
            files = context.getAssets().list(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(files.length > 0)
        {
            File dataDir = context.getExternalFilesDir(null);
            String dataDirPath = null;
            if (dataDir != null) {
                dataDirPath = dataDir.getAbsolutePath();
            }

            for (String fileName : files) {
                String tmpStr = index + File.separator + fileName;
                File modelPath = new File(dataDirPath + File.separator + tmpStr);
                if (modelPath.exists() && modelPath.isDirectory()) {
                    modelNames.add(modelPath.getName());
                }
            }
        }

        return modelNames;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

}
