package com.trc.android.router.compile;

import com.google.auto.service.AutoService;
import com.trc.android.router.annotation.compile.RouterAppModule;
import com.trc.android.router.annotation.uri.RouterDes;
import com.trc.android.router.annotation.uri.RouterMeta;
import com.trc.android.router.annotation.uri.RouterUri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.trc.android.router.annotation.uri.RouterUri"
        , "com.trc.android.router.annotation.compile.RouterAppModule"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RouterAnnotationProcessor extends AbstractProcessor {
    private Filer mFiler; //文件相关的辅助类
    HashSet<String> hashSet = new HashSet<>();
    private boolean isAppModule;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            processRouterComponent(roundEnv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void processRouterComponent(RoundEnvironment roundEnv) throws Exception {
        if (roundEnv.getElementsAnnotatedWith(RouterAppModule.class).iterator().hasNext()) {
            isAppModule = true;
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(RouterUri.class)) {
            Item item = new Item();
            item.className = element.toString();
            if (element.getAnnotation(RouterDes.class) != null)
                item.des = element.getAnnotation(RouterDes.class).value();
            if (element.getAnnotation(RouterMeta.class)!=null)
                item.meta = element.getAnnotation(RouterMeta.class).value();
            item.uris = element.getAnnotation(RouterUri.class).value();
            hashSet.add(item.toString());
        }
        if (!roundEnv.processingOver()) return;
        if (isAppModule || !hashSet.isEmpty()) {
            File file = new File(new File("").getAbsolutePath() + "/build/router.txt");
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) file.createNewFile();
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String className = br.readLine();
            LinkedList<String> list = new LinkedList<>();
            list.addAll(hashSet);
            while (null != className) {
                if (hashSet.add(className)) {
                    list.remove(className);
                }
                className = br.readLine();
            }
            fis.close();

            FileOutputStream fos = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            for (String str : list) {
                osw.write(str + "\n");
            }
            osw.flush();
            osw.close();


            if (isAppModule) {
                AnnotatedClass annotatedClass = new AnnotatedClass(hashSet);
                annotatedClass.generateFile().writeTo(mFiler);
                file.deleteOnExit();
                String s = "注意本次扫描到的Class公共有" + hashSet.size() + "个";
                printBlue(s);
                DocUtil.generateDoc(hashSet);
            }

        }
    }

    private void printBlue(String s){
        System.out.println("\033[34;4m"+s+"\033[0m");
    }


}