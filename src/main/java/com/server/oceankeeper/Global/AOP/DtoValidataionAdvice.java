package com.server.oceankeeper.Global.AOP;

import com.server.oceankeeper.Global.Exception.DtoValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;
@Component
@Aspect
public class DtoValidataionAdvice {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping(){}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping(){}


    //유효성 검사 aop로 따로 빼놓았음
    @Around("postMapping() || putMapping()")
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs(); // jointPoint의 매개변수
        for(Object arg : args){

            //valid 통과를못해서 bindingResult에 오류가 있을 경우 예외를 던진다.
            if(arg instanceof BindingResult){
                BindingResult bindingResult = (BindingResult) arg;
                if(bindingResult.hasErrors()){
                    Map<String, String> errorMap = new HashMap<>();

                    for(FieldError error : bindingResult.getFieldErrors()){
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    throw new DtoValidationException("유효성 검사 실패", errorMap);
                }
            }
        }
        //정상적 메서드 실행
        return proceedingJoinPoint.proceed();
    }
}
