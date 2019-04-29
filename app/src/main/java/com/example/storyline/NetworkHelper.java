package com.example.storyline;

import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHelper {

    private final static String protocode = "http://", serverIP = "123.202.177.194/", mainFolder = "StoryLine/", phpFunctionFolder = "phpFunction/", imageFolder = "image/", iconFolder = "icon/";
    private final static String[] functions = {
            "login",//0
            "register",//1
            "listOwnStory",//2
            "createStory",//3
            "listStoryNode",//4
            "createStoryNode",//5
            "getNodeCount"//6
    };

    //=======================================================================


    private static String getURL(int i) {
        if (i >= 0 && i < functions.length) {
            return protocode + serverIP + mainFolder + phpFunctionFolder + functions[i] + ".php";
        }
        return null;
    }
    public static String getImageFolderFullPath() {
        return protocode + serverIP + mainFolder + imageFolder;
    }


    private static String send(String url, String data) {
        HttpURLConnection conn = null;
        try {
            URL s = new URL(url);
            conn = (HttpURLConnection) s.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String login(String name, String password) {
        return send(getURL(0), "name=" + name + "&password=" + password);
    }

    public static String register(String name, String password) {
        return send(getURL(1), "name=" + name + "&password=" + password);
    }

    public static String listStory(String id) {
        return send(getURL(2), "id=" + id);
    }

    public static String createStory(String id,String title) {
        return send(getURL(3), "id=" + id+"&title="+title);
    }

    public static String listNode(String storyId) {
        return send(getURL(4), "storyId=" + storyId);
    }

    public static String createStoryNode(String storyId,String des,String image,String lat,String lng) {
        return send(getURL(5), "storyId=" + storyId+"&des="+des+"&image="+image+ "&lat="+lat+"&lng="+lng);
    }


    public static Drawable downloadImage(String fileFullName) {
        fileFullName = NetworkHelper.getImageFolderFullPath() + fileFullName;
        try {
            return Drawable.createFromStream((InputStream) new URL(fileFullName).getContent(), fileFullName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch ( OutOfMemoryError e ){
            e.printStackTrace();
        } catch ( Exception e ){
            e.printStackTrace();
        }   return null;
    }

}
