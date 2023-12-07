package cn.chengygy.engine;

import cn.chengygy.entity.Rule;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RuleEngine {
    private static List<Rule> rules;   // 规则库
    private static List<String> oldfacts;  // 原事实库
    private static List<String> facts;  // 事实库
    private static List<String> results; // 匹配到的结果，包括中间结论，即更新后的事实库与原来的差别
    private static final String RULES_DIRECTORY = "static/data/";
    public RuleEngine(){
        rules=new ArrayList<>();
    }
    public RuleEngine(String fileName){
        rules=new ArrayList<>();
        // 读取规则库.json文件，结果存入rules里
        loadRules(fileName);
    }
    public void loadRules(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource(RULES_DIRECTORY + fileName);
            InputStream inputStream = resource.getInputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            rules = objectMapper.readValue(inputStream, new TypeReference<List<Rule>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 获取规则列表
    public List<Rule> getRules() {
        return rules;
    }
    // 添加新的规则
    public void addRule(Rule rule){
        rules.add(rule);
    }
    public void setFacts(List<String> fact){
        facts=new ArrayList<>(fact);
        oldfacts=new ArrayList<>(fact);
    }
    public List<String> getResults(){
        results=new ArrayList<>(facts);
        results.removeAll(oldfacts);
        return results;
    }
    // 判断规则是否已经存在
    public boolean contains(Rule rule){
        for(Rule R:rules){
            if(R.equals(rule)) return true;
        }
        return false;
    }
    // 扫描所有规则，匹配一条可用的规则
    public boolean step_forward(){
        for(Rule rule:rules){
            if(try_rule(rule)) return true;
        }
        return false;
    }
    // 正向推理机函数，多次执行step_forward，直到没有可用的规则
    public boolean deduce(){
        boolean mark=false;
        while(step_forward()){
            mark=true;
        }
        return mark;
    }
    // 判断一条规则Rule的所有condition是否都在事实库中
    public boolean test_if(Rule rule){
        List<String> conditions=rule.getConditions();
        return new HashSet<>(facts).containsAll(conditions);
    }
    // 若test_if为true，则将这条rule的不在facts中的结论放入
    public boolean try_rule(Rule rule){
        if(recall(rule.getResult())) return false;
        if(test_if(rule)){
            facts.addAll(rule.getResult());
            return true;
        }
        return false;
    }
    // 判断fact是否在facts中，在则返回null，不在则加入，返回fact
    public String remember(String fact){
        if(facts.contains(fact)) return null;
        facts.add(fact);
        return fact;
    }
    // 判断fact是否在facts中，不在则返回false，在则返回true
    public boolean recall(List<String> fact){
        if(new HashSet<>(facts).containsAll(fact)) return true;
        return false;
    }
    // 判断rule中的结论是否都在facts中，在则返回false，否则将不在的结论添加到facts中，返回true
    public boolean use_then(Rule rule){
        List<String> result=rule.getResult();
        if(new HashSet<>(facts).containsAll(result)) return false;
        facts.addAll(result);
        return true;
    }
}