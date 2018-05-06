package homework.spring2.demo.controller;

import homework.spring2.demo.service.QueryService;
import homework.spring2.framework.annotation.Autowired;
import homework.spring2.framework.annotation.Component;
import homework.spring2.framework.annotation.RequestMapping;

@Component
@RequestMapping
public class QueryController {
    @Autowired
    private QueryService queryService;

    @RequestMapping("/query")
    public String query() {
        return "";
    }
}
