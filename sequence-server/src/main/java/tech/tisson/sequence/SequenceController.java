package tech.tisson.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供 http 服务，客户端可以使用 Feign 来声明服务
 *
 * @author zhuzhiou
 */
@RestController
public class SequenceController {

    @Autowired
    private Sequence sequence;

    @GetMapping("/next")
    public Long next() {
        return sequence.next();
    }
}
