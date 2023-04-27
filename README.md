# Leetcode-tag-explorer

This app provides an in-memory map of leetcode.com problem stats based on json objects retrieved from the browser console. You can query combinations that leetcode does not currently offer even to premium users.

N.B. Please don't ask me for the json files. You will need to get a **premium subscribtion** to scrape all leetcode problems. 

### Set up your resources

For now, simply put all json files with company tags under `src/main/resource/data/company` folder and all unfiltered json files under `src/main/resource/data/unfiltered`. File names don't matter and the JSON schema can be inferred through domain files listed [here](https://github.com/joshir/leetcode-tag-explorer/tree/main/src/main/java/com/joshir/domain). The object mapper I'm using is configured to ignore unkown properties and can be configured [here](https://github.com/joshir/leetcode-tag-explorer/blob/fc02500a1e2e923ee256c2ada62c94d7778e173b/src/main/java/com/joshir/domain/mapper/JsonMapper.java#L26).

<p>
  <img src="https://github.com/joshir/leetcode-tag-explorer/blob/main/img/Screenshot%202023-04-26%20at%209.09.57%20PM.png" alt="screenshot"/>
</p>



### Run the app in your local env 
`$ mvn spring-boot:run`

## Other proposed features
1. expose this api using graphql? (may be) 
2. expose a REST api with the ability to query problems by any combination of `company`, `tag`, `difficulty`, and `frequency` (yes)
3. create a simple client app in the browser to explore this data (may be)
4. rewrite this in `TypeScript` and create a chrome problem to display problem stats. (may be) Follow up question: can we possibly use this to explore leetcode's data store in the browser further?


