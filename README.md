# SygicMaps SDK examples for Android

Add navigation features to your app with SygicMaps framework. Examples are provided as an addition to [documentation](https://developers.sygic.com/maps-sdk/android/getting_started/).

## Structure

Each folder contains an Android Studio project. Projects are independent from each other however they share the same android package, so it is not possible to install more than one on the same device unless you change android package.

1. SygicStart - initialization of Sygic SDK and showing the map on the screen
2. Gsp - enabling GPS listening for Sygic SDK and showing some basics camera controls
3. Map - example of handling map clicks and adding some basic user objects on the map
4. Search - example of using online search feature and showing search result on the map
5. Navigate - example of calculating route and navigating from GPS position to the point on the map

## How to run

1. Clone this repository
2. Get your [API key](https://www.sygic.com/enterprise/get-api-key)
3. Open project/folder which you would like to try in Android Studio
4. Put API key into local.properties of this project as sygic.sdk.client.id="your api key"
5. Run
