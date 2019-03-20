package com.trc.android.gradle.plugin

import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

class JavassistInject {

    final static String ROUTER_CLASS = "com.trc.android.router.annotation.uri.RouterUri"


    static void scanClass(String path, HashSet<String> classSet) {
        File dir = new File(path)
        if (!dir.isDirectory()) {
            if (path.endsWith(".jar")) {
                JarFile jarFile = new JarFile(path);
                Enumeration<JarEntry> entrys = jarFile.entries();
                while (entrys.hasMoreElements()) {
                    JarEntry jarEntry = entrys.nextElement();
                    String className = jarEntry.getName().replace(File.separator, ".");
                    if (className.endsWith(".class")) {
                        className = className.substring(0, className.length() - 6)
                        classSet.add(className)
                    }

                }
            } else {
                LogUtil.error("III>>>:III>>>:III>>>:III>>>:III>>>:III>>>:III>>>:III>>>:III>>>:III>>>:" + path)
            }

        } else {
            //遍历文件夹
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                // 确保当前文件是class文件，并且不是系统自动生成的class文件，暂时屏蔽掉kotlin的类
                if (filePath.endsWith(".class")) {
                    String className = (filePath.substring(dir.getPath().length() + 1, filePath.length() - 6)).replaceAll(File.separator, '.')
                    classSet.add(className)
                }
            }
        }

    }

    static void generateRouterMapClass(HashSet<String> allScannedClass, ClassPool classPool, File targetDir) {

        try {
            HashSet set = new HashSet()
            for (String className : allScannedClass) {
                if (className.startsWith("android.")) continue
                if (className.startsWith("com.android")) continue
                try {
                    CtClass c = classPool.getCtClass(className)
                    Object[] annotations = c.getAnnotations()
                    for (int i = 0; i < annotations.length; i++) {
                        if (annotations[i].toString().contains(ROUTER_CLASS)) {
                            set.add(className)
                            break
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }

            if (set.isEmpty()) return
            LogUtil.error("总计扫描到" + set.size() + "个Class文件\n\n")
            CtClass ct = classPool.makeClass("com.trc.android.router.build.AddressList");
//创建类
            StringBuilder stringBuilder = new StringBuilder("private static final Class[] CLASSES = new Class[]{\n");
            for (String className : set) {
                stringBuilder.append(className).append(".class,\n")
                String routerAnnotation = classPool.get(className).getAnnotation(Class.forName("com.trc.android.router.annotation.uri.RouterUri")).toString()
                String value = routerAnnotation.substring(ROUTER_CLASS.length() + 8, routerAnnotation.length() - 1)
                LogUtil.error(className + " --- " + value)
            }
            if (!set.isEmpty())
                stringBuilder.setLength(stringBuilder.length() - 2)
            stringBuilder.append('};')
            String fieldExpresstion = stringBuilder.toString()
            CtField ctField = CtField.make(fieldExpresstion, ct)
            ct.addField(ctField)
            CtMethod ctMethod = CtMethod.make("public static Class[] getAnnotatedClasses() {\n" +
                    "    return CLASSES;\n" +
                    "  }", ct)
            ct.addMethod(ctMethod)
            ct.writeFile("./.gradle/router/")
//            ct.defrost()
            File file = new File("./.gradle/router/")
            FileUtils.copyDirectory(file, targetDir)
            LogUtil.error("\n\n\n生成AddressList, 位置:" + file.getAbsolutePath() + "/com/trc/android/router/build/AddressList.class")
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }


}