package com.keyun;

import java.io.*;
import java.net.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.keyun.util.FileReqUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @Author liwt
 * @Description //读取本地的HTML   操作文件
 * @Date 2018/11/27 13:47
 */
public class HtmlParser {
    public static void main(String[] args) {
        Document doc = Jsoup.parse(readHtml("src/main/resources/input.html"),"UTF-8");

        Elements chapters=doc.select(".chapter-title-text");

//        Elements links = doc.select("a[data-url]"); //带有href属性的a元素
//        System.out.println(links);

        String pathfoder="D:\\myfile\\制造业成本核算实务";

        for (Element chap:chapters){//章节
            String chapter_name=chap.text();
            Elements id_group=doc.select(chap.attr("href"));
            Elements links = id_group.select("a[data-url]");
            for (Element link : links) {//遍历所有 带有data-url  的a标签
                String text=link.text();
                String linkTitle = link.attr("data-title");
                String linkDataType = link.attr("data-type");
                String linkDataUrl = link.attr("data-url");
                String fileName = linkDataUrl.substring(linkDataUrl.lastIndexOf("."));     //为下载的文件命名
                String time= linkTitle+String.valueOf(System.currentTimeMillis())+fileName;

                File fl=null;
                if(linkDataUrl!=null&&!linkDataUrl.equals("")) {
                    if (linkDataType == null || linkDataType.equals("ppt")) {
                        fl = FileReqUtil.saveUrlAs(linkDataUrl, pathfoder, time, "GET");
                    } else {
                        fl = FileReqUtil.saveUrlAs(linkDataUrl, pathfoder + "\\"+chapter_name+"\\" + text, time, "GET");
                    }
                    if(fl!=null){
                        System.out.println(linkDataUrl+"下载成功");
                    }
                }else{
                    System.out.println("data-url 为空");
                }
            }
        }

    }


    protected List<List<String>> data = new LinkedList<List<String>>();
    /**
     * 获取value值
     *
     * @param e
     * @return
     */
    public static String getValue(Element e) {
        return e.attr("value");
    }

    /**
     * 获取
     * <tr>
     * 和
     * </tr>
     * 之间的文本
     *
     * @param e
     * @return
     */
    public static String getText(Element e) {
        return e.text();
    }

    /**
     * 识别属性id的标签,一般一个html页面id唯一
     *
     * @param body
     * @param id
     * @return
     */
    public static Element getID(String body, String id) {
        Document doc = Jsoup.parse(body);
        // 所有#id的标签
        Elements elements = doc.select("#" + id);
        // 返回第一个
        return elements.first();
    }

    /**
     * @Author liwt
     * @Description //识别属性class的标签
     * @Date 2018/11/27 13:48
     * @param body
     * @param classTag
     * @throws
     * @return org.jsoup.select.Elements
     */
    public static Elements getClassTag(String body, String classTag) {
        Document doc = Jsoup.parse(body);
        // 所有#id的标签
        return doc.select("." + classTag);
    }

    /**
     * 获取tr标签元素组
     *
     * @param e
     * @return
     */
    public static Elements getTR(Element e) {
        return e.getElementsByTag("tr");
    }

    /**
     * 获取td标签元素组
     *
     * @param e
     * @return
     */
    public static Elements getTD(Element e) {
        return e.getElementsByTag("td");
    }
    /**
     * 获取表元组
     * @param table
     * @return
     */
    public static List<List<String>> getTables(Element table){
        List<List<String>> data = new ArrayList<>();

        for (Element etr : table.select("tr")) {
            List<String> list = new ArrayList<>();
            for (Element etd : etr.select("td")) {
                String temp = etd.text();
                //增加一行中的一列
                list.add(temp);
            }
            //增加一行
            data.add(list);
        }
        return data;
    }
    /**
     * 读html文件
     * @param fileName
     * @return
     */
    public static String readHtml(String fileName){
        FileInputStream fis = null;
        StringBuffer sb = new StringBuffer();
        try {
            fis = new FileInputStream(fileName);
            byte[] bytes = new byte[1024];
            while (-1 != fis.read(bytes)) {
                sb.append(new String(bytes));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sb.toString();
    }


}