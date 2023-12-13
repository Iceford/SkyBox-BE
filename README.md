<!--
 * @Description:
 * @FilePath: \README.md
 * @Author: WhimsyQuester rongquanhuang01@gmail.com
 * @Date: 2023-11-11 15:58:57
 * @LastEditors: WhimsyQuester rongquanhuang01@gmail.com
 * @LastEditTime: 2023-11-30 00:07:33
 * Copyright (c) 2023 by WhimsyQuester , All Rights Reserved.
-->

## 后端项目结构

```bash
├───src
│   └───main
│       ├───java
│       │   └───com
│       │       └───skybox
│       │           ├───annotation      注解
│       │           ├───aspect          切面
│       │           ├───config          配置
│       │           ├───controller      控制器
│       │           │   ├───basecontroller
│       │           │   └───commonfilecontroller
│       │           ├───entity          实体
│       │           │   ├───constants   常量
│       │           │   ├───dto         数据传输对象
│       │           │   ├───enums       枚举
│       │           │   ├───po          持久化对象
│       │           │   ├───query       查询
│       │           │   └───vo          值对象
│       │           ├───exception       异常
│       │           ├───mappers         数据映射器
│       │           ├───service         服务层
│       │           │   └───impl
│       │           ├───task            任务
│       │           └───utils           工具
│       └───resources                   资源目录
│           └───com
│               └───skybox
│                   └───mappers         数据映射器
```

## 前端项目结构

```bash
src
├───assets  静态资源
│   ├───icon
│   └───icon-image
│       └───原图
├───components  可复用组件
│   └───preview
├───js  JavaScript 文件
├───router  路由配置相关文件
├───stores  状态管理相关文件
├───utils   工具类文件
└───views   视图组件
    ├───admin
    ├───main
    ├───recycle
    ├───share
    └───webshare

components：开始开发通用组件，这些组件可以在整个应用程序中重复使用。这样可以提高代码的可维护性和复用性。

views：接下来，开发应用程序的各个页面视图。根据你的项目需求，可能会有多个视图文件夹，如admin、main、recycle、share、webshare等。在每个视图文件夹中，你可以创建与该视图相关的组件、样式和逻辑。

router：在router文件夹中配置应用程序的路由。定义路由路径和对应的视图组件，以及任何必要的路由守卫或其他路由配置。

stores：如果你使用了Vue的状态管理库（如Vuex），则在stores文件夹中定义和管理应用程序的全局状态。这包括定义状态、操作和获取状态的方法。

utils：在utils文件夹中编写通用的工具函数或模块。这些工具函数可以用于处理日期、字符串操作、网络请求等等。这样可以提高代码的可维护性和可读性。

assets：在assets文件夹中存放应用程序所需的静态资源，如图标、图片等。根据你的项目需求，你可以在icon和icon-image文件夹中存放不同类型的图标资源。

js：在js文件夹中存放其他JavaScript文件，如插件、第三方库或其他辅助脚本。

```
