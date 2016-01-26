//
//  ILFirstTimeViewController.h
//  Inner Life
//
//  Created by Matthew Bates on 22/01/2016.
//  Copyright © 2016 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ILFirstTimeViewController : UIViewController

@property (weak, nonatomic) IBOutlet UITextField *txtStudyCode;
@property (weak, nonatomic) IBOutlet UIButton *btnStartFullApp;


- (IBAction)onStartDemoTapped:(UIButton *)sender;
- (IBAction)onStartFullApp:(UIButton *)sender;

@end
