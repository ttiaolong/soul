package org.dromara.soul.test.http.controller;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import rx.RxReactiveStreams;
import rx.Subscription;

/**
 * @author ttiaolong
 * @version 1.0.0
 * @date 2019/9/5
 */
@Slf4j
public class TestHystrixCommand extends HystrixCommand<Void> {

    private int index;

    protected TestHystrixCommand(Setter setter, int index) {
        super(setter);
        this.index = index;
    }

    @Override
    protected Void run() throws Exception {
        log.info("开始执行run()----{}", index);
        if (index == 5) {
            log.info("执行断点----{}", index);
        }
        if (index == 7) {
            throw new HystrixBadRequestException("测试HystrixBadRequestException");
        }
        if (index == 9) {
            throw new RuntimeException("测试fallback---" + index);
        }
        return Void.;
    }

    @Override
    protected Void getFallback() {
        log.info("开始执行getFallback()---{}", index);
        return super.getFallback();
    }

    public static void main(String[] args) {
        for (int i = 0; i <= 10; i++) {
            HystrixCommandProperties.Setter propertiesSetter = HystrixCommandProperties.Setter()
                    .withCircuitBreakerEnabled(true)
                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE);
            HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("groupKey");
            HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("commonKey");
            HystrixCommand.Setter setter = HystrixCommand.Setter.withGroupKey(groupKey).andCommandKey(commandKey).andCommandPropertiesDefaults(propertiesSetter);
            TestHystrixCommand command = new TestHystrixCommand(setter, i);
            Subscription subscribe = command.observe().subscribe();

        }
    }
}
