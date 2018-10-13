# cache-api

- 支持远程调用的海量存储的缓存api
- 支持多app租用（bid参数，指定你的app名称。若该名称相同，则你使用同一个app配置）：RemoteCacheServer.initConfig(String server, Integer port, String bid)
