package com.trc.android.router.compile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

public class DocUtil {

    public static void generateDoc(HashSet<String> typeElementList) {
        StringBuilder javaDoc = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("#yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        String generateTime = simpleDateFormat.format(System.currentTimeMillis());
        javaDoc.append("## 总共扫描到" + typeElementList.size() + "个类\n")
                .append("## 生成时间:" + generateTime).append("\n\n");
        int i = 0;
        for (String t : typeElementList) {
            i++;
            Item item = Item.parse(t);
            javaDoc.append("\n---\n* **Des:** ").append(item.des).append('\n')
                    .append("* **Uri:** ").append(Arrays.toString(item.uris)).append('\n')
                    .append("* **Class:** ").append(item.className).append('\n');
            if (!"null".equals(item.meta))
                javaDoc.append("* **Meta:** 参数解释及示例代码如下\n\n```\n").append(item.meta).append("\n```\n");
        }
        System.out.println(System.getProperties());
        File projectRootDir = null;
        try {
            projectRootDir = new File("").getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File routerDocFile = new File(projectRootDir, "router.md");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(routerDocFile);
            fileOutputStream.write(javaDoc.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
