package com.brucetoo.compiler;

import com.brucetoo.annotation.InjectActivity;
import com.brucetoo.annotation.InjectView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Bruce Too
 * On 09/03/2018.
 * At 16:44
 */

@AutoService(Processor.class)
public class InjectProcessor extends AbstractProcessor {

    private Elements elementUtils;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> diElements = roundEnv.getElementsAnnotatedWith(InjectActivity.class);
        for (Element element : diElements) {
            if(element instanceof TypeElement){
                TypeElement typeElement = (TypeElement) element;
                /**
                 public final class ButterKnife{
                 public static void bindView(MainActivity activity){
                   activity.成员变量 = (类型)activity.findViewById(id)
                 }
                 }
                 */
                //1.构建方法
                MethodSpec.Builder bindViewBuilder = MethodSpec.methodBuilder("bindView")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(void.class)
                        .addParameter(ClassName.get(typeElement.asType()), "activity");

                //2.获取注解类DIActivity中所有的成员变量，通过成员变量构造新类
                List<? extends Element> allMembers = elementUtils.getAllMembers(typeElement);
                for (Element item : allMembers){
                    InjectView diView = item.getAnnotation(InjectView.class);
                    if(diView != null){
                        bindViewBuilder.addStatement("activity.$N = ($T) activity.findViewById($L)",
                                item.getSimpleName(),ClassName.get(item.asType()),diView.value());
                    }
                }
                //3.创建类
                TypeSpec butterKnife = TypeSpec.classBuilder("ButterKnife")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(bindViewBuilder.build())
                        .build();

                //4.生成java文件
                JavaFile file = JavaFile.builder(elementUtils.getPackageOf(typeElement).getQualifiedName().toString(),butterKnife)
                        .build();
                try {
                    file.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                }
            }
        }
        return true;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> result = new HashSet<>();
        //这里必须用 getCanonicalName 获取类名字
        result.addAll(Collections.singleton(InjectActivity.class.getName()));
        result.addAll(Collections.singleton(InjectView.class.getName()));
        return result;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
