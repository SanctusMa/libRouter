package com.trc.android.gradle.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class PreDexTransform extends Transform {
    Project mProject

    PreDexTransform(Project project) {
        mProject = project
    }

    /**
     * Transfrom在Task列表中的名字
     * TransformClassesWith + getName() + For + Debug或Release
     */
    @Override
    String getName() {
        return "PreDex"
    }

    /**
     * 需要处理的数据类型，有两种枚举类型
     * CLASSES和RESOURCES，CLASSES代表处理的java的class文件，RESOURCES代表要处理java的资源
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指定 Transform 的作用范围
     *
     * EXTERNAL_LIBRARIES        只有外部库
     * PROJECT                   只有项目内容
     * PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * PROVIDED_ONLY             只提供本地或远程依赖项
     * SUB_PROJECTS              只有子项目。
     * SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * TESTED_CODE               由当前变量(包括依赖项)测试的代码
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 指明当前Transform是否支持增量编译
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 核心方法
     * @param inputs 传过来的输入流，有jar喝目录两种格式
     * @param outputProvider 修改后文件的输出目录
     */
    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        LogUtil.error("RouterPlugin开始扫描所有Class")
        //初始化类池
        ClassPool pool = new ClassPool(null)
        pool.appendSystemPath()
        String androidJarPath = mProject.android.bootClasspath[0].toString()
        //将project.android.bootClasspath 加入android.jar
        pool.appendClassPath(androidJarPath)
        HashSet allScannedClass = new HashSet();
        def lastDest = null
        inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->

                LogUtil.error("jarInput name = " + jarInput.name + ", path = " + jarInput.file.absolutePath)
                pool.appendClassPath(jarInput.file.getAbsolutePath())
                if (!jarInput.name.startsWith("com.android")
                        && !jarInput.name.startsWith("android.")
                        && !jarInput.name.startsWith("com.squareup.")
                        && !jarInput.name.startsWith("com.google.")
                        && !jarInput.name.startsWith("com.facebook.")
                        && !jarInput.name.startsWith("org.apache.")
                        && !jarInput.name.startsWith("org.apache.")
                        && !jarInput.name.startsWith("google.")) {

                    //插入字节码
                    JavassistInject.scanClass(jarInput.file.getAbsolutePath(), allScannedClass)
                }

                //重命名输出文件（同目录 copyFile 会冲突）
                def jarName = jarInput.name
                def md5Name = jarInput.file.hashCode()
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
            input.directoryInputs.each { DirectoryInput directoryInput ->
                LogUtil.error("directoryInput name = " + directoryInput.name + ", path = " + directoryInput.file.absolutePath)
                pool.appendClassPath(directoryInput.file.getAbsolutePath())
                //插入字节码
                JavassistInject.scanClass(directoryInput.file.getAbsolutePath(), allScannedClass)

                //修改后文件的输出路径
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                lastDest = dest
                //将 input 的目录复制到 output 指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
        LogUtil.error("RouterPlugin已完成Class扫描")

        JavassistInject.generateRouterMapClass(allScannedClass, pool, lastDest)

    }


}
