package com.shop.CategoryServiceRest.Aop;

import com.shop.CategoryServiceRest.Controller.CategoryController;
import com.shop.CategoryServiceRest.Model.Category;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.NoSuchElementException;

@Component
@Aspect
public class CategoryControllerAspect {
    private final static Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Around("@annotation(NoSuchElementPointcut)")
    public Object onThrowNoElement(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = null;
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] params = methodSignature.getMethod().getParameters();

        for (int i = 0; i < method.getParameterCount(); ++i) {
            for (Annotation ann : params[i].getAnnotations()) {
                if (ann instanceof PathVariable && ((PathVariable)ann).value().equals("id")) {
                    id = (Long)args[i];
                    break;
                }
            }

            if (id != null) {
                break;
            }
        }

        try {
            return joinPoint.proceed();
        } catch (NoSuchElementException ex) {
            logger.warn("Category with id - " + id + " not found");
            logger.error(ex.toString());

            Class<?> returnValue = methodSignature.getMethod().getReturnType();
            if (returnValue != Void.class) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else {
                return Void.class;
            }
        }
    }

    @Around("@annotation(BadRequestPointcut)")
    public Object onBadRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        BindingResult bindingResult = null;
        Category category = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg.getClass().getSimpleName().contains("BindingResult")) {
                bindingResult = (BindingResult)arg;
            }

            if (arg.getClass().getSimpleName().equals("Category")) {
                category = (Category)arg;
            }
        }

        if (bindingResult == null || bindingResult.hasErrors()) {
            logger.info("Bad request on category information");
            return new ResponseEntity<>(category, HttpStatus.BAD_REQUEST);
        } else {
            return joinPoint.proceed();
        }
    }
}
