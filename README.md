# AwesomeNavigation [ ![Download](https://api.bintray.com/packages/anoop44/maven/awesome-navigation/images/download.svg?version=1.2.0) ](https://bintray.com/anoop44/maven/awesome-navigation/1.2.0/link) <a href="https://android-arsenal.com/api?level=16"><img alt="API" src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat"/></a>

AwesomeNavigation is a collection of navigation components(styles) that can be used as bottom navigations as well as top navigation(Tab bar). Not all components in this are suitable for tab bar. As of now there is only two components, but more components will be added soon and option to choose the type you wanted to use will be added soon
Each navigation component is referred as a style(style1, style2 etc). Which style to be used can be selected via the navStyle attribute in layout xml. Its takes values like `style1`, `style2` etc.

![gif of AwesomeNavigation in use](https://raw.githubusercontent.com/anoop44/AwesomeNavigation/master/art/awesome-navigation-style1-demo.gif)

## Adding to project

### Gradle 
Add below code to your **root** `build.gradle` file (if you have multiple modules and only one of them require `AwesomeNavigation`, add the `jcenter()` repository only in that module's `build.gradle`).
```gradle
allprojects {
    repositories {
        jcenter()
    }
}
```
And add the following dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "ss.anoop:awesome-navigation:${latest-version}"
}
```
AwesomeNavigation can be included in your layout xml like any other view. Below is a simple example

## Usage
```xml 
<ss.anoop.awesomenavigation.AwesomeNavigation
        android:id="@+id/bottom_navigation"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_alignParentBottom="true"
        android:background="#fff"
        app:navItems="@menu/bottom_navigation_items"
        app:textSize="14sp"
        app:iconSize="24dp"
        app:textColor="#000"
        app:spacing="4dp"
        app:itemChangeDuration="500"
        app:navStyle="style1"/>
```
### Customization

Table below describes the properties available to customize the AwesomeNavigation. Some options are applicable to only certain styles


| Property Name          | Format    | Description | Styles applicable                                                                                                                                                                                                 |
|------------------------|-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|
| navItems               | reference | It refers to a menu xml which contains list of items to be added in the navigation. Currently it reads `title`, `icon`, `selectedIcon` and `id` from a single item in the menu. `selectedIcon` may not be used by all navigation components.                               | all styles |
| textColor              | color     | defines the color of the text shown on each navigation item   | all styles |
| textSize               | dimension | defines the size of the text shown on each navigation item. As any other text size mention it in `sp`   | all styles|
| iconSize               | dimension | defines the size of icon in every navigation item | all styles |
| spacing                | dimension | defines the space between text and icon | all styles |
| itemChangeDuration     | integer   | defines the duration of animation | all styles |
| selectedColor          | color     | defines the color of selected tab background | style 2 |
| cornerRadius           | dimension | defines corner radius of selection background | style 2|

### Change Listener
AwesomeNavigation offers an `OnNavigationSelectedListener` that informs when selected tab is changed or same item is re-selected. The callback has two parameters, `id` and `position`.

```kotlin
bottomNavigation.setOnNavigationSelectedListener(object : OnNavigationSelectedListener {
            override fun onSelectNavigation(id: Int, position: Int) {
                changeContent(id, position)
            }

            override fun onReselectNavigation(id: Int, position: Int) {
                scrollToTop()
            }
        })
```


## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/anoop44/AwesomeNavigation/stargazers)__ for this repository. :star:

# License
```
Copyright 2020 anoop44 (Anoop)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```