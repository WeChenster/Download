package com.keyun;

import java.io.BufferedReader;
import java.io.*;
import java.io.FileReader;
import java.io.IOException;


import com.keyun.util.FileReqUtil;
import com.keyun.util.HttpReqUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName HtmlGeterRecouces
 * @Description //获取本地文件的sercid 获取爱课程  课程章节所有资源
 * @Author liwt
 * @Date 2018/11/26$ 20:50$
 * @Version v0.0.1
 */
public class HtmlGeterRecouces {
    public static void main(String[] args) {
//        String s="src/main/resources/secid.txt";
//        String[] sbyte=fileMedthom(s);
//        for (int i = 0 ; i <sbyte.length ; i++ ) {
//            System.out.println(sbyte[i]);
//        }
        String URL_STR="http://www.icourses.cn/web/sword/portal/getRess?sectionId=";
        String  sercid_txt="src/main/resources/secid.txt";
        String[] sbyte=fileMedthom(sercid_txt);
        JSONArray array=JSONArray.fromObject(getJSON(URL_STR,sbyte));//获取所有目录组合的json数据
        System.out.println(array.toString());
        DownloadOpt(array);
    }

    /**
     * @Author liwt
     * @Description //取出文件中的secid
     * @Date 2018/11/26 21:25
     * @param path
     * @throws
     * @return java.lang.String[]
     */
    public static String[] fileMedthom(String path) {
        StringBuffer sbuffer=new StringBuffer();
        try {
            FileReader rd=new FileReader(path);
            BufferedReader bufferRd=new BufferedReader(rd);
            String str=null;
            while ((str=bufferRd.readLine())!=null){
                if(str.startsWith("VM")){
                    String[] chars=str.split(" ");
                    sbuffer.append(chars[1].toString());
                    sbuffer.append("$");
                }
            }
            bufferRd.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return sbuffer.toString().split("\\$");
    }

    /**
     * @Author liwt
     * @Description //通过http获取json数据
     * @Date 2018/11/27 9:10
     * @param url
     * @param arr
     * @throws
     * @return net.sf.json.JSONArray
     */
    public static JSONArray getJSON(String url, String[] arr){
//      http://www.icourses.cn/web//sword/portal/getRess?sectionId=46611
        JSONArray array=new JSONArray();
        for (int i = 0 ; i <arr.length ; i++ ) {
            System.out.println(url+arr[i]);
            String jsonstr=HttpReqUtil.doGet(url+arr[i],null,"utf-8");
            if(jsonstr!=null){
                JSONObject obj=JSONObject.fromObject(jsonstr);
                array.add(i,obj);
            }
        }
        return array;
    }

    /**
     * @Author liwt
     * @Description //遍历定义好的数组   执行下载操作
     * @Date 2018/11/27 10:07
     * @param array
     * @throws
     * @return void
     */
    public static void DownloadOpt(JSONArray array){
        String filePath = "D:\\myfile\\";      //保存目录

        for (int i = 0; i < array.size(); i++) { //所有的目录
            JSONObject obj=array.getJSONObject(i);//单个secid  对象
            if(!obj.isNullObject()&&obj.containsKey("status")&&obj.getString("status").equals("200")){
                JSONObject object= JSONObject.fromObject(obj.get("model"));
                JSONArray dataArray=JSONArray.fromObject(object.get("listRes"));//取到listRes 数组
                for (int j = 0; j < dataArray.size(); j++) { //单个章节内容
                    JSONObject obj2=dataArray.getJSONObject(j);
                    String fullResUrl=obj2.getString("fullResUrl");
                    String characterId=obj2.getString("characterId");
                    String title=obj2.getString("title");
                    String fileName = fullResUrl.substring(fullResUrl.lastIndexOf("."));     //为下载的文件命名

                    if(!fileName.equals(".mp4")){
                        String time= title+String.valueOf(System.currentTimeMillis())+fileName;
                        File file = FileReqUtil.saveUrlAs(fullResUrl, filePath+characterId,time,"GET");
                        if(file!=null){
                            System.out.println("characterId:"+characterId+"下载成功");
                        }
                    }else{
                        System.out.println("MP4格式 过滤下载");
                    }
                }
            }
        }
    }



}
