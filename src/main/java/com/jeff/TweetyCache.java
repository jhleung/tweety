package com.jeff;

import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TweetyCache {
    private LinkedHashMap<String, Response> cache;
    private String lastMessagePublished;
    private String publishedTweetSincePull, publishedTweetSinceFilter;

    private Response timeline;
    private Response filteredTimeline;

    public TweetyCache() { cache = new LinkedHashMap<>(); }

    public void put(String key, Response value) {
        if (cache.containsKey(key)) {
            cache.remove(key);
        }
        cache.put(key, value);
        lastMessagePublished = key;
        expireCacheEntries();
        expireTimelineCache();
    }

    public Response get(String key) { return cache.get(key); }

    public boolean contains(String key) { return cache.containsKey(key) && cache.get(key).getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(); }

    public Response getTimeline() { return timeline; }

    public Response getFilteredTimeline() { return filteredTimeline; }

    public void setTimeline(Response t) {
        timeline = t;
        publishedTweetSincePull = lastMessagePublished;
    }

    public void setFilteredTimeline(Response ft) {
        filteredTimeline = ft;
        publishedTweetSinceFilter = lastMessagePublished;
    }

    public boolean useCachedTimeline() {
       return publishedTweetSincePull == lastMessagePublished && timeline != null;
    }

    public boolean useCachedFilteredTimeline() {
        return publishedTweetSinceFilter == lastMessagePublished && filteredTimeline != null;
    }

    private void expireCacheEntries() {
        for (Map.Entry<String, Response> entry : cache.entrySet()) {
            if (System.currentTimeMillis() -  entry.getValue().getLastModified().getTime() >= TimeUnit.DAYS.toMillis(1))
                cache.remove(entry.getKey());
            else
                break;
        }
    }

    private void expireTimelineCache() {
        if (timeline != null && isAtLeastAnHourOld(timeline.getLastModified().getTime()))
            timeline = null;
        if (filteredTimeline != null && isAtLeastAnHourOld(filteredTimeline.getLastModified().getTime()))
            filteredTimeline = null;
    }

    private boolean isAtLeastAnHourOld(long millisAtWrite) { return System.currentTimeMillis() - millisAtWrite >= TimeUnit.HOURS.toMillis(1); }
}
