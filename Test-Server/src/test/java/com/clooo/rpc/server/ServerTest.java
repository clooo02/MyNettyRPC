package com.clooo.rpc.server;

public class ServerTest {
    public static void main(String[] args) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        String className = stack[stack.length - 1].getClassName();

        String substring = className.substring(0, className.lastIndexOf("."));
        System.out.println(substring);
    }
}
