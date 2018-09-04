package com.trc.android.router.compile;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.Arrays;
import java.util.HashSet;

import javax.lang.model.element.Modifier;

class AnnotatedClass {

    private HashSet<String> typeElementList;

    AnnotatedClass(HashSet<String> typeElements) {
        typeElementList = typeElements;
    }


    JavaFile generateFile() {
        //generateMethod
        MethodSpec.Builder getAnnotatedClasses = MethodSpec.methodBuilder("getAnnotatedClasses")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Class[].class);
        getAnnotatedClasses.addStatement("return CLASSES");

        StringBuilder classInitCodeBlock = new StringBuilder();
        classInitCodeBlock.append("new Class[]{\n");
        StringBuilder javaDoc = new StringBuilder();
        for (String t : typeElementList) {
            Item item = Item.parse(t);
            javaDoc.append("<P><b>Page:</b>&nbsp;&nbsp;<font color=\"#FF0000\">").append(item.des).append("</font>").append('\n')
                    .append("<Br><b>Uri:&nbsp;&nbsp;</b>").append(Arrays.toString(item.uris)).append('\n')
                    .append("<Br><b>Class:&nbsp;&nbsp;</b>\n").append(item.className).append('\n');
            classInitCodeBlock.append(item.className).append(".class").append(',').append('\n');
        }
        classInitCodeBlock.setLength(classInitCodeBlock.length() - 2);
        classInitCodeBlock.append('}');
        FieldSpec.Builder classesField = FieldSpec.builder(Class[].class, "CLASSES")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(CodeBlock.of(classInitCodeBlock.toString()));

        //generaClass
        TypeSpec injectClass = TypeSpec.classBuilder("AddressList")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getAnnotatedClasses.build())
                .addField(classesField.build())
                .addJavadoc(CodeBlock.of("<b>总共" + typeElementList.size() + "个类</b>\n"+javaDoc.toString()))
                .build();
        return JavaFile.builder("com.trc.android.router.build", injectClass).build();
    }
}