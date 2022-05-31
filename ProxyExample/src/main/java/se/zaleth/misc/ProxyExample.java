/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.misc;

import java.lang.reflect.*;

/**
 *
 * @author krister
 */
public class ProxyExample {

    private interface Dummy {
        
        public void foo();
        public void bar();
        
    }
    
    private class Orig implements Dummy {
        
        public void foo() {
            System.out.println("Orig.foo()");
        }
        
        public void bar() {
            System.out.println("Orig.bar()");
        }
    }
    
    private class Wrapper implements InvocationHandler {
        
        private Dummy inner;
        
        public Wrapper(Dummy dummy) {
            inner = dummy;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            if(method.getName().equals("foo")) {
                System.out.println("Wrapper.foo()");
                return null;
            }
            try {
                return method.invoke(inner, args);
            }catch(Exception e) {
                return null;
            }
        }
    }
    
    public static void main(String[] args) {
        new ProxyExample();
    }
    
    public ProxyExample() {
        Dummy orig = new Orig();
        Dummy proxy = (Dummy) Proxy.newProxyInstance(Dummy.class.getClassLoader(),
                new Class[] { Dummy.class }, new Wrapper(orig));
        orig.foo();
        proxy.foo();
        proxy.bar();
    }
}
