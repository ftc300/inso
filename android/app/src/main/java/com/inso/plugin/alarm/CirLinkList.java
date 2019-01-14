package com.inso.plugin.alarm;


import com.inso.plugin.tools.L;

/**
 * Created by chendong on 2017/7/12.
 * 周一到周日循环链表
 */
public class CirLinkList {

    class Element
    {
        public Object value=null;
        public Element next=null;
    }
    private Element header = null;//头结点

    public CirLinkList() {
        initList();
    }

    /**
     * 初始化链表
     * */
    void initList()
    {
        header = new Element();
        header.value=null;
        header.next=header;
    }

    /**
     * 插入链表
     * */
    void insertList(Object o)
    {
        Element e=new Element();
        e.value=o;
        if(header.next==header)//第一次插入元素
        {
            header.next=e;
            e.next=header;
        }else//不是第一次插入元素
        {
            //temp引用在栈中，temp和header引用都指向堆中的initList()中new的Element对象
            Element temp = header;
            while(temp.next!=header)//寻找最后一个元素
            {
                temp=temp.next;
            }
            temp.next=e;
            e.next=header;//新插入的最后一个节点指向头结点
        }
    }

    /**
     * 删除链表中第i个元素
     * */
    void deletelist(Object o)
    {
        Element temp =header;
        while(temp.next!=header)
        {
            //判断temp当前指向的结点的下一个结点是否是要删除的结点
            if(temp.next.value.equals(o))
            {
                temp.next=temp.next.next;//删除结点
            }else
            {
                temp=temp.next;//temp“指针”后移
            }
        }
    }

    /**
     * 获取链表的第i个位置的元素
     * */
    Element getElement(int i)
    {
        if(i<=0 || i>size())
        {
            System.out.println("获取链表的位置有误！返回null");
            return null;
        }
        else
        {
            int count =0;
            Element element = new Element();
            Element temp = header;
            while(temp.next!=header)
            {
                count++;
                if(count==i)
                {
                    element.value=temp.next.value;
                }
                temp=temp.next;
            }
            return element;
        }
    }



    Element getNextElement(int i)
    {
        if(i<=0 || i>size())
        {
            System.out.println("Error index return null");
            return null;
        }
        else
        {
            Element element = null;
            if(i < size()) element = getElement(i+1);
            else if(i==size()) element = getElement(1);
            return element;
        }
    }



    int getElementIndex(Object o)
    {
        if(isContain(o)) {
            Element temp = header;
            int size = 1;
            if (o instanceof Integer) {
                while (temp.next != header) {
                    if(o==getElement(size).value)
                        return size;
                    else
                    {
                        size++;
                        temp = temp.next;
                    }
                }
            }
            return -2;
        }else {
            return -1;
        }
    }




    /**
     * 链表长度
     * */
    int size()
    {
        Element temp = header;
        int size=0;
        while(temp.next!=header)
        {
            size++;
            temp=temp.next;
        }
        return size;
    }

    /**
     * 判断链表中是否存在某元素
     * */
    Boolean isContain(Object o)
    {
        Element temp =header;
        while(temp.next!=header)
        {
            if(temp.next.value.equals(o))
            {
                return true;
            }
            temp=temp.next;
        }
        return false;
    }
    /**
     * 打印链表
     * */
    void print()
    {
        L.e("Element Print:");
        Element temp =header;
        while(temp.next!=header)
        {
            temp=temp.next;
            L.e(temp.value+"\t");
        }
    }
}