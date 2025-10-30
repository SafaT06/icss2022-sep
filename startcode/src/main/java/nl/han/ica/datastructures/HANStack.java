package nl.han.ica.datastructures;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class HANStack<T> implements IHANStack<T> {

  private List<T> stack;

  public HANStack() {
    this.stack = new ArrayList<>();
  }

  @Override
  public void push(T value) {
    stack.add(value);
  }
  @Override
  public T pop() {
    if (stack.isEmpty()) {
      throw new EmptyStackException();
    }
    return stack.remove(stack.size() - 1);
  }

  @Override
  public T peek() {
    if (stack.isEmpty()) {
      throw new EmptyStackException();
    }
    return stack.get(stack.size() - 1);
  }
}