package com.github.adebmbng.gorepogen.services

import com.github.adebmbng.gorepogen.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
