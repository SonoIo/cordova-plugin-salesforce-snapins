var cordova = require('cordova');
var exec    = require('cordova/exec');

function successCallback() {}
function errorCallback() {}

function SalesforceSnapInsPlugin() {}

// Keyboard types
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_DEFAULT = 0;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_ASCII_CAPABLE = 1;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_NUMBERS_AND_PUNCTATION = 2;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_URL = 3;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_NUMBER_PAD = 4;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_PHONE_PAD = 5;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_NAME_PHONE_PAD = 6;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_EMAIL_ADDRESS = 7;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_DECIMAL_PAD = 8;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_TWITTER = 9;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_WEB_SEARCH = 10;
SalesforceSnapInsPlugin.prototype.KEYBOARD_TYPE_ASCII_CAPABLE_NUMBER_PAD = 11;

// Autocorrection
SalesforceSnapInsPlugin.prototype.AUTOCORRECTION_TYPE_DEFAULT = 0;
SalesforceSnapInsPlugin.prototype.AUTOCORRECTION_TYPE_NO = 1;
SalesforceSnapInsPlugin.prototype.AUTOCORRECTION_TYPE_YES = 2;

/**
 * Inizialite the plugin
 * @version 1.0.0
 * @param  {Object} options - Settings of the plugin
 * @param  {Object} [options.liveAgentChat] - Live Agent Chat options
 * @param  {String} [options.liveAgentChat.liveAgentPod]
 * @param  {String} [options.liveAgentChat.orgId]
 * @param  {String} [options.liveAgentChat.deploymentId]
 * @param  {String} [options.liveAgentChat.buttonId]
 * @param  {Object} [options.colors] - iOS only, it configures the color palette. For Android use the config.xml
 * @param  {String} [options.colors.brandPrimary]
 * @param  {String} [options.colors.brandPrimaryInverted]
 * @param  {String} [options.colors.brandSecondary]
 * @param  {String} [options.colors.brandSecondaryInverted]
 * @param  {String} [options.colors.contrastInverted]
 * @param  {String} [options.colors.contrastPrimary]
 * @param  {String} [options.colors.contrastQuaternary]
 * @param  {String} [options.colors.contrastSecondary]
 * @param  {String} [options.colors.contrastTertiary]
 * @param  {String} [options.colors.feedbackPrimary]
 * @param  {String} [options.colors.feedbackSecondary]
 * @param  {String} [options.colors.feedbackTertiary]
 * @param  {String} [options.colors.navbarBackground]
 * @param  {String} [options.colors.navbarInverted]
 * @param  {String} [options.colors.overlay]
 * @param  {Function} success - Success callback
 * @param  {Function} error - Error callback
 * @return {SalesforceSnapInsPlugin} - Instance of the plugin
 * @example
 * cordova.plugin.SalesforceSnapInsPlugin.initialize({
 *   liveAgentChat: {
 *     liveAgentPod: "[...].salesforceliveagent.com",
 *     orgId: "[...]",
 *     deploymentId: "[...]",
 *     buttonId: "[...]"
 *   },
 *   colors: {
 *     brandPrimary: "#ffcc00",
 *     brandSecondary: "#00ccff"
 *   }
 * });
 */
SalesforceSnapInsPlugin.prototype.initialize = function initialize(options, success, error) {
    if (!options) throw new Error('Cannot initialize SalesforceSnapInsPlugin without the options parameter');
    if (!success) success = successCallback;
    if (!error) error = errorCallback;
    exec(success, error, 'SalesforceSnapInsPlugin', 'initialize', [ options ]);
    return this;
};

/**
 * Add pre-chat field
 * @version 1.0.0
 * @param  {Object} fields - Fields
 * @param  {Function} success - Success callback
 * @param  {Function} error - Error callback
 * @return {SalesforceSnapInsPlugin} - Instance of the plugin
 * @example
 * cordova.plugin.SalesforceSnapIns.clearPrechatFields();
 * cordova.plugin.SalesforceSnapIns.addPrechatField({
 *   type: 'text',
 *   label: 'Email',
 *   required: true,
 *   keyboardType: SalesforceSnapIns.KEYBOARD_TYPE_EMAIL_ADDRESS,
 *   autocorrectionType: SalesforceSnapIns.AUTOCORRECTION_TYPE_NO
 * });
 */
SalesforceSnapInsPlugin.prototype.addPrechatField = function addPrechatField(fields, success, error) {
    if (!fields) throw new Error('Cannot addPrechatField without the fields parameter');
    if (!success) success = successCallback;
    if (!error) error = errorCallback;
    exec(success, error, 'SalesforceSnapInsPlugin', 'addPrechatField', [ fields ]);
    return this;
};

/**
 * Clear pre-chat fields
 * @version 1.0.0
 * @param  {Function} success - Success callback
 * @param  {Function} error - Error callback
 * @return {SalesforceSnapInsPlugin} - Instance of the plugin
 */
SalesforceSnapInsPlugin.prototype.clearPrechatFields = function clearPrechatFields(success, error) {
    if (!success) success = successCallback;
    if (!error) error = errorCallback;
    exec(success, error, 'SalesforceSnapInsPlugin', 'clearPrechatFields', [ ]);
    return this;
};

/**
 * Add pre-chat entity
 * @version 1.0.0
 * @param  {Object} entities - Entities
 * @param  {Function} success - Success callback
 * @param  {Function} error - Error callback
 * @return {SalesforceSnapInsPlugin} - Instance of the plugin
 * @example
 * cordova.plugin.SalesforceSnapIns.clearPrechatEntities();
 * var accountEntity = {
 *   name: "Account",
 *   linkToEntityName: "Account",
 *   linkToEntityField: "AccountId",
 *   saveToTranscript: "AccountId",
 *   showOnCreate: true,
 *   fieldMap: [
 *     {
 *       fieldName: "email",
 *       label: "Email",
 *       isExactMatch: true,
 *       doCreate: true,
 *       doFind: true
 *     }
 *   ]
 * };
 * cordova.plugin.SalesforceSnapIns.addPrechatEntity(accountEntity);
 */
SalesforceSnapInsPlugin.prototype.addPrechatEntity = function addPrechatEntity(entities, success, error) {
    if (!entities) throw new Error('Cannot addPrechatEntity without the entities parameter');
    if (!success) success = successCallback;
    if (!error) error = errorCallback;
    exec(success, error, 'SalesforceSnapInsPlugin', 'addPrechatEntity', [ entities ]);
    return this;
};

/**
 * Clear pre-chat entities
 * @version 1.0.0
 * @param  {Object} entities - Entities
 * @param  {Function} success - Success callback
 * @param  {Function} error - Error callback
 * @return {SalesforceSnapInsPlugin} - Instance of the plugin
 */
SalesforceSnapInsPlugin.prototype.clearPrechatEntities = function clearPrechatEntities(success, error) {
    if (!success) success = successCallback;
    if (!error) error = errorCallback;
    exec(success, error, 'SalesforceSnapInsPlugin', 'clearPrechatEntities', [ ]);
    return this;
};

/**
 * Open the Live Agent Chat panel.
 * If you want to check the presence of an agent use the method SalesforceSnapInsPlugin.determineAvailability.
 * @version 1.0.0
 * @param  {function} success -
 * @param  {function} error -
 * @return {SalesforceSnapInsPlugin} - Instance of the plugin
 * @example
 * cordova.plugin.SalesforceSnapIns.openLiveAgentChat(function() {
 *   console.log('It works! :P');
 * }, function (err) {
 *   console.error(err);
 * });
 */
SalesforceSnapInsPlugin.prototype.openLiveAgentChat = function openLiveAgentChat(success, error) {
    if (!success) success = successCallback;
    if (!error) error = errorCallback;
    exec(success, error, 'SalesforceSnapInsPlugin', 'openLiveAgentChat', [ ]);
    return this;
};

/**
 * Check the availability of a agent
 * @version 1.0.0
 * @param  {function} success - (available) if available is true it's safe to call SalesforceSnapInsPlugin.openLiveAgent
 * @param  {function} error - (err)
 * @return {SalesforceSnapInsPlugin} - Instance of the plugin
 * @example
 * cordova.plugin.SalesforceSnapIns.determineAvailability(function(available) {
 *   if (available) {
 *     SalesforceSnapIns.openLiveAgentChat(function() {
 *       console.log('It works! :P');
 *     }, function (err) {
 *       console.error(err);
 *     });
 *   }
 * }, function (err) {
 *   console.error(err);
 * });
 */
SalesforceSnapInsPlugin.prototype.determineAvailability = function determineAvailability(success, error) {
    if (!success) success = successCallback;
    if (!error) error = errorCallback;
    exec(success, error, 'SalesforceSnapInsPlugin', 'determineAvailability', [ ]);
    return this;
};

module.exports = new SalesforceSnapInsPlugin();
