# questionsAndroid

#### Android Studio
Download: http://developer.android.com/tools/studio/index.html

#### Quick Start
1. Fork COMP3111/questionsAndroid
2. Launch Android Studio
3. Quick Start > Checkout project from version control
4. Specify information such as git repository URL (copy and paste from github) and your local directory
5. Wait until the project is loaded

#### Configuration
- In package hk.ust.cse.hunkim.questionroom
- MainActivity class
- Please change this firebase URL to your app URL. Otherwise, it won't work.
```
 // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://classquestion.firebaseio.com/";
```
#### CI
