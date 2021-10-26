# Demo运行说明文档-Windows 

### 目录：
本文档内容目录：

[TOC]

------
### 1. 简介 
本文档旨在说明如何将NERtc SDK的Windows Demo运行起来，体验NERtc SDK美颜的功能。1v1_beauty 是 NERtc 的面部跟踪和虚拟道具功能在PC中的集成，作为一款集成示例。

------
### 2. Demo文件结构
本小节，描述Demo文件结构，各个目录，以及重要文件的功能。

```
+personcall-1v1_nertc_beauty
  +3rdparty                      //第三方库目录
    +jsoncpp                     //json库文件目录
    +nertc                       //NERtc SDK库文件目录
  +build_prj                     //工程生成目录
    +data                        //资源目录
    +x86                         //可执行文件执行所需的动态库目录
    build.bat                    //CMAKE 生成VS工程的脚本
    demo_debug_copy_win32.bat    //拷贝动态库脚本
  +source                        //GUI文件目录
  +data 			  	         //资源目录
```
------
### 3. 运行Demo 

#### 3.1 开发环境
##### 3.1.1 支持平台
```
Windows7及以上
```
##### 3.1.2 开发环境
```
vs2017
vs2017解决方案平台：Win32
Qt5.14.2及以上
```
#### 3.2 编译运行

- 安装CMake 3.10及以上版本, 点击build.bat 生成脚本，在build目录中打开工程，编译。选择1v1_beauty为启动项目
- 编译运行会报找不到动态库的错误，此时点击demo_debug_copy_win32.bat用于库的拷贝
