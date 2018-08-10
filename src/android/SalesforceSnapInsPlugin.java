package org.apache.cordova.salesforce;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.salesforce.android.chat.core.AgentAvailabilityClient;
import com.salesforce.android.chat.core.ChatConfiguration;
import com.salesforce.android.chat.core.ChatCore;
import com.salesforce.android.chat.core.model.AvailabilityState;
import com.salesforce.android.chat.core.model.ChatEntity;
import com.salesforce.android.chat.core.model.ChatUserData;
import com.salesforce.android.chat.ui.ChatUI;
import com.salesforce.android.chat.ui.ChatUIClient;
import com.salesforce.android.chat.ui.ChatUIConfiguration;
import com.salesforce.android.service.common.utilities.control.Async;
import com.salesforce.android.chat.ui.model.PreChatTextInputField;
import com.salesforce.android.chat.ui.model.PreChatPickListField;

import java.util.List;


public class SalesforceSnapInsPlugin extends CordovaPlugin {

    private ChatConfiguration.Builder liveAgentChatConfigBuilder;
    private List<ChatUserData> liveAgentChatUserData;
    private List<ChatEntity> liveAgentChatEntities;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    }

    private Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("initialize")) {
            JSONObject options;
            JSONObject liveAgentChatOptions;
            try {
                options = (JSONObject)args.get(0);
            } catch (JSONException e) {
                callbackContext.error("Unable parse options");
                return false;
            }

            if (options.has("liveAgentChat")) {
                try {
                    liveAgentChatOptions = (JSONObject) options.get("liveAgentChat");
                } catch (JSONException e) {
                    callbackContext.error("Unable parse options.liveAgentChat");
                    return false;
                }
                try {
                    this.initializeLiveAgentChat(liveAgentChatOptions);
                } catch (JSONException e) {
                    callbackContext.error("Unable parse options.liveAgentChat parameters");
                    return false;
                }
            }

            // TODO: here add SOS and Case management initializations

            callbackContext.success();

        } else if (action.equals("openLiveAgentChat")) {

            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);

            Activity mainActivity = this.cordova.getActivity();

            ChatUI.configure(ChatUIConfiguration.create(this.buildLiveAgentChatConfig()))
                .createClient(getApplicationContext())
                .onResult(new Async.ResultHandler<ChatUIClient>() {
                    @Override public void handleResult (Async<?> operation, @NonNull ChatUIClient chatUIClient) {
                        chatUIClient.startChatSession((FragmentActivity) mainActivity);
                        callbackContext.success();
                    }
                });

        } else if (action.equals("determineAvailability")) {

            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);

            AgentAvailabilityClient client = ChatCore.configureAgentAvailability(this.buildLiveAgentChatConfig());
            client.check()
                .onResult(new Async.ResultHandler<AvailabilityState>() {
                    @Override
                    public void handleResult(Async<?> async, @NonNull AvailabilityState state) {
                        switch (state.getStatus()) {
                            case AgentsAvailable:
                                callbackContext.success("available");
                                break;
                            case NoAgentsAvailable:
                                callbackContext.success("not available");
                                break;
                            case Unknown:
                                callbackContext.error("Unknown error");
                                break;
                        }
                    }
                });
        } else if (action.equals("addPrechatField")) {

            JSONObject field;
            try {
                field = (JSONObject)args.get(0);
            } catch (JSONException e) {
                callbackContext.error("Unable parse field");
                return false;
            }

            return this.addPrechatField(field, callbackContext);
        } else if (action.equals("clearPrechatFields")) {
            return this.clearPrechatFields(callbackContext);
        }

        return true;
    }

    private void initializeLiveAgentChat(JSONObject options) throws JSONException {
        String liveAgentPod = (String) options.get("liveAgentPod");
        String orgId = (String) options.get("orgId");
        String deploymentId = (String) options.get("deploymentId");
        String buttonId = (String) options.get("buttonId");

        this.liveAgentChatConfigBuilder = new ChatConfiguration.Builder(orgId, buttonId, deploymentId, liveAgentPod);
    }

    private ChatConfiguration buildLiveAgentChatConfig() {
        this.liveAgentChatConfigBuilder.chatUserData(this.liveAgentChatUserData);
        this.liveAgentChatConfigBuilder.chatEntities(this.liveAgentChatEntities);
        return this.liveAgentChatConfigBuilder.build();
    }

    private boolean addPrechatField(JSONObject field, CallbackContext callbackContext) {
        String type;
        String label;
        String value;
        boolean isRequired;
        int keyboardType;
        int autocorrectionType;
        JSONArray values;

        try {
            type = (String) field.get("type");
        } catch (JSONException e) {
            type = "text";
        }

        try {
            label = (String) field.get("label");
        } catch (JSONException e) {
            label = "Label";
        }

        try {
            value = (String) field.get("value");
        } catch (JSONException e) {
            value = "empty";
        }

        try {
            isRequired = (boolean) field.get("isRequired");
        } catch (JSONException e) {
            isRequired = false;
        }

        try {
            keyboardType = (int) field.get("keyboardType");
        } catch (JSONException e) {
            keyboardType = 0;
        }

        try {
            autocorrectionType = (int) field.get("autocorrectionType");
        } catch (JSONException e) {
            autocorrectionType = 0;
        }

        try {
            values = (JSONArray) field.get("values");
        } catch (JSONException e) {
            values = new JSONArray();
        }

        switch (type) {
            case "text":
                PreChatTextInputField newTextField = new PreChatTextInputField.Builder()
                        .required(isRequired)
                        .inputType(this.mapKeyboardType(keyboardType))
                        // .mapToChatTranscriptFieldName("Email__c") // Method not supportd on iOS
                        .build(label, label);
                this.liveAgentChatUserData.add(newTextField);
                break;
            case "hidden":
                ChatUserData newHiddenField = new ChatUserData(
                        label,
                        value,
                        true);
                this.liveAgentChatUserData.add(newHiddenField);
                break;
            case "picker":
                if (values.length() > 0) {
                    PreChatPickListField.Builder newPickerFieldBuilder = new PreChatPickListField.Builder();
                    newPickerFieldBuilder.required(isRequired);

                    JSONObject aField;
                    String aLabel;
                    String aValue;
                    for (int i = 0; i < values.length(); i++) {
                        try {
                            aField = values.getJSONObject(i);
                        } catch (JSONException e) {
                            continue;
                        }

                        try {
                            aLabel = (String) aField.get("label");
                        } catch (JSONException e) {
                            aLabel = "Label";
                        }

                        try {
                            aValue = (String) aField.get("value");
                        } catch (JSONException e) {
                            aValue = "";
                        }

                        newPickerFieldBuilder.addOption(new PreChatPickListField.Option(aLabel, aValue));
                    }

                    PreChatPickListField newPickerField = newPickerFieldBuilder.build(label, label);
                }
                break;
        }

        callbackContext.success();

        return true;
    }

    private boolean clearPrechatFields(CallbackContext callbackContext) {
        this.liveAgentChatUserData.clear();
        return true;
    }

    private int mapKeyboardType(int keyboardType) {
        switch (keyboardType) {
            case 0:
                return EditorInfo.TYPE_CLASS_TEXT;
            case 1:
                return EditorInfo.TYPE_CLASS_TEXT;
            case 2:
                return EditorInfo.TYPE_CLASS_NUMBER;
            case 3:
                return EditorInfo.TYPE_TEXT_VARIATION_URI;
            case 4:
                return EditorInfo.TYPE_CLASS_NUMBER;
            case 5:
                return EditorInfo.TYPE_CLASS_PHONE;
            case 6:
                return EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME;
            case 7:
                return EditorInfo.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;
            case 8:
                return EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;
            case 9:
                return EditorInfo.TYPE_CLASS_TEXT;
            case 10:
                return EditorInfo.TYPE_CLASS_TEXT;
            case 11:
                return EditorInfo.TYPE_CLASS_TEXT;
            default:
                return EditorInfo.TYPE_CLASS_TEXT;
        }
    }


}
