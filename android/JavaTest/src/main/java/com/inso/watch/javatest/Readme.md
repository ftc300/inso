

第二章：Java语法

    可变参数
    问题：一个方法接受的参数个数不固定，例 如：System.out.println(countScore(2,3,5));System.out.println(countScore(1,2,3,5)); 可变参数的特点：只能出现在参数列表的最后；这个要记住 ...位于变量类型和变量名之间，前后有无空格都可以; 调用可变参数的方法时，编译器为该可变参数隐含创建一个数组，在方法体中以数组的形式访问可变参数。
    for ( type 变量名：集合变量名 ) 迭代变量必须在( )中定义！集合变量可以是数组或实现了Iterable接口的集合类
    public static int add(int x,int ...args)
    { int sum = x; for(int arg:args) { sum += arg; } return sum; }
    自动装箱和自动拆箱
    Integer num1 = 12;
    System.out.println(num1 + 12);
    Integer num1 = 12; Integer num2 = 12; 这块相等，<=127都是真的 System.out.println(num1 == num2); Integer num3 = 129; 这块不相等，因为是对象 Integer num4 = 129;System.out.println(num3 == num4);
    Integer num5 = Integer.valueOf(12);
    Integer num6 = Integer.valueOf(12) ;
    这块的道理同上 System.out.println(num5 == num6);
    枚举：

为什么要有枚举问题：要定义星期几或性别的变量，该怎么定义？假设用1-7分别表示星期一到星期日，但有 人可能会写成int weekday = 0;或即使使用常量方式也无法阻止意外。枚举就是要让某个类型的变量的取值只能为若干个固定值中的一个，否则，编译器就会报错。 枚举可以让编译器在编译时就可以控制源程序中填写的非法值，普通变量的方式在开发阶段无法实现这一目标。用普通类如何实现枚举功能，定义一个Weekday 的类来模拟枚举功能。私有的构造方法 每个元素分别用一个公有的静态成员变量表示 可以有若干公有方法或抽象方法。采用抽象方法定义nextDay就将大量的if.else语句转移成了 一个个独立的类。
枚举的基本应用举例：
定义一个Weekday的枚举。 扩展：枚举类的values,valueOf,name,toString,ordinal等方法（记住，讲课时要先于自定义方法前介绍，讲课更流畅）总结：枚举是一种特殊的类，其中的每个元素都是该类的一个实例对象，例如可以调用 WeekDay.SUN.getClass().getName和WeekDay.class.getName()。
枚举的高级应用
枚举就相当于一个类，其中也可以定义构造方法、成员变量、普通方法和 抽象方法。 枚举元素必须位于枚举体中的最开始部分，枚举元素列表的后要有分号与 其他成员分隔。把枚举中的成员方法或变量等放在枚举元素的前面，编译器报告错误。 带构造方法的枚举构造方法必须定义成私有的 如果有多个构造方法，该如何选择哪个构造方法？ 枚举元素MON和MON（）的效果一样，都是调用默认的构造方法。 定义枚举TrafficLamp 实现普通的next方法 实现抽象的next方法：每个元素分别是由枚举类的子类来生成的实例对象，这些子类采用类似内部类的方式进行定义。 增加上表示时间的构造方法带方法的枚举枚举只有一个成员时，就可以作为一种单例的实现方式。


第三章：反射

    反射的基石Class类
    对比提问： Person类代表人，它的实例对象就是张三，李四这样一个个具体的 人， Java程序中的各个Java类属于同一类事物，描述这类事物的Java类名就是 Class。对比提问：众多的人用一个什么类表示？众多的Java类用一个什么类表 示？人Person Java类Class. Class类代表Java类，它的各个实例对象又分别对应什么呢？对应各个类在内存中的字节码，例如，Person类的字节码，ArrayList类的字节码，等等。 一个类被类加载器加载到内存中，占用一片存储空间，这个空间里面的内容就是类的字节码，不同的类的字节码是不同的，所以它们在内存中的内容是不同的，这一个个的空间可分别用一个个的对象来 表示，这些对象显然具有相同的类型，这个类型是什么呢？如何得到各个字节码对应的实例对象（ Class类型） 类名.class，例如，System.class 对象.getClass()，例如，new Date().getClass() Class.forName("类名")，例如，Class.forName("java.util.Date");九个预定义Class实例对象：
    参看Class.isPrimitive方法的帮助 Int.class == Integer.TYPE Class.isArray()

数组类型的Class实例对象总之，只要是在源程序中出现的类型，都有各自的Class实例对象，例如，int[]，void…

    反射
    反射就是把Java类中的各种成分映射成相应的java类。例如，一个Java类中用一个Class类的对象来表示，一个类中的组成部分：成员变量，方法，构造方法，包等等信息也用一个个的Java类来 表示，就像汽车是一个类，汽车中的发动机，变速箱等等也是一个个的类。表示java类的Class类显然要提供一系列的方法，来获得其中的变量，方法，构造方法，修饰符，包等信息，这些信 息就是用相应类的实例对象来表示，它们是Field、Method、 Contructor、Package等等。一个类中的每个成员都可以用相应的反射API类的一个实例对象 来表示，通过调用Class类的方法可以得到这些实例对象后，得到这些实例对象后有什么用呢？怎么用呢？这正是学习和应用反 射的要点。
    Constructor类
    Constructor类代表某个类中的一个构造方法 得到某个类所有的构造方法：例子：Constructor [] constructors= Class.forName("java.lang.String").getConstructors();得到某一个构造方法：Constructor constructor = Class.forName(“java.lang.String”).getConstructor(StringBuffer.class); //获得方法时要用到类型创建实例对象：

通常方式：String str = new String(new StringBuffer("abc")); 反射方式： String str = (String)constructor.newInstance(new StringBuffer("abc"));//调用获得的方法时要用到上面相同类型的实例对象Class.newInstance()方法：
例子：String obj = (String)Class.forName("java.lang.String").newInstance(); 该方法内部先得到默认的构造方法，然后用该构造方法创建实例对象。该方法内部的具体代码是怎样写的呢？用到了缓存机制来保存默认构造方法的实例对象。

    Field类
    Field类代表某个类中的一个成员变量 演示用eclipse自动生成Java类的构造方法 问题：得到的Field对象是对应到类上面的成员变量，还是对应到对象上的成员变量？类只有一个，而该类的实例对象有多个，如果 是与对象关联，哪关联的是哪个对象呢？所以字段fieldX 代表的是 x的定义，而不是具体的x变量。示例代码：
    ReflectPoint point = new ReflectPoint(1,7);
    Field y = Class.forName("cn.itcast.corejava.ReflectPoint").getField("y"); System.out.println(y.get(point));
    //Field x = Class.forName("cn.itcast.corejava.ReflectPoint").getField("x"); Field x = Class.forName("cn.itcast.corejava.ReflectPoint").getDeclaredField("x"); x.setAccessible(true); System.out.println(x.get(point));作业：将任意一个对象中的所有String类型的成员变量所对应的字 符串内容中的"b"改成"a”
    Method类
    Method charAt = Class.forName("java.lang.String").getMethod("charAt", int.class);调用方法：
    通常方式：System.out.println(str.charAt(1));
    反射方式： System.out.println(charAt.invoke(str, 1));如果传递给Method对象的invoke()方法的第一个参数为null，这有着什么样的意义呢？说明该Method对象对应的是一个静态方法！jdk1.4和jdk1.5的invoke方法的区别：Jdk1.5：public Object invoke(Object obj,Object... args) Jdk1.4：public Object invoke(Object obj,Object[] args)，即按jdk1.4的语法，需要将一个数组作为参数传递给invoke方法时，数组中的每个元素分别对应被 调用方法中的一个参数，所以，调用charAt方法的代码也可以用Jdk1.4改写为 charAt.invoke(“str”, new Object[]{1})形式。
    用反射方式执行某个类中的main方法
    目标： 写一个程序，这个程序能够根据用户提供的类名，去执行该类中的main方法。用普通方式调完后，大家要明白为什么要用反射方式去调啊?
    问题:
    启动Java程序的main方法的参数是一个字符串数组，即public static void main(String[] args)，通过反射方式来调用这个main方法时，如何为invoke方法传递参数呢？按jdk1.5的语法，整个数组是一个参数，而按jdk1.4的语法，数组中的每个元素对应一个参数，当把一个字符串数组作为参数传递给 invoke方法时，javac会到底按照哪种语法进行处理呢？jdk1.5肯定要兼容 jdk1.4的语法，会按jdk1.4的语法进行处理，即把数组打散成为若干个单独的参数。所以，在给main方法传递参数时，不能使用代码mainMethod.invoke(null,new String[]{“xxx”})，javac只把它当作jdk1.4的语法进行 理解，而不把它当作jdk1.5的语法解释，因此会出现参数类型不对的问题. 解决办法： mainMethod.invoke(null,new Object[]{new String[]{"xxx"}}); mainMethod.invoke(null,(Object)new String[]{"xxx"}); ，编译器会作特殊处理，编译时不把参数当作数组看待，也就不会数组打散成若干个参数了
    数组的反射
    具有相同维数和元素类型的数组属于同一个类型，即 具有相同的Class实例对象。 代表数组的Class实例对象的getSuperClass()方法返 回的父类为Object类对应的Class。 基本类型的一维数组可以被当作Object类型使用，不 能当作Object[]类型使用；非基本类型的一维数组，既可以当做Object类型使用，又可以当做Object[]类型使用Arrays.asList()方法处理int[]和String[]时的差异. Array工具类用于完成对数组的反射操作。 思考题：怎么得到数组中的元素类型,基本数据类型不是object，一维数据时object。
    用Array反射操作数组，可以知道其长度。
    反射的作用实现框架功能
    框架与框架要解决的核心问题
    我做房子卖给用户住，由用户自己安装门窗和空调，我做的房子就是框架，用户需要使用我的框架，把门窗插入进我提供的框架中。框架与工具类有区别，工具 类被用户的类调用，而框架则是调用用户提供的类.
    框架要解决的核心问题
    我在写框架（房子）时，你这个用户可能还在上小学，还不会写程序呢？我写的.
    综合案例
    框架程序怎样能调用到你以后写的类（门窗）呢？ 因为在写才程序时无法知道要被调用的类名，所以，在程序中无法直接new 某个 类的实例对象了，而要用反射方式来.
    先直接用new. 语句创建ArrayList和HashSet的实例对象，演示用eclipse自动生成 ReflectPoint类的equals和hashcode方法，比较两个集合的运行结果差异。 然后改为采用配置文件加反射的方式创建ArrayList和HashSet的实例对象，比较 观察运行结果差异。 引入了elipse对资源文件的管理方式的讲解.

 

 

第四章：泛型

    了解泛型
    ArrayList<E>类定义和ArrayList<Integer>类引用中涉及如下术语:
    整个称为ArrayList<E>泛型类型 ArrayList<E>中的E称为类型变量或类型参数 整个ArrayList<Integer>称为参数化的类型 ArrayList<Integer>中的Integer称为类型参数的实例或实际类型参数 ArrayList<Integer>中的<>念着typeof ArrayList称为原始类型 参数化类型可以引用一个原始类型的对象，编译报告警告，例如， Collection<String> c = new Vector();//可不可以，不就是编译器一句话的事吗？原始类型可以引用一个参数化类型的对象，编译报告警告，例如， Collection c = new Vector<String>();//原来的方法接受一个集合参数，新的类型也要能传进去 Vector<String> v = new Vector<Object>(); //错误!///不写<Object>没错，写了就是明知故犯 Vector<Object> v = new Vector<String>(); //也错误
    参数化类型与原始类型的兼容性
    参数化类型不考虑类型参数的继承关系： 编译器不允许创建泛型变量的数组。即在创建数组实例时，数组的元素不 能使用参数化的类型，例如，下面语句有错误:
    Vector<Integer> vectorList[] = new Vector<Integer>[10];
    思考题：下面的代码会报错误吗
    Vector v1 = new Vector<String>(); Vector<Object> v = v1;
    泛型中的？通配符
    定义一个方法，该方法用于打印出任意参数化类型的集合中的所有数据，该方法如何定义呢？错误方式:
    public static void printCollection(Collection<Object> cols)
    { for(Object obj:cols) {

System.out.println(obj); } /* cols.add("string");//没错 cols = new HashSet<Date>();//会报告错误！*/ }
正确方式:
public static void printCollection(Collection<> cols) { for(Object obj:cols) { System.out.println(obj); } //cols.add("string");//错误，因为它不知自己未来匹配就一定是String cols.size();//没错，
此方法与类型参数没有关系 cols = new HashSet<Date>(); }
总结:
使用通配符可以引用其他各种参数化的类型，通配符定义的变量主要用作引用，可以调用与参数化无 关的方法，不能调用与参数化有关的方法.

    泛型中的？通配符的扩展
    限定通配符的上边界： 限定通配符的下边界： 提示:
    正确：Vector< extends Number> x = new Vector<Integer>();
    错误：Vector< extends Number> x = new Vector<String>();
    正确：Vector< super Integer> x = new Vector<Number>();
    错误：Vector< super Integer> x = new Vector<Byte>(); 限定通配符总是包括自己。 只能用作引用，不能用它去给其他变量赋值Vector< extends Number> y = new Vector<Integer>(); Vector<Number> x = y; 上面的代码错误，原理与Vector<Object > x11 = new Vector<String>();相似. 只能通过强制类型转换方式来赋值。
    泛型集合类的综合案例
    能写出下面的代码即代表掌握了Java的泛型集合类
    HashMap<String,Integer> hm = new HashMap<String,Integer>();
    hm.put("zxx",19);
    hm.put("lis",18);
    Set<Map.Entry<String,Integer>> mes= hm.entrySet(); for(Map.Entry<String,Integer> me : mes) {
        System.out.println(me.getKey() + ":" + me.getValue());
    }
    对在jsp页面中也经常要对Set或Map集合进行迭代: <c:forEach items=“${map}” var=“entry”> ${entry.key}:${entry.value} </c:forEach>
    定义泛型方法:
    Java的泛型方法没有C++模板函数功能强大，java中的如下代码无法通过编译:
    <T> T add(T x,T y) { return (T) (x+y); //return null; }
    用于放置泛型的类型参数的尖括号应出现在方法的其他所有修饰符之后和在方法的返回类型之前，也就是紧邻返回 值之前。按照惯例，类型参数通常用单个大写字母表示.
    交换数组中的两个元素的位置的泛型方法语法定义如下：
    static <E> void swap(E[] a, int i, int j)
    { E t = a[i]; a[i] = a[j]; a[j] = t; }//或用一个面试题讲：把一个数组中的元素的顺序颠倒一下:
    只有引用类型才能作为泛型方法的实际参数，swap(new int[3],3,5);语句会报告编译错误。除了在应用泛型时可以使用extends限定符，在定义泛型时也可以使用extends限定符，例如， Class.getAnnotation()方法的定义。并且可以用&来指定多个边界，如<V extends Serializable & cloneable> void method(){} 普通方法、构造方法和静态方法中都可以使用泛型。也可以用类型变量表示异常，称为参数化的异常，可以用于方法的throws列表中，但是不能用于catch 子句中。 在泛型中可以同时有多个类型参数，在定义它们的尖括号中用逗号分，例如：public static <K,V> V getValue(K key) { return map.get(key);}
    泛型方法的练习题
    编写一个泛型方法，自动将Object类型的对象转换成其他类型。 定义一个方法，可以将任意类型的数组中的所有元素填充为相应类型的某个对象。 采用自定泛型方法的方式打印出任意参数化类型的集合中的所有 内容。在这种情况下，前面的通配符方案要比范型方法更有效，当一个类型变量用来表达两个参数之间或者参数和返回值之间的关系时，即同一个类型变量在方法签名的两处被使用，或者类型变量在方法体代码中也被使 用而不是仅在签名的时候使用，才需要使用范型方法。定义一个方法，把任意参数类型的集合中的数据安全地复制到相应类型的数组中。 定义一个方法，把任意参数类型的一个数组中的数据安全地复制 到相应类型的另一个数组中。
    类型参数的类型推断
    编译器判断范型方法的实际类型参数的过程称为类型推断，类型推断是相对于知觉 推断的，其实现方法是一种非常复杂的过程。 根据调用泛型方法时实际传递的参数类型或返回值的类型来推断，具体规则如下：
    当某个类型变量只在整个参数列表中的所有参数和返回值中的一处被应用了，那么根据调用方法时该处 的实际应用类型来确定，这很容易凭着感觉推断出来，即直接根据调用方法时传递的参数类型或返回值来决定泛型参数的类型，例如： swap(new String[3],3,4) static <E> void swap(E[] a, int i, int j) 当某个类型变量在整个参数列表中的所有参数和返回值中的多处被应用了，如果调用方法时这多处的实 际应用类型都对应同一种类型来确定，这很容易凭着感觉推断出来，例如： add(3,5) static <T> T add(T a, T b) 当某个类型变量在整个参数列表中的所有参数和返回值中的多处被应用了，如果调用方法时这多处的实际应用类型对应到了不同的类型，且没有使用返回值，这时候取多个参数中的最大交集类型，例如，下 面语句实际对应的类型就是Number了，编译没问题，只是运行时出问题： fill(new Integer[3],3.5f) static <T> void fill(T[] a, T v) 当某个类型变量在整个参数列表中的所有参数和返回值中的多处被应用了，如果调用方法时这多处的实际应用类型对应到了不同的类型， 并且使用返回值，这时候优先考虑返回值的类型，例如，下面语句实 际对应的类型就是Integer了，编译将报告错误，将变量x的类型改为float，对比eclipse报告的错误提示，接着再将变量x类型改为Number，则没有了错误： int x =(3,3.5f) static <T> T add(T a, T b) 参数类型的类型推断具有传递性，下面第一种情况推断实际参数类型为Object，编译没有问题，而第二种 情况则根据参数化的Vector类实例将类型变量直接确定为String类型，编译将出现问题： copy(new Integer[5],new String[5]) static <T> void copy(T[] a,T[] b); copy(new Vector<String>(), new Integer[5]) static <T> void copy(Collection<T> a , T[] b);
    定义泛型类型
    如果类的实例对象中的多处都要用到同一个泛型参数，即这些地方引用的泛型类型要保持同 一个实际类型时，这时候就要采用泛型类型的方式进行定义，也就是类级别的泛型，语法格式如下：
    public class GenericDao<T>
    { private T field1; public void save(T obj){}
      public T getById(int id){} }
    类级别的泛型是根据引用该类名时指定的类型信息来参数化类型变量的，例如，如下两种方式 都可以GenericDao<String> dao = null; new genericDao<String>();
    在对泛型类型进行参数化时，类型参数的实例必须是引用类型，不能是基本类型。 当一个变量被声明为泛型时，只能被实例变量、方法和内部类调用，而不能被静态变量和静态方法调用。因为静态成员是被所有参数化的类所共享的，所以静态成员不应该有类级别的类型参数。
    问题：类中只有一个方法需要使用泛型，是使用类级别的泛型，还是使用方法级别的泛型？
    泛型的继承
    继承时，对泛型父类的类型参数不实例化
    代码    代码  
    通过反射获得泛型的参数化类型
    示例代码：
    Class GenericalReflection {
    private Vector<Date> dates = new Vector<Date>();
    public void setDates(Vector<Date> dates) { this.dates = dates; }
    public static void main(String[] args) {
    Method methodApply = GenericalReflection.class.getDeclaredMethod("applyGeneric", Vector.class); ParameterizedType pType = (ParameterizedType) (methodApply .getGenericParameterTypes())[0];
    System.out.println("setDates(" + ((Class) pType.getRawType()).getName() + "<" + ((Class) (pType.getActualTypeArguments()[0])).getName() + ">)" ); }
    }
    泛型DAO的应用:
    public abstract class DaoBaseImpl<T> implements DaoBase<T> {
    protected Class<T> clazz; public DaoBaseImpl() { Type type = this.getClass().getGenericSuperclass(); ParameterizedType pt = (ParameterizedType) type; this.clazz = (Class) pt.getActualTypeArguments()[0]; System.out.println("clazz = " + this.clazz); }}
    public class ArticleDaoImpl extends DaoBaseImpl<Article> implements ArticleDao { }

 

第五章：类加载器及其代理

    类加载器
    简要介绍什么是类加载器和类加载器的作用 Java虚拟机中可以安装多个类加载器，系统默认三个主要 类加载器，每个类负责加载特定位置的类： BootStrap,ExtClassLoader,AppClassLoader 类加载器也是Java类，因为其他是java类的类加载器本身 也要被类加载器加载，显然必须有第一个类加载器不是不是java类，这正是BootStrap。 Java虚拟机中的所有类装载器采用具有父子关系的树形结 构进行组织，在实例化每个类装载器对象时，需要为其指 定一个父级类装载器对象或者默认采用系统类装载器为其父级类加载。
    类加载器之间的父子关系和管辖范围图

第六章：线程并发库

        传统线程机制的回顾
        创建线程的两种传统方式 在Thread子类覆盖的run方法中编写运行代码, 在传递给Thread对象的Runnable对象的run方法中编写代码总结：查看Thread类的run()方法的源代码，可以看到其实这两种方式都是在涉及一个以往知识点：能否在run方法声明上抛出InterruptedException异常，以便 省略run方法内部对Thread.sleep()语句的try…catch处理. 调用Thread对象的run方法，如果Thread类的run方法没有被覆盖，并且为该 Thread对象设置了一个Runnable对象，该run方法会调用Runnable对象的run 方法。 问题：如果在Thread子类覆盖的run方法中编写了运行代码，也为Thread子 类对象传递了一个Runnable对象，那么，线程运行时的执行代码是子类的 run方法的代码？还是Runnable对象的run方法的代码？涉及到的一个以往知识点：匿名内部类对象的构造方法如何调用父类的非默认构造方法.
        定时器的应用 Timer类 TimerTask类
        定时器的应用 Timer类 TimerTask类
        线程的同步互斥与通信
        使用synchronized代码块及其原理 使用synchronized方法分析静态方法所使用的同步监视器对象是什 么？ wait与notify实现线程间的通信.
        多个线程访问共享对象和数据的方式
        如果每个线程执行的代码相同，可以使用同一个Runnable对象，这个 Runnable对象中有那个共享数据，例如，买票系统就可以这么做。 如果每个线程执行的代码不同，这时候需要用不同的Runnable对象，有 如下两种方式来实现这些Runnable对象之间的数据共享：将共享数据封装在另外一个对象中，然后将这个对象逐一传递给各个Runnable对象。每个线程对共享数据的操作方法也分配到那个对象身上去完成，这样容易实 现针对该数据进行的各个操作的互斥和通信。将这些Runnable对象作为某一个类中的内部类，共享数据作为这个外部类中的成 员变量，每个线程对共享数据的操作方法也分配给外部类，以便实现对共享数据进行的各个操作的互斥和通信，作为内部类的各个Runnable对象调用外部类的这 些方法。 上面两种方式的组合：将共享数据封装在另外一个对象中，每个线程对共享数据的操作方法也分配到那个对象身上去完成，对象作为这个外部类中的成员变量或 方法中的局部变量，每个线程的Runnable对象作为外部类中的成员内部类或局部内部类。 总之，要同步互斥的几段代码最好是分别放在几个独立的方法中，这些方法再放 在同一个类中，这样比较容易实现它们之间的同步互斥和通信。极端且简单的方式，即在任意一个类中定义一个static的变量，这将被所 有线程共享。
        ThreadLocal实现线程范围的共享变量
        见下页的示意图和辅助代码解释ThreadLocal的作用和目的：用于实现线程内的数据共享，即对于相同的程序代码，多个模块在同一个线程中运行时要共享一份数据，而在另外线程中运行时又共享另 外一份数据。 每个线程调用全局ThreadLocal对象的set方法，就相当于往其内部的map中增加一条记录，key分别 是各自的线程，value是各自的set方法传进去的值。在线程结束时可以调用ThreadLocal.clear()方法，这样会更快释放内存，不调用也可以，因为线程结束后也可以自动释放相关的ThreadLocal变量。 ThreadLocal的应用场景：订单处理包含一系列操作：减少库存量、增加一条流水台账、修改总账，这几个操作要在同一个事务中完成，通常也即同一个线程中进行处理，如果累加公司应收款的操作失败了，则应该把前面的操作回滚，否则，提交 所有操作，这要求这些操作使用相同的数据库连接对象，而这些操作的代码分别位于不同的模块类中。银行转账包含一系列操作： 把转出帐户的余额减少，把转入帐户的余额增加，这两个操作要在同一个事务中 完成，它们必须使用相同的数据库连接对象，转入和转出操作的代码分别是两个不同的帐户对象的方法。例如Strut2的ActionContext，同一段代码被不同的线程调用运行时，该代码操作的数据是每个线程各自的状态和数据，对于不同的线程来说，getContext方法拿到的对象都不相同，对同一个线程来说，不管调用 getContext方法多少次和在哪个模块中getContext方法，拿到的都是同一个。
        实验案例：定义一个全局共享的ThreadLocal变量，然后启动多个线程向该ThreadLocal变量中存储 一个随机值，接着各个线程调用另外其他多个类的方法，这多个类的方法中读取这个ThreadLocal变 量的值，就可以看到多个类在同一个线程中共享同一份数据。 实现对ThreadLocal变量的封装，让外界不要直接操作ThreadLocal变量。对基本类型的数据的封装，这种应用相对很少见。 对对象类型的数据的封装，比较常见，即让某个类针对不同线程分别创建一个独立的实例对象。
        线程范围内共享数据的示意图
        线程1 绑定的数据
        对象与模块A
        对象与模块B
        对象与模块C
        线程1 变量或 表达式 变量或 表达式 变量或 表达式
        线程2
        线程2 绑定的数据
        Java5中的线程并发库
        看java.util.concurrent包及子包的API帮助文档了java.util.concurrent.atomic包。查看atomic包文档页下面的介绍通过如下两个方法快速理解atomic包的意义。
        顺带解释volatile类型的作用，需要查看java语言规范。AtomicInteger类的boolean compareAndSet(expectedValue, updateValue); AtomicIntegerArray类的int addAndGet(int i, int delta);java.util.concurrent.lock
        线程池的概念与Executors类的应用创建固定大小的线程池 创建缓存线程池 创建单一线程池。
        线程池
        线程池的概念与Executors类的应用， 三种方式：创建固定大小的线程池 创建缓存线程池 创建单一线程池.
        关闭线程池：
        shutdown与shutdownNow的比较
        用线程池启动定时器
        调用ScheduledExecutorService的schedule方法，返回的ScheduleFuture对象可以取消任务。 支持间隔重复任务的定时方式，不直接支持绝对定时方式，需要转换成相对时间方式。
        Callable&Future
        Future取得的结果类型和Callable返回的结果类型必须一致，这是通过泛型来实现的。 Callable要采用ExecutorSevice的submit方法 提交，返回的future对象可以取消任务。 CompletionService用于提交一组Callable任务， 其take方法返回已完成的一个Callable任务对 应的Future对象。好比我同时种了几块地的麦子，然后就等待收割。收割时，则是那块先成熟了，则先去收割哪块麦子。
        Lock&Condition实现线程同步通信
        Lock比传统线程模型中的synchronized方式更加面向对象，与生活中的锁类似， 锁本身也应该是一个对象。两个线程执行的代码片段要实现同步互斥的效果，它们必须用同一个Lock对象。 读写锁：分为读锁和写锁，多个读锁不互斥，读锁与写锁互斥，这是由jvm自己 控制的，你只要上好相应的锁即可。如果你的代码只读数据，可以很多人同时读， 但不能同时写，那就上读锁；如果你的代码修改数据，只能有一个人在写，且不能同时读取，那就上写锁。总之，读的时候上读锁，写的时候上写锁！ 在等待 Condition 时，允许发生“虚假唤醒”，这通常作为对基础平台语义的让步。对于大多数应用程序，这带来的实际影响很小，因为 Condition 应该总是在 一个循环中被等待，并测试正被等待的状态声明。某个实现可以随意移除可能的虚假唤醒，但建议应用程序程序员总是假定这些虚假唤醒可能发生，因此总是在 一个循环中等待。 一个锁内部可以有多个Condition，即有多路等待和通知，可以参看jdk1.5提供的 Lock与Condition实现的可阻塞队列的应用案例，从中除了要体味算法，还要体味面向对象的封装。在传统的线程机制中一个监视器对象上只能有一路等待和通 知，要想实现多路等待和通知，必须嵌套使用多个同步监视器对象。（如果只用 一个Condition，两个放的都在等，一旦一个放的进去了，那么它通知可能会导致 另一个放接着往下走。）
        Semaphore实现信号灯
        Semaphore可以维护当前访问自身的线程个数，并提 供了同步机制。使用Semaphore可以控制同时访问资源的线程个数，例如，实现一个文件允许的并发访问数。Semaphore实现的功能就类似厕所有5个坑，假如有十个人要上厕所，那么同时能有多少个人去上厕所呢？同时只能有5 个人能够占用，当5个人中的任何一个人让开后，其中在等待的另外5个人中又有一个可以占用了。 另外等待的5个人中可以是随机获得优先机会，也可以是按照先来后到的顺序获得机会，这取决于构造Semaphore对象时 传入的参数选项。单个信号量的Semaphore对象可以实现互斥锁的功能， 并且可以是由一个线程获得了“锁”，再由另一个线 程释放“锁”，这可应用于死锁恢复的一些场合。
        其他同步工具类
        CyclicBarrier
        表示大家彼此等待，大家集合好后才开始出发，分散活动后又在指定地点集合碰面，这就好比整个公司的人员利用周末时间集体郊游一样，先各自从家出发到公 司集合后，再同时出发到公园游玩，在指定地点集合后再同时开始就餐，…
        CountDownLatch
        犹如倒计时计数器，调用CountDownLatch对象的countDown方法就将计数器减1，当计数到达0时，则所有等待者或单个等待者开始执行。这直接通过代码来说明 CountDownLatch的作用，这样学员的理解效果更直接。 可以实现一个人（也可以是多个人）等待其他所有人都来通知他，这犹如一个计划需要多个领导都签字后才能继续向下实施。还可以实现一个人通知多个人的效 果，类似裁判一声口令，运动员同时开始奔跑。用这个功能做百米赛跑的游戏程 序不错哦！
        Exchanger
        用于实现两个人之间的数据交换，每个人在完成一定的事务后想与对方交换数据，第一个先拿出数据的人将一直等待第二个人拿着数据到来时，才能彼此交换数据。
        可阻塞的队列
        什么是可阻塞队列，阻塞队列的作用与实际应用，阻塞队 列的实现原理。
        阻塞队列与Semaphore有些相似，但也不同，阻塞队 列是一方存放数据，另一方释放数据，Semaphore通 常则是由同一方设置和释放信号量。
        ArrayBlockingQueue
        只有put方法和take方法才具有阻塞功能。用3个空间的队列来演示阻塞队列的功能和效果。 用两个具有1个空间的队列来实现同步通知的功能。
        同步集合
        传统集合类在并发访问时的问题说明，见附件 传统方式下用Collections工具类提供的synchronizedCollection方法来获得 同步集合，分析该方法的实现源码。 传统方式下的Collection在迭代集合时，不允许对集合进行修改。用空中网面试的同步级线程题进行演示 根据AbstractList的checkForComodification方法的源码，分析产生ConcurrentModificationException异常的原因。
        Java5中提供了如下一些同步集合类：
        通过看java.util.concurrent包下的介绍可以知道有哪些并发集合，ConcurrentHashMap CopyOnWriteArrayList CopyOnWriteArraySet
