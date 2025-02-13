package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * （切面增强类）这个类专门用来增强mapper 里面的添加和更新方法，完成公共字段的自动填充
 */
@Component
@Aspect
public class AutoFillAspect {

    /**
     * 对所有打上这个@AutoFill注解的做前置增强，目的是为了增强这些方法的参数而已
     * @param joinPoint
     */
    @Before("@annotation(com.sky.annotation.AutoFill)")
    public void autoFill(JoinPoint joinPoint) throws NoSuchFieldException, IllegalAccessException {

        // 0 获取方法参数:: 添加或者更新的参数只有一个
        Object arg = joinPoint.getArgs()[0];

        // 1 获取方法签名
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();

        // 2 根据方法签名获取方法的Method对象
        Method method = signature.getMethod();

        // 3 根据方法Method对象，获取方法身上的注解 @AutoFill
        AutoFill annotation = method.getAnnotation(AutoFill.class);

        // 4 根据注解的对象，获取它里面的属性值
        OperationType value = annotation.value();

        // 5 判定是添加还是更新，以便于区分到底是填充4个还是2个值
        if(value == OperationType.INSERT){
            // 添加操作
//            System.out.println("来自动填充添加的属性了..." + method);
            // 5.1 获取到了参数的字节码对象
            Class clazz = arg.getClass();

            // 5.2 可以获取属性、 获取4个set方法
            Field f1 = clazz.getDeclaredField("createTime");
            Field f2 = clazz.getDeclaredField("updateTime");
            Field f3 = clazz.getDeclaredField("createUser");
            Field f4 = clazz.getDeclaredField("updateUser");

            // 5.3 暴力反射
            f1.setAccessible(true);
            f2.setAccessible(true);
            f3.setAccessible(true);
            f4.setAccessible(true);

            // 5.4 给属性赋值
            f1.set(arg, LocalDateTime.now());
            f2.set(arg, LocalDateTime.now());
            f3.set(arg, BaseContext.getCurrentId());
            f4.set(arg, BaseContext.getCurrentId());

        }
        else{
            // 更新操作
//            System.out.println("来自动填充更新的属性了..." + method);
            // 5.1 获取到了参数的字节码对象
            Class clazz = arg.getClass();

            // 5.2 可以获取属性、 获取4个set方法
            Field f1 = clazz.getDeclaredField("updateTime");
            Field f2 = clazz.getDeclaredField("updateUser");

            // 5.3 暴力反射
            f1.setAccessible(true);
            f2.setAccessible(true);

            // 5.4 给属性赋值
            f1.set(arg, LocalDateTime.now());
            f2.set(arg, BaseContext.getCurrentId());

        }

        // 6 通过反射来取得对应的4个属性或者2个属性 对象

        // 7 通过反射直接给他们赋值即可



    }
}
