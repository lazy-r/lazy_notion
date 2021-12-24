package top.lazyr.notion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lazyr.notion.model.pojo.MovieItem;
import top.lazyr.notion.service.VideoService;

import java.util.Map;

/**
 * @author lazyr
 * @created 2021/12/18
 */
@RestController
public class VideoController {
    @Autowired
    private VideoService service;

    @GetMapping("/movie")
    public Map<String, Object> getVideoNames(String keyword) {
        return service.getVideoNameLink(keyword);
    }

    @PostMapping("/movie")
    public String createVideoItem(@RequestBody MovieItem movieItem) {
        System.out.println("url => " + movieItem.getUrl() + ", status => " + movieItem.getStatus());
        return service.createVideoItem(movieItem.getUrl(), movieItem.getStatus(), movieItem.getType());
    }
}
