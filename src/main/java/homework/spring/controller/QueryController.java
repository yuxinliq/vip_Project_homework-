package homework.spring.controller;

import homework.spring.annotation.Autowired;
import homework.spring.annotation.Component;
import homework.spring.annotation.RequestMapping;
import homework.spring.service.QueryService;

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
