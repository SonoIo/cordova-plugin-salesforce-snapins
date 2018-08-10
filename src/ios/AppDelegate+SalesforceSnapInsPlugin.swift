//
//  AppDelegate+SFChat.swift
//  HelloCordova
//
//  Created by BAGGIO Matteo on 27/07/18.
//

import Foundation
import UIKit
import UserNotifications

extension AppDelegate {

	override open func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey : Any]? = nil) -> Bool {
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
		return super.application(application, didFinishLaunchingWithOptions: launchOptions)
	}

}
