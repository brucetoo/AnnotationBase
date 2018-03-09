##Android APT(Annotation Processing Tool)

#### 1、概念
> Annotations是一种元数据，为一些额外的数据信息，不会参与代码的任何运算逻辑，故而不会对原程序代码的操作产生直接的影响

##### 1.1、元数据作用
- 创建文档
- 跟踪代码中的依赖性
- 执行编译时检查
- 代码分析
- 数据还可用于协助程序元素与框架之间的通讯(aspect)

##### 1.2、元注解
- @Documented
  > 注解是否将包含在JavaDoc中，表示是否将注解信息添加在java文档中
- @Retention
  > Annotation被保留的时间长短
  - RetentionPolicy.SOURCE(**提供辅助信息**)
    - 可以为编译器提供额外信息，以便于检测错误，抑制警告等，如@Override、@SuppressWarnings等这类注解就是用于标识，可以用作一些检验
  - RetentionPolicy.CLASS(**编译时动态处理**)
    - 一般会在编译的时候，根据注解标识，动态生成一些类或者生成一些xml，*在运行时期，这类注解是没有的，也就是在类加载的时候丢弃*。
    - 能在动态生成的类做一些操作，因为没有反射，效率和直接调用方法没什么区别。ParcelableGenerator、butterknife 、androidannotaion都使用了类似技术
  - RetentionPolicy.RUNTIME(**运行时动态处理**)
    - 在运行时拿到类的Class对象，然后遍历其方法、变量，判断有无注解声明，然后做一些事情,EventBus就是属于这类
- @Target
  > 注解可修饰的对象范围

    ElementType | 说明
    ---|---
    ElementType.PACKAGE | 用于记录java文件的package信息
    ElementType.TYPE | 用于描述类、接口或enum声明
    ElementType.CONSTRUCTOR | 构造方法声明
    ElementType.METHOD | 方法声明
    ElementType.PARAMETER | 参数声明
    ElementType.FIELD |字段声明（包括枚举常量），用于描述成员变量
    ElementType.LOCAL_VARIABLE | 局部变量声明
    ElementType.ANNOTATION_TYPE | 另一个注释 


- @Inherited
  > 该注解类型被自动继承
  - 如果一个使用了@Inherited修饰的annotation类型被用于一个class，则这个annotation将被用于该class的子类
  
#### 2、注解处理器
> 作用就是生成java代码
