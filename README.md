# Project in progress...

Java Library for the [BlueSky API](https://docs.bsky.app/).

List of the implemented endpoints
available [here](https://github.com/RedouaneBALI/RedSky/blob/main/src/main/java/io/github/redouanebali/IBlueskyClient.java).

# How to use it

## Maven

Add in your pom.xml

```
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>
```

and

```
  <dependencies>
    <dependency>
      <artifactId>redsky</artifactId>
      <groupId>com.github.RedouaneBALI</groupId>
      <version>1.0.4</version>
    </dependency>
  </dependencies>  
```

More info on [jitpack](https://jitpack.io/private#RedouaneBALI/redsky).

## Usage

### Credentials

Credentials can be used with env variable or property file.

Example :

Environment variables : `BLUESKY_USERNAME=xxxxxxxx.bsky.social;BLUESKY_PASSWORD=xxxx-xxxx-xxxx-xxxx`

```
      BlueskyClient client = new BlueskyClient();
      String username = System.getenv("BLUESKY_USERNAME");
      String password = System.getenv("BLUESKY_PASSWORD");
      client.login(username, password);
```

or

```
    BlueskyClient client = new BlueskyClient();
    Properties properties = new Properties();
    try (InputStream input = new FileInputStream("bluesky.properties")) {
      properties.load(input);
    }
    String username = properties.getProperty("bluesky.username");
    String password = properties.getProperty("bluesky.password");
    client.login(username, password);
```

bluesky.properties :

```
bluesky.username=xxxxxxxx.bsky.social
bluesky.password=xxxx-xxxx-xxxx-xxxx
````

### Available methods

See [here](https://github.com/RedouaneBALI/redsky/blob/main/src/main/java/io/github/redouanebali/IBlueskyClient.java)

### Usage exemples

See usage
examples [here](https://github.com/RedouaneBALI/RedSky/blob/main/src/test/java/com.github.redouanebali.IntegrationTest.java).
