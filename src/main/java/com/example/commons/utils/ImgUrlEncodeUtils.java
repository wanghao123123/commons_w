package com.example.commons.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hao.wang
 * @date Created in 2021/4/30
 */
@Slf4j
public class ImgUrlEncodeUtils {

    public static String imgUrlEncode(String regex,String chars){
        Pattern https = Pattern.compile(regex);
        Matcher matcher = https.matcher(chars);
        List<Integer> i = new ArrayList<>();
        while (true){
            boolean b = matcher.find();
            if (b){
                int start = matcher.start();
                i.add(start);
            }else{
                break;
            }
        }
        i.add(chars.length());
        StringJoiner b =new StringJoiner(",");
        try {
        for (int i1 = 0; i1 < i.size()-1; i1++) {
            String substring = chars.substring(i.get(i1), i.get(i1+1));
            if(substring.endsWith(",")){
                String substring1 = substring.substring(0,substring.length() - 1);
                b.add(URLEncoder.encode(substring1, "utf-8"));
            }else{
                b.add(URLEncoder.encode(substring, "utf-8"));
            }
        }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        return b.toString();
    }
}
