
# cordova-plugin-salesforce-snapins

Description

## Installation

```
cordova plugin add cordova-plugin-salesforce-snapins
```

## Supported platforms

- Android
- iOS

## Usage

```
window.plugins.SalesforceSnapIns.initialize({
	colors: {
	},
	liveAgent: {
		liveAgentPod: '...',
		orgId: '...',
		deploymentId: '...',
		buttonId: '...'
	}
});
```

## Color customization

See [official customization page iOS](https://developer.salesforce.com/docs/atlas.en-us.noversion.service_sdk_ios.meta/service_sdk_ios/customize_colors.htm)
and [Android](https://developer.salesforce.com/docs/atlas.en-us.noversion.service_sdk_android.meta/service_sdk_android/android_customize_colors.htm)
to learn more about color customization.

Constants available are:

| Constant iOS | Contant Android | Description |
| ------------ | --------------- | ----------- |
| brandPrimary | salesforce_brand_primary | First data category, the Show More button, the footer stripe, the selected article. |


## iOS

```
window.plugins.SalesforceSnapIns.initialize({
	// ...
	colors: {
		brandPrimary: '#50e3c2'
	},
	// ...
});
```

## Android

`config.xml`

```
<edit-config file="AndroidManifest.xml" target="/manifest/uses-sdk" mode="merge">
	<color name="salesforce_brand_primary">#50e3c2</color>
	<color name="salesforce_brand_secondary">#4a90e2</color>
	<color name="salesforce_contrast_inverted">#ffffff</color>
	<color name="salesforce_contrast_primary">#333333</color>
	<color name="salesforce_contrast_secondary">#767676</color>
	<color name="salesforce_feedback_primary">#e74c3c</color>
</edit-config>
```
