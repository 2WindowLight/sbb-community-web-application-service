package com.mysite.sbb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
@RequiredArgsConstructor
@Getter
@Setter
public class HelloLombook {
    private  final String hello;
    private final int lombok;
    public static void main(String[] args) {
        HelloLombook h1 = new HelloLombook("hi",5);

        System.out.println(h1.getHello());
        System.out.println(h1.getLombok());
    }

}
