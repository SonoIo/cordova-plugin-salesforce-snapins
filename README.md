
# cordova-plugin-salesforce-snapins

Cordova plugin to integrate Salesforce Snap-ins SDK for iOS and Android on a Cordova project.

For more information about Salesforce SDK look at:

- [Salesforce iOS SDK](https://developer.salesforce.com/docs/atlas.en-us.noversion.service_sdk_ios.meta/service_sdk_ios/servicesdk_using_live_agent.htm)
- [Salesforce Android SDK](https://developer.salesforce.com/docs/atlas.en-us.noversion.service_sdk_android.meta/service_sdk_android/servicesdk_using_live_agent.htm)



## Installation

```
cordova plugin add cordova-plugin-salesforce-snapins
```



## Supported platforms

- Android
- iOS



## Usage

```js
var SalesforceSnapIns = window.cordova.plugins.SalesforceSnapIns;

SalesforceSnapIns.initialize({
	colors: { // iOS only
		brandPrimary: "#007F7F"
	},
	liveAgent: {
		liveAgentPod: '...',
		orgId: '...',
		deploymentId: '...',
		buttonId: '...'
	}
});

// Optional, if you don't need to change pre-chat fields you can omit this line
SalesforceSnapIns.clearPrechatFields();

SalesforceSnapIns.addPrechatField({
	type: 'hidden', // could be: text, hidden, picker
	label: 'Subject',
	value: 'Live Agent Chat support',
	transcriptField: 'subject__c',
	required: true
});

SalesforceSnapIns.addPrechatField({
	type: 'text',
	label: 'Name',
	required: true
});

SalesforceSnapIns.addPrechatField({
	type: 'text',
	label: 'Email',
	required: true,
	keyboardType: SalesforceSnapIns.KEYBOARD_TYPE_EMAIL_ADDRESS,
	autocorrectionType: SalesforceSnapIns.AUTOCORRECTION_TYPE_NO
});

SalesforceSnapIns.addPrechatField({
	type: 'picker',
	label: 'Argomento',
	required: true,
	values: [
		{ label: 'Reso',   value: 'reso' },
		{ label: 'Ordine', value: 'ordine' }
	]
});

// Optional, if you don't need to change pre-chat entities you can omit this line
SalesforceSnapIns.clearPrechatEntities();

var accountEntity = {
	name: "Account",
	linkToEntityName: "Account",
	linkToEntityField: "AccountId",
	saveToTranscript: "AccountId",
	showOnCreate: true,
	fieldMap: [
		{
			fieldName: "Name__c",
			label: "Name",
			isExactMatch: true,
			doCreate: true,
			doFind: false
		},
		{
			fieldName: "Email__c",
			label: "Email",
			isExactMatch: true,
			doCreate: true,
			doFind: true
		}
	]
};
SalesforceSnapIns.addPrechatEntity(accountEntity);

var caseEntity = {
	name: "Case",
	linkToEntityName: "Account",
	linkToEntityField: "AccountId",
	saveToTranscript: "AccountId",
	showOnCreate: true,
	fieldMap: [
		{
			fieldName: "Subject",
			label: "Subject",
			doCreate: true
		}
	]
};
SalesforceSnapIns.addPrechatEntity(caseEntity);

SalesforceSnapIns.determineAvailability(function (available) {
	if (available) {
		SalesforceSnapIns.openLiveAgentChat(function() {
			console.log('Chat opened');
		}, function () {
			console.error('Unable to open chat');
		});
	}
}, function (err) {
	console.error(err);
});

```

## initialize(options, [success], [error])

```js
cordova.plugins.SalesforceSnapIns.initialize({
	liveAgent: {
		liveAgentPod: '...',
		orgId: '...',
		deploymentId: '...',
		buttonId: '...'
	}
});
```

| Parameter | Required | Description |
| --------- | -------- | ----------- |
| **options** |  Yes | Configuration object |
| options.**liveAgentChat** | No | |
| options.liveAgentChat.**liveAgentPod** | Yes |  |
| options.liveAgentChat.**orgId** | Yes | |
| options.liveAgentChat.**deploymentId** | Yes | |
| options.liveAgentChat.**buttonId** | Yes | |
| options.**colors** | No | See customization section for more details |
| **success** | No | No prameters |
| **error** | No | **err**: error object |



## determineAvailability(success, error)

```js
cordova.plugins.SalesforceSnapIns.determineAvailability(function (available) {
	if (available) {
		// Open chat with cordova.plugins.SalesforceSnapIns.openLiveAgentChat
	} else {
		// No agents available
	}
}, function (err) {
	// An error occurred
});
```

| Parameter | Required | Description |
| --------- | -------- | ----------- |
| **success** | No | **available**: if true there is at least one agent available for the chat |
| **error** | No | **err**: error object |



## openLiveAgentChat(success, error)

It's suggested to call `determineAvailability()` before `openLiveAgentChat()`.

```js
cordova.plugins.SalesforceSnapIns.openLiveAgentChat(function () {
	// Chat opened
}, function (err) {
	// An error occurred
});
```

| Parameter | Required | Description |
| --------- | -------- | ----------- |
| **success** | No | No prameters |
| **error** | No | **err**: error object |



## addPrechatField(field, [success], [error])

```js
cordova.plugins.SalesforceSnapIns.addPrechatField({
	type: 'hidden', // could be: text, hidden, picker
	label: 'Subject',
	value: 'Live Agent Chat support',
	transcriptField: 'subject__c',
	required: true
});

cordova.plugins.SalesforceSnapIns.addPrechatField({
	type: 'text',
	label: 'Name',
	required: true
});

cordova.plugins.SalesforceSnapIns.addPrechatField({
	type: 'text',
	label: 'Email',
	required: true,
	keyboardType: cordova.plugins.SalesforceSnapIns.KEYBOARD_TYPE_EMAIL_ADDRESS,
	autocorrectionType: cordova.plugins.SalesforceSnapIns.AUTOCORRECTION_TYPE_NO
});

cordova.plugins.SalesforceSnapIns.addPrechatField({
	type: 'picker',
	label: 'Argomento',
	required: true,
	values: [
		{ label: 'Reso',   value: 'reso' },
		{ label: 'Ordine', value: 'ordine' }
	]
});
```

| Parameter | Required | Description |
| --------- | -------- | ----------- |
| **field** | Yes | Field details |
| field.**type** | Yes | Can be: `text`, `hidden`, `picker` |
| field.**label** | Yes | Displayed field name to the user |
| field.**value** | Yes (only for `hidden`) | Displayed field name to the user |
| field.**transcriptField** | No | Transcript field |
| field.**required** | No | Default **false** |
| field.**keyboardType** | No (only for `text`) | Default **KEYBOARD_TYPE_DEFAULT**. <br />Possible values:<br />**KEYBOARD_TYPE_DEFAULT**<br />**KEYBOARD_TYPE_ASCII_CAPABLE**<br />**KEYBOARD_TYPE_NUMBERS_AND_PUNCTATION**<br />**KEYBOARD_TYPE_URL**<br />**KEYBOARD_TYPE_NUMBER_PAD**<br />**KEYBOARD_TYPE_PHONE_PAD**<br />**KEYBOARD_TYPE_NAME_PHONE_PAD**<br />**KEYBOARD_TYPE_EMAIL_ADDRESS**<br />**KEYBOARD_TYPE_DECIMAL_PAD**<br />**KEYBOARD_TYPE_TWITTER**<br />**KEYBOARD_TYPE_WEB_SEARCH**<br />**KEYBOARD_TYPE_ASCII_CAPABLE_NUMBER_PAD** |
| field.**autocorrectionType** | No (only for `text`) | Default **AUTOCORRECTION_TYPE_DEFAULT**. Used on iOS only.<br />Possible values:<br />**AUTOCORRECTION_TYPE_DEFAULT**<br />**AUTOCORRECTION_TYPE_NO**<br />**AUTOCORRECTION_TYPE_YES** |
| field.**values[]** | Yes (only for `picker`) | Array of options |
| field.values[].**label** | Yes (only for `picker`) | Label showed to the user |
| field.values[].**value** | Yes (only for `picker`) | Value of the option |
| **success** | No | No prameters |
| **error** | No | **err**: error object |



## clearPrechatFields([success], [error])

```js
// Optional, if you don't need to change pre-chat fields you can omit this line
cordova.plugins.SalesforceSnapIns.clearPrechatFields();
```

| Parameter | Required | Description |
| --------- | -------- | ----------- |
| **success** | No | No prameters |
| **error** | No | **err**: error object |



## addPrechatEntity(entities, [success], [error])

```js
var accountEntity = {
	name: "Account",
	linkToEntityName: "Account",
	linkToEntityField: "AccountId",
	saveToTranscript: "AccountId",
	showOnCreate: true,
	fieldMap: [
		{
			fieldName: "Name__c",
			label: "Name",
			isExactMatch: true,
			doCreate: true,
			doFind: false
		},
		{
			fieldName: "Email__c",
			label: "Email",
			isExactMatch: true,
			doCreate: true,
			doFind: true
		}
	]
};
SalesforceSnapIns.addPrechatEntity(accountEntity);
```

| Parameter | Required | Description |
| --------- | -------- | ----------- |
| **entities** | Yes | Salesforce entity options |
| entities.**name** | Yes | Entity name |
| entities.**linkToEntityName** | No | Name of another entity linked to this |
| entities.**linkToEntityField** | No | Field of another entity linked to this |
| entities.**showOnCreate** | No | Default **false**. If true the entity will be shown to the agent when a new chat start |
| entities.**fieldMap[]** | Yes | Describe how Salesforce should map pre-chat fields |
| entities.fieldMap[].**fieldName** | Yes | Field name |
| entities.fieldMap[].**label** | Yes | Label of the filed. Should match the pre-chat field label or data could not be sended correctly |
| entities.fieldMap[].**isExactMatch** | No | Default **false** |
| entities.fieldMap[].**doCreate** | No | Default **false** |
| entities.fieldMap[].**doFind** | No | Default **false** |
| **success** | No | No prameters |
| **error** | No | **err**: error object |



## clearPrechatEntities([success], [error])

```js
// Optional, if you don't need to change pre-chat entities you can omit this line
cordova.plugins.SalesforceSnapIns.clearPrechatEntities();
```

| Parameter | Required | Description |
| --------- | -------- | ----------- |
| **success** | No | No prameters |
| **error** | No | **err**: error object |



## Color customization

See [official customization page for iOS](https://developer.salesforce.com/docs/atlas.en-us.noversion.service_sdk_ios.meta/service_sdk_ios/customize_colors.htm)
and [Android](https://developer.salesforce.com/docs/atlas.en-us.noversion.service_sdk_android.meta/service_sdk_android/android_customize_colors.htm)
to learn more about color customization.

Constants available are:

| iOS constant | Android constant | Description |
| ------------ | --------------- | ----------- |
| navbarBackground | salesforce_toolbar | Background color for the navigation bar. |
| navbarInverted | salesforce_toolbar_inverted | Navigation bar text and icon color. |
| brandPrimary | salesforce_brand_primary | First data category, the Show More button, the footer stripe, the selected article. |
| brandSecondary | salesforce_brand_secondary | Used throughout the UI for button colors. |
| brandPrimaryInverted | salesforce_title_color | Text on data category headers and the chevron on the Knowledge home page. |
| brandSecondaryInverted | salesforce_brand_contrast | Text on areas where a brand color is used for the background. |
| contrastPrimary | salesforce_contrast_primary | Primary body text color. |
| contrastSecondary | salesforce_contrast_secondary | Subcategory headers. |
| contrastTertiary | salesforce_contrast_tertiary | Search background color. |
| contrastQuaternary | salesforce_contrast_quaternary | Icon image background color. |
| contrastInverted | salesforce_contrast_inverted | Page background, navigation bar, table cell background. |
| feedbackPrimary | salesforce_feedback_primary | Text color for error messages. |
| feedbackSecondary | salesforce_feedback_secondary | Connection quality indicators. Background color for the Resume button when the two-way camera is active. |
| feedbackTertiary | salesforce_feedback_tertiary | Connection quality indicators. |
| overlay | salesforce_overlay | Background for the Knowledge home screen. |


### iOS

```js
window.plugins.SalesforceSnapIns.initialize({
	// ...
	colors: {
		navbarBackground: "#FAFAFA",
		navbarInverted: "#010101",
		brandPrimary: "#007F7F",
		brandSecondary: "#2872CC",
		brandPrimaryInverted: "#FBFBFB",
		brandSecondaryInverted: "#FCFCFC",
		contrastPrimary: "#000000",
		contrastSecondary: "#767676",
		contrastTertiary: "#BABABA",
		contrastQuaternary: "#F1F1F1",
		contrastInverted: "#FFFFFF",
		feedbackPrimary: "#E74C3C",
		feedbackSecondary: "#2ECC71",
		feedbackTertiary: "#F5A623",
		overlay: "#000000"
	},
	// ...
});
```


### Android

```xml
<!-- config.xml -->
<platform name="android">
	<config-file parent="/resources" target="app/src/main/res/values/colors.xml">
		<color name="salesforce_toolbar">#FAFAFA</color>
		<color name="salesforce_toolbar_inverted">#010101</color>
		<color name="salesforce_brand_primary">#007F7F</color>
		<color name="salesforce_brand_secondary">#2872CC</color>
		<color name="salesforce_brand_contrast">#FCFCFC</color>
		<color name="salesforce_contrast_primary">#000000</color>
		<color name="salesforce_contrast_secondary">#767676</color>
		<color name="salesforce_contrast_tertiary">#BABABA</color>
		<color name="salesforce_contrast_quaternary">#F1F1F1</color>
		<color name="salesforce_contrast_inverted">#FFFFFF</color>
		<color name="salesforce_feedback_primary">#E74C3C</color>
		<color name="salesforce_feedback_secondary">#2ECC71</color>
		<color name="salesforce_feedback_tertiary">#F5A623</color>
		<color name="salesforce_title_color">#FBFBFB</color>
		<color name="salesforce_overlay">#000000</color>
	</config-file>
</platform>
```

**Attention!** after `cordova prepare android` the color configuration will be cached into `android/android.json` file.
If you want to remove, add or change any color after the first prepare be sure to clean `android.json`.
Alternatively you can type your color directly into `app/src/main/res/values/colors.xml` with the drawback that you will lose them renewing the platform.


## Prepare for submission (iOS)

As documented on [Salesforce documentation](https://developer.salesforce.com/docs/atlas.en-us.noversion.service_sdk_ios.meta/service_sdk_ios/prepare_for_appstore.htm)
on XCode project:

- select `Build Phases`
- create `Run script`
- paste this line of code `$PODS_ROOT/ServiceSDK/Frameworks/ServiceCore.framework/prepare-framework`


## TODO

- Add Case management support
- Add SOS support
- Enhance color customization for Android

## LICENSE

MIT
