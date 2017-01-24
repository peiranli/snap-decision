package com.example.dots;

import java.util.*;
//import java.lang.Integer;

public class TrialVector<E> 
{
  private LinkedList<E> list;
  private int score = 0;
  private Integer pos = new Integer(1);
  private Integer neg = -1;
  private int capacity = 0;
  
  public TrialVector()
  {
    list = new LinkedList<E>();
  }
  
  public TrialVector(int i)
  {
    list = new LinkedList<E>();
    capacity = i;
  }
  
  public void addTrial(E i)
  {
    addLast(i);
    
    if(list.size() > capacity)
    {  
      Integer a = (Integer)removeFirst();
      //if(a.intValue() == 1)
       // score--;
    }
  }
  
  public int getScore()
  {
    return score;
  }

  public double percentCorrect()
  {
      return (double)getScore()/size();
  }
  
  public int size()
  {
    return list.size();
  }
  
  public void addFirst(E i)
  {
    Integer a = (Integer)i;
    list.addFirst(i);
    if(a.intValue() == 1) score++;
  }
  
  public void addLast(E i)
  {
    Integer a = (Integer)i;
    list.addLast(i);
    if(a.intValue() == 1) score++;
  }
  
  public E removeFirst()
  {
    E element =  list.removeFirst();
    Integer a = (Integer)element;
    if(a.intValue() == 1) score--;
    return element;
  }
  
  public E removeLast()
  {
    E element =  list.removeLast();
    Integer a = (Integer)element;
    if(a.intValue() == 1) score--;
    return element;
  }
  

}
