package homework.spring2.demo.controller;

import homework.spring2.demo.service.QueryService;
import homework.spring2.framework.annotation.Autowired;
import homework.spring2.framework.annotation.Controller;
import homework.spring2.framework.annotation.RequestMapping;
import homework.spring2.framework.annotation.RequestParam;

@Controller
@RequestMapping("/home")
public class QueryController {
    @Autowired
    private QueryService queryService;

    @RequestMapping("/query")
    public String query(@RequestParam("teacher") String teacher, @RequestParam("data") String data, @RequestParam("token") String token) {
        return "first.html";
    }

    @RequestMapping("/add")
    public String add() {
        return "";
    }
}
