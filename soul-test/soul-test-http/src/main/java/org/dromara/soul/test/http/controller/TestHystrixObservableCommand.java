package org.dromara.soul.test.http.controller;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscription;

/**
 * @author ttiaolong
 * @version 1.0.0
 * @date 2019/9/5
 */
@Slf4j
public class TestHystrixObservableCommand extends HystrixObservableCommand<Void> {

    private int index;

    public TestHystrixObservableCommand(Setter setter, int index) {
        super(setter);
        this.index = index;
    }

    @Override
    protected Observable<Void> construct() {
        log.info("开始执行construct()----{}", index);
        if (index == 5) {
            log.info("执行断点");
        }
        if (index == 7) {
            throw new HystrixBadRequestException("测试HystrixBadRequestException");
        }
        if (index == 9) {
            throw new RuntimeException("测试fallback");
        }
        return RxReactiveStreams.toObservable(Mono.empty());
    }

    @Override
    protected Observable<Void> resumeWithFallback() {
        log.info("开始执行resumeWithFallback()---{}", index);
        return super.resumeWithFallback();
    }

    public static void main(String[] args) {
        for (int i = 0; i <= 10; i++) {
            HystrixCommandProperties.Setter propertiesSetter = HystrixCommandProperties.Setter()
                            .withCircuitBreakerEnabled(true)
                            .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE);
            HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("groupKey");
            HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("commonKey");
            Setter setter = Setter.withGroupKey(groupKey).andCommandKey(commandKey).andCommandPropertiesDefaults(propertiesSetter);
            TestHystrixObservableCommand command = new TestHystrixObservableCommand(setter, i);
            Subscription subscribe = command.toObservable().subscribe();
        }
    }
}
