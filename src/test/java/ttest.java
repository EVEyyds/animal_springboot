import cn.chengygy.engine.RuleEngine;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ttest {
    @Test
    public void ttt(){
        RuleEngine ruleEngine=new RuleEngine("rules.json");
        List<String> conditions=new ArrayList<>();
        conditions.add("有毛发");
        conditions.add("吃肉");
        conditions.add("有黄褐色皮毛");
        conditions.add("有暗斑点");
        ruleEngine.setFacts(conditions);
        if(ruleEngine.deduce()){
            System.out.println("匹配到了");
            System.out.println(ruleEngine.getResults());
        }else {
            System.out.println("匹配不到");
        }
    }
}
