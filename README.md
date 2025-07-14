# Java Red Alert 2

### 简介
本项目是由 @Charlie-Skywalker 发起的一个复刻红警2的开源项目，起初作者只是想分享自己的开发过程和成果在视频平台上，没想到吸引了不少人，其中还有志同道合的人想要参与共同开发，于是有了这个开源项目。

### 玩家指引
* 游戏启动
  * 拉取项目到本地后，设置运行环境为jdk1.8，运行MainTest.main方法即可启动游戏

### 开发者指引
* 源代码目录介绍
  * game3d目录，没啥用
  * j3d目录，为方便开发时查看渲染效果的目录
  * redAlert目录，游戏代码目录
* resource目录介绍
  * pal目录，存放pal素材
  * png目录，存放png素材
  * shp目录，存放shp素材
  * temp目录，存放地图资源
  * wav目录，存放音效素材

* 游戏主要由界面图形渲染线程，物品（ShapeUnit）帧计算线程池，AWT事件线程，和多个Swing容器监控线程组成
* 核心类（提供关键数据结构和算法）
  * CenterPoint
  * ShpFileReader
  * PointUtil
  * XunLuBean
  * MoveUtil
* 其他
  * ShapeUnit 是游戏中所有单位的公共父类
  * MainPanel 是游戏主界面，绘制游戏场景和单位
  * ShpResourceCenter 是资源中心，里面记录了当前玩家选中的单位、场上可移动的单位等
  * GameContext 记录了游戏的上下文

想了解更多信息请查看代码

## 文档
&emsp;&emsp;由于是本人个人兴趣所致才写的项目，代码可能没有那么完美，希望能有更多的人加入来完善它。

&emsp;&emsp;如果有疑问可以直接搜索 ISSUE 或者 在上面直接提交问题。

## 参考
&emsp;&emsp;由于这是业余项目，因此，在写该项目的过程中，参考了很多网友分享的技术文章、示例代码等。包括但不限于以下列出的几个：
1. https://xxxx
2. https://xxxx
3. https://xxxx

&emsp;&emsp;还有网上一些其他的未列出的文章，目前不记得地址了，如果您发现其中有直接引用或借鉴您的地方，请与我联系，我会再第一时间进行处理，谢谢！

## FAQ
Q：是否支持联机？

A：属于开发计划的一环，具体时间我们也无法确定。

Q：如何参与开发？

A：请查看如何参与贡献

## 如何贡献
1. FORK -> PR
2. 加入javaRedAlert2开发，共同完善

## 许可证
MIT License © Charlie-Skywalker