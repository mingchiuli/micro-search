package org.chiu.micro.search.rpc.wrapper;

import org.springframework.stereotype.Component;
import org.chiu.micro.search.dto.BlogEntityDto;
import org.chiu.micro.search.lang.Result;
import org.chiu.micro.search.rpc.BlogHttpService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BlogHttpServiceWrapper {

    private final BlogHttpService blogHttpService;

    public BlogEntityDto findById(Long blogId) {
        Result<BlogEntityDto> result = blogHttpService.findById(blogId);
        return result.getData();
    }
}
