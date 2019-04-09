package com.trc.android.gradle.plugin

import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import org.apache.commons.io.FileUtils

import java.text.SimpleDateFormat
import java.util.jar.JarEntry
import java.util.jar.JarFile

class JavassistInject {

    final static String ROUTER_CLASS = "com.trc.android.router.annotation.uri.RouterUri"


    static void scanClass(String path, HashSet<String> classSet) {
        File dir = new File(path)
        if (!dir.isDirectory()) {
            if (path.endsWith(".jar")) {
                JarFile jarFile = new JarFile(path);
                Enumeration<JarEntry> entrys = jarFile.entries()
                while (entrys.hasMoreElements()) {
                    JarEntry jarEntry = entrys.nextElement()
                    String className = jarEntry.getName().replaceAll('/', '.')
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
                    String className = (filePath.substring(dir.getPath().length() + 1, filePath.length() - 6)).replace((char) File.separatorChar, '.'.charAt(0))
                    classSet.add(className)
                }
            }
        }

    }

    static void generateRouterMapClass(HashSet<String> allScannedClass, ClassPool classPool, File targetDir) {

        try {
            HashSet set = new HashSet()
            getRouterRelatedClasses(allScannedClass, classPool, set)

            if (set.isEmpty()) return
            LogUtil.error("总计扫描到" + set.size() + "个Class文件\n\n")
            CtClass ct = classPool.makeClass("com.trc.android.router.build.AddressList");
//创建类
            StringBuilder stringBuilder = new StringBuilder("private static final Class[] CLASSES = new Class[]{\n");
            def routerUriClass = Class.forName(ROUTER_CLASS)
            for (String className : set) {
                stringBuilder.append(className).append(".class,\n")
                String routerAnnotation = classPool.get(className).getAnnotation(routerUriClass).toString()
                String value = routerAnnotation.substring(ROUTER_CLASS.length() + 8, routerAnnotation.length() - 1)
                LogUtil.error(className + " --- " + value)
            }
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
            LogUtil.error("\n\n\nAddressList.class文件创建成功 位置:" + file.getAbsolutePath() + "/com/trc/android/router/build/AddressList.class")
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void generateRouterDoc(HashSet<String> allScannedClass, ClassPool classPool) {
        try {
            HashSet set = new HashSet()
            getRouterRelatedClasses(allScannedClass, classPool, set)

            if (set.isEmpty()) return


            StringBuilder javaDoc = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("#yyyy-MM-dd hh:mm:ss", Locale.CHINA)
            String generateTime = simpleDateFormat.format(System.currentTimeMillis())
            javaDoc.append("## 总共" + set.size() + "个路由\n")

            for (String className : set) {
                String des
                String meta
                String uri
                for (Object o : classPool.get(className).getAnnotations()) {
                    String annotationStr = o.toString()
                    if (annotationStr.startsWith("@com.trc.android.router.annotation.uri.RouterUri(")) {
                        uri = annotationStr.substring(56, annotationStr.length() - 2).replace('"', '')
                    } else if (annotationStr.startsWith("@com.trc.android.router.annotation.uri.RouterDes")) {
                        des = annotationStr.substring(55, annotationStr.length() - 1)
                        des = des.substring(1, des.length() - 1)
                    } else if (annotationStr.startsWith("@com.trc.android.router.annotation.uri.RouterMeta")) {
                        meta = annotationStr.substring(56, annotationStr.length() - 1)
                        meta = meta.substring(1, meta.length() - 1)
                    }
                }

                javaDoc.append("\n---\n\n* **Des:** ").append(des).append('\n')
                        .append("* **Uri:** ").append(uri).append('\n')
                        .append("* **Class:** ").append(className).append('\n');

                if (null != meta)
                    javaDoc.append("* **Meta:** 参数解释及示例代码如下\n    - ").append(meta.replaceAll("\n", "\n    - ")).append("\n");


                File projectRootDir = null;
                try {
                    projectRootDir = new File("").getCanonicalFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File routerDocFile = new File(projectRootDir, "router.md");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(routerDocFile)
                    fileOutputStream.write(javaDoc.toString().getBytes())
                    fileOutputStream.flush()
                    fileOutputStream.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
            LogUtil.error("Router.md文档创建成功 位置:项目根目录")


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private
    static void getRouterRelatedClasses(HashSet<String> allScannedClass, ClassPool classPool, HashSet set) {
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
    }
}