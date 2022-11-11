# 包含javaagent和基于attach机制实现的动态增强简单示例

## javaagent方式使用
项目启动参数添加`-javaagent:/Users/martin/develop/projects/my-attach/agent/target/agent-1.0-SNAPSHOT-jar-with-dependencies.jar`

## attach方式使用
项目启动后得到pid
执行`java -jar -Xbootclasspath/a:/Library/Java/JavaVirtualMachines/jdk1.8.0_333.jdk/Contents/Home/lib/tools.jar my-attach-core.jar <PID> /Users/martin/develop/projects/my-attach/agent/target/agent-1.0-SNAPSHOT-jar-with-dependencies.jar`
