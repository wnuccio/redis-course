# Redis with Jedis

## Create and connect to a Redis instance
This project uses Reidslabs to get a running instance of Redis,
and the Jedis library for Java to connect to Redis.

Configuration:
- go to https://app.redislabs.com/#/login, and within a subscription create a new free database
- pick up the following information from the created db:
  - in the **General** section, the _url_ and the _port_ from the public endpoint
  - in the **Security** section, the _password_ (no need to specify the username)
- in RedisClientFactory use the previous information to establish a connection:
```
Jedis client = new Jedis("redis-19321.c240.us-east-1-3.ec2.cloud.redislabs.com", 19321);
client.auth("7vkXLfZRpRe5fyJ4kFM4wN0UHUJJQ76I");
``` 

