# lc-tag-explorer

This is an in-memory map of leetcode.com problem stats based on json objects retrieved from the browser console (Please don't ask me for the json files. You will need to get a premium subscribtion for yourself to scrape problem stats for your personal use.) 

# proposed features
1. expose this api using graphql with the ability to query problems by `company`, `type`, `difficulty`, or even by `list(company)`, `type`, `difficulty`. Off the top, other queries like: "most frequetly occuring questions across all companies by frequency, type, difficulty" etc.
2. rewrite this is `TypeScript` and create a chrome problem to display problem stats. (may be)
3. create an actual db schema and feed the in-memory map to it, restructuring some of the raw data from leetcode.
