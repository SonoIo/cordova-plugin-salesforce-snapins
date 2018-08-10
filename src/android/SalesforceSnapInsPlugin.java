package org.apache.cordova.salesforce;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.salesforce.android.chat.core.AgentAvailabilityClient;
import com.salesforce.android.chat.core.ChatConfiguration;
import com.salesforce.android.chat.core.ChatCore;
import com.salesforce.android.chat.core.model.AvailabilityState;
import com.salesforce.android.chat.ui.ChatUI;
import com.salesforce.android.chat.ui.ChatUIClient;
import com.salesforce.android.chat.ui.ChatUIConfiguration;
import com.salesforce.android.service.common.utilities.control.Async;
import com.sfsplugin.test.MainActivity;



public class SalesforceSnapInsPlugin extends CordovaPlugin {

    private String liveAgentPod;
    private String orgId;
    private String deploymentId;
    private String buttonId;

    private ChatConfiguration liveAgentChatConfig;

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
                liveAgentChatOptions = (JSONObject)options.get("liveAgentChat");
                this.liveAgentPod = (String)liveAgentChatOptions.get("liveAgentPod");
                this.orgId = (String)liveAgentChatOptions.get("orgId");
                this.deploymentId = (String)liveAgentChatOptions.get("deploymentId");
                this.buttonId = (String)liveAgentChatOptions.get("buttonId");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            this.liveAgentChatConfig = new ChatConfiguration.Builder(this.orgId, this.buttonId, this.deploymentId, this.liveAgentPod).build();

            callbackContext.success();

        } else if (action.equals("openLiveAgentChat")) {

            Activity mainActivity = this.cordova.getActivity();

            ChatUI.configure(ChatUIConfiguration.create(this.liveAgentChatConfig))
                .createClient(getApplicationContext())
                .onResult(new Async.ResultHandler<ChatUIClient>() {
                    @Override public void handleResult (Async<?> operation, @NonNull ChatUIClient chatUIClient) {
                        chatUIClient.startChatSession((FragmentActivity) mainActivity);
                    }
                });

        } else if (action.equals("determineAvailability")) {

            AgentAvailabilityClient client = ChatCore.configureAgentAvailability(this.liveAgentChatConfig);

            client.check()
                .onResult(new Async.ResultHandler<AvailabilityState>() {
                    @Override
                    public void handleResult(Async<?> async, @NonNull AvailabilityState state) {
                        switch (state.getStatus()) {
                            case AgentsAvailable: {
                                callbackContext.success(1);
                                break;
                            }
                            case NoAgentsAvailable: {
                                callbackContext.success(0);
                                break;
                            }
                            case Unknown: {
                                callbackContext.error("Unknown error");
                                break;
                            }
                        }
                    }
                });
        }


        return false;
    }


}
