package com.example.commons.utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.nacos.common.codec.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hao.wang
 * @date Created in 2021/7/7
 */
@Slf4j
public class DingTalkUtils {

    //支付相关
    private static final String PAY_WEB_HOOK = "???";
    private static final String PAY_SECRET = "???";
    private static final String PAY_KEYWORD = "支付";
    public static final String[] PAY_PARAM = {PAY_WEB_HOOK, PAY_SECRET, PAY_KEYWORD};


    //发送超时时间10s
    private static final int TIME_OUT = 10000;


    /**
     * 钉钉机器人文档地址https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq
     * <p>
     * 3选1【方式一，自定义关键词 】 【方式二，加签 ，创建机器人时选择加签 secret以SE开头】【方式三，IP地址（段）】
     *
     * @param msgStr     发送内容
     * @param mobileList 通知具体人的手机号码列表 （可选）
     * @return
     */
    public static String sendMsg(String[] params,
                                 String msgStr,
                                 List<String> mobileList) {

        String content = "【" + params[2] + "监测】: " + msgStr;
        try {
            //钉钉机器人地址（配置机器人的webhook）
            Long timestamp = System.currentTimeMillis();
            String sign = getSign(timestamp, params[1]);
            String webhookStr = params[0] +
                    "&timestamp=" +
                    timestamp +
                    "&sign=" +
                    sign;
            // System.out.println("webhook:" + webhook);
            //是否通知所有人
            boolean isAtAll = false;
            //组装请求内容
            String reqStr = buildReqStr(content, isAtAll, mobileList);
            //推送消息（http请求）
            String result = postJson(webhookStr, reqStr);
            log.info("推送结果result == " + result);
            return result;
        } catch (Exception e) {
            log.info("发送群通知异常 异常原因：", e);
            return null;
        }
    }


    /**
     * 组装请求报文
     * 发送消息类型 text
     *
     * @param content
     * @return
     */

    private static String buildReqStr(String content, boolean isAtAll, List<String> mobileList) throws JsonProcessingException {
        //消息内容
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("content", content);
        //通知人
        Map<String, Object> atMap = new HashMap<>();
        //1.是否通知所有人
        atMap.put("isAtAll", isAtAll);
        //2.通知具体人的手机号码列表
        atMap.put("atMobiles", mobileList);
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("msgtype", "text");
        reqMap.put("text", contentMap);
        reqMap.put("at", atMap);
        return new ObjectMapper().writeValueAsString(reqMap);
    }


    private static String postJson(String url, String reqStr) {
        String body = null;
        try {
            body = HttpRequest.post(url).body(reqStr).timeout(TIME_OUT).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }


    /**
     * 自定义机器人获取签名
     * 创建机器人时选择加签获取secret以SE开头
     *
     * @param timestamp
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     */

    private static String getSign(Long timestamp, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);
    }


}
