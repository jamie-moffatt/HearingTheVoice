//
//  ILUserFormViewController.h
//  Inner Life
//
//  Created by Matthew Bates on 14/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ILUserFormViewController : UIViewController

- (IBAction)showUserCodeHelp:(id)sender;
@property (weak, nonatomic) IBOutlet UITextField *txtUserCode;
@property (weak, nonatomic) IBOutlet UITextField *txtAge;
@property (weak, nonatomic) IBOutlet UISegmentedControl *segGender;
- (IBAction)submitUserDetails:(id)sender;

@end
