package homework.spring2.demo.service;

import homework.spring2.demo.dao.QueryDao;
import homework.spring2.framework.annotation.Autowired;
import homework.spring2.framework.annotation.Component;

@Component
public class QueryServiceImpl implements QueryService {
    @Autowired
    private QueryDao queryDao;
}
