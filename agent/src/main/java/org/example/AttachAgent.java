package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class AttachAgent {

    public static void agentmain(String args, Instrumentation inst) {
        try {
            System.out.println("start agentmain");
            String className = "com.doodl6.springboot.common.web.response.MapResponse";
            String methodName = "appendData";

            new AgentBuilder.Default()
                    .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                    .disableClassFormatChanges()
                    .type(ElementMatchers.named(className))
                    .transform((DynamicType.Builder<?> builder,
                                TypeDescription type,
                                ClassLoader loader,
                                JavaModule module) -> builder.visit(Advice.to(MyAdvice.class).on(ElementMatchers.named(methodName))))
                    .installOn(inst);
            System.out.println("finish agentmain");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void premain(String args, Instrumentation inst) {
        try {
            System.out.println("start premain");
            String className = "com.doodl6.springboot.common.web.response.MapResponse";
            String methodName = "appendData";

            AgentBuilder.Listener listener = new AgentBuilder.Listener() {
                @Override
                public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                    if (className.equals(s)) {
                        System.out.println("onDiscovery:" + s);
                    }
                }

                @Override
                public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {
                    System.out.println("onTransformation");
                }

                @Override
                public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                }

                @Override
                public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {
                    System.out.println("onError");
                    throwable.printStackTrace();
                }

                @Override
                public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                    if (className.equals(s)) {
                        System.out.println("onComplete:" + s);
                    }
                }
            };

            new AgentBuilder.Default().type(ElementMatchers.named(className))
                    .transform((builder, typeDescription, classLoader1, javaModule) -> builder.method(ElementMatchers.named(methodName))
                            .intercept(MethodDelegation.to(MyInterceptor.class)))
                    .with(listener)
                    .installOn(inst);
            System.out.println("finish premain");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static class MyInterceptor {

        @RuntimeType
        public static Object intercept(@Origin Method method,
                                       @SuperCall Callable<?> callable, @AllArguments Object[] args) throws Exception {
            System.out.println("开始执行方法: " + method.getName() + "，入参：" + Arrays.toString(args));
            Object result = callable.call();
            System.out.println(method.getName() + "方法执行完成，回参：" + result);
            return result;
        }
    }

    public static class MyAdvice {
        @Advice.OnMethodEnter
        public static void enter(@Advice.Origin Method method, @Advice.AllArguments Object[] args) {
            System.out.println("开始执行方法: " + method.getName() + "，入参：" + Arrays.toString(args));

        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Advice.Origin Method method, @Advice.Return Object result) {
            System.out.println(method.getName() + "方法执行完成，回参：" + result);
        }
    }
}