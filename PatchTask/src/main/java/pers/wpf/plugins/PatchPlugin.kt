package pers.wpf.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class PatchPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("patchConfig", PatchConfig::class.java)
        target.tasks.register("fixToGetPatch", PatchTask::class.java) {
//            it.dependsOn("assembleRelease")
        }
    }

}