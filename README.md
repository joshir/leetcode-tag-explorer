# lc-tag-explorer

This is an in-memory map of leetcode.com problem stats based on json objects retrieved from the browser console (Please don't ask me for the json files. You will need to get a premium subscribtion for yourself to scrape problem stats for your personal use.) 

# proposed features
1. expose this api using graphql with the ability to query problems by `company`, `type`, and `difficulty`.
2. rewrite this is `TypeScript` and create a chrome problem to display problem stats. (may be)
3. create an actual db schema and feed the in-memory map to it, restructuring some of the raw data from leetcode.
