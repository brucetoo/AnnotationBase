Android APT(Annotation Processing Tool)

Android APT(Annotation Processing Tool)
1、概念
1.1、元数据作用
1.2、元注解
2、Annotation生成代码
2.1 创建java annotation module
2.2 创建java compiler module
2.3 App工程配置
2.4 AbstractProcessor使用详解
2.4.1 首先需要在继承类上注解@AutoService(Processor.class)
2.4.2 关键方法解释
2.4.3 process生成类举例
2.4.4 AbstractProcessor调试
3、语言模型包(ing...)

1、概念

Annotations是一种元数据，为一些额外的数据信息，不会参与代码的任何运算逻辑，故而不会对原程序代码的操作产生直接的影响
1.1、元数据作用

创建文档

跟踪代码中的依赖性

执行编译时检查

代码分析

数据还可用于协助程序元素与框架之间的通讯(aspect)

1.2、元注解

@Documented

注解是否将包含在JavaDoc中，表示是否将注解信息添加在java文档中
@Retention

Annotation被保留的时间长短
RetentionPolicy.SOURCE(提供辅助信息)

可以为编译器提供额外信息，以便于检测错误，抑制警告等，如@Override、@SuppressWarnings等这类注解就是用于标识，可以用作一些检验

RetentionPolicy.CLASS(编译时动态处理)

一般会在编译的时候，根据注解标识，动态生成一些类或者生成一些xml，在运行时期，这类注解是没有的，也就是在类加载的时候丢弃。

能在动态生成的类做一些操作，因为没有反射，效率和直接调用方法没什么区别。ParcelableGenerator、butterknife 、androidannotaion都使用了类似技术

RetentionPolicy.RUNTIME(运行时动态处理)

在运行时拿到类的Class对象，然后遍历其方法、变量，判断有无注解声明，然后做一些事情,EventBus就是属于这类

@Target

注解可修饰的对象范围
ElementType	说明
ElementType.PACKAGE	用于记录java文件的package信息
ElementType.TYPE	用于描述类、接口或enum声明
ElementType.CONSTRUCTOR	构造方法声明
ElementType.METHOD	方法声明
ElementType.PARAMETER	参数声明
ElementType.FIELD	字段声明（包括枚举常量），用于描述成员变量
ElementType.LOCAL_VARIABLE	局部变量声明
ElementType.ANNOTATION_TYPE	另一个注释
@Inherited

该注解类型被自动继承
如果一个使用了@Inherited修饰的annotation类型被用于一个class，则这个annotation将被用于该class的子类

2、Annotation生成代码

2.1 创建java annotation module

在此举例创建的java module取名为 “annotation”,此module主要是存放annotation相关的注解类
gradle文件生成示例：

 

​
apply plugin: 'java-library'
​
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}
​
sourceCompatibility = "1.7"
targetCompatibility = "1.7"
注解类示例：

 

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PoetTest {
}
2.2 创建java compiler module

在此举例创建的java module取名为 “compiler”,此module主要是存放Processor代码生成工具类
gradle配置示例：

 

apply plugin: 'java-library'
​
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    //注解处理器，用来生成 META-INF/services/javax.annotation.processing.Processor文件的
    //只需要在你定义的注解处理器上添加 @AutoService(Processor.class)
    implementation 'com.google.auto.service:auto-service:1.0-rc3'
    //square公司开发的java文件生成 神库
    implementation 'com.squareup:javapoet:1.9.0'
    //为了能访问到注解类
    implementation project(':annotation')
}
​
sourceCompatibility = "1.7"
targetCompatibility = "1.7"
​
2.3 App工程配置

 

dependencies {
    implementation project(':annotation') 
    annotationProcessor project(':compiler')
}

注: 在gradle插件2.2版本后，提倡使用annotationProcessor代替android-apt

具体的解释请见APT,annotationProcessor,android-apt,Provided解析
2.4 AbstractProcessor使用详解

2.4.1 首先需要在继承类上注解@AutoService(Processor.class)

目的是使用google auto service简化注解处理器的添加，详细的解释和对比请见介绍编译时注解的使用方法
2.4.2 关键方法解释

 

public class MyProcessor extends AbstractProcessor {
​
    @Override
    public synchronized void init(ProcessingEnvironment env){
      /**
      ProcessingEnviroment提供很多有用的工具类Elements,Types和Filer。
      Filer是个接口，支持通过注解处理器创建新文件 
      Elements 元素操作辅助工具类
      */
    }
​
    @Override
    public boolean process(Set<? extends TypeElement> annoations, RoundEnvironment env) {
     /**
     1.相当于每个处理器的主函数main()。你在这里写你的扫描、评估和处理注解的代码，以及生成Java文件。
     2.输入参数annotations 请求处理的注解类型集合
     3.输入参数RoundEnviroment，可以让你查询出包含特定注解的被注解元素，相当于“有关全局源码的上下文环境”
     4.@return 如果返回 true，则这些注解已声明并且不要求后续 Processor 处理它们；如果返回 false，则这些注解未声明并且可能要求后续 Processor 处理它们
     */
    }
​
    @Override
    public Set<String> getSupportedAnnotationTypes() { 
     //用于指明注解处理器是注册给哪个注解的。注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称。
    }
​
    @Override
    public SourceVersion getSupportedSourceVersion() { 
     //用来指定你使用的Java版本。通常这里返回SourceVersion.latestSupported()
    }
    
    //后面两个方法可以用注解代替
    //@SupportedSourceVersion(SourceVersion.latestSupported())
    //@SupportedAnnotationTypes({// 合法注解全名的集合})
​
}
2.4.3 process生成类举例

关于类的具体生成细节，参考square公司的开源库javapoet有详尽的示例

 

        /** 1.首先要明白要生成一个怎么样的类，比如以下类
         package com.example.helloworld;
         public final class HelloWorld {
         public static void main(String[] args) {
         System.out.println("Hello, JavaPoet!");
         }
         }
         */
​
        //2. 定义方法，按照书写的顺序：限定符，返回值，参数，声明语句等
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello,JavaPoet")
                .build();
        //3. 定义类，也是按照书写的顺序
        TypeSpec helloworld = TypeSpec.classBuilder("HelloWord")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addMethod(main)
                .build();
        //生成java文件对象
        JavaFile file = JavaFile.builder("com.brucetoo.annotation", helloworld)
                .build();
        try {
            //通过processing生成对应的java文件
            file.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
当书写完Processor代码后，点击Android Studio的ReBuild Project，可以在在app的 build/generated/source/apt/debug目录下，即可看到生成的代码。

详尽的示例请见InjectProcessor示例

2.4.4 AbstractProcessor调试

在项目根目录下的 gradle.properties 文件中加入如下两条语句

 

org.gradle.jvmargs=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
org.gradle.parallel=true
点击 Edit Configurations 配置 Remote，直接点击确认用默认配置就行，名字任意定义，注意 address 与 gradle.properties 中的 address 保持一致(默认就是 5005)

点击debug编译按钮

在注解处理器项目中打上断点，然后Build -> Rebuild Project

可参考断点调试AbstractProcessor

3、语言模型包(ing...)