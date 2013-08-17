//
//  ILDashboardViewController.h
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ILSessionProgressBar.h"

@interface ILDashboardViewController : UIViewController

- (IBAction)openFirstTimeForm:(id)sender;
- (IBAction)answerQuestions:(id)sender;
- (IBAction)testNotification:(id)sender;

@property (weak, nonatomic) IBOutlet UILabel *sessionLabel;
@property (weak, nonatomic) IBOutlet ILSessionProgressBar *sessionProgressBar;


@end
