package com.example.commons.utils.auth;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Sets;
import com.zzyy.framework.common.exception.BizException;
import com.zzyy.framework.common.exception.WebServerException;
import com.zzyy.framework.common.util.ZSON;
import com.zzyy.framework.web.annotation.Authorize;
import com.zzyy.leaf.bean.db.AuthorityDB;
import com.zzyy.leaf.service.auth.AuthorityService;
import com.zzyy.leaf.tools.AuthTools;
import com.zzyy.leaf.tools.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 权限注册器
 *
 * @author hao.wang
 * @date Created in 2021/3/13
 */

@Lazy
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorityAutoRegistrar {

    private final AuthorityService authorityService;

    @EventListener(ApplicationStartedEvent.class)
    public void afterStart(ApplicationStartedEvent e) {

        // 获取所有需要注册的权限信息
        List<AuthorityDB> infos = new ArrayList<>();

        Map<String, String> methodUrl = new HashMap<>();
        RequestMappingHandlerMapping rmhm = e.getApplicationContext().getBean(RequestMappingHandlerMapping.class);
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : rmhm.getHandlerMethods().entrySet()) {
            Method method = entry.getValue().getMethod();
            Set<String> patterns = entry.getKey().getPatternsCondition().getPatterns();
            Optional<String> urlOpt = patterns.stream().findFirst();
            if (urlOpt.isEmpty()) {
                throw new RuntimeException("a mapping has no value. " + method.toString());
            }
            methodUrl.put(method.getDeclaringClass().getName() + method.getName(), urlOpt.get());
        }

        Map<String, Object> controllers = e.getApplicationContext().getBeansWithAnnotation(RestController.class);
        if (CollUtil.isEmpty(controllers)) {
            return;
        }
        for (Object controller : controllers.values()) {
            String clsName = controller.getClass().getName();
            // 直接在方法上定义， 不推荐
            collectInfo(clsName, controller.getClass(), methodUrl, infos);

            // 在接口定义
            Class<?>[] interfaces = controller.getClass().getInterfaces();
            if (interfaces.length == 0) {
                continue;
            }
            for (Class<?> i : interfaces) {
                collectInfo(clsName, i, methodUrl, infos);
            }
        }

        authorityService.saveAuthority(infos);

    }

    private void collectInfo(String clsName, Class<?> clz, Map<String, String> methodUrl, List<AuthorityDB> infos) {
        ReflectionUtils.doWithMethods(clz, method -> {
            Authorize authorize = method.getAnnotation(Authorize.class);
            String url = methodUrl.get(clsName + method.getName());
            String[] any = authorize.any();
            if (CommonUtils.isEmpty(any)) {
                AuthTools.addAuth(url, new AuthorityDB());
                return;
            }
            if (any.length != 2) {
                log.error("当前接口权限配置格式有误{}", ZSON.toJson(authorize));
                throw new BizException(WebServerException.SYSTEM_ERROR, "需要权限的接口any参数不正确");
            }
            String defaultName = any[1];
            AuthorityDB db = new AuthorityDB();
            db.setPath(Sets.newHashSet(url));
            db.setDefaultName(defaultName);
            String flag = any[0];
            db.setFlag(flag);
            String permissionKey = flag.split(":")[0];
            db.setPermissionKey(permissionKey);
            infos.add(db);
        }, method -> method.isAnnotationPresent(Authorize.class));
    }

}
