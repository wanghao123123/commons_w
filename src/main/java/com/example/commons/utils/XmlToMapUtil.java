package com.example.commons.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kyo
 * @date Created in 2021/7/7
 */

public class XmlToMapUtil {

    /**
     * xml字符串转map
     *
     * @param xml
     * @return
     */
    public static Map<String, Object> xml2map(String xml) throws DocumentException {
        Document doc = null;
        //将xml字符串转为document对象
        doc = DocumentHelper.parseText(xml);
        Map<String, Object> map = new HashMap<>();
        if (doc == null) {
            return map;
        } else {
            //获取根节点
            Element rootElement = doc.getRootElement();
            element2map(rootElement, map);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static void element2map(Element elmt, Map<String, Object> map) {
        if (null == elmt) {
            return;
        }
        String name = elmt.getName();

        //判断当前节点的内容是否为文本（最里面一层节点）
        if (elmt.isTextOnly()) {
            map.put(name, elmt.getText());
        } else {
            Map<String, Object> mapSub = new HashMap<>();

            //获取当前节点的所有子节点
            List<Element> elements = (List<Element>) elmt.elements();

            //利用递归获取节点值
            for (Element elmtSub : elements) {
                element2map(elmtSub, mapSub);
            }
            Object first = map.get(name);

            //判断
            if (null == first) {
                map.put(name, mapSub);
            } else {
                if (first instanceof List<?>) {
                    ((List<Map<String, Object>>) first).add(mapSub);
                } else {
                    List<Object> listSub = new ArrayList<Object>();
                    listSub.add(first);
                    listSub.add(mapSub);
                    map.put(name, listSub);
                }
            }
        }
    }
}
