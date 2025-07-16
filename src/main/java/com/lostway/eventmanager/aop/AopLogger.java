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

    @Around("execution(* com.lostway.eventmanager.service..*(..))")
    public Object logServiceMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Result result = getResult(joinPoint);
        log.debug("Вызван сервис: {}. Метод: {}. Параметры: {}. Итог: {}", result.className, result.methodName, result.argsString, result.returnValue);
        return result.returnValue;
    }

    @Around("execution(* com.lostway.eventmanager.controller..*(..))")
    public Object logControllerMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Result result = getResult(joinPoint);
        log.info("Вызван контроллер: {}. Метод: {}. Параметры: {}. Итог: {}", result.className(), result.methodName(), result.argsString(), result.returnValue());
        return result.returnValue();
    }

    private static Result getResult(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        String argsString = args.length > 0 ? Arrays.toString(args) : "без параметров";
        Object returnValue = joinPoint.proceed();
        return new Result(methodName, className, argsString, returnValue);
    }

    private record Result(String methodName, String className, String argsString, Object returnValue) {
    }
}
