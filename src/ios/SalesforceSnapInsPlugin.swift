//
//  SalesforceSnapInsPlugin.swift
//  SalesforceSnapInsPlugin
//
//  Created by BAGGIO Matteo on 07/08/18.
//

import Foundation
import UserNotifications
import ServiceCore
import ServiceChat

func hexStringToUIColor(_ hex: String) -> UIColor {
    var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()

    if (cString.hasPrefix("#")) {
        cString.remove(at: cString.startIndex)
    }

    if ((cString.count) != 6) {
        return UIColor.gray
    }

    var rgbValue:UInt32 = 0
    Scanner(string: cString).scanHexInt32(&rgbValue)

    return UIColor(
        red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
        green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
        blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
        alpha: CGFloat(1.0)
    )
}


@objc(SalesforceSnapInsPlugin) class SalesforceSnapInsPlugin: CDVPlugin, UNUserNotificationCenterDelegate, SCSChatSessionDelegate {

    static let sharedInstance = SalesforceSnapInsPlugin()
    static func shared() -> SalesforceSnapInsPlugin { return SalesforceSnapInsPlugin.sharedInstance }

    var liveAgentPod: String?
    var orgId: String?
    var deploymentId: String?
    var buttonId: String?

    private var liveAgentChatConfig: SCSChatConfiguration?

    // TODO: here add SOS and Case management configuration

    override func pluginInitialize () {
        ServiceCloud.shared().chatCore.add(delegate: self)

        if #available(iOS 10.0, *) {
            let center = UNUserNotificationCenter.current()
            center.delegate = SalesforceSnapInsPlugin.shared()
            center.requestAuthorization(options: [.alert,.sound], completionHandler: { granted, error in
                // Enable or disable features based on authorization
            })
            let generalCategory = UNNotificationCategory(identifier: "General", actions: [], intentIdentifiers: [], options: .customDismissAction)
            let categorySet: Set<UNNotificationCategory> = [generalCategory]
            center.setNotificationCategories(categorySet)
        }
    }

    @objc func initialize(_ command: CDVInvokedUrlCommand) {

        guard let options = command.argument(at: 0) as? Dictionary<String, Any> else {
            return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Missing options"), callbackId: command.callbackId)
        }

        if let colors = options["colors"] as? Dictionary<String, String> {
            if let colorsErrorMessage = self.initializeColors(colors) {
                return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: colorsErrorMessage), callbackId: command.callbackId)
            }
        }

        if let liveAgentOptions = options["liveAgentChat"] as? Dictionary<String, Any> {
            if let liveAgentErrorMessage = self.initializeLiveAgentChat(liveAgentOptions) {
                return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: liveAgentErrorMessage), callbackId: command.callbackId)
            }
        }

        // TODO: here add SOS and Case management initializations

        self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_OK), callbackId: command.callbackId)
    }

    // MARK: - Live Agent Chat

    func initializeLiveAgentChat(_ options: Dictionary<String, Any>) -> String? {

        guard let liveAgentPod = options["liveAgentPod"] as? String else {
            return "Missing liveAgentChat.liveAgentPod"
        }

        guard let orgId = options["orgId"] as? String else {
            return "Missing liveAgentChat.orgId"
        }

        guard let deploymentId = options["deploymentId"] as? String else {
            return "Missing liveAgentChat.deploymentId"
        }

        guard let buttonId = options["buttonId"] as? String else {
            return "Missing liveAgentChat.buttonId"
        }

        self.liveAgentPod = liveAgentPod
        self.orgId = orgId
        self.deploymentId = deploymentId
        self.buttonId = buttonId



        self.liveAgentChatConfig = SCSChatConfiguration(liveAgentPod: liveAgentPod,
                                                        orgId: orgId,
                                                        deploymentId: deploymentId,
                                                        buttonId: buttonId)

        return nil
    }

    func setAppearanceColor(_ appearance: SCAppearanceConfiguration, color: UIColor, forName: String) {
        var colorToken: SCSAppearanceColorToken
        switch forName {
        case "brandPrimary":
            colorToken = .brandPrimary
        case "brandPrimaryInverted":
            colorToken = .brandPrimaryInverted
        case "brandSecondary":
            colorToken = .brandSecondary
        case "brandSecondaryInverted":
            colorToken = .brandSecondaryInverted
        case "contrastInverted":
            colorToken = .contrastInverted
        case "contrastPrimary":
            colorToken = .contrastPrimary
        case "contrastQuaternary":
            colorToken = .contrastQuaternary
        case "contrastSecondary":
            colorToken = .contrastSecondary
        case "contrastTertiary":
            colorToken = .contrastTertiary
        case "feedbackPrimary":
            colorToken = .feedbackPrimary
        case "feedbackSecondary":
            colorToken = .feedbackSecondary
        case "feedbackTertiary":
            colorToken = .feedbackTertiary
        case "navbarBackground":
            colorToken = .navbarBackground
        case "navbarInverted":
            colorToken = .navbarInverted
        case "overlay":
            colorToken = .overlay
        default:
            colorToken = .brandPrimary
        }
        appearance.setColor(color, forName: colorToken)
    }

    func initializeColors(_ colors: Dictionary<String, String>) -> String? {
        let appearance = SCAppearanceConfiguration()
        colors.forEach { (arg) in
            let (aColorName, aHexString) = arg
            self.setAppearanceColor(appearance, color: hexStringToUIColor(aHexString), forName: aColorName)
        }

        // TODO: customize font
        // let descriptor = UIFontDescriptor(fontAttributes: [UIFontDescriptor.AttributeName.family : "Proxima Nova"])
        // config.setFontDescriptor(descriptor, fontFileName: "ProximaNova-Light.otf", forWeight: SCFontWeightLight)

        ServiceCloud.shared().appearanceConfiguration = appearance

        return nil
    }

    @objc func addPrechatField(_ command: CDVInvokedUrlCommand) {

        guard let field = command.argument(at: 0) as? Dictionary<String, Any> else {
            return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Missing field"), callbackId: command.callbackId)
        }

        guard let config = self.liveAgentChatConfig else {
            return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Initialize plugin with liveAgentChat option before add pre-chat field"), callbackId: command.callbackId)
        }

        let type = field["type"] as? String ?? "text"
        let label = field["label"] as? String ?? "Label"
        let value = field["value"] as? String ?? "empty"
        let transcriptField = field["transcriptField"] as? String ?? nil
        let isRequired = field["required"] as? Bool ?? false
        let keyboardType = field["keyboardType"] as? Int ?? 0
        let autocorrectionType = field["autocorrectionType"] as? Int ?? 0
        let values = field["values"] as? [Dictionary<String, Any>]

        switch type {
        case "text":
            let newTextField = SCSPrechatTextInputObject(label: label)!
            newTextField.isRequired = isRequired
            newTextField.keyboardType = UIKeyboardType(rawValue: keyboardType)!
            newTextField.autocorrectionType = UITextAutocorrectionType(rawValue: autocorrectionType)!
            if transcriptField != nil {
                newTextField.transcriptFields.add(transcriptField!)
            }
            config.prechatFields.append(newTextField)
        case "hidden":
            let newHiddenField = SCSPrechatObject(label: label, value: value)
            if transcriptField != nil {
                newHiddenField.transcriptFields.add(transcriptField!)
            }
            config.prechatFields.append(newHiddenField)
        case "picker":
            if values != nil {
                let pickerOptions = NSMutableArray()
                values?.forEach { aValue in
                    let aPickerOptionLabel = aValue["label"] as? String ?? "empty"
                    let aPickerOptionValue = aValue["value"] as? String ?? ""
                    pickerOptions.add(SCSPrechatPickerOption(label: aPickerOptionLabel, value: aPickerOptionValue))
                }
                let pickerField = SCSPrechatPickerObject(label: label, options: pickerOptions as NSArray as? [SCSPrechatPickerOption])
                pickerField!.isRequired = isRequired
                if transcriptField != nil {
                    pickerField?.transcriptFields.add(transcriptField!)
                }
                config.prechatFields.append(pickerField!)
            }
        default:
            return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Unknown field type \(type)"), callbackId: command.callbackId)
        }

        self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_OK), callbackId: command.callbackId)
    }

    @objc func clearPrechatFields(_ command: CDVInvokedUrlCommand) {
        guard let config = self.liveAgentChatConfig else {
            return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Initialize plugin with liveAgentChat option before clear pre-chat fields"), callbackId: command.callbackId)
        }
        // Remove old pre-chat fields
        config.prechatFields.removeAll()
        self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_OK), callbackId: command.callbackId)
    }

    @objc func addPrechatEntity(_ command: CDVInvokedUrlCommand) {
        guard let field = command.argument(at: 0) as? Dictionary<String, Any> else {
            return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Missing entity"), callbackId: command.callbackId)
        }

        guard let config = self.liveAgentChatConfig else {
            return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Initialize plugin with liveAgentChat option before add pre-chat entity"), callbackId: command.callbackId)
        }

        let name = field["name"] as? String ?? "entity"
        let linkToEntityName = field["linkToEntityName"] as? String ?? ""
        let linkToEntityField = field["linkToEntityField"] as? String ?? ""
        let saveToTranscript = field["saveToTranscript"] as? String ?? ""
        let showOnCreate = field["showOnCreate"] as? Bool ?? false
        let fieldMap = field["fieldMap"] as? [Dictionary<String, Any>]

        let newEntity = SCSPrechatEntity(entityName: name)
        newEntity.linkToEntityName = linkToEntityName
        newEntity.linkToEntityField = linkToEntityField
        newEntity.saveToTranscript = saveToTranscript
        newEntity.showOnCreate = showOnCreate

        if fieldMap != nil {
            fieldMap?.forEach { aField in
                let aFieldName = aField["fieldName"] as? String ?? ""
                let aLabel = aField["label"] as? String ?? ""
                let anIsExactMatch = aField["isExactMatch"] as? Bool ?? false
                let aDoCreate = aField["doCreate"] as? Bool ?? false
                let aDoFind = aField["doFind"] as? Bool ?? false

                let aNewFieldMapEntry = SCSPrechatEntityField(fieldName: aFieldName, label: aLabel)
                aNewFieldMapEntry.isExactMatch = anIsExactMatch
                aNewFieldMapEntry.doCreate = aDoCreate
                aNewFieldMapEntry.doFind = aDoFind
                newEntity.entityFieldsMaps.add(aNewFieldMapEntry)
            }
        }

        config.prechatEntities.append(newEntity)

        self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_OK), callbackId: command.callbackId)
    }

    @objc func clearPrechatEntities(_ command: CDVInvokedUrlCommand) {
        guard let config = self.liveAgentChatConfig else {
            return self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Initialize plugin with liveAgentChat option before clear pre-chat entities"), callbackId: command.callbackId)
        }
        // Remove old pre-chat fields
        config.prechatEntities.removeAll()
        self.commandDelegate!.send(CDVPluginResult(status: CDVCommandStatus_OK), callbackId: command.callbackId)
    }

    @objc func openLiveAgentChat(_ command: CDVInvokedUrlCommand) {
        let config = self.liveAgentChatConfig!
        ServiceCloud.shared().chatUI.showChat(with: config, showPrechat: !config.prechatFields.isEmpty)
        let result: CDVPluginResult = CDVPluginResult(status: CDVCommandStatus_OK)
        commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func determineAvailability(_ command: CDVInvokedUrlCommand) {
        let chat = ServiceCloud.shared().chatCore!
        let config = self.liveAgentChatConfig!
        let commandDelegate = self.commandDelegate!
        chat.determineAvailability(with: config, completion: { (error: Error?, available: Bool) in
            var result: CDVPluginResult
            if (error != nil) {
                result = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "error")
            } else if (available) {
                result = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: true)
            } else {
                result = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: false)
            }
            commandDelegate.send(result, callbackId: command.callbackId)
        })
    }

    @objc func session(_ session: SCSChatSession!, didError error: Error!, fatal: Bool) {
        print("Chat error: \(error.localizedDescription)")
    }

    // MARK: - UNUserNotificationCenterDelegate

    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        // let chat = ServiceCloud.shared().chatUI!
        // chat.handle(response.notification) // When fixed by Salesforce uncomment this line
    }

    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let chat = ServiceCloud.shared().chatUI!
        if (chat.shouldDisplayNotificationInForeground()) {
            completionHandler(.alert)
        }
    }

}
