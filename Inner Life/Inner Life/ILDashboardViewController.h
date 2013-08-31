//
//  ILDashboardViewController.h
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ILSessionProgressBar.h"
#import "ILNotificationClockView.h"

@interface ILDashboardViewController : UIViewController

- (void)updateSessionProgressBar;
- (IBAction)answerQuestions:(id)sender;
- (IBAction)sync:(UIButton *)sender;

@property (weak, nonatomic) IBOutlet ILSessionProgressBar *sessionProgressBar;
@property (weak, nonatomic) IBOutlet ILNotificationClockView *clock;

@property BOOL needsJump;
@property NSInteger sessionToJumpTo;

@end
