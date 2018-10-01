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
import com.salesforce.android.chat.core.model.ChatEntityField;
import com.salesforce.android.chat.core.model.ChatUserData;
import com.salesforce.android.chat.ui.ChatUI;
import com.salesforce.android.chat.ui.ChatUIClient;
import com.salesforce.android.chat.ui.ChatUIConfiguration;
import com.salesforce.android.service.common.utilities.control.Async;
import com.salesforce.android.chat.ui.model.PreChatTextInputField;
import com.salesforce.android.chat.ui.model.PreChatPickListField;

import java.util.ArrayList;
import java.util.List;


public class SalesforceSnapInsPlugin extends CordovaPlugin {

    private ChatConfiguration.Builder liveAgentChatConfigBuilder;
    private List<ChatUserData> liveAgentChatUserData = new ArrayList<ChatUserData>();
    private List<ChatEntity> liveAgentChatEntities = new ArrayList<ChatEntity>();

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
                                callbackContext.success("true");
                                break;
                            case NoAgentsAvailable:
                                callbackContext.success("false");
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
        } else if (action.equals("addPrechatEntity")) {

            JSONObject entity;
            try {
                entity = (JSONObject)args.get(0);
            } catch (JSONException e) {
                callbackContext.error("Unable parse entity");
                return false;
            }

            return this.addPrechatEntity(entity, callbackContext);
        } else if (action.equals("clearPrechatEntities")) {
            return this.clearPrechatEntities(callbackContext);
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
//        int autocorrectionType; // not used on Android
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
            isRequired = (boolean) field.get("required");
        } catch (JSONException e) {
            isRequired = false;
        }

        try {
            keyboardType = (int) field.get("keyboardType");
        } catch (JSONException e) {
            keyboardType = 0;
        }

//        try {
//            autocorrectionType = (int) field.get("autocorrectionType");
//        } catch (JSONException e) {
//            autocorrectionType = 0;
//        }

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
                        // .mapToChatTranscriptFieldName("Email__c") // Method not supported on iOS
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
                    this.liveAgentChatUserData.add(newPickerField);
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

    private boolean addPrechatEntity(JSONObject field, CallbackContext callbackContext) {
        String name;
        String linkToEntityName;
        String linkToEntityField;
        String saveToTranscript;
        boolean showOnCreate;
        JSONArray fieldMap;

        try {
            name = (String) field.get("name");
        } catch (JSONException e) {
            name = "entity";
        }

        try {
            linkToEntityName = (String) field.get("linkToEntityName");
        } catch (JSONException e) {
            linkToEntityName = "";
        }

        try {
            linkToEntityField = (String) field.get("linkToEntityField");
        } catch (JSONException e) {
            linkToEntityField = "";
        }

        try {
            saveToTranscript = (String) field.get("saveToTranscript");
        } catch (JSONException e) {
            saveToTranscript = "";
        }

        try {
            showOnCreate = (boolean) field.get("showOnCreate");
        } catch (JSONException e) {
            showOnCreate = false;
        }

        try {
            fieldMap = (JSONArray) field.get("fieldMap");
        } catch (JSONException e) {
            fieldMap = new JSONArray();
        }

        if (fieldMap.length() > 0) {
            ChatEntity.Builder entityBuilder = new ChatEntity.Builder();
            entityBuilder.linkToAnotherSalesforceObject(linkToEntityName, linkToEntityField);
            entityBuilder.linkToTranscriptField(saveToTranscript);
            entityBuilder.showOnCreate(showOnCreate);

            JSONObject aField;
            ChatUserData aChatUserData;
            String aFieldName;
            String aLabel;
            boolean anIsExactMatch;
            boolean aDoCreate;
            boolean aDoFind;
            for (int i = 0; i < fieldMap.length(); i++) {
                try {
                    aField = fieldMap.getJSONObject(i);
                } catch (JSONException e) {
                    continue;
                }

                try {
                    aFieldName = (String) aField.get("fieldName");
                } catch (JSONException e) {
                    aFieldName = "";
                }

                try {
                    aLabel = (String) aField.get("label");
                } catch (JSONException e) {
                    aLabel = "";
                }

                try {
                    anIsExactMatch = (boolean) aField.get("isExactMatch");
                } catch (JSONException e) {
                    anIsExactMatch = false;
                }

                try {
                    aDoCreate = (boolean) aField.get("doCreate");
                } catch (JSONException e) {
                    aDoCreate = false;
                }

                try {
                    aDoFind = (boolean) aField.get("doFind");
                } catch (JSONException e) {
                    aDoFind = false;
                }

                aChatUserData = this.getChatUserDataFromLabel(aLabel);

                if (aChatUserData != null) {
                    entityBuilder.addChatEntityField(new ChatEntityField.Builder()
                            .doFind(aDoFind)
                            .isExactMatch(anIsExactMatch)
                            .doCreate(aDoCreate)
                            .build(aFieldName, aChatUserData));
                }
            }

            ChatEntity newEntity = entityBuilder.build(name);
            this.liveAgentChatEntities.add(newEntity);
        }

        callbackContext.success();

        return true;
    }

    private boolean clearPrechatEntities(CallbackContext callbackContext) {
        this.liveAgentChatEntities.clear();
        return true;
    }

    private ChatUserData getChatUserDataFromLabel(@NonNull String label) {
        for(ChatUserData aChatUserData : this.liveAgentChatUserData){
            if (aChatUserData.getAgentLabel().equals(label)) {
                return aChatUserData;
            }
        }
        return null;
    }


}
