# Garmin Live Activity On Phone
[![Actions Status](https://github.com/basva923/GarminLive/workflows/Android%20CI/badge.svg)](https://github.com/basva923/GarminLive/actions)

This is NOT an application provided by Garmin.

This application retrieves live activity data from your Garmin devices and shows it on the screen.

An apk release build is available in the release assets of this repo: [v1.0](https://github.com/basva923/GarminLive/releases/tag/v1.0)

## Requirements
- Garmin Connect is installed on your phone
- This application is installed on your phone
- PhoneActivity is installed on your Garmin device: [Github PhoneActivity](https://github.com/basva923/PhoneAcitvity)

## Documentation
To show your activity on your phone:
1. Connect your Garmin device to your phone.
2. Open the PhoneActivity app on your Garmin device.
3. Wait for a GPS signal (shown on top).
4. Open the GarminLive app on your phone.
5. Press start to begin your activity.


## Build instructions
The easiest way to contribute/build/try with the code is to import this project into android studio.

To show the mapbox map, create a free mapbox code, create a secrets.xml file in the [values](app/src/main/res/values) folder. And past in the following content:
```xml
<resources>
    <string name="mapbox_access_token">PASTE_YOUR_TOKEN_HERE</string>
</resources>
```
And create a secret access token to download the mapbox sdk, which is exampled here: [Mapbox Docs](https://docs.mapbox.com/android/maps/overview/#install-the-maps-sdk).

To test the application without connecting to a Garmin device. Load the mock controller by commenting and uncommenting the corresponding line in the setupActivity function in [MainActivity.kt](app/src/main/java/com/github/basva923/garminphoneactivity/MainActivity.kt).

## Some screenshots
<img alt="Main screen with time, power, speed, heart rate and cadence" src="screenshots/large_overview.png" width="200px">
<img alt="The simple overview, power, speed, heart rate and cadence" src="screenshots/simple_overview.png" width="200px">
<img alt="The map with current location and track" src="screenshots/map.png" width="200px">
<img alt="The settings with mass, FTP, FTP heart rate, bike settings and road type" src="screenshots/settings.png" width="200px">
<img alt="The larger overview of some fields" src="screenshots/all_fields.png" width="200px">

## Use Cases
- Use an old phone as a head unit on your bike, it you have a Garmin watch.

## Limitations
- The only activity type that is supported is Cycling.
