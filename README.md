# Project 3 - *SjeTwitterClient*

**SjeTwitterClient** is an android app that allows a user to view his Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **12** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x]	User can **sign in to Twitter** using OAuth login
* [x]	User can **view tweets from their home timeline**
  * [x] User is displayed the username, name, and body for each tweet
  * [x] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
  * [x] User can view more tweets as they scroll with [infinite pagination](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews). Number of tweets is unlimited.
    However there are [Twitter Api Rate Limits](https://dev.twitter.com/rest/public/rate-limiting) in place.
* [x] User can **compose and post a new tweet**
  * [x] User can click a “Compose” icon in the Action Bar on the top right
  * [x] User can then enter a new tweet and post this to twitter
  * [x] User is taken back to home timeline with **new tweet visible** in timeline

The following **optional** features are implemented:

* [x] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [x] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [x] User can **pull down to refresh tweets timeline**
* [x] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.
* [ ] User can tap a tweet to **open a detailed tweet view**
* [ ] User can **select "reply" from detail view to respond to a tweet**
* [x] Improve the user interface and theme the app to feel "twitter branded"
  * Uses twitter image for tweet button
  * Material design style tweet button
  * Process 'action done' on the tweet text area
  * Tweet area text limited to 140 chars

The following **bonus** features are implemented:

* [ ] User can see embedded image media within the tweet detail view
* [ ] Compose tweet functionality is build using modal overlay

The following **additional** features are implemented:

* [x] Use JodaTime for time parsing and relative timestamp formatting
* [x] Use FontAwesome for extra text icons
* [x] Revert to persisted data on network error

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

<img src='SjeTwitterClient.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes
Last project needed latest version of Async HTTP to remove some errors/warnings, but using it in this project caused auth errors on twitter, which took some time to work out
Breakponts failing for async tasks is a major pain!
Persisting data properly using ActiveAndroid was difficult - replacing user objects deleted other tweets. Had to check for existing users.

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android
- [JodaTime Android](https://github.com/dlew/joda-time-android) - Useful Time classes
- [Android Iconify](https://github.com/JoanZapata/android-iconify) - IconTextView for displaying FontAwesome icons

Also, bundled with the base template:
- [Codepath OAuth](https://github.com/codepath/android-oauth-handler)
- [scribe-java](https://github.com/scribejava/scribejava)

## License

    Copyright 2015 Scott Ellis

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.