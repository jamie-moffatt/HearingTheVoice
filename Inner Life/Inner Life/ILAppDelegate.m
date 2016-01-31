//
//  ILAppDelegate.m
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILAppDelegate.h"

#import "ILAppManager.h"
#import "ILQuestionFormViewController.h"

#import <Fabric/Fabric.h>
#import <Crashlytics/Crashlytics.h>

@implementation ILAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Hook for error reporting through Crashlytics
    [Fabric with:@[[Crashlytics class]]];

    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    
    // Override point for customization after application launch.
    
    dashboard = [[ILDashboardViewController alloc] initWithNibName:@"ILDashboard" bundle:nil];
    
    self.navigationController = [[UINavigationController alloc] initWithRootViewController:dashboard];
    self.window.rootViewController = self.navigationController;
    [self.window makeKeyAndVisible];
    
    [ILAppManager setDefaults];
    [ILAppManager setFirstRun:NO];
    
    return YES;
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
    NSInteger sessionID = [[notification.userInfo objectForKey:@"SESSION_ID"] integerValue];
    ILQuestionFormViewController* questionForm = [[ILQuestionFormViewController alloc] initWithNibName:nil bundle:nil];
    questionForm.dashboard = dashboard;
    questionForm.currentSession = sessionID;
    [dashboard.navigationController pushViewController:questionForm animated:YES];
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    [dashboard updateSessionProgressBar];
    [dashboard.clock setNeedsDisplay];
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
