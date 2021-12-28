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
    +vs2017                      //资源目录
      build_x64.bat              //CMAKE 生成x64工程的脚本
      build_x86.bat              //CMAKE 生成Win32工程的脚本
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
Qt5.14.2及以上
```
#### 3.2 编译运行

- 在engine.h中需要设置app_key_

- 安装CMake 3.10及以上版本,用户根据VS及Qt库的配置选择 build_x64.bat或build_x86 生成vs工程，在x64或x86目录中打开工程，编译。选择1v1_beauty为启动项目

- 选择美颜资源路径为nebeauty所在的路径。如：xxx/data/beauty/nebeauty

#### 3.3 功能模块
##### 3.3.1 startBeauty

调用此接口后，开启美颜引擎。美颜引擎开启后，后续美颜启用才能生效。美颜引擎开启失败或未开启，都会导致美颜无效。

##### 3.3.2 enableBeauty

开启或关闭美颜功能。美颜开启需在美颜引擎开启之后使用。



