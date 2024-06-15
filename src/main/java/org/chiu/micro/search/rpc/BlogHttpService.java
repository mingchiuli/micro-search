package org.chiu.micro.search.rpc;

import org.chiu.micro.search.dto.BlogEntityDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface BlogHttpService {

    @GetExchange("/{blogId}")
    BlogEntityDto findById(@PathVariable Long blogId);
}
