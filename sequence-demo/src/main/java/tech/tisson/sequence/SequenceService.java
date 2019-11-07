package tech.tisson.sequence;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "sequence")
public interface SequenceService {

    @GetMapping("/next")
    Long next();
}
