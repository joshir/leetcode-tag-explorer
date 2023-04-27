# leetcode-tag-explorer

This app provides an in-memory map of leetcode.com problem stats based on json objects retrieved from the browser console. You can query combinations that leetcode does not currently offer even to premium users.

N.B. Please don't ask me for the json files. You will need to get a **premium subscribtion** to scrape all leetcode problems. 

### set up your resources
For now, simply put all json files with company tags under `src/main/resource/data/company` folder and all unfiltered json files under `src/main/resource/data/unfiltered`.
<p>
  <img src="https://github.com/joshir/leetcode-tag-explorer/blob/main/img/Screenshot%202023-04-26%20at%209.09.57%20PM.png" alt="screenshot"/>
</p>

### run the app in your local env 
`$ mvn spring-boot:run`

## other proposed features
1. expose this api using graphql? (may be) 
2. expose a REST api with the ability to query problems by anyone combination of `company`, `tag`, `difficulty`, and `frequency` (yes)
3. create a simple client app in the browser to explore this data (may be)
4. rewrite this in `TypeScript` and create a chrome problem to display problem stats. (may be) Follow up question: can we possibly use this to explore leetcode's data store in the browser further?


