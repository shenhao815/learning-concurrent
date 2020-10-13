package com.it.atomic;

import com.it.common.Sleeper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Test41 {

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            demo(
                    () -> new AtomicLong(0),
                    adder->adder.getAndIncrement()
            );
        }
        Sleeper.sleep(2000);
        for (int i = 0; i < 5; i++) {
            demo(()->new LongAdder(),adder->adder.increment());

        }
    }

    private static <T> void demo(
            Supplier<T> addSupplier,
            Consumer<T> action
    ){
        T adder = addSupplier.get();

        long start = System.nanoTime();

        List<Thread> ts = new ArrayList<>();
        // 4个线程，每人累加50 万
        for (int i = 0; i < 4; i++) {
            ts.add(new Thread(()->{
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }
        ts.forEach(thread -> thread.start());
        ts.forEach(thread -> {
            try{
                thread.join();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(adder+" cost: "+(end-start)/1000_000);
    }
}