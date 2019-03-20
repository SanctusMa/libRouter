package com.trc.android.gradle.plugin

import org.gradle.api.Project


class LogUtil {
    private static Project sProject
     static setProject(Project project){
        sProject = project
    }

     static void error(String content){
        sProject.logger.error(content)
    }

}