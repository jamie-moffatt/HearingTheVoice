//
//  ILAppDelegate.h
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ILDashboardViewController.h"

@interface ILAppDelegate : UIResponder <UIApplicationDelegate>
{
    ILDashboardViewController* dashboard;
}

@property (strong, nonatomic) UIWindow* window;
@property (strong, nonatomic) UINavigationController* navigationController;

@end
