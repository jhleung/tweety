package com.jeff;

import com.jeff.resources.TweetyResource;
import dagger.Component;

@Component(modules = {TweetyModule.class})
public interface TweetyComponent {
    TweetyResource getTweetyResource();
}
