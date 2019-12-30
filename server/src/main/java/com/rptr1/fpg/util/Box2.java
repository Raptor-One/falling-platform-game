package com.rptr1.fpg.util;

public class Box2<A,B>
{
    private A valueA;
    private B valueB;

    public Box2( A valueA, B valueB )
    {
        this.valueA = valueA;
        this.valueB = valueB;
    }

    public A getValueA()
    {
        return valueA;
    }

    public void setValueA( A valueA )
    {
        this.valueA = valueA;
    }

    public B getValueB()
    {
        return valueB;
    }

    public void setValueB( B valueB )
    {
        this.valueB = valueB;
    }
}
