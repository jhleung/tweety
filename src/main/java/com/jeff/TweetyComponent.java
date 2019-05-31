package com.jeff;

import com.jeff.resources.TweetyResource;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {TweetyModule.class})
public interface TweetyComponent {
    TweetyResource getTweetyResource();
}
