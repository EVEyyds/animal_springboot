# 设计说明文档
## 一、系统说明 {#1}
本系统是一个简易的***动物识别专家系统***，基于***Spring Boot***和***html***实现。
***整体效果***：在Spring Boot中定义规则库，html页面中输入已知的事实作为事实库，在Spring Boot中利用自定义的规则引擎匹配出结论，返回并显示在html页面中。

## 二、主要类说明 {#2}
### 1.规则类(Rule) {#2.1}
```java
	public class Rule {
		List<String> conditions;
	 	List<String> result;
	 }
```
Rule类包括两个List：规则前件（Conditions）和规则后件（result）。
### 2.规则引擎类(RuleEngine) {#2.2}
```java
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
```
规则引擎中定义了规则库表、事实表、结论表。都使用List实现，规则、事实、文件都使用String类型。   
通过读取resource\static\data目录下的.json文件中的规则库，初始化rules。  
事实库由每次使用RuleEngine时传入。调用RuleEngine.decure来实现正向推理。  
**规则引擎**正向推理的过程如下：
从规则库中取一条规则，判断其规则后件是否都在事实库中，若有不在的，则判断其规则前件是否都在事实库中，若都在，则将规则后件都放入事实库中，然后重新取一条规则，直至规则库中找不到有规则符合要求，则执行结束。
## 三、规则库说明 {#3}
### 1.初始化使用的**初始规则库文件.json文件格式**示例 {#3.1}
```json
	[ 
	 {
    "conditions": ["是鸟","不会飞","有长脖子","有长腿","有黑白二色"],
    "result": ["是鸵鸟"]
    },
    {
    "conditions": ["是鸟","不会飞","会游泳","有黑白二色"],
    "result": ["是企鹅"]
    },
    {
    "conditions": ["是鸟","善飞"],
    "result": ["动物是信天翁"]
    }
	]
```
将规则前件写入conditions，规则后件写入result，如果前件和后件有意义相同的事实，要求表达方式一致。
### 2.本系统初始应用的**规则库**如下 {#3.2}
```
R1:if动物有毛发then动物是哺乳动物
R2:if动物有奶then动物是哺乳动物
R3:if动物有羽毛then动物是鸟
R4:if动物会飞and会生蛋then动物是鸟
R5:if动物吃肉then动物是食肉动物
R6:if动物有锋利牙齿and有爪and眼向前方then动物是食肉动物
R7:if动物是哺乳动物and有蹄then动物是有蹄类动物
R8:if动物是哺乳动物and反刍then动物是有蹄类动物
R9:if动物是哺乳动物and是食肉动物and有黄褐色皮毛and有暗斑点then动物是豹
R10:if动物是哺乳动物and是食肉动物and有黄褐色皮毛and有黑色条纹then动物是虎
R11:if动物是有蹄类动物and有长脖子and有长腿and有暗斑点then动物是长颈鹿
R12:if动物是有蹄类动物and有黑色条纹then动物是斑马
R13:if动物是鸟and不会飞and有长脖子and有长腿and有黑白二色then动物是鸵鸟
R14:if动物是鸟and不会飞and会游泳and有黑白二色then动物是企鹅
R15:if动物是鸟and善飞then动物是信天翁
```
## 四、接口设计 {#4}
**AnimalController.java**
```java
@RestController
@RequestMapping("/animal")
public class AnimalController {
    @Autowired
    private RuleService ruleService;
    @PostMapping( "/result")
    public Res<List<String>> rule(Request form){
        List<String> results=ruleService.rule(form);
        return new Res<>(200,results,"sucess");
    }
}
```
**RuleService.java**

```java
@Service
public class RuleService {
    List<String> SList=new ArrayList<>(Arrays.asList(
        "有毛发",
        "有奶",
        "有羽毛",
        "会飞",
        "会生蛋",
        "吃肉",
        "有锋利牙齿",
        "有爪",
        "眼向前方",
        "有蹄",
        "反刍",
        "有黄褐色皮毛",
        "有暗斑点",
        "有黑色条纹",
        "有长脖子",
        "有长腿",
        "不会飞",
        "有黑白二色",
        "会游泳",
        "善飞"));

    public List<String> rule(Request request){
        RuleEngine ruleEngine=new RuleEngine("rules.json");
        List<String> list=new ArrayList<>();
        if(request.getCondition1()!=null) list.add(SList.get(0));
        if(request.getCondition2()!=null) list.add(SList.get(1));
        if(request.getCondition3()!=null) list.add(SList.get(2));
        if(request.getCondition4()!=null) list.add(SList.get(3));
        if(request.getCondition5()!=null) list.add(SList.get(4));
        if(request.getCondition6()!=null) list.add(SList.get(5));
        if(request.getCondition7()!=null) list.add(SList.get(6));
        if(request.getCondition8()!=null) list.add(SList.get(7));
        if(request.getCondition9()!=null) list.add(SList.get(8));
        if(request.getCondition11()!=null) list.add(SList.get(9));
        if(request.getCondition12()!=null) list.add(SList.get(10));
        if(request.getCondition14()!=null) list.add(SList.get(11));
        if(request.getCondition15()!=null) list.add(SList.get(12));
        if(request.getCondition16()!=null) list.add(SList.get(13));
        if(request.getCondition18()!=null) list.add(SList.get(14));
        if(request.getCondition19()!=null) list.add(SList.get(15));
        if(request.getCondition21()!=null) list.add(SList.get(16));
        if(request.getCondition22()!=null) list.add(SList.get(17));
        if(request.getCondition23()!=null) list.add(SList.get(18));
        if(request.getCondition24()!=null) list.add(SList.get(19));
        ruleEngine.setFacts(list);
        if(ruleEngine.deduce()){
            return ruleEngine.getResults();
        }else {
            return new ArrayList<>(Arrays.asList("未识别出结果"));
        }
    }
}
```
## 五、页面设计 {#5}
**index.html**
```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>简易动物识别专家系统</title>
  </head>
  <body>
    <iframe name="formsubmit" style="display: none"></iframe>
    <h1>简易动物识别专家系统--在线使用</h1>
    <form action="/animal/result" method="post" target="formsubmit">
      <h3>选择要识别的动物的特征</h3>
      <input type="checkbox" name="condition1" value="1" />有毛发<br />
      <input type="checkbox" name="condition2" value="1" />有奶<br />
      <input type="checkbox" name="condition3" value="1" />有羽毛<br />
      <input type="checkbox" name="condition4" value="1" />会飞<br />
      <input type="checkbox" name="condition5" value="1" />会生蛋<br />
      <input type="checkbox" name="condition6" value="1" />吃肉<br />
      <input type="checkbox" name="condition7" value="1" />有锋利牙齿<br />
      <input type="checkbox" name="condition8" value="1" />有爪<br />
      <input type="checkbox" name="condition9" value="1" />眼向前方<br />
      <input type="checkbox" name="condition11" value="1" />有蹄<br />
      <input type="checkbox" name="condition12" value="1" />反刍<br />
      <input type="checkbox" name="condition14" value="1" />有黄褐色皮毛<br />
      <input type="checkbox" name="condition15" value="1" />有暗斑点<br />
      <input type="checkbox" name="condition16" value="1" />有黑色条纹<br />
      <input type="checkbox" name="condition18" value="1" />有长脖子<br />
      <input type="checkbox" name="condition19" value="1" />有长腿<br />
      <input type="checkbox" name="condition21" value="1" />不会飞<br />
      <input type="checkbox" name="condition22" value="1" />有黑白二色<br />
      <input type="checkbox" name="condition23" value="1" />会游泳<br />
      <input type="checkbox" name="condition24" value="1" />善飞<br />
      <input type="submit" />
    </form>
    <br />
    识别结果为：<text id="result"></text>
    <br />
    <text
      >注意：识别的出的动物种类是根据输入的动物特征中是否有匹配的特征，有匹配成功的即为可能的动物种类</text
    >
    <script>
      document
        .querySelector("form")
        .addEventListener("submit", function (event) {
          event.preventDefault(); 
          var xhr = new XMLHttpRequest();
          xhr.open("POST", "/animal/result", true);
          xhr.onload = function () {
            if (xhr.status === 200) {
              var response = JSON.parse(xhr.responseText);
              document.getElementById("result").textContent = response.data;
            }
          };
          xhr.send(new FormData(event.target));
        });
    </script>
  </body>
</html>
```
## 六、系统使用演示 {#6}
<img src="https://www.chengygy.cn/file/example231207.png" style="width:50%;" alt="图片无法查看">
用户点击复选框选中动物具有的特征，提交后即可显示识别结果。识别结果可能有多个，包括所有中间的结论和最后的结果。


​	

