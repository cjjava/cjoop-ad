### cjoop-ad - 行政区划数据抓取工具
# Installation

## Java Requirements
* 在你的电脑上需要有JDK1.7+的环境.

## Maven Requirements
* 在你的电脑上需要安装maven环境.

#### Install with mvn

```sh
$ mvn package
```

target会生成相应的cjoop-ad-crawler-0.0.1-SNAPSHOT-bin.zip压缩包,复制到任意位置,解压后运行startup.bat文件启动程序.支持多个省同时下载数据包,也可以多台电脑同时运行该程序.只需将最后的文件汇总即可.抓取的数据包在文件夹province中.文件名:四川省_51.txt

![](https://github.com/cjjava/cjoop-ad/blob/master/show.jpg)

## Found a bug?
如果有请在[这里](https://github.com/cjjava/cjoop-ad/issues/new)提交,我会及时修复.
