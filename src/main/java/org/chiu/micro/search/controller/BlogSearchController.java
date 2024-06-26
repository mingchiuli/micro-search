package org.chiu.micro.search.controller;

import org.chiu.micro.search.vo.BlogEntityVo;
import org.chiu.micro.search.lang.Result;
import org.chiu.micro.search.page.PageAdapter;
import org.chiu.micro.search.service.BlogSearchService;
import org.chiu.micro.search.vo.BlogDocumentVo;

import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * @author mingchiuli
 * @create 2022-11-30 8:48 pm
 */
@RestController
@RequestMapping(value = "/search")
@RequiredArgsConstructor
@Validated
public class BlogSearchController {

    private final BlogSearchService blogSearchService;

    @GetMapping("/public/blog")
    public Result<PageAdapter<BlogDocumentVo>> searchBlogs(@RequestParam(value = "currentPage", defaultValue = "-1") Integer currentPage,
                                                           @RequestParam(value = "allInfo") Boolean allInfo,
                                                           @RequestParam(value = "year", required = false) String year,
                                                           @RequestParam(value = "keywords") @Size(min = 1, max = 20) String keywords) {
        return Result.success(() -> blogSearchService.selectBlogsByES(currentPage, keywords, allInfo, year));
    }

    @GetMapping("/sys/blogs")
    public Result<PageAdapter<BlogEntityVo>> searchAllBlogs(@RequestParam(defaultValue = "1") Integer currentPage,
                                                            @RequestParam(defaultValue = "5") Integer size,
                                                            @RequestParam(value = "keywords")  @Size(min = 1, max = 20) String keywords,
                                                            @RequestParam Long userId,
                                                            @RequestBody List<String> roles) {
        return Result.success(() -> blogSearchService.searchAllBlogs(keywords, currentPage, size, userId, roles));
    }

}
