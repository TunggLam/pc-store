package com.example.pcstore.aop;

import com.example.pcstore.exception.AuthenticationException;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.redis.TokenRedisService;
import com.example.pcstore.utils.JWTUtils;
import com.example.pcstore.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class SecuredAspect {

    private static final Logger LOGGER = LoggingFactory.getLogger(SecuredAspect.class);
    public static final String AUTHORIZATION = "Authorization";

    private final TokenRedisService tokenRedisService;

    @Before(value = "@annotation(com.example.pcstore.aop.Secured)")
    public void before(JoinPoint joinPoint) {
        try {
            /* Lấy ra role được chỉ định ở API */
            String role = getCurrentRole(joinPoint);

            /* Lấy token từ redis. Token này được lưu khi khác hàng đăng nhập thành công */
            String tokenRedis = tokenRedisService.get(JWTUtils.getUsername()).trim();

            /* Lấy token từ header */
            String tokenHeaders = JWTUtils.getCurrentToken();

            /* Lấy danh sách role từ token */
            List<String> rolesCurrent = JWTUtils.getRoles(tokenHeaders);

            /* Nếu không có role nào thì chỉ cần kiểm tra xem có trùng token được lưu ở Redis hay không */
            if (StringUtils.isNullOrEmpty(role)) {
                checkEqualsToken(tokenRedis, tokenHeaders);
            } else {
                switch (role) {
                    case "ROLE_ADMIN", "ROLE_USER": {
                        checkRole(rolesCurrent, role);
                        checkEqualsToken(tokenRedis, tokenHeaders);
                        break;
                    }
                    case "ALL": {
                        checkEqualsToken(tokenRedis, tokenHeaders);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw e;
            }
            LOGGER.error("Unauthorized. {}", e.getMessage());
            throw new AuthenticationException();
        }
    }

    private static void checkRole(List<String> rolesCurrent, String role) {
        if (!rolesCurrent.contains(role)) {
            throw new AuthenticationException("Bạn không có quyền truy cập vào chức năng này", 403);
        }
    }

    private static void checkEqualsToken(String token, String tokenCurrent) {
        /* Nếu không có token được lưu ở redis hoặc token trên header thì sẽ throw */
        if (StringUtils.isNullOrEmpty(token) || StringUtils.isNullOrEmpty(tokenCurrent)) {
            throw new AuthenticationException();
        }

        /* Nếu token trên header và token trong redis không trùng nhau cũng throw */
        if (!tokenCurrent.equals(token)) {
            throw new AuthenticationException();
        }
    }

    private String getCurrentRole(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Secured secured = method.getAnnotation(Secured.class);
        return secured.role().getValue();
    }

}

