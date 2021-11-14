先运行package

**然后可以运行下面的命令启动Springboot**
```
java -jar -Dloader.path=resources,lib MoocWeb-0.0.1-SNAPSHOT.jar
```

分离打包使用java命令启动失败 参考 [这个连接](https://blog.csdn.net/u013314786/article/details/81120240)


你需要修改的部分:
1. 需要修改 RunScrapy.java 中的path字段为你的路径
2. 需要修改 MoocUtils.java 中的System.setProperty为你的路径