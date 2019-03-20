package com.trc.android.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {
    void apply(Project project) {
        LogUtil.setProject(project)
        LogUtil.error("====================== RouterPlugin Transform插件已注册 ======================")
        def classTransform = new PreDexTransform(project)
        project.android.registerTransform(classTransform)
    }
}