package com.lostway.eventmanager.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AopLogger {

    @Around("execution(* com.lostway.eventmanager..*(..))")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        String argsString = args.length > 0 ? Arrays.toString(args) : "без параметров";

        try {
            Object returnValue = joinPoint.proceed();
            log.info("Класс: {}. Метод: {}. Параметры: {}. Итог: {}", className, methodName, argsString, returnValue);
            return returnValue;
        } catch (Throwable e) {
            log.error("Ошибка в {}.{} с параметрами {}: {}", className, methodName, argsString, e.getMessage(), e);
            throw e;
        }
    }
}
