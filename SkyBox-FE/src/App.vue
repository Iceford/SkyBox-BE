<!--
 * @Description: 一个 Vue 组件的代码片段
 * @FilePath: \SkyBox-FE\src\App.vue
 * @Author: WhimsyQuester rongquanhuang01@gmail.com
 * @Date: 2023-11-14 11:54:47
 * @LastEditors: WhimsyQuester rongquanhuang01@gmail.com
 * @LastEditTime: 2023-11-14 17:20:41
 * Copyright (c) 2023 by WhimsyQuester , All Rights Reserved. 
-->

<template>
  <el-config-provider :locale="locale" :message="config">
    <router-view></router-view>
  </el-config-provider>
</template>

<script setup>
import { reactive } from "vue";
import zhCn from "element-plus/lib/locale/lang/zh-cn";
const locale = zhCn;
const config = reactive({
  max: 1,
});
</script>

<style scoped>
</style>
